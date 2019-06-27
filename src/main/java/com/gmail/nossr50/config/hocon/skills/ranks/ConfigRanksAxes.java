package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksAxes {

    @Setting(value = "Axes-Limit-Break")
    private SkillRankProperty limitBreak = new SkillRankProperty(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

    @Setting(value = "Skull-Splitter")
    private SkillRankProperty skullSplitter = new SkillRankProperty(5);

    @Setting(value = "Critical-Strikes")
    private SkillRankProperty criticalStrikes = new SkillRankProperty(1);

    @Setting(value = "Greater-Impact")
    private SkillRankProperty greaterImpact = new SkillRankProperty(25);

    @Setting(value = "Armor-Impact")
    private SkillRankProperty armorImpact = new SkillRankProperty(1, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100);

    @Setting(value = "Axe-Mastery")
    private SkillRankProperty axeMastery = new SkillRankProperty(5, 10, 15, 20);

    public SkillRankProperty getLimitBreak() {
        return limitBreak;
    }

    public SkillRankProperty getSkullSplitter() {
        return skullSplitter;
    }

    public SkillRankProperty getCriticalStrikes() {
        return criticalStrikes;
    }

    public SkillRankProperty getGreaterImpact() {
        return greaterImpact;
    }

    public SkillRankProperty getArmorImpact() {
        return armorImpact;
    }

    public SkillRankProperty getAxeMastery() {
        return axeMastery;
    }
}
