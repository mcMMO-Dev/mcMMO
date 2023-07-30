package com.gmail.nossr50.config;

import com.gmail.nossr50.commands.levelup.LevelUpCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CommandOnLevelUpConfig extends BukkitConfig {

    public static final String LEVEL_UP_COMMANDS = "level_up_commands";

    public CommandOnLevelUpConfig(@NotNull File dataFolder) {
        super("commandonlevelup", dataFolder);
    }

    @Override
    protected void loadKeys() {
        final ConfigurationSection configurationSection = config.getConfigurationSection(LEVEL_UP_COMMANDS);
     }

    private LevelUpCommand buildCommand() {
        LevelUpCommand.LevelUpCommandBuilder builder = new LevelUpCommand.LevelUpCommandBuilder();
        return builder.build();
    }
}
