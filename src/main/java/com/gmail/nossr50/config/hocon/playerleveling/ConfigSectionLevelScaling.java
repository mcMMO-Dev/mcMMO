package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionLevelScaling {

    /* DEFAULT VALUES */
    public static final boolean USE_RETRO_MODE_DEFAULT = false;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Use-Retro-Mode",
            comment = "Enables 1-1000 Level Scaling" +
                    "\nIf set to false, Standard Scaling is used instead (1-100 Level Scaling)")
    private boolean useRetroMode = USE_RETRO_MODE_DEFAULT;



    /*
     * GETTER BOILERPLATE
     */

    public boolean isRetroModeEnabled() {
        return useRetroMode;
    }
}
