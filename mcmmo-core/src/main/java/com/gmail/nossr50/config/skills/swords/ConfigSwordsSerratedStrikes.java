package com.gmail.nossr50.config.skills.swords;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSwordsSerratedStrikes {

    @Setting(value = "Damage-Modifier", comment = "The amount of damage dealt by this ability when hitting opponents in an AOE is divided by this number.")
    private double damageModifier = 4.0;

    public double getSerratedStrikesDamageModifier() {
        return damageModifier;
    }
}
