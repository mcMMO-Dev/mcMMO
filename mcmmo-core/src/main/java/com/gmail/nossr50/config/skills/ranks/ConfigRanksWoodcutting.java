package com.gmail.nossr50.config.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksWoodcutting {

    @Setting(value = "Tree-Feller")
    private SkillRankProperty treeFeller = new SkillRankProperty(5);

    @Setting(value = "Harvest-Lumber")
    private SkillRankProperty harvestLumber = new SkillRankProperty(1);

    @Setting(value = "Leaf-Blower")
    private SkillRankProperty leafBlower = new SkillRankProperty(30);

    public SkillRankProperty getTreeFeller() {
        return treeFeller;
    }

    public SkillRankProperty getHarvestLumber() {
        return harvestLumber;
    }

    public SkillRankProperty getLeafBlower() {
        return leafBlower;
    }
}
