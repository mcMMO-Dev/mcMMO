package com.gmail.nossr50.config.hocon.database;

import com.gmail.nossr50.database.SQLDatabaseManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionMySQL {

    /* DEFAULT VALUES */
    private static final boolean USE_MYSQL_DEFAULT = false;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Use-MySQL", comment = "If set to true, mcMMO will use MySQL/MariaDB instead of FlatFile storage" +
            "\nIt is highly recommended to use a MySQL/MariaDB server over FlatFile," +
            " especially if the number of players on your Minecraft server is fairly high." +
            "\nDefault value: "+USE_MYSQL_DEFAULT)
    private boolean useMySQL = USE_MYSQL_DEFAULT;

    @Setting(value = "User", comment = "Your MySQL User Settings")
    private ConfigSectionUser configSectionUser = new ConfigSectionUser();

    @Setting(value = "Database", comment = "Database settings for MySQL/MariaDB")
    private ConfigSectionDatabase configSectionDatabase = new ConfigSectionDatabase();

    @Setting(value = "Server", comment = "Your MySQL/MariaDB server settings.")
    private UserConfigSectionServer userConfigSectionServer = new UserConfigSectionServer();

    /*
     * GETTER BOILERPLATE
     */

    public boolean isMySQLEnabled() {
        return useMySQL;
    }

    public ConfigSectionUser getConfigSectionUser() {
        return configSectionUser;
    }

    public ConfigSectionDatabase getConfigSectionDatabase() {
        return configSectionDatabase;
    }

    public UserConfigSectionServer getUserConfigSectionServer() {
        return userConfigSectionServer;
    }

    /*
     * HELPER METHODS
     */

    public int getMaxPoolSize(SQLDatabaseManager.PoolIdentifier poolIdentifier)
    {
        switch (poolIdentifier)
        {
            case LOAD:
                return userConfigSectionServer.getConfigSectionMaxPoolSize().getLoad();
            case SAVE:
                return userConfigSectionServer.getConfigSectionMaxPoolSize().getSave();
            case MISC:
                return userConfigSectionServer.getConfigSectionMaxPoolSize().getMisc();
            default:
                return 20;
        }
    }

    public int getMaxConnections(SQLDatabaseManager.PoolIdentifier poolIdentifier)
    {
        switch (poolIdentifier)
        {
            case LOAD:
                return userConfigSectionServer.getConfigSectionMaxConnections().getLoad();
            case SAVE:
                return userConfigSectionServer.getConfigSectionMaxConnections().getSave();
            case MISC:
                return userConfigSectionServer.getConfigSectionMaxConnections().getMisc();
            default:
                return 20;
        }
    }
}
