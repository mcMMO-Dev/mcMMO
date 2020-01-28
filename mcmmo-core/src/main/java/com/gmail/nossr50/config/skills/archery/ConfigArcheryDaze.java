package com.gmail.nossr50.config.skills.archery;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArcheryDaze {
    private static final double DAZE_BONUS_DMG_DEFAULT = 4.0D;
    private static final double DAZE_MAX_CHANCE_DEFAULT = 50.0D;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION
        + "\nDefault value: "+DAZE_MAX_CHANCE_DEFAULT)
    private double maxChance = DAZE_MAX_CHANCE_DEFAULT;

    @Setting(value = "Bonus-Damage", comment = "How much bonus damage is applied when daze is applied to a target." +
            "\nDefault value: "+DAZE_BONUS_DMG_DEFAULT)
    private double bonusDamage = DAZE_BONUS_DMG_DEFAULT;

    public double getMaxChance() {
        return maxChance;
    }

    public double getDazeBonusDamage() {
        return bonusDamage;
    }
}
