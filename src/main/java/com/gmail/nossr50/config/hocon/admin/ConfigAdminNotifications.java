package com.gmail.nossr50.config.hocon.admin;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAdminNotifications {
    private static final boolean SEND_ADMIN_NOTIFICATIONS_DEFAULT = true;

    @Setting(value = "Send-Admin-Notifications", comment = "Send admins notifications about sensitive commands being executed" +
            "\nDefault value: " + SEND_ADMIN_NOTIFICATIONS_DEFAULT)
    private boolean sendAdminNotifications = SEND_ADMIN_NOTIFICATIONS_DEFAULT;

    public boolean isSendAdminNotifications() {
        return sendAdminNotifications;
    }
}
