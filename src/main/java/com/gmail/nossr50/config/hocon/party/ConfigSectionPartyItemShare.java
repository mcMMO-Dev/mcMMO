package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigSectionPartyItemShare {

    @Setting(value = "Party-Item-Weights", comment = "The values in this section are used only for loot distribution EQUAL" +
            "\nWhen a player gets a good drop their odds to get another good drop plummet until other party members catch up in item weight points.")
    ConfigSectionPartyItemWeights partyItemWeights = new ConfigSectionPartyItemWeights();

    @Setting(value = "Party-Item-Share-Settings")
    ConfigSectionPartyItemShareSettings partyItemShareSettings = new ConfigSectionPartyItemShareSettings();

    public HashMap<String, Integer> getItemShareMap() {
        return partyItemWeights.getItemShareMap();
    }

    public ConfigSectionPartyItemWeights getPartyItemWeights() {
        return partyItemWeights;
    }

    public ConfigSectionPartyItemShareSettings getPartyItemShareSettings() {
        return partyItemShareSettings;
    }
}