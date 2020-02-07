package com.gmail.nossr50.config.skills.acrobatics.dodge;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDodge {

    public static final double DAMAGE_REDUCTION_DIVISOR_DEFAULT = 2.0D;
    public static final String FIFTY_PERCENT_EXAMPLE = "50";
    public static final String MAX_BONUS_LEVEL_EXAMPLE = "100";
    public static final String ODDS_PERCENTAGE_EXAMPLE = "25%";
    public static final double CHANCE_AT_MAX_SKILL_DEFAULT = 100.0D;

    @Setting(value = "Damage-Reduction-Divisor", comment = "If a player successfully dodges the incoming damage will be divided by this value." +
            "\nPlayers can dodge almost all types of damage from other entities, such as player damage, monster damage, etc." +
            "\nAs an example, a value of 2.0 for this setting would result in the player taking half damage." +
            "\nHigher values would further decrease the amount of damage the player takes after a successful dodge." +
            "\nDefault value: " + DAMAGE_REDUCTION_DIVISOR_DEFAULT)
    private double damageReductionDivisor = DAMAGE_REDUCTION_DIVISOR_DEFAULT;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 100.0;

    public double getChanceAtMaxSkill() {
        return maxChance;
    }

    public double getDamageReductionDivisor() {
        return damageReductionDivisor;
    }
}