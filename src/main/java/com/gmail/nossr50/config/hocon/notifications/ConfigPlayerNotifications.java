package com.gmail.nossr50.config.hocon.notifications;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigPlayerNotifications {

    private final static HashMap<NotificationType, PlayerNotification> NOTIFICATION_MAP_DEFAULT;

    static {
        NOTIFICATION_MAP_DEFAULT = new HashMap<>();

        NOTIFICATION_MAP_DEFAULT.put(NotificationType.ABILITY_OFF, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.HARDCORE_MODE, new PlayerNotification(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.REQUIREMENTS_NOT_MET, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.ABILITY_COOLDOWN, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.LEVEL_UP_MESSAGE, new PlayerNotification(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.HOLIDAY, new PlayerNotification(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.TOOL, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUBSKILL_MESSAGE, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUBSKILL_MESSAGE_FAILED, new PlayerNotification(true, true, false));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUBSKILL_UNLOCKED, new PlayerNotification(true, true, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUPER_ABILITY, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.SUPER_ABILITY_ALERT_OTHERS, new PlayerNotification(true, true, false));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.ITEM_MESSAGE, new PlayerNotification(true, false, true));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.NO_PERMISSION, new PlayerNotification(true, true, false));
        NOTIFICATION_MAP_DEFAULT.put(NotificationType.PARTY_MESSAGE, new PlayerNotification(true, true, false));
    }

    @Setting(value = "Notification-Settings")
    private HashMap<NotificationType, PlayerNotification> notificationSettingHashMap = NOTIFICATION_MAP_DEFAULT;

    public HashMap<NotificationType, PlayerNotification> getNotificationSettingHashMap() {
        return notificationSettingHashMap;
    }

    public PlayerNotification getPlayerNotification(NotificationType notificationType) {
        return notificationSettingHashMap.get(notificationType);
    }
}