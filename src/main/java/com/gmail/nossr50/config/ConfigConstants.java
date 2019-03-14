package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;

import java.io.File;

/**
 * Constants relating to config folders and paths
 */
public class ConfigConstants {
    /* HOCON ESCAPE CHARACTER FOR UNDERSCORES */
    public static final String HOCON_FRIENDLY_UNDERSCORE = "\\_";

    /* FOLDER NAMES */
    public static final String FOLDER_NAME_CONFIG       = "config";
    public static final String FOLDER_NAME_SKILLS       = "skills";
    public static final String FOLDER_NAME_EXPERIENCE   = "Experience Settings";
    public static final String FOLDER_NAME_DEFAULTS     = "defaults";

    /* RELATIVE PATHS */
    public final static String RELATIVE_PATH_CONFIG_DIR = File.separator + FOLDER_NAME_CONFIG + File.separator;
    public final static String RELATIVE_PATH_SKILLS_DIR = RELATIVE_PATH_CONFIG_DIR + FOLDER_NAME_SKILLS + File.separator;
    public final static String RELATIVE_PATH_XP_DIR     = RELATIVE_PATH_CONFIG_DIR + FOLDER_NAME_EXPERIENCE + File.separator;

    /**
     * Return the data folder for mcMMO
     * @return the File for the data folder used by mcMMO
     */
    public static File getDataFolder()
    {
        return mcMMO.p.getDataFolder();
    }

    public static File getConfigFolder() {
        return new File(getDataFolder(), FOLDER_NAME_CONFIG);
    }

    public static File getDefaultsFolder() {
        return new File(getConfigFolder().getAbsolutePath(), FOLDER_NAME_DEFAULTS);
    }

    public static File getDefaultsConfigFolder() {
        return new File(getDefaultsFolder().getAbsolutePath(), FOLDER_NAME_CONFIG);
    }

    public static File getDefaultsSkillFolder() {
        return new File(getDefaultsConfigFolder().getAbsolutePath(), FOLDER_NAME_SKILLS);
    }

    public static File getDefaultsXPFolder() {
        return new File(getDefaultsConfigFolder().getAbsolutePath(), FOLDER_NAME_EXPERIENCE);
    }

    public static File getConfigSkillFolder() {
        return new File(getConfigFolder().getAbsolutePath(), FOLDER_NAME_SKILLS);
    }

    public static File getConfigXPFolder() {
        return new File(getConfigFolder().getAbsolutePath(), FOLDER_NAME_EXPERIENCE);
    }

    /**
     * Creates all directories used by mcMMO config files
     */
    public static void makeAllConfigDirectories()
    {
        /* CONFIG DIRECTORY */

        if(!getConfigFolder().exists())
            getConfigFolder().mkdirs();

        /* DEFAULT DIRECTORIES */

        if(!getDefaultsFolder().exists())
            getDefaultsFolder().mkdirs();

        if(!getDefaultsConfigFolder().exists())
            getDefaultsConfigFolder().mkdirs();

        if(!getDefaultsSkillFolder().exists())
            getDefaultsSkillFolder().mkdirs();

        if(!getDefaultsXPFolder().exists())
            getDefaultsXPFolder().mkdirs();

        /* CONFIG SUBDIRECTORIES */

        if(!getConfigSkillFolder().exists())
            getConfigSkillFolder().mkdirs();

        if(!getConfigXPFolder().exists())
            getConfigXPFolder().mkdirs();
    }
}
