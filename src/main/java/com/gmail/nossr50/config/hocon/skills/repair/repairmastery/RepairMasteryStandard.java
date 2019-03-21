package com.gmail.nossr50.config.hocon.skills.repair.repairmastery;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class RepairMasteryStandard {

    public static final int MAX_BONUS_LEVEL_DEFAULT = 100;
    public static final double MAX_BONUS_PERCENTAGE = 200.0D;
    public static final String PLAYER_LEVEL_FIFTY_PERCENT_EXAMPLE = "50";
    public static final String MAX_BONUS_LEVEL_EXAMPLE = "100";
    public static final String BONUS_PERCENTAGE_EXAMPLE = "100%";

    @Setting(value = "Standard-Mode-Max-Bonus-Level", comment = "At the max bonus level a player will have full benefits from this scaling skill." +
            "\nSkills dynamically adjust their rewards to match the max bonus level, you can think of it as a curve that calculates what bonuses " +
            "\n   a player should have based on how far they are from the max bonus level value, and the other parameters used for the scaling of the sub-skill." +
            "\nNote: This is the setting for STANDARD MODE!" +
            "\nDefault value: "+MAX_BONUS_LEVEL_DEFAULT)
    private int maxBonusLevel = MAX_BONUS_LEVEL_DEFAULT;

    @Setting(value = "Standard-Mode-Max-Bonus-Percentage", comment = "This is the maximum benefit for additional repair amount from this skill when the player reaches \"Max-Bonus-Level\"." +
            "\nRepair Mastery's bonus to repair is dynamically adjusted based on the players level difference from the \"Max-Bonus-Level\", you can think of it as a curve where reaching \"Max-Bonus-Level\" is the peak." +
            "\nAs an example, imagine \"Standard-Mode-Max-Bonus-Percentage\" was set to " + MAX_BONUS_PERCENTAGE + " and the \"Max-Bonus-Level\" was " + MAX_BONUS_LEVEL_EXAMPLE + "," +
            "\n   and the player was level " + PLAYER_LEVEL_FIFTY_PERCENT_EXAMPLE + " for this skill, that would give the player " + BONUS_PERCENTAGE_EXAMPLE + "% added to the repair amount on the item before other bonuses." +
            "\nNote: This is the setting for STANDARD MODE!" +
            "\nDefault value: "+MAX_BONUS_PERCENTAGE)
    private double maxBonusPercentage = MAX_BONUS_PERCENTAGE;

    public int getMaxBonusLevel() {
        return maxBonusLevel;
    }

    public double getMaxBonusPercentage() {
        return maxBonusPercentage;
    }
}