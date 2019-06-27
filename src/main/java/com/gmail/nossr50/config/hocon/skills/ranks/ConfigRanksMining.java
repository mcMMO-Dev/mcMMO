package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksMining {

    @Setting(value = "Super-Breaker")
    private SkillRankProperty superBreaker = new SkillRankProperty(5);

    @Setting(value = "Bigger-Bombs")
    private SkillRankProperty biggerBombs = new SkillRankProperty(10);

    @Setting(value = "Demolitions-Expertise")
    private SkillRankProperty demolitionsExpertise = new SkillRankProperty(10);

    @Setting(value = "Double-Drops")
    private SkillRankProperty doubleDrops = new SkillRankProperty(1);

    @Setting(value = "Blast-Mining")
    private SkillRankProperty blastMining = new SkillRankProperty(10, 25, 35, 50, 65, 75, 85, 100);

    public SkillRankProperty getSuperBreaker() {
        return superBreaker;
    }

    public SkillRankProperty getBiggerBombs() {
        return biggerBombs;
    }

    public SkillRankProperty getDemolitionsExpertise() {
        return demolitionsExpertise;
    }

    public SkillRankProperty getDoubleDrops() {
        return doubleDrops;
    }

    public SkillRankProperty getBlastMining() {
        return blastMining;
    }
}
