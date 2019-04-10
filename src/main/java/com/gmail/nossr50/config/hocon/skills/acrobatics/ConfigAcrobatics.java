package com.gmail.nossr50.config.hocon.skills.acrobatics;

import com.gmail.nossr50.config.hocon.skills.ConfigSubSkillScalingRNG;
import com.gmail.nossr50.config.hocon.skills.acrobatics.dodge.ConfigDodge;
import com.gmail.nossr50.config.hocon.skills.acrobatics.roll.ConfigRoll;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAcrobatics {

    @Setting(value = "Roll", comment = "Settings related to the Roll Sub-Skill." +
            "\nSettings related to preventing abuse of this skill can be found in the anti_exploit config file.")
    private ConfigRoll roll = new ConfigRoll();

    public ConfigRoll getRoll() {
        return roll;
    }

    @Setting(value = "Dodge", comment = "Settings related to the Dodge Sub-Skill." +
            "\nSettings related to preventing abuse of this skill can be found in the anti_exploit config file.")
    private ConfigDodge dodge = new ConfigDodge();

    public ConfigDodge getDodge() {
        return dodge;
    }

    public ConfigSubSkillScalingRNG getRNGSettings() {
        return dodge.getRNGSettings();
    }

    public double getDamageReductionDivisor() {
        return dodge.getDamageReductionDivisor();
    }
}