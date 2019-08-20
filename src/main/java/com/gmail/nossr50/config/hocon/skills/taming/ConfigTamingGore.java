package com.gmail.nossr50.config.hocon.skills.taming;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigTamingGore {

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 50.0;

    @Setting(value = "Gore-Bleed-Tick-Length", comment = "How many times to apply the bleed DOT from gore before it wears off.")
    private int goreBleedTicks = 2;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME, comment = ConfigConstants.MAX_BONUS_LEVEL_DESCRIPTION)
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

    @Setting(value = "Gore-Damage-Modifier")
    private double goreMofifier = 2.0;

    public double getMaxChance() {
        return maxChance;
    }

    public MaxBonusLevel getMaxBonusLevel() {
        return maxBonusLevel;
    }

    public double getGoreMofifier() {
        return goreMofifier;
    }

    public int getGoreBleedTicks() {
        return goreBleedTicks;
    }
}
