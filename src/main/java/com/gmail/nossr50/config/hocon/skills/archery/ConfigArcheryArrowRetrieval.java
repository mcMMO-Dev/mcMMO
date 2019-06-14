package com.gmail.nossr50.config.hocon.skills.archery;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArcheryArrowRetrieval {

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 100.0D;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME)
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

    public double getMaxChance() {
        return maxChance;
    }

    public MaxBonusLevel getMaxBonusLevel() {
        return maxBonusLevel;
    }
}
