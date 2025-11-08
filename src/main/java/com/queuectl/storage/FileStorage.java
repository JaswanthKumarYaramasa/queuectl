package com.queuectl.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.queuectl.core.Job;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileStorage {
    private static final Gson gson = new Gson();

    // Load jobs from default file
    public List<Job> loadJobs() {
        return loadJobs("jobs.json");
    }

    // Load jobs from specific file
    public List<Job> loadJobs(String filename) {
        try {
            if (!Files.exists(Path.of(filename))) {
                return new ArrayList<>(); // ✅ safe empty list if file missing
            }

            try (Reader r = new FileReader(filename)) {
                Type t = new TypeToken<List<Job>>() {}.getType();
                List<Job> list = gson.fromJson(r, t);
                return (list == null) ? new ArrayList<>() : list; // ✅ avoid null
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // ✅ always return a non-null list
        }
    }

    // Save jobs to default file
    public void saveJobs(List<Job> jobs) {
        saveJobs(jobs, "jobs.json");
    }

    // Save jobs to specific file
    public void saveJobs(List<Job> jobs, String filename) {
        try (Writer w = new FileWriter(filename)) {
            gson.toJson(jobs, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Compatibility for old method names
    public void writeJobs(String filename, List<Job> jobs) {
        saveJobs(jobs, filename);
    }

    public List<Job> readJobs(String filename) {
        return loadJobs(filename);
    }
}
