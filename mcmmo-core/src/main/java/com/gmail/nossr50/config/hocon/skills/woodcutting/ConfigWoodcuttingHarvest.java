package com.gmail.nossr50.config.hocon.skills.woodcutting;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigWoodcuttingHarvest {

    private static final double MAX_CHANCE_DEFAULT = 100.0;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = MAX_CHANCE_DEFAULT;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME)
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

    public double getMaxChance() {
        return maxChance;
    }

    public MaxBonusLevel getMaxBonusLevel() {
        return maxBonusLevel;
    }

}
