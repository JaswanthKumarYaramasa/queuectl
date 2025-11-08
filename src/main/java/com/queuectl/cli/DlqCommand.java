package com.queuectl.cli;

import com.queuectl.core.DLQManager;
import com.queuectl.core.Job;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = "dlq",
        description = "DLQ operations",
        subcommands = {DlqCommand.ListCmd.class, DlqCommand.RetryCmd.class}
)
public class DlqCommand {

    @Command(name = "list", description = "List all DLQ jobs")
    static class ListCmd implements Runnable {
        @Override
        public void run() {
            java.util.List<Job> dlq = DLQManager.getInstance().list();
            if (dlq == null || dlq.isEmpty()) {
                System.out.println("DLQ is empty ✅");
            } else {
                System.out.println("DLQ jobs:");
                for (Job j : dlq) {
                    System.out.println("  " + j);
                }
            }
        }
    }

    @Command(name = "retry", description = "Retry a DLQ job by ID")
    static class RetryCmd implements Runnable {
        @Parameters(index = "0", description = "Job ID to retry")
        String id;

        @Override
        public void run() {
            Job j = DLQManager.getInstance().retry(id);
            if (j == null) {
                System.err.println("❌ DLQ job not found: " + id);
            } else {
                System.out.println("♻️ Retried job: " + id);
            }
        }
    }
}
