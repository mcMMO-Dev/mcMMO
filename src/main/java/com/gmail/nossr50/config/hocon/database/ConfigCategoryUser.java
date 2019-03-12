package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCategoryUser {

    @Setting(value = "User_Name", comment = "The authorized user for your MySQL/MariaDB DB")
    private String username = "example_user_name";

    @Setting(value = "User_Password", comment = "The password for your authorized user")
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
