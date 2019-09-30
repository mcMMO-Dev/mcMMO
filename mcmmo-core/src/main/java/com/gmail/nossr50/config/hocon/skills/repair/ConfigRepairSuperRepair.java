package com.gmail.nossr50.config.hocon.skills.repair;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairSuperRepair {

    private static final String FIFTY_PERCENT_EXAMPLE = "50";
    private static final String MAX_BONUS_LEVEL_EXAMPLE = "100";
    private static final String ODDS_PERCENTAGE_EXAMPLE = "25%";
    private static final double CHANCE_AT_MAX_SKILL_DEFAULT = 100.0D;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME, comment = "Max bonus level is the level a player needs to reach in this skill to receive maximum benefits, such as better RNG odds or otherwise." +
            "\nSkills dynamically adjust their rewards to match the max bonus level, you can think of it as a curve that calculates what bonuses " +
            "\n a player should have based on how far they are from the max bonus level value, and the other parameters used for the scaling of the sub-skill.")
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = "The maximum success chance for this Sub-Skill." +
            "\nA value of 100.0 would be equivalent to 100% chance of success." +
            "\nPlayers only have Max-Success-Chance when their skill level has reached the maximum bonus level." +
            "\nMax skill chance is dynamically adjusted based on the players level difference from the \"Max-Bonus-Level\", you can think of it as a curve where reaching \"Max-Bonus-Level\" is the peak." +
            "\nAs an example, imagine \""+ConfigConstants.MAX_CHANCE_FIELD_NAME+"\" was set to " + FIFTY_PERCENT_EXAMPLE + " and the \""+ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME+"\" was " + MAX_BONUS_LEVEL_EXAMPLE + "," +
            "\n and the player was level " + FIFTY_PERCENT_EXAMPLE + " for this skill, that would give the player " + ODDS_PERCENTAGE_EXAMPLE + " odds to succeed with this skill.")
    private double chanceAtMaxSkill = CHANCE_AT_MAX_SKILL_DEFAULT;

}