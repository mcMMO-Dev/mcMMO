package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyLimit {

    public static final boolean USE_LIMIT_DEFAULT = false;
    public static final int PARTY_SIZE_LIMIT_DEFAULT = 5;

    @Setting(value = "Max-Party-Size",
            comment =   "The maximum size for parties, parties bigger than this size will be dismantled." +
                    "\nThis setting is only used if \"Enforce-Size-Limit\" is true." +
                    "\nPlayers can bypass this limit with the following permission node \"mcmmo.bypass.partylimit\"" +
                    "\nDefault value: "+PARTY_SIZE_LIMIT_DEFAULT)
    public int partyMaxSize = PARTY_SIZE_LIMIT_DEFAULT;

    @Setting(value = "Enforce-Size-Limit",
            comment =   "Limits parties to a maximum size defined by \"Max-Party-Size\"" +
                    "\nParties over the current limit will be dismantled" +
                    "\nPlayers can bypass this limit with the following permission node \"mcmmo.bypass.partylimit\"" +
                    "\nDefault value: "+USE_LIMIT_DEFAULT)
    public boolean useCap = USE_LIMIT_DEFAULT;

}
