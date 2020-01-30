package com.gmail.nossr50.config.skills;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMaxLevel {
    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME, comment = "Max bonus level is the level a player needs to reach in this skill to receive maximum benefits, such as better RNG odds or otherwise." +
            "\nSkills dynamically adjust their rewards to match the max bonus level, you can think of it as a curve that calculates what bonuses " +
            "\n a player should have based on how far they are from the max bonus level value, and the other parameters used for the scaling of the sub-skill.")
    private int maxBonusLevel = 1000;

    public int getMaxBonusLevel() {
        return maxBonusLevel;
    }
}
