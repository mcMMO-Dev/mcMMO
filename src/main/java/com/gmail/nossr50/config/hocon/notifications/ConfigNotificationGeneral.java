package com.gmail.nossr50.config.hocon.notifications;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigNotificationGeneral {

    public static final boolean PLAYER_TIPS_DEFAULT = true;
    @Setting(value = "Player-Tips", comment = "Allows mcMMO to send players automated helpful tips." +
            "\n Default value: "+PLAYER_TIPS_DEFAULT)
    public boolean playerTips = PLAYER_TIPS_DEFAULT;

    public boolean isPlayerTips() {
        return playerTips;
    }
}