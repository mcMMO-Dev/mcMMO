package com.gmail.nossr50.config.hocon.skills.repair.repairmastery;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairRepairMastery {

    @Setting(value = ConfigConstants.MAX_BONUS_PERCENTAGE_FIELD_NAME)
    private double maxBonusPercentage = 200.0D;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME, comment = ConfigConstants.MAX_BONUS_LEVEL_DESCRIPTION)
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

    public double getMaxBonusPercentage() {
        return maxBonusPercentage;
    }

    public MaxBonusLevel getMaxBonusLevel() {
        return maxBonusLevel;
    }
}