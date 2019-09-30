package com.gmail.nossr50.config.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksUnarmed {

    @Setting(value = "Unarmed-Limit-Break")
    private SkillRankProperty limitBreak = new SkillRankProperty(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

    @Setting(value = "Berserk")
    private SkillRankProperty berserk = new SkillRankProperty(5);

    @Setting(value = "Arrow-Deflect")
    private SkillRankProperty arrowDeflect = new SkillRankProperty(20);

    @Setting(value = "Disarm")
    private SkillRankProperty disarm = new SkillRankProperty(25);

    @Setting(value = "Iron-Grip")
    private SkillRankProperty ironGrip = new SkillRankProperty(60);

    @Setting(value = "Iron-Arm-Style")
    private SkillRankProperty ironArmStyle = new SkillRankProperty(1, 25, 50, 75, 100);

    public SkillRankProperty getLimitBreak() {
        return limitBreak;
    }

    public SkillRankProperty getBerserk() {
        return berserk;
    }

    public SkillRankProperty getArrowDeflect() {
        return arrowDeflect;
    }

    public SkillRankProperty getDisarm() {
        return disarm;
    }

    public SkillRankProperty getIronGrip() {
        return ironGrip;
    }

    public SkillRankProperty getIronArmStyle() {
        return ironArmStyle;
    }
}
