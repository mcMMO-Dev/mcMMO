package com.gmail.nossr50.config.hocon.skills.repair;

import com.gmail.nossr50.config.hocon.skills.ConfigSubSkillScalingRNG;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairSuperRepair {

    @Setting(value = "Settings")
    private ConfigSubSkillScalingRNG superRepair = new ConfigSubSkillScalingRNG();

    public ConfigSubSkillScalingRNG getSuperRepair() {
        return superRepair;
    }
}