package com.gmail.nossr50.config.hocon.notifications;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigNotificationGeneral {

    private static final boolean PLAYER_TIPS_DEFAULT = true;
    public static final boolean PROFILE_LOADED_DEFAULT = false;
    @Setting(value = "Player-Tips", comment = "Allows mcMMO to send players automated helpful tips." +
            "\n Default value: " + PLAYER_TIPS_DEFAULT)
    private boolean playerTips = PLAYER_TIPS_DEFAULT;

    @Setting(value = "Show-Profile-Loaded-Message", comment = "If set to true, players will be shown a message when their profile has been loaded." +
            "\nDefault value: "+PROFILE_LOADED_DEFAULT)
    private boolean showProfileLoadedMessage = PROFILE_LOADED_DEFAULT;

    public boolean isShowProfileLoadedMessage() {
        return showProfileLoadedMessage;
    }

    public boolean isPlayerTips() {
        return playerTips;
    }
}