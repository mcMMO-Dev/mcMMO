package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceFormulaLinear {

    private static final int BASE_DEFAULT = 1020;
    private static final float MULTIPLIER_DEFAULT = 20.0F;

    @Setting(value = "Base-Amount", comment = "Default value: " + BASE_DEFAULT)
    private int baseModifier = BASE_DEFAULT;

    @Setting(value = "Multiplier", comment = "Default value: " + MULTIPLIER_DEFAULT)
    private float multiplier = MULTIPLIER_DEFAULT;

    public int getLinearBaseModifier() {
        return baseModifier;
    }

    public float getLinearMultiplier() {
        return multiplier;
    }

}