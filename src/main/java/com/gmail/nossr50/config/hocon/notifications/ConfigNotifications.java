package com.gmail.nossr50.config.hocon.notifications;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigNotifications {

    private static final boolean SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT = true;

    @Setting(value = "Player-Notifications", comment = "Settings for player notifications" +
            "\nPlayer notifications are often sent to the action bar (The action bar is the location above player health/armor/hunger displays)" +
            "\nYou can configure where these notifications are sent and whether or not they are sent at all.")
    private ConfigPlayerNotifications playerNotifications = new ConfigPlayerNotifications();


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

    public ConfigPlayerNotifications getPlayerNotificationsConfig() {
        return playerNotifications;
    }

    public ConfigNotificationGeneral getConfigNotificationGeneral() {
        return configNotificationGeneral;
    }

    public HashMap<NotificationType, PlayerNotification> getNotificationSettingHashMap() {
        return playerNotifications.getNotificationSettingHashMap();
    }

    public PlayerNotification getPlayerNotification(NotificationType notificationType) {
        return playerNotifications.getPlayerNotification(notificationType);
    }
}
