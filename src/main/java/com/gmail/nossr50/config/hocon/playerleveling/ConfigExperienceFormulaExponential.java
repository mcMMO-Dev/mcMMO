package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceFormulaExponential {

    private static final int BASE_DEFAULT = 2000;
    private static final float MULTIPLIER_DEFAULT = 0.1f;
    private static final float EXPONENT_DEFAULT = 1.80f;

    @Setting(value = "Base-Amount", comment = "Default value: " + BASE_DEFAULT)
    private int baseModifier = BASE_DEFAULT;

    @Setting(value = "Multiplier", comment = "Default value: " + MULTIPLIER_DEFAULT)
    private float multiplier = MULTIPLIER_DEFAULT;

    @Setting(value = "Exponent", comment = "Default value: " + EXPONENT_DEFAULT)
    private float exponent = EXPONENT_DEFAULT;

    public int getExponentialBaseModifier() {
        return baseModifier;
    }

    public float getExponentialMultiplier() {
        return multiplier;
    }

    public float getExponentialExponent() {
        return exponent;
    }
}