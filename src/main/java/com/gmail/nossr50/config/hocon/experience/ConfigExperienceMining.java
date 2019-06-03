package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceMining {

    private static final HashMap<String, Integer> MINING_EXPERIENCE_DEFAULT;

    static {
        MINING_EXPERIENCE_DEFAULT = new HashMap<>();

        /* UNDER THE SEA */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:tube_coral_block", 75);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:brain_coral_block", 85);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:bubble_coral_block", 70);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:fire_coral_block", 90);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:horn_coral_block", 125);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:prismarine", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:prismarine_brick_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:prismarine_brick_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:prismarine_bricks", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:prismarine_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:prismarine_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:dark_prismarine", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:dark_prismarine_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:dark_prismarine_stairs", 30);

        /* ORE */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:coal_ore", 100);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:diamond_ore", 750);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:emerald_ore", 1000);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:gold_ore", 350);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:iron_ore", 250);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:lapis_ore", 400);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:nether_quartz_ore", 100);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:redstone_ore", 150);

        /* HELL RELATED */

        MINING_EXPERIENCE_DEFAULT.put("minecraft:nether_brick", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:glowstone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:netherrack", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:magma_block", 30);

        /* BADLANDS BIOME */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:terracotta", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:black_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:blue_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:brown_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:cyan_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:gray_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:green_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:light_blue_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:light_gray_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:lime_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:magenta_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:orange_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:pink_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:purple_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:red_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:white_terracotta", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:yellow_terracotta", 50);

        /* COMMON OR BIOME INDEPENDENT MINERALS */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:andesite", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:diorite", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:granite", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:stone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_cobblestone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_cobblestone_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_cobblestone_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_cobblestone_wall", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_stone_bricks", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_stone_brick_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_stone_brick_wall", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:mossy_stone_brick_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:obsidian", 150);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:stone_bricks", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:stone_brick_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:stone_brick_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:stone_brick_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:cracked_stone_bricks", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:infested_cracked_stone_bricks", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:chiseled_quartz_block", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:chiseled_red_sandstone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:chiseled_sandstone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:chiseled_stone_bricks", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:infested_chiseled_stone_bricks", 30);

        /* SNOWY BIOME */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:blue_ice", 50);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:packed_ice", 100);

        /* DESERT BIOME */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:sandstone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:sandstone_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:sandstone_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:smooth_sandstone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:red_sandstone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:red_sandstone_slab", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:red_sandstone_stairs", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:smooth_red_sandstone", 30);

        /* END RELATED */
        MINING_EXPERIENCE_DEFAULT.put("minecraft:end_stone", 30);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:end_stone_bricks", 200);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:purpur_block", 200);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:purpur_pillar", 250);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:purpur_slab", 150);
        MINING_EXPERIENCE_DEFAULT.put("minecraft:purpur_stairs", 250);
    }

    @Setting(value = "Mining-Experience")
    private HashMap<String, Integer> miningExperienceMap = MINING_EXPERIENCE_DEFAULT;

    public HashMap<String, Integer> getMiningExperienceMap() {
        return miningExperienceMap;
    }
}