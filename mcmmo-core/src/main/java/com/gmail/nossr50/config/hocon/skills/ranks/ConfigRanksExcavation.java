package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksExcavation {

    @Setting(value = "Giga-Drill-Breaker")
    private SkillRankProperty gigaDrillBreaker = new SkillRankProperty(5);

    @Setting(value = "Archaeology")
    private SkillRankProperty archaeology = new SkillRankProperty(1, 25, 35, 50, 65, 75, 85, 100);

    public SkillRankProperty getGigaDrillBreaker() {
        return gigaDrillBreaker;
    }

    public SkillRankProperty getArchaeology() {
        return archaeology;
    }
}
