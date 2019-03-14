package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionLevelingGeneral {

    /* DEFAULT VALUES */
    private static final int STARTING_LEVEL_DEFAULT = 1;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Player-Level-Scaling",
            comment = "Level Scaling is a new feature of mcMMO." +
                    "\nServer admins can choose between two level scaling modes." +
                    "\nEach mode is meant to be identical to each other in terms of the speed of progression." +
                    "\nIn Retro player skills and levels scale the way they always have, on a 1-1000 scale." +
                    "\nIn Standard player skills scale instead from 1-100." +
                    "\nAs an example, reaching level 10 in Standard will take the same amount of time as reaching level 100 in Retro" +
                    "\n\nTo make upgrading mcMMO easier for the vast majority of existing servers, RetroMode will be turned on by default in the following circumstances" +
                    "\n1) That your server has a config.yml file that does not yet have a RetroMode setting (this means your server has not yet updated from the old system which did not have two level scaling options)" +
                    "\n2) You are already using RetroMode in your old YAML config files" +
                    "\n\nIf either of these is true, RetroMode will be turned on by default. If for some reason you had wiped your config files, you will need to come in here and turn RetroMode back on." +
                    "\nNOTE: RetroMode and Standard use the EXACT same DB, it does not alter any information within that DB. It is not dangerous to switch between Standard and Retro.")
    private ConfigSectionLevelScaling configSectionLevelScaling = new ConfigSectionLevelScaling();

    @Setting(value = "Player-Starting-Level",
            comment = "\nPlayers will start at this level in all skills if they aren't already saved in the database." +
                    "\nHistorically this number has been 0, but this was changed in 2.1.X to 1 as I felt it was better to start from 1 than 0." +
                    "\nDefault value: "+STARTING_LEVEL_DEFAULT)
    private int startingLevel = STARTING_LEVEL_DEFAULT;

    /*
     * GETTER BOILERPLATE
     */

    public int getStartingLevel() {
        return startingLevel;
    }

    public ConfigSectionLevelScaling getConfigSectionLevelScaling() {
        return configSectionLevelScaling;
    }
}
