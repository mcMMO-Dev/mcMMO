package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksAcrobatics {

    @Setting(value = "Dodge")
    private SkillRankProperty dodgeRanks = new SkillRankProperty(2);

    public SkillRankProperty getDodgeRanks() {
        return dodgeRanks;
    }
}
