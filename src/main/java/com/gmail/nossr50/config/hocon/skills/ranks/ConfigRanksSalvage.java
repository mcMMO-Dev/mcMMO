package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksSalvage {

    @Setting(value = "Scrap-Collector")
    private SkillRankProperty scrapCollector = new SkillRankProperty(1, 10, 15, 20, 25, 30, 35, 40);

    @Setting(value = "Arcane-Salvage")
    private SkillRankProperty arcaneSalvage = new SkillRankProperty(10, 25, 35, 50, 65, 75, 85, 100);

    public SkillRankProperty getScrapCollector() {
        return scrapCollector;
    }

    public SkillRankProperty getArcaneSalvage() {
        return arcaneSalvage;
    }
}
