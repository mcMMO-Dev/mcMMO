package com.gmail.nossr50.config.hocon.event;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigEventExperienceRate {

    public static final boolean SHOW_XP_RATE_ON_JOIN_DEFAULT = true;
    public static final boolean BROADCAST_EVENT_MESSAGES_DEFAULT = true;

    @Setting(value = "Show-Details-On-Player-Join", comment = "Show players info about ongoing XP rate events when they join the server." +
            "\nDefault value: "+SHOW_XP_RATE_ON_JOIN_DEFAULT)
    private boolean showXPRateInfoOnPlayerJoin = SHOW_XP_RATE_ON_JOIN_DEFAULT;

    @Setting(value = "Broadcast-Event-Messages", comment = "Whether or not to broadcast info about the event to players on the server" +
            "\nA broadcast is a message sent to all players connected to the server" +
            "\nDefault value: "+BROADCAST_EVENT_MESSAGES_DEFAULT)
    private boolean broadcastXPRateEventMessages = BROADCAST_EVENT_MESSAGES_DEFAULT;

    public boolean isShowXPRateInfoOnPlayerJoin() {
        return showXPRateInfoOnPlayerJoin;
    }

    public boolean isBroadcastXPRateEventMessages() {
        return broadcastXPRateEventMessages;
    }
}
