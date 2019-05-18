package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.*;

@ConfigSerializable
public class ConfigLevelingDiminishedReturns {

    /*
    Diminished_Returns:
    #This is the minimum amount of XP a player will earn after reaching the timed threshold (this is to prevent punishing a player too hard for earning XP)
    ## A value of 1 would mean that a player gets FULL XP, which defeats the purpose of diminished returns, the default value is 0.05 (5% minimum XP)
    ### Set this value to 0 to turn it off
    Guaranteed_Minimum_Percentage: 0.05
    Enabled: false
     */

    private static final HashMap<PrimarySkillType, Integer> SKILL_THRESHOLDS_DEFAULT;
    public static final float GURANTEED_MIN_DEFAULT = 0.05f;

    static {
        SKILL_THRESHOLDS_DEFAULT = new HashMap<>();
        SKILL_THRESHOLDS_DEFAULT.put(ACROBATICS, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(ALCHEMY, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(ARCHERY, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(AXES, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(EXCAVATION, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(FISHING, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(HERBALISM, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(MINING, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(REPAIR, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(SWORDS, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(TAMING, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(UNARMED, 10000);
        SKILL_THRESHOLDS_DEFAULT.put(WOODCUTTING, 10000);
    }

    private static final boolean DIMINISHED_RETURNS_DEFAULT = false;
    private static final int DIMINISHED_TIME_DEFAULT = 10;

    @Setting(value = "Enabled", comment = "Setting this to true will enable Diminished Returns on XP Gains." +
            "\nDefault value: " + DIMINISHED_RETURNS_DEFAULT)
    private boolean diminishedReturnsEnabled = DIMINISHED_RETURNS_DEFAULT;

    @Setting(value = "Time-Interval-In-Minutes", comment = "The period of time in which to measure a players XP gain and reduce gains above a threshold during that time" +
            "\nPlayers will be able to gain up to the threshold of XP in this time period before having their XP drastically reduced" +
            "\nDefault value: " + DIMINISHED_TIME_DEFAULT)
    private int dimishedReturnTimeInterval = DIMINISHED_TIME_DEFAULT;

    @Setting(value = "Skill-Thresholds", comment = "The amount of XP that a player can gain without penalty in the defined time interval." +
            "\nDefault value: 10000 for each skill, undefined skills will default to this value")
    private HashMap<PrimarySkillType, Integer> skillThresholds = SKILL_THRESHOLDS_DEFAULT;

    @Setting(value = "Guaranteed-Minimum", comment = "The multiplier applied to an XP gain when a player has reached diminishing returns to guarantee that some XP is still gained." +
            "\nPlayers will gain (raw XP * guaranteedMinimum) if they are under sever enough diminishing return penalty (ie their XP would normally fall below this value)" +
            "\nDefault value: ")
    private float guaranteedMinimums = GURANTEED_MIN_DEFAULT;

    public int getSkillThreshold(PrimarySkillType primarySkillType) {
        if (skillThresholds.get(primarySkillType) == null)
            return 10000;

        return skillThresholds.get(primarySkillType);
    }

    public float getGuaranteedMinimums() {
        return guaranteedMinimums;
    }

    public boolean isDiminishedReturnsEnabled() {
        return diminishedReturnsEnabled;
    }

    public int getDimishedReturnTimeInterval() {
        return dimishedReturnTimeInterval;
    }

    public HashMap<PrimarySkillType, Integer> getSkillThresholds() {
        return skillThresholds;
    }
}
