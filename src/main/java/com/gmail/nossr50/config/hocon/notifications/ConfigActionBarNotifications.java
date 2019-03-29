package com.gmail.nossr50.config.hocon.notifications;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigActionBarNotifications {

    @Setting(value = "Notification-Settings")
    private HashMap<NotificationType, ActionBarNotificationSetting> notificationSettingHashMap;

    public HashMap<NotificationType, ActionBarNotificationSetting> getNotificationSettingHashMap() {
        return notificationSettingHashMap;
    }
}