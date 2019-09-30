package com.gmail.nossr50.config.skills.smelting;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigSmelting {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigSmeltingSubSkills subskills = new ConfigSmeltingSubSkills();

    public ConfigSmeltingSubSkills getSubskills() {
        return subskills;
    }

    public ConfigSmeltingSecondSmelt getSmeltingSecondSmelt() {
        return subskills.getSmeltingSecondSmelt();
    }


    public double getMaxChance() {
        return subskills.getMaxChance();
    }

    public MaxBonusLevel getMaxBonusLevel() {
        return subskills.getMaxBonusLevel();
    }

    public HashMap<Integer, Integer> getXpMultiplierTable() {
        return subskills.getXpMultiplierTable();
    }
}