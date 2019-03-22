package com.gmail.nossr50.config.hocon.skills.acrobatics.roll;

import com.gmail.nossr50.config.hocon.skills.ConfigSubSkillScalingRNG;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRoll {

    public static final double ROLL_DAMAGE_THRESHOLD_DEFAULT = 7.0D;

    @Setting(value = "Damage-Threshold", comment = "Rolling will reduce up to this much damage." +
            "\nGraceful Rolls will reduce twice this value." +
            "\nDefault value: "+ROLL_DAMAGE_THRESHOLD_DEFAULT)
    private double damageTheshold = ROLL_DAMAGE_THRESHOLD_DEFAULT;

    @Setting(value = "RNG-Settings", comment = "Settings related to random chance elements for this Sub-Skill.")
    private ConfigSubSkillScalingRNG rng = new ConfigSubSkillScalingRNG();

    public ConfigSubSkillScalingRNG getRNGSettings() {
        return rng;
    }

    public double getDamageTheshold() {
        return damageTheshold;
    }
}