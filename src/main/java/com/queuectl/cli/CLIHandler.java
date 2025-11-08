package com.queuectl.cli;

import picocli.CommandLine;

@CommandLine.Command(
        name = "queuectl",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "CLI-based background job queue system"
)
public class CLIHandler implements Runnable {

    public static void handle(String[] args) {
        int exitCode = new CommandLine(new CLIHandler())
                .addSubcommand("enqueue", new EnqueueCommand())
                .addSubcommand("worker", new WorkerCommand())
                .addSubcommand("status", new StatusCommand())
                .addSubcommand("list", new ListCommand())
                .addSubcommand("dlq", new DlqCommand())
                .addSubcommand("config", new ConfigCommand())
                .execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Use a subcommand: enqueue | worker | status | list | dlq | config");
    }
}
