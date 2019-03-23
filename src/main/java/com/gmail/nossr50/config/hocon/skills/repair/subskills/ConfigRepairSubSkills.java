package com.gmail.nossr50.config.hocon.skills.repair.subskills;

import com.gmail.nossr50.config.hocon.skills.repair.ConfigRepairArcaneForging;
import com.gmail.nossr50.config.hocon.skills.repair.ConfigRepairSuperRepair;
import com.gmail.nossr50.config.hocon.skills.repair.repairmastery.ConfigRepairMastery;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairSubSkills {

    @Setting(value = "Repair-Mastery", comment = "Settings related to the repair mastery subskill")
    private ConfigRepairMastery repairMastery = new ConfigRepairMastery();

    @Setting(value = "Super-Repair", comment = "Settings related to the super repair subskill")
    private ConfigRepairSuperRepair superRepair = new ConfigRepairSuperRepair();

    @Setting(value = "Arcane-Forging", comment = "Settings related to the arcane forging subskill")
    private ConfigRepairArcaneForging arcaneForging = new ConfigRepairArcaneForging();

    public ConfigRepairMastery getRepairMastery() {
        return repairMastery;
    }

    public ConfigRepairSuperRepair getSuperRepair() {
        return superRepair;
    }

    public ConfigRepairArcaneForging getArcaneForging() {
        return arcaneForging;
    }
}