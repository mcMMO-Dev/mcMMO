package com.gmail.nossr50.config.hocon.notifications;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigNotifications {

    public static final boolean SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT = true;

    @Setting(value = "Super-Ability-Tool-Raising-Lowering-Notification",
            comment = "Notifies the player when they go into the tool readying state for super abilities.")
    private boolean superAbilityToolMessage = SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT;

    public boolean isSuperAbilityToolMessage() {
        return superAbilityToolMessage;
    }

    @Setting(value = "Action-Bar-Notifications", comment = "Settings related to action bar messages." +
            "\nThe action bar is the area above your health and armor.")
    public ConfigActionBarNotifications actionBarNotifications = new ConfigActionBarNotifications();

    @Setting(value = "General", comment = "General settings for Notifications")
    public ConfigNotificationGeneral configNotificationGeneral = new ConfigNotificationGeneral();

    public ConfigActionBarNotifications getActionBarNotifications() {
        return actionBarNotifications;
    }

    public ConfigNotificationGeneral getConfigNotificationGeneral() {
        return configNotificationGeneral;
    }
}
