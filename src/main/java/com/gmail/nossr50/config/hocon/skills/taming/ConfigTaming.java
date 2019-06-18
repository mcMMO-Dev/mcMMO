package com.gmail.nossr50.config.hocon.skills.taming;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigTaming {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigTamingSubSkills subSkills = new ConfigTamingSubSkills();

    public ConfigTamingSubSkills getSubSkills() {
        return subSkills;
    }
}