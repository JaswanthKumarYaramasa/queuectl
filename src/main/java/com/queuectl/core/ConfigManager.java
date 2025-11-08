package com.queuectl.core;

import com.queuectl.storage.FileStorage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private static ConfigManager INSTANCE;
    private final FileStorage storage = new FileStorage();
    private final Map<String, String> cfg = new ConcurrentHashMap<>(); // ✅ typed map

    private ConfigManager() {
        cfg.put("max_retries", "3");
        cfg.put("backoff_base", "2");
        cfg.put("job-timeout", "60"); // ✅ default timeout
    }

    public static synchronized ConfigManager getInstance() {
        if (INSTANCE == null) INSTANCE = new ConfigManager();
        return INSTANCE;
    }

    public void set(String k, String v) {
        cfg.put(k, v);
    }

    public String get(String k) {
        return cfg.get(k);
    }

    public int getMaxRetries() {
        return Integer.parseInt(cfg.getOrDefault("max_retries", "3"));
    }

    public int getBackoffBase() {
        return Integer.parseInt(cfg.getOrDefault("backoff_base", "2"));
    }

    // ✅ Add this new method
    public int getJobTimeout() {
        try {
            return Integer.parseInt(cfg.getOrDefault("job-timeout", "60"));
        } catch (Exception e) {
            return 60;
        }
    }
}
