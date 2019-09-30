package com.gmail.nossr50.config.hocon.commands;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCommandsMisc {

    private static final boolean MATCH_OFFLINE = true;
    @Setting(value = "Match-Offline", comment = "If set to true mcMMO will attempt to match player names from commands to any player that exists in the DB, otherwise it will only try to match online players.")
    private boolean matchOffline = MATCH_OFFLINE;

    public boolean isMatchOfflinePlayers() {
        return matchOffline;
    }
}
