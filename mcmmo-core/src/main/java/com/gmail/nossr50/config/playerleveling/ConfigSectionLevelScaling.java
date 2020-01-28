package com.gmail.nossr50.config.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionLevelScaling {

    /* DEFAULT VALUES */
    public static final int LEVEL_SCALE_MODIFIER_DEFAULT = 1;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Cosmetic-Level-Scaling",
            comment = "Changes the max number of levels and adjusts settings appropriately" +
                    "\nThe default value will make level 1000 the max level, settings in the configs are based around that and are mutated based on this setting." +
                    "\nLeave this setting at a value of 1 if you do not wish to change the cosmetic amount of levels")
    private int cosmeticLevelScaleModifier = LEVEL_SCALE_MODIFIER_DEFAULT;

    public int getCosmeticLevelScaleModifier() {
        return cosmeticLevelScaleModifier;
    }
}
