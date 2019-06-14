package com.gmail.nossr50.config.hocon.skills.unarmed;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigUnarmed {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigUnarmedSubskills subskills = new ConfigUnarmedSubskills();

    public ConfigUnarmedSubskills getSubskills() {
        return subskills;
    }

    public ConfigUnarmedDisarm getDisarm() {
        return subskills.getDisarm();
    }

    public boolean isPreventItemTheft() {
        return subskills.isPreventItemTheft();
    }
}