package com.gmail.nossr50.config.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksAcrobatics {

    @Setting(value = "Dodge")
    private SkillRankProperty dodgeRanks = new SkillRankProperty(1);

    public SkillRankProperty getDodgeRanks() {
        return dodgeRanks;
    }
}
