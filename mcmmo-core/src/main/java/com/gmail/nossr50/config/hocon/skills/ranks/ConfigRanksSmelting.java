package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksSmelting {

    @Setting(value = "Fuel-Efficiency")
    private SkillRankProperty fuelEfficiency = new SkillRankProperty(10, 50, 75);

    @Setting(value = "Understanding-The-Art")
    private SkillRankProperty understandingTheArt = new SkillRankProperty(10, 25, 35, 50, 65, 75, 85, 100);

    public SkillRankProperty getFuelEfficiency() {
        return fuelEfficiency;
    }

    public SkillRankProperty getUnderstandingTheArt() {
        return understandingTheArt;
    }
}
