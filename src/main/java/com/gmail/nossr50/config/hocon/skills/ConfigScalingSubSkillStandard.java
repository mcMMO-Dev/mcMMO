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

    @Setting(value = "Standard-Mode-Max-Bonus-Level", comment = "At the max bonus level a player will have full benefits from this scaling skill." +
            "\nSkills dynamically adjust their rewards to match the max bonus level, you can think of it as a curve that calculates what bonuses " +
            "\n   a player should have based on how far they are from the max bonus level value, and the other parameters used for the scaling of the sub-skill." +
            "\nNote: This is the setting for STANDARD MODE!" +
            "\nDefault value: "+MAX_BONUS_LEVEL_DEFAULT)
    private int maxBonusLevel = MAX_BONUS_LEVEL_DEFAULT;

    @Setting(value = "Standard-Mode-Success-Rate-Cap-Percentage", comment = "This is the odds for RNG components of this sub-skill to succeed when a player has reached \"Max-Bonus-Level\"." +
            "\nMax skill chance is dynamically adjusted based on the players level difference from the \"Max-Bonus-Level\", you can think of it as a curve where reaching \"Max-Bonus-Level\" is the peak." +
            "\nAs an example, imagine \"Standard-Mode-Success-Rate-Cap-Percentage\" was set to " + FIFTY_PERCENT_EXAMPLE + " and the \"Max-Bonus-Level\" was " + MAX_BONUS_LEVEL_EXAMPLE + "," +
            "\n   and the player was level " + FIFTY_PERCENT_EXAMPLE + " for this skill, that would give the player " + ODDS_PERCENTAGE_EXAMPLE + " odds to succeed with this skill." +
            "\nNote: This is the setting for STANDARD MODE!" +
            "\nDefault value: "+CHANCE_AT_MAX_SKILL_DEFAULT)
    private double chanceAtMaxSkill = CHANCE_AT_MAX_SKILL_DEFAULT;

    public int getMaxBonusLevel() {
        return maxBonusLevel;
    }

    public double getChanceAtMaxSkill() {
        return chanceAtMaxSkill;
    }
}