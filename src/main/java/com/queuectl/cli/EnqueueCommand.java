package com.queuectl.cli;

import com.google.gson.Gson;
import com.queuectl.core.Job;
import com.queuectl.core.JobQueue;
import com.queuectl.core.ConfigManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "enqueue", description = "Add a job using JSON or raw command string")
public class EnqueueCommand implements Runnable {
    // Accept all arguments after 'enqueue' as one payload
    @Parameters(index = "0", arity = "1..*", description = "Either JSON like '{\"command\":\"echo hi\",\"max_retries\":3}' or raw command string")
    private String[] payloadParts;

    @Override
    public void run() {
        JobQueue jq = JobQueue.getInstance();
        Gson gson = new Gson();

        // Join all parts into one single string
        String payload = String.join(" ", payloadParts).trim();
        Job job;

        try {
            // Try to parse JSON
            job = gson.fromJson(payload, Job.class);
            if (job.getCommand() == null) throw new Exception("Missing command");
            if (job.getMaxRetries() == 0)
                job = Job.create(job.getCommand(), ConfigManager.getInstance().getMaxRetries());
        } catch (Exception e) {
            // Treat as raw command string
            job = Job.create(payload, ConfigManager.getInstance().getMaxRetries());
        }

        jq.addJob(job);
        System.out.println("Enqueued job " + job.getId() + " (" + job.getCommand() + ")");
    }
}
