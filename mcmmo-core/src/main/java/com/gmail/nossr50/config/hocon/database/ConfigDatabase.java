package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDatabase {

    /*
     * CONFIG NODES
     */

    @Setting(value = "Database-Cleaning",
            comment = "Settings to automatically purge old users to keep database sizes small.")
    private ConfigSectionCleaning configSectionCleaning = new ConfigSectionCleaning();

    @Setting(value = "FlatFile", comment = "FlatFile is a plain text database used by mcMMO." +
            "\nIt is recommended that you use MySQL/MariaDB instead because FlatFile is notoriously slow.")
    private ConfigDatabaseFlatFile configDatabaseFlatFile = new ConfigDatabaseFlatFile();

    @Setting(value = "MySQL", comment = "Settings for using MySQL or MariaDB database" +
            "\nI recommend using MariaDB, its completely compatible with MySQL and runs a lot better" +
            "\nI also recommend having the MySQL/MariaDB server in the same datacenter or LAN as your Minecraft server" +
            "\nmcMMO uses ASYNC threaded requests for SQL, so the latency is not really a big deal," +
            " but ideally you want low latency to your SQL server anyways!")
    private ConfigSectionMySQL configSectionMySQL = new ConfigSectionMySQL();

    @Setting(value = "General", comment = "Settings that apply to both databases, both MySQL/MariaDB and FlatFile.")
    private ConfigSectionDatabaseGeneral configSectionDatabaseGeneral = new ConfigSectionDatabaseGeneral();

    /*
     * GETTER BOILERPLATE
     */

    public ConfigDatabaseFlatFile getConfigDatabaseFlatFile() {
        return configDatabaseFlatFile;
    }

    public ConfigSectionDatabaseGeneral getConfigSectionDatabaseGeneral() {
        return configSectionDatabaseGeneral;
    }

    public ConfigSectionMySQL getConfigSectionMySQL() {
        return configSectionMySQL;
    }

    public ConfigSectionCleaning getConfigSectionCleaning() {
        return configSectionCleaning;
    }
}
