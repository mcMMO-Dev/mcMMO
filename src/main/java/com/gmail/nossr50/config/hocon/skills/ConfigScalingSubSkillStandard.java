package com.gmail.nossr50.config.hocon.skills;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigScalingSubSkillStandard {

    public static final String FIFTY_PERCENT_EXAMPLE = "50";
    public static final String MAX_BONUS_LEVEL_EXAMPLE = "100";
    public static final String ODDS_PERCENTAGE_EXAMPLE = "25%";
    public static final int MAX_BONUS_LEVEL_DEFAULT = 100;
    public static final double CHANCE_AT_MAX_SKILL_DEFAULT = 100.0D;

    @Setting(value = "Max-Bonus-Level", comment = "Max bonus level is the level a player needs to reach in this skill to receive maximum benefits, such as better RNG odds or otherwise." +
            "\nSkills dynamically adjust their rewards to match the max bonus level, you can think of it as a curve that calculates what bonuses " +
            "\n a player should have based on how far they are from the max bonus level value, and the other parameters used for the scaling of the sub-skill." +
            "\n\nNote: This is the setting for STANDARD MODE!" +
            "\nDefault value: "+MAX_BONUS_LEVEL_DEFAULT)
    private int maxBonusLevel = MAX_BONUS_LEVEL_DEFAULT;

    @Setting(value = "Max-Success-Chance", comment = "The maximum success chance for this Sub-Skill." +
            "\nA value of 100.0 would be equivalent to 100% chance of success." +
            "\nPlayers only have Max-Success-Chance when their skill level has reached the maximum bonus level." +
            "\nMax skill chance is dynamically adjusted based on the players level difference from the \"Max-Bonus-Level\", you can think of it as a curve where reaching \"Max-Bonus-Level\" is the peak." +
            "\nAs an example, imagine \"Max-Success-Chance\" was set to " + FIFTY_PERCENT_EXAMPLE + " and the \"Max-Bonus-Level\" was " + MAX_BONUS_LEVEL_EXAMPLE + "," +
            "\n and the player was level " + FIFTY_PERCENT_EXAMPLE + " for this skill, that would give the player " + ODDS_PERCENTAGE_EXAMPLE + " odds to succeed with this skill." +
            "\n\nNote: This is the setting for STANDARD MODE!" +
            "\nDefault value: "+CHANCE_AT_MAX_SKILL_DEFAULT)
    private double chanceAtMaxSkill = CHANCE_AT_MAX_SKILL_DEFAULT;

    public int getMaxBonusLevel() {
        return maxBonusLevel;
    }

    public double getChanceAtMaxSkill() {
        return chanceAtMaxSkill;
    }
}