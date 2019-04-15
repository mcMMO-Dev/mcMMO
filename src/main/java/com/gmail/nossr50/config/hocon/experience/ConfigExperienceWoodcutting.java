package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceWoodcutting {

    private static final HashMap<String, Integer> WOODCUTTING_EXPERIENCE_DEFAULT;

    static {
        WOODCUTTING_EXPERIENCE_DEFAULT = new HashMap<>();

        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.OAK_LOG.getKey().toString(), 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.SPRUCE_LOG.getKey().toString(), 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.BIRCH_LOG.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.JUNGLE_LOG.getKey().toString(), 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.ACACIA_LOG.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.DARK_OAK_LOG.getKey().toString(), 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_OAK_LOG.getKey().toString(), 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_SPRUCE_LOG.getKey().toString(), 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_BIRCH_LOG.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_JUNGLE_LOG.getKey().toString(), 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_ACACIA_LOG.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_DARK_OAK_LOG.getKey().toString(), 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.OAK_WOOD.getKey().toString(), 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.SPRUCE_WOOD.getKey().toString(), 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.BIRCH_WOOD.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.JUNGLE_WOOD.getKey().toString(), 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.ACACIA_WOOD.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.DARK_OAK_WOOD.getKey().toString(), 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_OAK_WOOD.getKey().toString(), 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_SPRUCE_WOOD.getKey().toString(), 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_BIRCH_WOOD.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_JUNGLE_WOOD.getKey().toString(), 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_ACACIA_WOOD.getKey().toString(), 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.STRIPPED_DARK_OAK_WOOD.getKey().toString(), 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.RED_MUSHROOM_BLOCK.getKey().toString(), 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.BROWN_MUSHROOM_BLOCK.getKey().toString(), 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put(Material.MUSHROOM_STEM.getKey().toString(), 80);
    }

    @Setting(value = "Woodcutting-Experience")
    private HashMap<String, Integer> woodcuttingExperienceMap;

    public HashMap<String, Integer> getWoodcuttingExperienceMap() {
        return woodcuttingExperienceMap;
    }
}
