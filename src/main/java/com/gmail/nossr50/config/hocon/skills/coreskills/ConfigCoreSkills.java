package com.gmail.nossr50.config.hocon.skills.coreskills;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCoreSkills {

    @Setting(value = "Core Skills for Acrobatics" +
            "\nCore skills are ones built into mcMMO")
    private ConfigCoreSkillsAcrobatics configCoreSkillsAcrobatics = new ConfigCoreSkillsAcrobatics();

    public boolean isRollEnabled() {
        return configCoreSkillsAcrobatics.isRollEnabled();
    }

    public ConfigCoreSkillsAcrobatics getConfigCoreSkillsAcrobatics() {
        return configCoreSkillsAcrobatics;
    }

    public boolean isAcrobaticsEnabled() {
        return getConfigCoreSkillsAcrobatics().isAcrobaticsEnabled();
    }
}
