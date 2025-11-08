package com.queuectl.core;

import com.queuectl.storage.FileStorage;
import java.time.Instant;                    // ✅ Fix: import this
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JobQueue {
    private static JobQueue INSTANCE;
    private final Map<String, Job> jobs = new ConcurrentHashMap<>();
    private final Set<String> processed = ConcurrentHashMap.newKeySet();  // ✅ Fix: declare processed
    private final FileStorage storage = new FileStorage();

    private JobQueue() {
        List<Job> loaded = storage.loadJobs();
        if (loaded == null) loaded = new ArrayList<>();
        for (Job j : loaded) {
            if (j != null && j.getId() != null) jobs.put(j.getId(), j);
        }
    }

    public static synchronized JobQueue getInstance() {
        if (INSTANCE == null) INSTANCE = new JobQueue();
        return INSTANCE;
    }

    public synchronized void addJob(Job j) {
        jobs.put(j.getId(), j);
        persist();
    }

    public synchronized void persist() {
        storage.saveJobs(new ArrayList<>(jobs.values()));
    }

    public boolean isProcessed(String jobId) {
        return processed.contains(jobId);
    }

    public void markProcessed(String jobId) {
        processed.add(jobId);
    }

    public Collection<Job> allJobs() {
        return jobs.values();
    }

    // ✅ Fixed: scheduled job-safe version
    public synchronized Job pickPendingJob() {
        Instant now = Instant.now();
        for (Job job : jobs.values()) {
            if ("pending".equalsIgnoreCase(job.getState()) && !processed.contains(job.getId())) {
                try {
                    if (job.getRunAt() != null && Instant.parse(job.getRunAt()).isAfter(now)) {
                        continue; // wait until scheduled
                    }
                } catch (Exception ignored) {}
                job.setState("running");
                processed.add(job.getId());
                persist();
                return job;
            }
        }
        return null;
    }

    public synchronized void markCompleted(Job job) {
        job.setState("completed");
        processed.add(job.getId());
        persist();
    }

    public synchronized void markFailed(Job job, int backoffBase) {
        job.incrementAttempts();
        if (job.getAttempts() < job.getMaxRetries()) {
            job.setState("pending");
            System.err.println("[JobQueue] Retrying job " + job.getId() + " after backoff...");
        } else {
            job.setState("failed");
            System.err.println("[JobQueue] Job permanently failed: " + job.getId());
            processed.add(job.getId());
        }
        persist();
    }

    public synchronized Map<String, Long> countByState() {
        Map<String, Long> counts = new HashMap<>();
        for (Job job : jobs.values()) {
            String state = job.getState() == null ? "unknown" : job.getState();
            counts.put(state, counts.getOrDefault(state, 0L) + 1);
        }
        return counts;
    }

    public synchronized List<Job> listByState(String state) {
        List<Job> filtered = new ArrayList<>();
        for (Job job : jobs.values()) {
            if (state.equalsIgnoreCase(String.valueOf(job.getState()))) filtered.add(job);
        }
        return filtered;
    }
}
