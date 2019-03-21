package com.gmail.nossr50.config.hocon.skills.repair.repairmastery;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairMastery {

    @Setting(value = "Settings")
    private RepairMasterySettings settings = new RepairMasterySettings();

    public RepairMasterySettings getSettings() {
        return settings;
    }
}