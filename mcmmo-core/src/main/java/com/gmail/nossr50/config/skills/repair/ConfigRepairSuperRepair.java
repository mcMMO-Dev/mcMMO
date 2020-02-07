package com.gmail.nossr50.config.skills.repair;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairSuperRepair {

    private static final String FIFTY_PERCENT_EXAMPLE = "50";
    private static final String MAX_BONUS_LEVEL_EXAMPLE = "100";
    private static final String ODDS_PERCENTAGE_EXAMPLE = "25%";
    private static final double CHANCE_AT_MAX_SKILL_DEFAULT = 100.0D;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 100.0;

}