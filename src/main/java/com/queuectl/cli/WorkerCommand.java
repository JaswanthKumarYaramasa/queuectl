package com.queuectl.cli;

import com.queuectl.core.WorkerManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "worker", description = "Manage workers")
public class WorkerCommand implements Runnable {
    @Option(names = "--start", description = "Start workers")
    boolean start;

    @Option(names = "--count", defaultValue = "1", description = "Number of workers")
    int count;

    @Option(names = "--stop", description = "Stop workers")
    boolean stop;

    @Override
    public void run() {
        WorkerManager wm = WorkerManager.getInstance();
        if (stop) wm.stopAll();
        else if (start) wm.startWorkers(count);
        else System.out.println("Use --start or --stop");
    }
}
