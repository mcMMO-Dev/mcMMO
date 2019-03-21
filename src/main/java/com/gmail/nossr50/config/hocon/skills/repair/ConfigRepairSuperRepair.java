package com.gmail.nossr50.config.hocon.skills.repair;

import com.gmail.nossr50.config.hocon.skills.ConfigScalingSubSkill;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairSuperRepair {

    @Setting(value = "Settings")
    private ConfigScalingSubSkill superRepair = new ConfigScalingSubSkill();

    public ConfigScalingSubSkill getSuperRepair() {
        return superRepair;
    }
}