package com.queuectl.core;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.UUID;

public class Job {
    private String id;
    private String command;
    private String state;
    private int attempts;

    @SerializedName("max_retries")
    private int maxRetries;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("run_at")
    private String runAt; // new — for scheduled jobs

    // Empty constructor for Gson
    public Job() {}

    // Factory constructor
    public static Job create(String command, int maxRetries) {
        Job j = new Job();
        j.id = UUID.randomUUID().toString();
        j.command = command;
        j.state = "pending";
        j.attempts = 0;
        j.maxRetries = maxRetries;
        j.createdAt = Instant.now().toString();
        j.updatedAt = j.createdAt;
        j.runAt = null;
        return j;
    }

    // Getters & setters
    public String getId() { return id; }
    public String getCommand() { return command; }
    public String getState() { return state; }
    public int getAttempts() { return attempts; }
    public int getMaxRetries() { return maxRetries; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getRunAt() { return runAt; }

    public void setRunAt(String runAt) { this.runAt = runAt; }
    public void setState(String state) { this.state = state; this.updatedAt = Instant.now().toString(); }
    public void incrementAttempts() { this.attempts++; this.updatedAt = Instant.now().toString(); }
    public void resetAttempts() { this.attempts = 0; this.updatedAt = Instant.now().toString(); }

    @Override
    public String toString() {
        return String.format(
                "Job{id=%s, cmd='%s', state=%s, attempts=%d/%d, runAt=%s}",
                id, command, state, attempts, maxRetries, runAt
        );
    }
}
