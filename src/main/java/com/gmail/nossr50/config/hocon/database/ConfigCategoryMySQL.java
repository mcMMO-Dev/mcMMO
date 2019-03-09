package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCategoryMySQL {

    @Setting(value = "Enabled", comment = "If set to true, mcMMO will use MySQL/MariaDB instead of FlatFile storage")
    private boolean enabled = true;

    @Setting(value = "Database", comment = "Database settings for MySQL/MariaDB")
    private ConfigCategoryDatabase configCategoryDatabase;


}
