package com.gmail.nossr50.config.commands;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.util.commands.CommandExposureRegistry;
import java.io.File;
import org.jetbrains.annotations.NotNull;

public class CommandExposureConfig extends BukkitConfig {
    private final @NotNull CommandExposureRegistry registry;

    public CommandExposureConfig(@NotNull File dataFolder) {
        super("commands.yml", dataFolder);
        registry = new CommandExposureRegistry(config);
    }

    @Override
    protected void loadKeys() {
    }

    public @NotNull CommandExposureRegistry getRegistry() {
        return registry;
    }
}
