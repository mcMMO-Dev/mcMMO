package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionDatabase {

    @Setting(value = "Database_Name", comment = "The database name for your DB, this DB must already exist on the SQL server.")
    private String databaseName = "example_database_name";

    @Setting(value = "Table_Prefix", comment = "The Prefix that will be used for tables in your DB")
    private String tablePrefix = "mcmmo_";

    /*
     * GETTER BOILERPLATE
     */

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }


}
