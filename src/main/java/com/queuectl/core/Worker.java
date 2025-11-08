package com.queuectl.core;

import java.io.*;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker implements Runnable {
    private final JobQueue queue = JobQueue.getInstance();
    private final ConfigManager cfg = ConfigManager.getInstance();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final String name;

    public Worker(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("[" + name + "] started");

        while (running.get()) {
            Job job = queue.pickPendingJob();
            if (job == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
                continue;
            }

            System.out.println("[" + name + "] executing job " + job.getId() + " : " + job.getCommand());

            File logFile = new File("logs", "job_" + job.getId() + ".log");
            logFile.getParentFile().mkdirs(); // ensure /logs exists

            try (BufferedWriter log = new BufferedWriter(new FileWriter(logFile, true))) {
                ProcessBuilder pb = new ProcessBuilder();
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    pb.command("cmd.exe", "/c", job.getCommand());
                } else {
                    pb.command("bash", "-c", job.getCommand());
                }
                Process p = pb.start();

                // ✅ Timeout watchdog
                ScheduledExecutorService killer = Executors.newSingleThreadScheduledExecutor();
                Future<?> killTask = killer.schedule(() -> {
                    if (p.isAlive()) {
                        p.destroyForcibly();
                        System.err.println("[" + job.getId() + "] killed by timeout");
                        try {
                            log.write("[" + Instant.now() + "] KILLED BY TIMEOUT\n");
                            log.flush();
                        } catch (IOException ignored) {}
                    }
                }, cfg.getJobTimeout(), TimeUnit.SECONDS);

                // ✅ Capture output
                BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line;
                while ((line = out.readLine()) != null) {
                    System.out.println("[" + job.getId() + "][stdout] " + line);
                    log.write("[stdout] " + line + "\n");
                    log.flush();
                }
                while ((line = err.readLine()) != null) {
                    System.err.println("[" + job.getId() + "][stderr] " + line);
                    log.write("[stderr] " + line + "\n");
                    log.flush();
                }

                int exit = p.waitFor();
                killTask.cancel(true);
                killer.shutdownNow();

                if (exit == 0) {
                    queue.markCompleted(job);
                    System.out.println("[" + name + "] job completed: " + job.getId());
                } else {
                    System.out.println("[" + name + "] job failed (exit=" + exit + "): " + job.getId());
                    queue.markFailed(job, cfg.getBackoffBase());
                }
            } catch (Exception e) {
                System.err.println("[" + name + "] exception: " + e.getMessage());
                queue.markFailed(job, cfg.getBackoffBase());
            }
        }

        System.out.println("[" + name + "] stopped");
    }

    public void stop() {
        running.set(false);
    }
}
