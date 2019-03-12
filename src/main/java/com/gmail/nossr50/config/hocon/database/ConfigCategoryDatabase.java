package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCategoryDatabase {

    @Setting(value = "Database_Name", comment = "The database name for your DB, this DB must already exist on the SQL server.")
    private String databaseName = "example_database_name";

    @Setting(value = "Table_Prefix", comment = "The Prefix that will be used for tables in your DB")
    private String tablePrefix = "mcmmo_";

    @Setting(value = "Max_Connections", comment = "This setting is the max simultaneous MySQL/MariaDB connections allowed at a time, this needs to be high enough to support multiple player logins in quick succession")
    private ConfigCategoryMaxConnections configCategoryMaxConnections;

    @Setting(value = "Max_Pool_Size", comment = "This setting is the max size of the pool of cached connections that we hold at any given time.")
    private ConfigCategoryMaxPoolSize configCategoryMaxPoolSize;

    /*
     * GETTER BOILERPLATE
     */

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public ConfigCategoryMaxConnections getConfigCategoryMaxConnections() {
        return configCategoryMaxConnections;
    }

    public ConfigCategoryMaxPoolSize getConfigCategoryMaxPoolSize() {
        return configCategoryMaxPoolSize;
    }
}
