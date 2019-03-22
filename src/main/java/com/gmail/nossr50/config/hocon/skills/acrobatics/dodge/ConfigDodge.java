package com.gmail.nossr50.config.hocon.skills.acrobatics.dodge;

import com.gmail.nossr50.config.hocon.skills.ConfigSubSkillScalingRNG;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDodge {

    public static final double DAMAGE_REDUCTION_DIVISOR_DEFAULT = 2.0D;

    @Setting(value = "Damage-Reduction-Divisor", comment = "If a player successfully dodges the incoming damage will be divided by this value." +
            "\nPlayers can dodge almost all types of damage from other entities, such as player damage, monster damage, etc." +
            "\nAs an example, a value of 2.0 for this setting would result in the player taking half damage." +
            "\nHigher values would further decrease the amount of damage the player takes after a successful dodge." +
            "\nDefault value: "+DAMAGE_REDUCTION_DIVISOR_DEFAULT)
    private double damageReductionDivisor = DAMAGE_REDUCTION_DIVISOR_DEFAULT;

    @Setting(value = "RNG-Settings", comment = "Settings related to random chance elements for this Sub-Skill.")
    private ConfigSubSkillScalingRNG rng = new ConfigSubSkillScalingRNG();

    public ConfigSubSkillScalingRNG getRNGSettings() {
        return rng;
    }

    public double getDamageReductionDivisor() {
        return damageReductionDivisor;
    }
}