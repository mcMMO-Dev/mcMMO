package com.gmail.nossr50.config.hocon.skills.acrobatics;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.config.hocon.skills.acrobatics.dodge.ConfigDodge;
import com.gmail.nossr50.config.hocon.skills.acrobatics.roll.ConfigRoll;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAcrobatics {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE, comment = "Sub-Skill settings for Acrobatics")
    private ConfigAcrobaticsSubSkills subSkills = new ConfigAcrobaticsSubSkills();

    public ConfigAcrobaticsSubSkills getSubSkills() {
        return subSkills;
    }

    public ConfigRoll getRoll() {
        return subSkills.getRoll();
    }

    public ConfigDodge getDodge() {
        return subSkills.getDodge();
    }

    public double getDamageTheshold() {
        return getRoll().getDamageTheshold();
    }

    public double getDamageReductionDivisor() {
        return getDodge().getDamageReductionDivisor();
    }
}