package com.gmail.nossr50.config;

import com.gmail.nossr50.commands.levelup.LevelUpCommand;
import com.gmail.nossr50.commands.levelup.LevelUpCommandManager;
import com.gmail.nossr50.commands.levelup.LevelUpCondition;
import com.gmail.nossr50.commands.levelup.RegistrationSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loads {@code levelupcommands.yml} and registers each valid entry with the
 * {@link LevelUpCommandManager} as a {@link RegistrationSource#CONFIG} registration. Loading
 * clears previous config registrations first, so reloading this config never disturbs
 * registrations other plugins added through the API.
 */
public class CommandOnLevelUpConfig extends BukkitConfig {

    public static final String LEVEL_UP_COMMANDS = "level_up_commands";
    public static final String ENABLED = "enabled";
    public static final String SKILLS = "skills";
    public static final String LEVELS = "levels";
    public static final String POWER_LEVELS = "power_levels";
    public static final String COMMANDS = "commands";
    public static final String RUN_AS = "run_as";
    public static final String ALL_SKILLS = "all";

    public CommandOnLevelUpConfig(@NotNull File dataFolder) {
        super("levelupcommands.yml", dataFolder);
        // TODO: loadKeys() should really get called in super
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        final LevelUpCommandManager manager = mcMMO.p.getLevelUpCommandManager();
        manager.clearConfigRegistrations();

        final ConfigurationSection root = config.getConfigurationSection(LEVEL_UP_COMMANDS);
        if (root == null) {
            LogUtils.debug(mcMMO.p.getLogger(),
                    "No " + LEVEL_UP_COMMANDS + " section found in " + fileName);
            return;
        }

        int loaded = 0;
        for (String key : root.getKeys(false)) {
            final ConfigurationSection entry = root.getConfigurationSection(key);
            if (entry == null) {
                warn(key, "entry is not a section, skipping it");
                continue;
            }

            if (!entry.getBoolean(ENABLED, true)) {
                LogUtils.debug(mcMMO.p.getLogger(),
                        "Level up command '" + key + "' is disabled, skipping it");
                continue;
            }

            final LevelUpCommand command = buildCommand(key, entry);
            if (command != null) {
                manager.register(command, RegistrationSource.CONFIG);
                loaded++;
            }
        }

        mcMMO.p.getLogger().info(
                "Loaded " + loaded + " level up command(s) from " + fileName);
    }

    private @Nullable LevelUpCommand buildCommand(@NotNull String key,
            @NotNull ConfigurationSection entry) {
        final List<String> commands = readCommands(entry);
        if (commands.isEmpty()) {
            warn(key, "no commands defined, skipping it");
            return null;
        }

        final Set<PrimarySkillType> skills = readSkills(key, entry);
        final List<Integer> levels = readPositiveLevels(key, entry, LEVELS);
        final List<Integer> powerLevels = readPositiveLevels(key, entry, POWER_LEVELS);

        if (!skills.isEmpty() && levels.isEmpty()) {
            warn(key, "has " + SKILLS + " but no " + LEVELS + ", skipping it");
            return null;
        }
        if (skills.isEmpty() && !levels.isEmpty()) {
            warn(key, "has " + LEVELS + " but no valid " + SKILLS + ", skipping it");
            return null;
        }
        if (skills.isEmpty() && powerLevels.isEmpty()) {
            warn(key, "needs " + SKILLS + " with " + LEVELS + ", or " + POWER_LEVELS
                    + ", skipping it");
            return null;
        }

        final LevelUpCommand.RunAs runAs = readRunAs(key, entry);
        return new LevelUpCommand(LevelUpCondition.of(skills, levels, powerLevels), commands,
                runAs);
    }

    private @NotNull List<String> readCommands(@NotNull ConfigurationSection entry) {
        if (entry.isString(COMMANDS)) {
            final String command = entry.getString(COMMANDS);
            return command == null || command.isBlank() ? List.of() : List.of(command);
        }
        final List<String> commands = new ArrayList<>();
        for (String command : entry.getStringList(COMMANDS)) {
            if (command != null && !command.isBlank()) {
                commands.add(command);
            }
        }
        return commands;
    }

    private @NotNull Set<PrimarySkillType> readSkills(@NotNull String key,
            @NotNull ConfigurationSection entry) {
        final List<String> skillNames;
        if (entry.isString(SKILLS)) {
            final String skillName = entry.getString(SKILLS);
            skillNames = skillName == null ? List.of() : List.of(skillName);
        } else {
            skillNames = entry.getStringList(SKILLS);
        }

        final Set<PrimarySkillType> skills = new LinkedHashSet<>();
        for (String skillName : skillNames) {
            if (ALL_SKILLS.equalsIgnoreCase(skillName)) {
                skills.addAll(mcMMO.p.getSkillTools().getNonChildSkills());
                continue;
            }
            final PrimarySkillType skill = mcMMO.p.getSkillTools().matchSkill(skillName);
            if (skill == null) {
                warn(key, "lists unknown skill '" + skillName + "', ignoring that skill");
            } else {
                skills.add(skill);
            }
        }
        return skills;
    }

    private @NotNull List<Integer> readPositiveLevels(@NotNull String key,
            @NotNull ConfigurationSection entry, @NotNull String path) {
        final List<Integer> levels = new ArrayList<>();
        for (int level : entry.getIntegerList(path)) {
            if (level > 0) {
                levels.add(level);
            } else {
                warn(key, "lists non-positive value " + level + " under " + path
                        + ", ignoring that value");
            }
        }
        return levels;
    }

    private @NotNull LevelUpCommand.RunAs readRunAs(@NotNull String key,
            @NotNull ConfigurationSection entry) {
        final String runAsName = entry.getString(RUN_AS, LevelUpCommand.RunAs.CONSOLE.name());
        try {
            return LevelUpCommand.RunAs.valueOf(runAsName.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            warn(key, "has unknown " + RUN_AS + " '" + runAsName + "', using CONSOLE");
            return LevelUpCommand.RunAs.CONSOLE;
        }
    }

    private void warn(@NotNull String key, @NotNull String message) {
        mcMMO.p.getLogger().warning(
                "Level up command '" + key + "' in " + fileName + " " + message);
    }
}
