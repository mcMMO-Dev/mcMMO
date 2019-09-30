package com.gmail.nossr50.config.skills.coreskills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCoreSkillsCombatToggles {

    @Setting(value = "PVE-Toggles", comment = "When set to false, offensive combat related abilities from a skill will not trigger in PVE." +
            "\nPVE = Player Versus Environment" +
            "\nEnvironment is stuff like Monsters, Animals")
    private ConfigCoreSkillCombatMap pve = new ConfigCoreSkillCombatMap();

    @Setting(value = "PVE-Toggles", comment = "When set to false, offensive combat related abilities from a skill will not trigger in PVP." +
            "\nPVP = Player Versus Player")
    private ConfigCoreSkillCombatMap pvp = new ConfigCoreSkillCombatMap();

    public ConfigCoreSkillCombatMap getPve() {
        return pve;
    }

    public ConfigCoreSkillCombatMap getPvp() {
        return pvp;
    }

    public boolean isPVEEnabled(PrimarySkillType primarySkillType) {
        return pve.getCombatToggleMap().get(primarySkillType);
    }

    public boolean isPVPEnabled(PrimarySkillType primarySkillType) {
        return pvp.getCombatToggleMap().get(primarySkillType);
    }
}
