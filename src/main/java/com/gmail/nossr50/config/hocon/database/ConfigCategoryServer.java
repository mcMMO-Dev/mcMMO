package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCategoryServer {

    @Setting(value = "Use_SSL", comment =   "Enables SSL for MySQL/MariaDB connections, newer versions of MySQL will spam your console if you aren't using SSL." +
                                            " It is recommended that you turn this on if you are using a newer version of MySQL," +
                                            " if you run into issues with SSL not being supported, turn this off.")
    private boolean useSSL = true;

    @Setting(value = "Server_Port", comment = "Your MySQL/MariaDB server port")
    private int serverPort = 3306;

    @Setting(value = "Server_Address", comment = "The address for your MySQL/MariaDB server")
    private String serverAddress = "localhost";

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
}
