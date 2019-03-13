package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionMaxConnections {

    @Setting(value = "Misc_Connection_Limit")
    private int misc = 30;

    @Setting(value = "Load_Connection_Limit")
    private int load = 30;

    @Setting(value = "Save_Connection_Limit")
    private int save = 30;

    /*
     * GETTER BOILERPLATE
     */

    public int getMisc() {
        return misc;
    }

    public int getLoad() {
        return load;
    }

    public int getSave() {
        return save;
    }
}
