package com.gmail.nossr50.config.hocon.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionMaxPoolSize {

    /* DEFAULT VALUES */
    private static final int MISC_DEFAULT = 10;
    private static final int LOAD_DEFAULT = 20;
    private static final int SAVE_DEFAULT = 20;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Misc-Pool", comment = "Default value: "+MISC_DEFAULT)
    private int misc = 10;

    @Setting(value = "Load-Pool", comment = "Default value: "+LOAD_DEFAULT)
    private int load = 20;

    @Setting(value = "Save-Pool", comment = "Default value: "+SAVE_DEFAULT)
    private int save = 20;

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
