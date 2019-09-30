package com.gmail.nossr50.config.skills.axes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxesAxeMastery {
    private static final double AXE_MASTERY_MULTIPLIER_DEFAULT = 1.0D;

    @Setting(value = "Axe-Mastery-Rank-Damage-Multiplier", comment = "This value is multiplied against the current rank of Axe Mastery to determine bonus damage." +
            "\nWith the default config value of 1.0, at rank 4 a player will deal 4.0 extra damage with Axes (1.0 * 4)" +
            "\nDefault Value: "+ AXE_MASTERY_MULTIPLIER_DEFAULT)
    private double axeMasteryMultiplier = AXE_MASTERY_MULTIPLIER_DEFAULT;

    public double getAxeMasteryMultiplier() {
        return axeMasteryMultiplier;
    }
}
