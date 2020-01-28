package com.gmail.nossr50.config.skills.woodcutting;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigWoodcuttingHarvest {

    private static final double MAX_CHANCE_DEFAULT = 100.0;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = MAX_CHANCE_DEFAULT;

    public double getMaxChance() {
        return maxChance;
    }

}
