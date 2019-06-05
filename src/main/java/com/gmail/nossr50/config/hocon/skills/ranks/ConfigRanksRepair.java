package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksRepair {

    @Setting(value = "Arcane-Forging")
    private SkillRankProperty arcaneForging = new SkillRankProperty(10, 25, 35, 50, 65, 75, 85, 100);

    @Setting(value = "Repair-Mastery")
    private SkillRankProperty repairMastery = new SkillRankProperty(75);

    @Setting(value = "Super-Repair")
    private SkillRankProperty superRepair = new SkillRankProperty(40);

    public SkillRankProperty getArcaneForging() {
        return arcaneForging;
    }

    public SkillRankProperty getRepairMastery() {
        return repairMastery;
    }

    public SkillRankProperty getSuperRepair() {
        return superRepair;
    }
}
