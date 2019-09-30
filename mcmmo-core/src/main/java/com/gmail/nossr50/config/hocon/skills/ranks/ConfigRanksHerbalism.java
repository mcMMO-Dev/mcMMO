package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksHerbalism {

    @Setting(value = "Green-Terra")
    private SkillRankProperty greenTerra = new SkillRankProperty(5);

    @Setting(value = "Green-Thumb")
    private SkillRankProperty greenThumb = new SkillRankProperty(25, 50, 75, 100);

    @Setting(value = "Farmers-Diet")
    private SkillRankProperty farmersDiet = new SkillRankProperty(20, 40, 60, 80, 100);

    @Setting(value = "Double-Drops")
    private SkillRankProperty doubleDrops = new SkillRankProperty(1);

    public SkillRankProperty getGreenTerra() {
        return greenTerra;
    }

    public SkillRankProperty getGreenThumb() {
        return greenThumb;
    }

    public SkillRankProperty getFarmersDiet() {
        return farmersDiet;
    }

    public SkillRankProperty getDoubleDrops() {
        return doubleDrops;
    }
}
