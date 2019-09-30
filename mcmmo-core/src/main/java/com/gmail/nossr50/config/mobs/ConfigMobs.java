package com.gmail.nossr50.config.mobs;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMobs {

    @Setting(value = "Combat", comment = "Settings related to combat with Mobs (Monsters / Animals / Etc)")
    private ConfigSectionCombat combat = new ConfigSectionCombat();

    public ConfigSectionCombat getCombat() {
        return combat;
    }
}