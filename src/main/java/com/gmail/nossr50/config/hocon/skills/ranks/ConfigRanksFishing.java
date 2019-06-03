package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksFishing {

    @Setting(value = "Magic-Hunter")
    private SkillRankProperty magicHunter = new SkillRankProperty(20);

    @Setting(value = "Shake")
    private SkillRankProperty shake = new SkillRankProperty(15);

    @Setting(value = "Master-Angler")
    private SkillRankProperty masterAngler = new SkillRankProperty(50);

    @Setting(value = "Ice-Fishing")
    private SkillRankProperty iceFishing = new SkillRankProperty(5);

    @Setting(value = "Fishermans-Diet")
    private SkillRankProperty fishermansDiet = new SkillRankProperty(20, 40, 60, 80, 100);

    @Setting(value = "Treasure-Hunter")
    private SkillRankProperty treasureHunter = new SkillRankProperty(10, 25, 35, 50, 65, 75, 85, 100);

    @Setting(value = "Inner-Peace")
    private SkillRankProperty innerPeace = new SkillRankProperty(33, 66, 100);

    public SkillRankProperty getMagicHunter() {
        return magicHunter;
    }

    public SkillRankProperty getShake() {
        return shake;
    }

    public SkillRankProperty getMasterAngler() {
        return masterAngler;
    }

    public SkillRankProperty getIceFishing() {
        return iceFishing;
    }

    public SkillRankProperty getFishermansDiet() {
        return fishermansDiet;
    }

    public SkillRankProperty getTreasureHunter() {
        return treasureHunter;
    }

    public SkillRankProperty getInnerPeace() {
        return innerPeace;
    }
}
