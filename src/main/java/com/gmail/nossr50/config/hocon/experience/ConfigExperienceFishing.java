package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceFishing {
    private final static HashMap<String, Integer> FISHING_DEFAULT_XP_MAP;
    public static final int SHAKE_XP_DEFAULT = 50;

    static {
        FISHING_DEFAULT_XP_MAP = new HashMap<>();

        FISHING_DEFAULT_XP_MAP.put(Material.COD.getKey().toString(), 100);
        FISHING_DEFAULT_XP_MAP.put(Material.SALMON.getKey().toString(), 600);
        FISHING_DEFAULT_XP_MAP.put(Material.TROPICAL_FISH.getKey().toString(), 10000);
        FISHING_DEFAULT_XP_MAP.put(Material.PUFFERFISH.getKey().toString(), 2400);
    }

    @Setting(value = "Fishing-Experience-Values", comment = "Experience values for Fishing.")
    HashMap<String, Integer> fishingXPMap = FISHING_DEFAULT_XP_MAP;

    @Setting(value = "Shake", comment = "XP Granted when shaking a mob")
    private int shakeXP = SHAKE_XP_DEFAULT;

    public HashMap<String, Integer> getFishingXPMap() {
        return fishingXPMap;
    }

    public int getShakeXP() {
        return shakeXP;
    }
}