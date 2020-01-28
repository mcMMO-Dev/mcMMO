package com.gmail.nossr50.config.skills.repair.repairmastery;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairRepairMastery {

    @Setting(value = ConfigConstants.MAX_BONUS_PERCENTAGE_FIELD_NAME)
    private double maxBonusPercentage = 200.0D;

    public double getMaxBonusPercentage() {
        return maxBonusPercentage;
    }
}