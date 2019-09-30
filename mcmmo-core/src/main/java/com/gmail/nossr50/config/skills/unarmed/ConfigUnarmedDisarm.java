package com.gmail.nossr50.config.skills.unarmed;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigUnarmedDisarm {

    private static final boolean PREVENT_ITEM_THEFT = false;

    @Setting(value = "Prevent-Item-Theft", comment = "Prevents weapons thrown to the ground by disarm from being picked up by anyone but the owner of said item." +
            "\nDefault value: "+PREVENT_ITEM_THEFT)
    private boolean preventItemTheft = PREVENT_ITEM_THEFT;

    public boolean isPreventItemTheft() {
        return preventItemTheft;
    }
}
