package com.gmail.nossr50.config.admin;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAdmin {

    @Setting(value = "Admin-Notifications", comment = "Settings related to admin alerts in mcMMO.")
    private ConfigAdminNotifications configAdminNotifications = new ConfigAdminNotifications();

    public boolean isSendAdminNotifications() {
        return configAdminNotifications.isSendAdminNotifications();
    }
}