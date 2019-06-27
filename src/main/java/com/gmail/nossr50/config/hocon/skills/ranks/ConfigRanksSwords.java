package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksSwords {

    @Setting(value = "Swords-Limit-Break")
    private SkillRankProperty limitBreak = new SkillRankProperty(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

    @Setting(value = "Stab")
    private SkillRankProperty stab = new SkillRankProperty(75, 100);

    @Setting(value = "Counter-Attack")
    private SkillRankProperty counterAttack = new SkillRankProperty(20);

    @Setting(value = "Rupture")
    private SkillRankProperty rupture = new SkillRankProperty(1, 15, 75, 90);

    @Setting(value = "Serrated-Strikes")
    private SkillRankProperty serratedStrikes = new SkillRankProperty(5);

    public SkillRankProperty getLimitBreak() {
        return limitBreak;
    }

    public SkillRankProperty getStab() {
        return stab;
    }

    public SkillRankProperty getCounterAttack() {
        return counterAttack;
    }

    public SkillRankProperty getRupture() {
        return rupture;
    }

    public SkillRankProperty getSerratedStrikes() {
        return serratedStrikes;
    }
}
