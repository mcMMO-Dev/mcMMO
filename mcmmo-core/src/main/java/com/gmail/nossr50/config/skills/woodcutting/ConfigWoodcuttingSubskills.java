package com.gmail.nossr50.config.skills.woodcutting;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigWoodcuttingSubskills {

    @Setting(value = "Harvest-Lumber")
    private ConfigWoodcuttingHarvest harvest = new ConfigWoodcuttingHarvest();

    public ConfigWoodcuttingHarvest getHarvest() {
        return harvest;
    }
}
