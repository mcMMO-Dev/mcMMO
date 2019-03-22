package com.gmail.nossr50.config.hocon.skills.salvage;

import com.gmail.nossr50.config.hocon.skills.salvage.general.ConfigSalvageGeneral;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSalvage {

    @Setting(value = "Arcane-Salvage", comment = "Settings related to the Arcane Salvage Sub-Skill")
    ConfigArcaneSalvage configArcaneSalvage = new ConfigArcaneSalvage();

    @Setting(value = "General")
    ConfigSalvageGeneral general = new ConfigSalvageGeneral();

    public ConfigArcaneSalvage getConfigArcaneSalvage() {
        return configArcaneSalvage;
    }

    public ConfigSalvageGeneral getGeneral() {
        return general;
    }
}