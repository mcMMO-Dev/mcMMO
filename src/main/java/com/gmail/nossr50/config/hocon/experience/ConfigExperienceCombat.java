package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceCombat {

    private static final boolean PVP_XP_ENABLED_DEFAULT = false;

    @Setting(value = "PVP-XP", comment = "If true, players will gain XP from PVP interactions." +
            "\nBe careful turning this on as this can potentially allow for unwanted behaviour from players." +
            "\nDefault value: "+PVP_XP_ENABLED_DEFAULT)
    private boolean pvpXPEnabled = PVP_XP_ENABLED_DEFAULT;

    public boolean isPvpXPEnabled() {
        return pvpXPEnabled;
    }

}