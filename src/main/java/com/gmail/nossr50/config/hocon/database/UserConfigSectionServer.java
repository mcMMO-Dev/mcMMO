package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class UserConfigSectionServer {

    @Setting(value = "Use_SSL", comment =   "Enables SSL for MySQL/MariaDB connections." +
                                            "\nIf your SQL server supports SSL, it is recommended to have it on but not necessary." +
                                            "\nIf you run into any issues involving SSL, its best to just turn this off.")
    private boolean useSSL = true;

    @Setting(value = "Server_Port", comment = "Your MySQL/MariaDB server port" +
            "\nThe default port is typically 3306 for MySQL, but every server configuration is different!")
    private int serverPort = 3306;

    @Setting(value = "Server_Address", comment = "The address for your MySQL/MariaDB server" +
            "If the MySQL server is hosted on the same machine, you can use the localhost alias")
    private String serverAddress = "localhost";

    @Setting(value = "Max_Connections", comment = "This setting is the max simultaneous MySQL/MariaDB connections allowed at a time." +
            "\nThis needs to be high enough to support multiple player logins in quick succession, it is recommended that you do not lower these values")
    private UserConfigSectionMaxConnections userConfigSectionMaxConnections = new UserConfigSectionMaxConnections();

    @Setting(value = "Max_Pool_Size", comment = "This setting is the max size of the pool of cached connections that we hold at any given time.")
    private UserConfigSectionMaxPoolSize userConfigSectionMaxPoolSize = new UserConfigSectionMaxPoolSize();

    /*
     * GETTER BOILERPLATE
     */

    public boolean isUseSSL() {
        return useSSL;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public UserConfigSectionMaxConnections getUserConfigSectionMaxConnections() {
        return userConfigSectionMaxConnections;
    }

    public UserConfigSectionMaxPoolSize getUserConfigSectionMaxPoolSize() {
        return userConfigSectionMaxPoolSize;
    }


}
