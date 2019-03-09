package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;

import java.io.File;

/**
 * Constants relating to config folders and paths
 */
public class ConfigConstants {
    /* FOLDER NAMES */
    public static final String FOLDER_NAME_CONFIG = "config";
    public static final String FOLDER_NAME_SKILLS = "skills";
    public static final String FOLDER_NAME_EXPERIENCE = "Experience Settings";

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
}
