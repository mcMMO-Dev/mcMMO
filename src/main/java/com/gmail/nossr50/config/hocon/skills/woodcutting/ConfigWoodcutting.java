package com.gmail.nossr50.config.hocon.skills.woodcutting;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigWoodcutting {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE, comment = "Settings for Woodcutting sub-skills")
    private ConfigWoodcuttingSubskills subskills = new ConfigWoodcuttingSubskills();

    public ConfigWoodcuttingSubskills getSubskills() {
        return subskills;
    }
}