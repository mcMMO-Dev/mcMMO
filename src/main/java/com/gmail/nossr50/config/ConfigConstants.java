package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;

import java.io.File;
import java.util.ArrayList;

/**
 * Constants relating to config folders and paths
 */
public class ConfigConstants {
    public final static ArrayList<String> EXAMPLE_BLACKLIST_WORLDS_LIST_DEFAULT;
    public final static String SKILL_SCALING_BENEFIT_EXPLANATION = "\nSub-Skills dynamically adjust their rewards to match the max bonus level, you can think of it as a curve that calculates what bonuses " +
            "\n a player should have based on how far they are from the max bonus level value, and the other parameters used for the scaling of that sub-skill.";
    /* FOLDER NAMES */
    public static final String FOLDER_NAME_CONFIG = "config";
    public static final String FOLDER_NAME_SKILLS = "skills";
    public static final String FOLDER_NAME_EXPERIENCE = "Experience Settings";
    public static final String FOLDER_NAME_DEFAULTS = "defaults";

    /* RELATIVE PATHS */
    public final static String RELATIVE_PATH_CONFIG_DIR = File.separator + FOLDER_NAME_CONFIG + File.separator;
    public final static String RELATIVE_PATH_SKILLS_DIR = RELATIVE_PATH_CONFIG_DIR + FOLDER_NAME_SKILLS + File.separator;
    public final static String RELATIVE_PATH_XP_DIR = RELATIVE_PATH_CONFIG_DIR + FOLDER_NAME_EXPERIENCE + File.separator;
    private final static String[] EXAMPLE_BLACKLIST_WORLDS = {"Example_15434453", "Example_2324423", "Example_323423465"};

    /* Field Names & Comments */
    public static final String SUB_SKILL_NODE = "Sub-Skill";
    public final static String MAX_CHANCE_FIELD_NAME = "Max-Chance";
    public final static String STATIC_ACTIVATION_FIELD_NAME = "Activation-Chance";
    public final static String MAX_BONUS_LEVEL_FIELD_NAME = "Max-Bonus-Level";
    public final static String MAX_BONUS_LEVEL_DESCRIPTION = "This is the level at which full benefits for this skill will be reached." +
            "\nProperties of this skill may or may not scale with level, but those that do will gradually increase until max level is achieved.";
    public final static String MAX_BONUS_PERCENTAGE_FIELD_NAME = "Max-Bonus-Percentage";
    public final static String MAX_CHANCE_FIELD_DESCRIPTION = "The maximum probability for this skill to succeed.";

    //Add the worlds to the list
    static {
        EXAMPLE_BLACKLIST_WORLDS_LIST_DEFAULT = new ArrayList<>();
        EXAMPLE_BLACKLIST_WORLDS_LIST_DEFAULT.add(EXAMPLE_BLACKLIST_WORLDS[0]);
        EXAMPLE_BLACKLIST_WORLDS_LIST_DEFAULT.add(EXAMPLE_BLACKLIST_WORLDS[1]);
        EXAMPLE_BLACKLIST_WORLDS_LIST_DEFAULT.add(EXAMPLE_BLACKLIST_WORLDS[2]);
    }

    /**
     * Return the data folder for mcMMO
     *
     * @return the File for the data folder used by mcMMO
     */
    public static File getDataFolder(mcMMO pluginRef) {
        return pluginRef.getDataFolder();
    }

    public static File getConfigFolder(mcMMO pluginRef) {
        return new File(getDataFolder(pluginRef), FOLDER_NAME_CONFIG);
    }

    public static File getDefaultsFolder(mcMMO pluginRef) {
        return new File(getConfigFolder(pluginRef).getAbsolutePath(), FOLDER_NAME_DEFAULTS);
    }

    public static File getDefaultsConfigFolder(mcMMO pluginRef) {
        return new File(getDefaultsFolder(pluginRef).getAbsolutePath(), FOLDER_NAME_CONFIG);
    }

    public static File getDefaultsSkillFolder(mcMMO pluginRef) {
        return new File(getDefaultsConfigFolder(pluginRef).getAbsolutePath(), FOLDER_NAME_SKILLS);
    }

    public static File getDefaultsXPFolder(mcMMO pluginRef) {
        return new File(getDefaultsConfigFolder(pluginRef).getAbsolutePath(), FOLDER_NAME_EXPERIENCE);
    }

    public static File getConfigSkillFolder(mcMMO pluginRef) {
        return new File(getConfigFolder(pluginRef).getAbsolutePath(), FOLDER_NAME_SKILLS);
    }

    public static File getConfigXPFolder(mcMMO pluginRef) {
        return new File(getConfigFolder(pluginRef).getAbsolutePath(), FOLDER_NAME_EXPERIENCE);
    }

    /**
     * Creates all directories used by mcMMO config files
     */
    public static void makeAllConfigDirectories(mcMMO pluginRef) {
        /* CONFIG DIRECTORY */

        if (!getConfigFolder(pluginRef).exists())
            getConfigFolder(pluginRef).mkdirs();

        /* DEFAULT DIRECTORIES */

        if (!getDefaultsFolder(pluginRef).exists())
            getDefaultsFolder(pluginRef).mkdirs();

        if (!getDefaultsConfigFolder(pluginRef).exists())
            getDefaultsConfigFolder(pluginRef).mkdirs();

        if (!getDefaultsSkillFolder(pluginRef).exists())
            getDefaultsSkillFolder(pluginRef).mkdirs();

        if (!getDefaultsXPFolder(pluginRef).exists())
            getDefaultsXPFolder(pluginRef).mkdirs();

        /* CONFIG SUBDIRECTORIES */

        if (!getConfigSkillFolder(pluginRef).exists())
            getConfigSkillFolder(pluginRef).mkdirs();

        if (!getConfigXPFolder(pluginRef).exists())
            getConfigXPFolder(pluginRef).mkdirs();
    }
}
