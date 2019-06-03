package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksSalvage {

    @Setting(value = "Advanced-Salvage")
    private SkillRankProperty advancedSalvage = new SkillRankProperty(35);

    @Setting(value = "Arcane-Salvage")
    private SkillRankProperty arcaneSalvage = new SkillRankProperty(10, 25, 35, 50, 65, 75, 85, 100);

    public SkillRankProperty getAdvancedSalvage() {
        return advancedSalvage;
    }

    public SkillRankProperty getArcaneSalvage() {
        return arcaneSalvage;
    }
}
