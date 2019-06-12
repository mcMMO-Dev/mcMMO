package com.gmail.nossr50.config.hocon.notifications;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigNotifications {

    private static final boolean SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT = true;

    @Setting(value = "Action-Bar-Notifications", comment = "Settings related to action bar messages." +
            "\nThe action bar is the area above your health and armor.")
    private ConfigActionBarNotifications actionBarNotifications = new ConfigActionBarNotifications();


    @Setting(value = "General", comment = "General settings for Notifications")
    private ConfigNotificationGeneral configNotificationGeneral = new ConfigNotificationGeneral();

    @Setting(value = "Super-Ability-Tool-Raising-Lowering-Notification",
            comment = "Notifies the player when they go into the tool readying state for super abilities.")
    private boolean superAbilityToolMessage = SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT;


    public boolean isShowProfileLoadedMessage() {
        return configNotificationGeneral.isShowProfileLoadedMessage();
    }

    public boolean isPlayerTips() {
        return configNotificationGeneral.isPlayerTips();
    }

    public static boolean isSuperAbilityToolNotificationDefault() {
        return SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT;
    }

    public boolean isSuperAbilityToolMessage() {
        return superAbilityToolMessage;
    }

    public ConfigActionBarNotifications getActionBarNotifications() {
        return actionBarNotifications;
    }

    public ConfigNotificationGeneral getConfigNotificationGeneral() {
        return configNotificationGeneral;
    }
}
