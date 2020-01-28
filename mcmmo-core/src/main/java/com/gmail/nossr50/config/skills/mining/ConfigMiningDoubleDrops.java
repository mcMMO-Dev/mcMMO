package com.gmail.nossr50.config.skills.mining;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMiningDoubleDrops {
    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 100.0;

    @Setting(value = "Silk-Touch-Double-Drops", comment = "Allow silk touch to benefit from double drops.")
    private boolean allowSilkTouchDoubleDrops = true;

    public boolean isAllowSilkTouchDoubleDrops() {
        return allowSilkTouchDoubleDrops;
    }
}
