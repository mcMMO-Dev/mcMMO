package com.gmail.nossr50.config.playerleveling;

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
                    "\nServer admins can adjust level scaling modifiers to tweak a bunch of settings related to progression at once." +
                    "\nIt is not recommended to change this away from defaults unless you know what you are doing.")
    private ConfigSectionLevelScaling configSectionLevelScaling = new ConfigSectionLevelScaling();

    @Setting(value = "Player-Starting-Level",
            comment = "\nPlayers will start at this level in all skills if they aren't already saved in the database." +
                    "\nHistorically this number has been 0, but this was changed in 2.1.X to 1 as I felt it was better to start from 1 than 0." +
                    "\nDefault value: " + STARTING_LEVEL_DEFAULT)
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

    public int getCosmeticLevelScaleModifier() {
        return configSectionLevelScaling.getCosmeticLevelScaleModifier();
    }
}
