package com.gmail.nossr50.config.skills.acrobatics;

import com.gmail.nossr50.config.skills.acrobatics.dodge.ConfigDodge;
import com.gmail.nossr50.config.skills.acrobatics.roll.ConfigRoll;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAcrobaticsSubSkills {

    @Setting(value = "Roll", comment = "Settings related to the Roll Sub-Skill." +
            "\nSettings related to preventing abuse of this skill can be found in the anti_exploit config file.")
    private ConfigRoll roll = new ConfigRoll();

    @Setting(value = "Dodge", comment = "Settings related to the Dodge Sub-Skill." +
            "\nSettings related to preventing abuse of this skill can be found in the anti_exploit config file.")
    private ConfigDodge dodge = new ConfigDodge();

    public ConfigRoll getRoll() {
        return roll;
    }

    public ConfigDodge getDodge() {
        return dodge;
    }

}
