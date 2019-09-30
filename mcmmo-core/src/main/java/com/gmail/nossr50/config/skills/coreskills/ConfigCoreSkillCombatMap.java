package com.gmail.nossr50.config.skills.coreskills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigCoreSkillCombatMap {

    private static final HashMap<PrimarySkillType, Boolean> COMBAT_TOGGLE_DEFAULT;

    static {
        COMBAT_TOGGLE_DEFAULT = new HashMap<>();

        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {

            COMBAT_TOGGLE_DEFAULT.put(primarySkillType, true);
        }

    }

    @Setting(value = "Combat-Toggles")
    private HashMap<PrimarySkillType, Boolean> combatToggleMap = COMBAT_TOGGLE_DEFAULT;

    public HashMap<PrimarySkillType, Boolean> getCombatToggleMap() {
        return combatToggleMap;
    }
}
