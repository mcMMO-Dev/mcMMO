package com.gmail.nossr50.config.hocon.skills.taming;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigTamingShockProof {
    public static final double DEFAULT_SHOCKPROOF_MOD = 6.0D;

    @Setting(value = "Damage-Reduction-Modifier", comment = "Damage modified by activation of shock proof will be divided by this value" +
            "\nDefault value: "+DEFAULT_SHOCKPROOF_MOD)
    private double shockProofModifier = DEFAULT_SHOCKPROOF_MOD;

    public double getShockProofModifier() {
        return shockProofModifier;
    }
}
