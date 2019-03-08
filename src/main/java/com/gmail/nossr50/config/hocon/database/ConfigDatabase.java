package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDatabase {

    @Setting(value = "MySQL", comment = "Settings for using MySQL or MariaDB database")
    private ConfigCategoryMySQL configCategoryMySQL;

}
