package com.gmail.nossr50.config.hocon.party.data;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigPartyData {

    @Setting(value = "Parties")
    private HashMap<String, ConfigPartyDataStore> partyName = new HashMap<>();
}
