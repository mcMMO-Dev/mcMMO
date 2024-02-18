package com.gmail.nossr50.config;

import com.gmail.nossr50.commands.levelup.LevelUpCommand;
import com.gmail.nossr50.commands.levelup.LevelUpCommandBuilder;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandOnLevelUpConfig extends BukkitConfig {

    public static final String LEVEL_UP_COMMANDS = "level_up_commands";
    public static final String LEVELS_SECTION = "levels";
    public static final String SKILLS_SECTION = "skills";
    public static final String CONDITION_SECTION = "condition";
    public static final String ENABLED = "enabled";
    public static final String COMMANDS = "commands";
    public static final String POWER_LEVEL_SECTION = "power_level";

    public CommandOnLevelUpConfig(@NotNull File dataFolder) {
        super("levelupcommands.yml", dataFolder);
        // TODO: loadKeys() should really get called in super
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        final ConfigurationSection configurationSection = config.getConfigurationSection(LEVEL_UP_COMMANDS);
        if (configurationSection == null) {
            LogUtils.debug(mcMMO.p.getLogger(), "No commands found in the level up commands config file.");
            return;
        }

        for (String key : configurationSection.getKeys(false)) {
            final ConfigurationSection commandSection = configurationSection.getConfigurationSection(key);
            if (commandSection == null) {
                mcMMO.p.getLogger().severe("Unable to load command section for key: " + key);
                continue;
            }

            LevelUpCommand levelUpCommand = buildSkillLevelUpCommand(commandSection);

            if (levelUpCommand == null) {
                mcMMO.p.getLogger().severe("Invalid command format for key: " + key);
            } else {
                mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);
                mcMMO.p.getLogger().info("Level up command successfully loaded from config for key: " + key);
            }
        }
    }

    private @Nullable LevelUpCommand buildSkillLevelUpCommand(final ConfigurationSection commandSection) {
        // TODO: Rework
        return null;
//        LevelUpCommandBuilder builder = new LevelUpCommandBuilder();
//        // check if command is enabled
//        if (!commandSection.getBoolean(ENABLED, true)) {
//            return null;
//        }
//        /* Condition Section */
//        ConfigurationSection condition = commandSection.getConfigurationSection(CONDITION_SECTION);
//        if (condition == null) {
//            mcMMO.p.getLogger().severe("No condition section found for command named " + commandSection.getName());
//            return null;
//        }
//
//        // Skill Filter
//        // check if skills is string or configuration section
//        if (condition.contains(SKILLS_SECTION)) {
//            if (condition.isString(SKILLS_SECTION)) {
//                String skillName = condition.getString(SKILLS_SECTION);
//                if (skillName != null) {
//                    PrimarySkillType primarySkillType = mcMMO.p.getSkillTools().matchSkill(skillName);
//                    if (primarySkillType != null) {
//                        builder.withSkillFilter(getSkillsFromFilter(new HashSet<>(Set.of(skillName))));
//                    }
//                }
//            } else {
//                ConfigurationSection skillsSection = condition.getConfigurationSection(SKILLS_SECTION);
//                if (skillsSection != null) {
//                    Set<String> skillNames = skillsSection.getKeys(false);
//                    Set<PrimarySkillType> skillsFromFilter = getSkillsFromFilter(skillNames);
//                    if (skillsFromFilter.isEmpty()) {
//                        LogUtils.debug(mcMMO.p.getLogger(), "No valid skills found for command named "
//                                + commandSection.getName() + "for condition section named " + skillsSection.getName());
//                    } else {
//                        builder.withSkillFilter(skillsFromFilter);
//                    }
//                }
//            }
//        }
//
//        // for now only simple condition is supported
//        if (!condition.contains(LEVELS_SECTION)) {
//            mcMMO.p.getLogger().severe("No condition.levels section found for command named " + commandSection.getName());
//            return null;
//        }
//
//        Collection<Integer> levels = condition.getIntegerList(LEVELS_SECTION);
//        if (levels.isEmpty()) {
//            mcMMO.p.getLogger().severe("No valid levels found in condition.levels for command named "
//                    + commandSection.getName());
//            return null;
//        }
//        builder.withLevels(levels);
//
//        // commands
//        if (commandSection.isString(COMMANDS)) {
//            String command = commandSection.getString(COMMANDS);
//            if (command != null) {
//                builder.command(command);
//            }
//        } else {
//            List<String> commands = commandSection.getStringList(COMMANDS);
//            if (commands.isEmpty()) {
//                mcMMO.p.getLogger().severe("No commands defined for command named "
//                        + commandSection.getName());
//                return null;
//            } else {
//                builder.commands(commands);
//            }
//        }
//
//        return builder.build();
    }
}
