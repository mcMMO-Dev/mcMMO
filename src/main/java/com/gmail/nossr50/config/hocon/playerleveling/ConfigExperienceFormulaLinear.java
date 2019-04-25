package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceFormulaLinear {

    private static final int BASE_DEFAULT = 1020;
    private static final double MULTIPLIER_DEFAULT = 20.0D;

    @Setting(value = "Base-Amount", comment = "The formula for Linear adds the base amount without any modifications to the level requirement for every level." +
            "\nDefault value: "+BASE_DEFAULT)
    private int baseModifier = BASE_DEFAULT;

    @Setting(value = "Multiplier", comment = "The multiplier is multiplied against the players level and then added to the base amount to determine the amount of XP to the next level" +
            "\nDefault value: "+MULTIPLIER_DEFAULT)
    private double multiplier = MULTIPLIER_DEFAULT;

    public int getLinearBaseModifier() {
        return baseModifier;
    }

    public double getLinearMultiplier() {
        return multiplier;
    }

}