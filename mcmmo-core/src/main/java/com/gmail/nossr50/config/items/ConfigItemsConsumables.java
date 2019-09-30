package com.gmail.nossr50.config.items;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigItemsConsumables {

    @Setting(value = "Chimaera-Wing", comment = "Settings relating to the Chimaera Wing." +
            "\nThe CW is an item in mcMMO that will teleport players to the bed they last rested at as long as they do not have any solid blocks above their head." +
            "\nThe CW is crafted using a custom recipe.")
    private ConfigItemsChimaeraWing chimaeraWing = new ConfigItemsChimaeraWing();

    public ConfigItemsChimaeraWing getChimaeraWing() {
        return chimaeraWing;
    }
}
