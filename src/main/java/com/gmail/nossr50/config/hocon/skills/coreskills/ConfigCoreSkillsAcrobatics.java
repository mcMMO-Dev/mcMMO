package com.gmail.nossr50.config.hocon.skills.coreskills;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCoreSkillsAcrobatics {

    private static final boolean ROLL_DEFAULT = true;
    private static final boolean ACROBATICS_MASTER_DEFAULT = true;

    @Setting(value = "Disable", comment = "Enable the primary skill Acrobatics")
    private boolean enableAcrobatics = ACROBATICS_MASTER_DEFAULT;

    @Setting(value = "Roll", comment = "Enable or disable the Roll skill.")
    private boolean enableRoll = ROLL_DEFAULT;

    public boolean isRollEnabled() {
        return enableRoll;
    }

    public boolean isAcrobaticsEnabled() {
        return enableAcrobatics;
    }
}
