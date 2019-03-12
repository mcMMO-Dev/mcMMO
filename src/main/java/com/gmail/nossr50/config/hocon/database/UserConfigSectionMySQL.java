package com.gmail.nossr50.config.hocon.database;

import com.gmail.nossr50.database.SQLDatabaseManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class UserConfigSectionMySQL {

    @Setting(value = "Enabled", comment = "If set to true, mcMMO will use MySQL/MariaDB instead of FlatFile storage")
    private boolean enabled = true;

    @Setting(value = "User", comment = "Your MySQL User Settings")
    private UserConfigSectionUser userConfigSectionUser;

    @Setting(value = "Database", comment = "Database settings for MySQL/MariaDB")
    private UserConfigSectionDatabase userConfigSectionDatabase;

    @Setting(value = "Server", comment = "Your MySQL/MariaDB server settings.")
    private UserConfigSectionServer userConfigSectionServer;

    /*
     * GETTER BOILERPLATE
     */

    public boolean isMySQLEnabled() {
        return enabled;
    }

    public UserConfigSectionUser getUserConfigSectionUser() {
        return userConfigSectionUser;
    }

    public UserConfigSectionDatabase getUserConfigSectionDatabase() {
        return userConfigSectionDatabase;
    }

    public UserConfigSectionServer getUserConfigSectionServer() {
        return userConfigSectionServer;
    }

    public int getMaxPoolSize(SQLDatabaseManager.PoolIdentifier poolIdentifier)
    {
        switch (poolIdentifier)
        {
            case LOAD:
                return userConfigSectionServer.getUserConfigSectionMaxPoolSize().getLoad();
            case SAVE:
                return userConfigSectionServer.getUserConfigSectionMaxPoolSize().getSave();
            case MISC:
                return userConfigSectionServer.getUserConfigSectionMaxPoolSize().getMisc();
            default:
                return 20;
        }
    }

    public int getMaxConnections(SQLDatabaseManager.PoolIdentifier poolIdentifier)
    {
        switch (poolIdentifier)
        {
            case LOAD:
                return userConfigSectionServer.getUserConfigSectionMaxPoolSize().getLoad();
            case SAVE:
                return userConfigSectionServer.getUserConfigSectionMaxPoolSize().getSave();
            case MISC:
                return userConfigSectionServer.getUserConfigSectionMaxPoolSize().getMisc();
            default:
                return 20;
        }
    }
}
