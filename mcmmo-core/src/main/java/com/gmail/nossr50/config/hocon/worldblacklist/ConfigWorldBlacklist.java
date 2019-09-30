package com.gmail.nossr50.config.hocon.worldblacklist;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class ConfigWorldBlacklist {
    @Setting(value = "World-Blacklist", comment = "Enter as many worlds as you want here." +
            "\nWhen a world is blacklisted, mcMMO ceases to function for players in that world outside of a few specific commands." +
            "\nIf you want only a certain part of a world to be blacklisted, " +
            "\nI instead recommend using WorldGuard and negating the \"mcmmo\" WorldGuard region flag.")
    private ArrayList<String> blackListedWorlds = ConfigConstants.EXAMPLE_BLACKLIST_WORLDS_LIST_DEFAULT;

    public ArrayList<String> getBlackListedWorlds() {
        return blackListedWorlds;
    }
}
