package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionSkillLevelCap {

    /* DEFAULT VALUES */
    private static final boolean USE_LEVEL_CAP_DEFAULT = false;
    private static final int LEVEL_CAP_DEFAULT = 0;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Enable")
    private boolean useLevelCap = USE_LEVEL_CAP_DEFAULT;

    @Setting(value = "Level-Cap",
            comment = "Players will be unable to level past this value" +
                "\nThe cap is the same for both Retro and Standard, " +
                    "so a cap of 50 will be the same value in either mode.")
    private int levelCap = LEVEL_CAP_DEFAULT;

    /*
     * GETTER BOILERPLATE
     */

    public boolean isLevelCapEnabled() {
        return useLevelCap;
    }

    public int getLevelCap() {
        return levelCap;
    }
}
