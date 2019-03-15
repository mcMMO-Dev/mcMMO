package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyGeneral {

    @Setting(value = "Party-Limitations")
    private ConfigSectionPartyLimit configSectionPartyLimit = new ConfigSectionPartyLimit();

    public int getPartySizeLimit() {
        return configSectionPartyLimit.partyMaxSize;
    }

    public boolean isPartySizeCapped() {
        return configSectionPartyLimit.useCap;
    }
}
