package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyChat {

    public static final String PARTY_CHAT_PREFIX_FORMAT_DEFAULT = "&a(&f{0}&a)";
    public static final String PARTY_CHAT_PREFIX_ALLY_DEFAULT = "&a(A)&r";

    public static final boolean PARTY_LEADER_GOLD_DEFAULT = true;
    public static final boolean PARTY_USE_DISPLAY_NAMES_DEFAULT = true;

    @Setting(value = "Prefix-Party-Members",
            comment = "This is the formatting used for the prefix at the beginning of a party chat message." +
                    "Default value: "+PARTY_CHAT_PREFIX_FORMAT_DEFAULT)
    private String partyChatPrefixFormat = PARTY_CHAT_PREFIX_FORMAT_DEFAULT;

    @Setting(value = "Prefix-Ally",
            comment = "This is the formatting used for the prefix at the beginning of a party chat message from an ally." +
                    "\nDefault value: "+PARTY_CHAT_PREFIX_ALLY_DEFAULT)
    private String partyChatPrefixAlly = PARTY_CHAT_PREFIX_ALLY_DEFAULT;

    @Setting(value = "Party-Leaders-Name-Uses-Gold-Coloring",
            comment = "Changes the party leader to use a gold coloring for their name." +
                    "\nDefault value: "+PARTY_LEADER_GOLD_DEFAULT)
    private boolean isPartyLeaderColoredGold = PARTY_LEADER_GOLD_DEFAULT;

    @Setting(value = "Use-Display-Names", comment = "Party chat will use formatted display names instead of the players raw nickname." +
            "\nDisplay names are often colored, modified, or styled differently from a players regular name." +
            "\nDisplay names are typically modified by chat plugins and the like." +
            "\nIf you'd rather player names were just their current Minecraft username, turn this off." +
            "\nDefault value: "+PARTY_USE_DISPLAY_NAMES_DEFAULT)
    private boolean partyDisplayNamesEnabled = PARTY_USE_DISPLAY_NAMES_DEFAULT;

    public String getPartyChatPrefixFormat() {
        return partyChatPrefixFormat;
    }

    public String getPartyChatPrefixAlly() {
        return partyChatPrefixAlly;
    }

    public boolean isPartyLeaderColoredGold() {
        return isPartyLeaderColoredGold;
    }

    public boolean isPartyDisplayNamesEnabled() {
        return partyDisplayNamesEnabled;
    }
}
