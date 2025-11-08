package com.queuectl.cli;

import com.queuectl.core.Job;
import com.queuectl.core.JobQueue;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.List;

@Command(name = "list", description = "List jobs by state")
public class ListCommand implements Runnable {
    @Option(names = "--state", required = true, description = "State (pending, processing, completed, failed, dead)")
    String state;

    @Override
    public void run() {
        List<Job> jobs = JobQueue.getInstance().listByState(state);
        System.out.println("Jobs in state '" + state + "':");
        for (Job j : jobs) System.out.println("  " + j);
    }
}
