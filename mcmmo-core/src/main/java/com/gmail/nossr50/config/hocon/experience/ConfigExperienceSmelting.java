package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceSmelting {
    private final static HashMap<String, Integer> SMELTING_EXPERIENCE_MAP_DEFAULT;

    static {
        SMELTING_EXPERIENCE_MAP_DEFAULT = new HashMap<>();

        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:coal_ore", 10);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:diamond_ore", 75);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:emerald_ore", 100);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:gold_ore", 35);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:iron_ore", 25);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:lapis_ore", 40);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:nether_quartz_ore", 25);
        SMELTING_EXPERIENCE_MAP_DEFAULT.put("minecraft:redstone_ore", 15);
    }

    @Setting(value = "Smelting-XP-Values")
    private HashMap<String, Integer> smeltingExperienceMap = SMELTING_EXPERIENCE_MAP_DEFAULT;

    public HashMap<String, Integer> getSmeltingExperienceMap() {
        return smeltingExperienceMap;
    }
}