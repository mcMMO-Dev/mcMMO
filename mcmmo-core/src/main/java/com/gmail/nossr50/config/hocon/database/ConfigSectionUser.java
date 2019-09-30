package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionUser {

    /*
     * CONFIG NODES
     */

    @Setting(value = "User-Name", comment = "The authorized user for your MySQL/MariaDB DB" +
            "\nThis needs to be an existing user")
    private String username = "example_user_name";

    @Setting(value = "User-Password", comment = "The password for your authorized user")
    private String password = "example_user_password";

    /*
     * GETTER BOILERPLATE
     */

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
