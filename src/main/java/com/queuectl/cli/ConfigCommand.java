package com.queuectl.cli;

import com.queuectl.core.ConfigManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "config", description = "Manage configuration",
        subcommands = {ConfigCommand.Set.class, ConfigCommand.Get.class})
public class ConfigCommand {

    @Command(name = "set", description = "Set a configuration key-value pair")
    static class Set implements Runnable {
        @Parameters(index = "0", description = "Configuration key")
        String key;

        @Parameters(index = "1", description = "Configuration value")
        String value;

        @Override
        public void run() {
            ConfigManager cfg = ConfigManager.getInstance();
            cfg.set(key, value);
            System.out.println("Updated " + key + " = " + value);
        }
    }

    @Command(name = "get", description = "Get configuration value by key")
    static class Get implements Runnable {
        @Parameters(index = "0", description = "Configuration key")
        String key;

        @Override
        public void run() {
            ConfigManager cfg = ConfigManager.getInstance();
            String val = cfg.get(key);
            System.out.println(key + " = " + (val == null ? "undefined" : val));
        }
    }
}
