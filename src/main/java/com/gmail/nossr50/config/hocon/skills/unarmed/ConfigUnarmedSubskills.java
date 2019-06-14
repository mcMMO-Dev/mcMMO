package com.gmail.nossr50.config.hocon.skills.unarmed;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigUnarmedSubskills {

    @Setting(value = "Disarm")
    public ConfigUnarmedDisarm disarm = new ConfigUnarmedDisarm();

    public ConfigUnarmedDisarm getDisarm() {
        return disarm;
    }

    public boolean isPreventItemTheft() {
        return disarm.isPreventItemTheft();
    }
}
