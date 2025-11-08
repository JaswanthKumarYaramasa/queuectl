package com.queuectl.cli;

import com.queuectl.core.JobQueue;
import picocli.CommandLine.Command;

@Command(name = "status", description = "Show job counts and worker status")
public class StatusCommand implements Runnable {
    @Override
    public void run() {
        JobQueue jq = JobQueue.getInstance();
        System.out.println("Job counts by state:");
        jq.countByState().forEach((s, c) -> System.out.println("  " + s + ": " + c));
        System.out.println("Active workers: " + com.queuectl.core.WorkerManager.getInstance().activeCount());
    }
}
