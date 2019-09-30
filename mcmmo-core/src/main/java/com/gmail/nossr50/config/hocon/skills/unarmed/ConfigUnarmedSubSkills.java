package com.gmail.nossr50.config.hocon.skills.unarmed;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigUnarmedSubSkills {

    @Setting(value = "Disarm")
    public ConfigUnarmedDisarm disarm = new ConfigUnarmedDisarm();

    public ConfigUnarmedLimitBreak unarmedLimitBreak = new ConfigUnarmedLimitBreak();

    public ConfigUnarmedDisarm getDisarm() {
        return disarm;
    }

    public boolean isPreventItemTheft() {
        return disarm.isPreventItemTheft();
    }

    public ConfigUnarmedLimitBreak getUnarmedLimitBreak() {
        return unarmedLimitBreak;
    }
}
