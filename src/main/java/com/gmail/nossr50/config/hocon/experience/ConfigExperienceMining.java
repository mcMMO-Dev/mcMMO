package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceMining {

    private static final HashMap<String, Integer> MINING_EXPERIENCE_DEFAULT;

    static {
        MINING_EXPERIENCE_DEFAULT = new HashMap<>();

        /* UNDER THE SEA */
        MINING_EXPERIENCE_DEFAULT.put(Material.TUBE_CORAL_BLOCK.getKey().toString(), 75);
        MINING_EXPERIENCE_DEFAULT.put(Material.BRAIN_CORAL_BLOCK.getKey().toString(), 85);
        MINING_EXPERIENCE_DEFAULT.put(Material.BUBBLE_CORAL_BLOCK.getKey().toString(), 70);
        MINING_EXPERIENCE_DEFAULT.put(Material.FIRE_CORAL_BLOCK.getKey().toString(), 90);
        MINING_EXPERIENCE_DEFAULT.put(Material.HORN_CORAL_BLOCK.getKey().toString(), 125);
        MINING_EXPERIENCE_DEFAULT.put(Material.PRISMARINE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.PRISMARINE_BRICK_SLAB.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.PRISMARINE_BRICK_STAIRS.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.PRISMARINE_BRICKS.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.PRISMARINE_SLAB.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.PRISMARINE_STAIRS.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.DARK_PRISMARINE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.DARK_PRISMARINE_SLAB.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.DARK_PRISMARINE_STAIRS.getKey().toString(), 30);

        /* ORE */
        MINING_EXPERIENCE_DEFAULT.put(Material.COAL_ORE.getKey().toString(), 100);
        MINING_EXPERIENCE_DEFAULT.put(Material.DIAMOND_ORE.getKey().toString(), 750);
        MINING_EXPERIENCE_DEFAULT.put(Material.EMERALD_ORE.getKey().toString(), 1000);
        MINING_EXPERIENCE_DEFAULT.put(Material.GOLD_ORE.getKey().toString(), 350);
        MINING_EXPERIENCE_DEFAULT.put(Material.IRON_ORE.getKey().toString(), 250);
        MINING_EXPERIENCE_DEFAULT.put(Material.LAPIS_ORE.getKey().toString(), 400);
        MINING_EXPERIENCE_DEFAULT.put(Material.NETHER_QUARTZ_ORE.getKey().toString(), 100);
        MINING_EXPERIENCE_DEFAULT.put(Material.REDSTONE_ORE.getKey().toString(), 150);

        /* HELL RELATED */

        MINING_EXPERIENCE_DEFAULT.put(Material.END_STONE_BRICKS.getKey().toString(), 200);
        MINING_EXPERIENCE_DEFAULT.put(Material.NETHER_BRICK.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.GLOWSTONE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.NETHERRACK.getKey().toString(), 30);

        /* BADLANDS BIOME */
        MINING_EXPERIENCE_DEFAULT.put(Material.TERRACOTTA.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.BLACK_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.BLUE_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.BROWN_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.CYAN_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.GRAY_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.GREEN_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.LIGHT_BLUE_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.LIGHT_GRAY_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.LIME_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.MAGENTA_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.ORANGE_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.PINK_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.PURPLE_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.RED_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.WHITE_TERRACOTTA.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.YELLOW_TERRACOTTA.getKey().toString(), 50);

        /* COMMON OR BIOME INDEPENDENT MINERALS */
        MINING_EXPERIENCE_DEFAULT.put(Material.ANDESITE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.DIORITE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.GRANITE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.STONE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.MOSSY_COBBLESTONE.getKey().toString(), 30);

        /* MISC */
        MINING_EXPERIENCE_DEFAULT.put(Material.OBSIDIAN.getKey().toString(), 150);
        MINING_EXPERIENCE_DEFAULT.put(Material.MOSSY_COBBLESTONE.getKey().toString(), 30);

        /* SNOWY BIOME */
        MINING_EXPERIENCE_DEFAULT.put(Material.BLUE_ICE.getKey().toString(), 50);
        MINING_EXPERIENCE_DEFAULT.put(Material.PACKED_ICE.getKey().toString(), 100);

        /* DESERT BIOME */
        MINING_EXPERIENCE_DEFAULT.put(Material.SANDSTONE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.SANDSTONE_SLAB.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.SANDSTONE_STAIRS.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.SMOOTH_SANDSTONE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.RED_SANDSTONE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.RED_SANDSTONE_SLAB.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.RED_SANDSTONE_STAIRS.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.SMOOTH_RED_SANDSTONE.getKey().toString(), 30);

        /* END RELATED */
        MINING_EXPERIENCE_DEFAULT.put(Material.END_STONE.getKey().toString(), 30);
        MINING_EXPERIENCE_DEFAULT.put(Material.PURPUR_BLOCK.getKey().toString(), 200);
        MINING_EXPERIENCE_DEFAULT.put(Material.PURPUR_PILLAR.getKey().toString(), 250);
        MINING_EXPERIENCE_DEFAULT.put(Material.PURPUR_SLAB.getKey().toString(), 150);
        MINING_EXPERIENCE_DEFAULT.put(Material.PURPUR_STAIRS.getKey().toString(), 250);
    }

    @Setting(value = "Mining-Experience")
    private HashMap<String, Integer> miningExperienceMap;

}