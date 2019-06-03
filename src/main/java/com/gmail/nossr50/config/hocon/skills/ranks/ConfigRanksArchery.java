package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksArchery {

    @Setting(value = "Limit-Break")
    private SkillRankProperty limitBreak = new SkillRankProperty(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

    @Setting(value = "Arrow-Retrieval")
    private SkillRankProperty arrowRetrieval = new SkillRankProperty(2);

    @Setting(value = "Skill-Shot")
    private SkillRankProperty skillShot = new SkillRankProperty(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100);

    public SkillRankProperty getLimitBreak() {
        return limitBreak;
    }

    public SkillRankProperty getArrowRetrieval() {
        return arrowRetrieval;
    }

    public SkillRankProperty getSkillShot() {
        return skillShot;
    }
}
