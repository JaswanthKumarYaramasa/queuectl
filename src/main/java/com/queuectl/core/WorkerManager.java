package com.queuectl.core;

import java.util.ArrayList;
import java.util.List;

public class WorkerManager {
    private static WorkerManager INSTANCE;
    private final List<Thread> workers = new ArrayList<>();

    private WorkerManager() {}

    public static synchronized WorkerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new WorkerManager();
        return INSTANCE;
    }

    public void startWorkers(int count) {
        stopAll(); // stop old ones if any

        System.out.println("[WorkerManager] Starting " + count + " worker(s)...");
        for (int i = 0; i < count; i++) {
            Worker w = new Worker("worker-" + i);
            Thread t = new Thread(w, "worker-" + i);
            workers.add(t);
            t.start();
        }

        // ✅ Keep alive until manually interrupted
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("[WorkerManager] Interrupted, stopping all workers...");
            stopAll();
        }
    }

    public void stopAll() {
        for (Thread t : workers) {
            t.interrupt();
        }
        workers.clear();
        System.out.println("[WorkerManager] All workers stopped.");
    }
    public int activeCount() {
        return (int) workers.stream().filter(Thread::isAlive).count();
    }

}
