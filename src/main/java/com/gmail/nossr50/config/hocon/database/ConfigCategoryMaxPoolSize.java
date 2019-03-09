package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCategoryMaxPoolSize {
    @Setting(value = "Misc")
    private int misc = 10;

    @Setting(value = "Load")
    private int load = 20;

    @Setting(value = "Save")
    private int save = 20;
}
