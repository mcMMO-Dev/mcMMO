package com.gmail.nossr50.config.skills.acrobatics.roll;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRoll {

    public static final double ROLL_DAMAGE_THRESHOLD_DEFAULT = 7.0D;
    public static final double CHANCE_AT_MAX_SKILL_DEFAULT = 100.0D;

    @Setting(value = "Damage-Threshold", comment = "Rolling will reduce up to this much damage." +
            "\nGraceful Rolls will reduce twice this value." +
            "\nDefault value: " + ROLL_DAMAGE_THRESHOLD_DEFAULT)
    private double damageTheshold = ROLL_DAMAGE_THRESHOLD_DEFAULT;

    public double getDamageTheshold() {
        return damageTheshold;
    }

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 100.0D;

    public double getChanceAtMaxSkill() {
        return maxChance;
    }
}
