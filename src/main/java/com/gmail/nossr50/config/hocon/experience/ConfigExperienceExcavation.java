package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceExcavation {

    private final static HashMap<String, Integer> EXCAVATION_EXPERIENCE_DEFAULT;

    static {
        EXCAVATION_EXPERIENCE_DEFAULT = new HashMap<>();
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.CLAY.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.DIRT.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.COARSE_DIRT.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.PODZOL.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.GRASS_BLOCK.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.GRAVEL.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.MYCELIUM.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.SAND.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.RED_SAND.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.SNOW.getKey().toString(), 20);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.SNOW_BLOCK.getKey().toString(), 40);
        EXCAVATION_EXPERIENCE_DEFAULT.put(Material.SOUL_SAND.getKey().toString(), 40);
    }

    @Setting(value = "Excavation-Experience")
    private HashMap<String, Integer> excavationExperienceMap;

    public HashMap<String, Integer> getExcavationExperienceMap() {
        return excavationExperienceMap;
    }
}
