package com.gmail.nossr50.config.skills.taming;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigTamingThickFur {
    public static final double DEFAULT_THICKFUR_MOD = 6.0D;

    @Setting(value = "Damage-Reduction-Modifier", comment = "Damage modified by activation of thick fur will be divided by this value" +
            "\nDefault value: "+ DEFAULT_THICKFUR_MOD)
    private double thickFurDamageModifier = DEFAULT_THICKFUR_MOD;

    public double getThickFurDamageModifier() {
        return thickFurDamageModifier;
    }
}
