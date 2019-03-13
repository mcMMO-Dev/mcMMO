package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDatabase {

    /*
     * CONFIG NODES
     */

    @Setting(value = "MySQL", comment = "Settings for using MySQL or MariaDB database")
    private ConfigSectionMySQL configSectionMySQL = new ConfigSectionMySQL();

    @Setting(value = "Enabled", comment = "If set to true, mcMMO will use MySQL/MariaDB instead of FlatFile storage")
    private boolean enabled = true;

    /*
     * GETTER BOILERPLATE
     */

    public ConfigSectionMySQL getConfigSectionMySQL() {
        return configSectionMySQL;
    }
}
