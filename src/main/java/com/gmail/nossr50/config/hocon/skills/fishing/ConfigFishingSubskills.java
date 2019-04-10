package com.gmail.nossr50.config.hocon.skills.fishing;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigFishingSubskills {

    @Setting(value = "Inner-Peace")
    private ConfigFishingInnerPeace innerPeace = new ConfigFishingInnerPeace();

    public ConfigFishingInnerPeace getInnerPeace() {
        return innerPeace;
    }
}