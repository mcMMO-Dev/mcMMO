package com.gmail.nossr50.config.hocon.hardcore;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigHardcoreDeathPenalty {
    private static final double PENALTY_PERCENTAGE_DEFAULT = 75.0D;
    private static final int LEVEL_THRESHOLD_DEFAULT = 0;
    private static final HashMap<PrimarySkillType, Boolean> HARDCORE_SKILL_TOGGLE_MAP_DEFAULT;

    static {
        HARDCORE_SKILL_TOGGLE_MAP_DEFAULT = new HashMap<>();

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(primarySkillType.isChildSkill())
                continue;

            HARDCORE_SKILL_TOGGLE_MAP_DEFAULT.put(primarySkillType, false);
        }

    }

    @Setting(value = "Death-Penalty-Percentage", comment = "The amount of levels a player will lose when they die with Hardcore mode enabled." +
            "\nWith default settings, a player who died at level 100 would have their skills reduced to 25 after death." +
            "\nDefault value: "+PENALTY_PERCENTAGE_DEFAULT)
    private double penaltyPercentage = PENALTY_PERCENTAGE_DEFAULT;

    @Setting(value = "Safe-Level-Threshold", comment = "Players will not be subject to hardcore penalties for skills below this level." +
            "\nDefault value: "+LEVEL_THRESHOLD_DEFAULT)
    private int levelThreshold = LEVEL_THRESHOLD_DEFAULT;

    @Setting(value = "Skills-Using-Hardcore-Mode", comment = "Hardcore mode is enabled on a per skill basis" +
            "\nYou can choose which skills participate in this list.")
    private HashMap<PrimarySkillType, Boolean> skillToggleMap = HARDCORE_SKILL_TOGGLE_MAP_DEFAULT;

    public double getPenaltyPercentage() {
        return penaltyPercentage;
    }

    public int getLevelThreshold() {
        return levelThreshold;
    }

    public HashMap<PrimarySkillType, Boolean> getSkillToggleMap() {
        return skillToggleMap;
    }
}
