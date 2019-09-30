package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyGeneral {

    public static final boolean PARTY_SYSTEM_DEFAULT = true;

    @Setting(value = "Party-Limitations")
    private ConfigSectionPartyLimit configSectionPartyLimit = new ConfigSectionPartyLimit();

    @Setting(value = "Enable-Party-System", comment = "Turn this off to completely disable the mcMMO party system." +
            "\nDefault value: " + PARTY_SYSTEM_DEFAULT)
    private boolean enablePartySystem = PARTY_SYSTEM_DEFAULT;

    public int getPartySizeLimit() {
        return configSectionPartyLimit.partyMaxSize;
    }

    public boolean isPartySizeCapped() {
        return configSectionPartyLimit.useCap;
    }

    public boolean isEnablePartySystem() {
        return enablePartySystem;
    }
}
