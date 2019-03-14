package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionMaxConnections {

    /* DEFAULT VALUES */
    private static final int MISC_DEFAULT = 30;
    private static final int LOAD_DEFAULT = 30;
    private static final int SAVE_DEFAULT = 30;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Misc-Connection-Limit", comment = "Default value: "+MISC_DEFAULT)
    private int misc = 30;

    @Setting(value = "Load-Connection-Limit", comment = "Default value: "+LOAD_DEFAULT)
    private int load = 30;

    @Setting(value = "Save-Connection-Limit", comment = "Default value: "+SAVE_DEFAULT)
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
