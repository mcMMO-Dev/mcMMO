package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCategoryServer {

    @Setting(value = "Use_SSL", comment = "Enables SSL for MySQL/MariaDB connections, newer versions of MySQL will spam your console if you aren't using SSL")
    private boolean useSSL;

    @Setting(value = "Server_Port", comment = "Your MySQL/MariaDB server port")
    private String serverPort;

    @Setting(value = "Server_Address", comment = "The address for your MySQL/MariaDB server")
    private String serverAddress;
}
