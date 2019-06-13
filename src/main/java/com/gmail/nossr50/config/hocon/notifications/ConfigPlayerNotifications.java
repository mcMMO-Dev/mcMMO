package com.gmail.nossr50.config.hocon.notifications;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigPlayerNotifications {

    private final static HashMap<NotificationType, PlayerNotificationSettings> NOTIFICATION_MAP_DEFAULT;

    static {
        NOTIFICATION_MAP_DEFAULT = new HashMap<>();

        NOTIFICATION_MAP_DEFAULT.put(NotificationType.ABILITY_OFF, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.HARDCORE_MODE, new PlayerNotificationSettings(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.REQUIREMENTS_NOT_MET, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.ABILITY_COOLDOWN, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.LEVEL_UP_MESSAGE, new PlayerNotificationSettings(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.HOLIDAY, new PlayerNotificationSettings(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.TOOL, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUBSKILL_MESSAGE, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUBSKILL_MESSAGE_FAILED, new PlayerNotificationSettings(true, true, false));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUBSKILL_UNLOCKED, new PlayerNotificationSettings(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUPER_ABILITY, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUPER_ABILITY_ALERT_OTHERS, new PlayerNotificationSettings(true, true, false));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.ITEM_MESSAGE, new PlayerNotificationSettings(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.NO_PERMISSION, new PlayerNotificationSettings(true, true, false));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.PARTY_MESSAGE, new PlayerNotificationSettings(true, true, false));
    }

    @Setting(value = "Notification-Settings")
    private HashMap<NotificationType, PlayerNotificationSettings> notificationSettingHashMap = NOTIFICATION_MAP_DEFAULT;

    public HashMap<NotificationType, PlayerNotificationSettings> getNotificationSettingHashMap() {
        return notificationSettingHashMap;
    }

    public PlayerNotificationSettings getPlayerNotification(NotificationType notificationType) {
        return notificationSettingHashMap.get(notificationType);
    }
}