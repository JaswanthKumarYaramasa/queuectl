package com.queuectl.core;

import com.queuectl.storage.FileStorage;
import java.util.*;

public class DLQManager {
    private static DLQManager INSTANCE;
    private final FileStorage storage = new FileStorage();
    private static final String DLQ_FILE = "dlq.json";

    private DLQManager() {}

    public static synchronized DLQManager getInstance() {
        if (INSTANCE == null) INSTANCE = new DLQManager();
        return INSTANCE;
    }

    public synchronized void add(Job job) {
        List<Job> dlq = storage.loadJobs(DLQ_FILE);
        job.setState("dead");
        dlq.add(job);
        storage.saveJobs(dlq, DLQ_FILE);
        System.err.println("[DLQ] Moved job to DLQ: " + job.getId());
    }

    public synchronized List<Job> list() {
        return storage.loadJobs(DLQ_FILE);
    }

    public synchronized Job retry(String id) {
        List<Job> dlq = storage.loadJobs(DLQ_FILE);
        Job found = null;
        Iterator<Job> it = dlq.iterator();
        while (it.hasNext()) {
            Job j = it.next();
            if (j.getId().equals(id)) {
                found = j;
                it.remove();
                break;
            }
        }
        storage.saveJobs(dlq, DLQ_FILE);
        if (found != null) {
            found.resetAttempts();
            found.setState("pending");
            JobQueue.getInstance().addJob(found);
        }
        return found;
    }
}
