package com.gmail.nossr50.config.hocon.skills.archery;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArcheryLimitBreak {

    private static final boolean DEFAULT_PVE = false;

    @Setting(value = "PVE", comment = "If true, the bonus damage from Limit Break will apply to PVE in addition to PVP." +
            "\nDefault value: "+DEFAULT_PVE)
    private boolean PVE = DEFAULT_PVE;

    public boolean isEnabledForPVE() {
        return PVE;
    }

}
