package com.gmail.nossr50.config.hocon.database;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDatabase extends Config {

    @Setting(value = "MySQL", comment = "Settings for using MySQL or MariaDB database")
    private ConfigCategoryMySQL configCategoryMySQL;

    public ConfigDatabase() {
        super("mysql", ConfigConstants.getDataFolder(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR,
                true,true, false, true);
    }

    @Override
    public void unload() {

    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }
}
