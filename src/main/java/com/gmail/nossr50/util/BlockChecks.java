package com.gmail.nossr50.util;

import org.bukkit.Material;

import com.gmail.nossr50.config.Config;

public class BlockChecks {

    /**
     * Checks to see if a block type awards XP.
     *
     * @param material The type of Block to check
     * @return true if the block type awards XP, false otherwise
     */
    public static boolean shouldBeWatched(Material material) {
        switch (material) {
        case BROWN_MUSHROOM:
        case CACTUS:
        case CLAY:
        case COAL_ORE:
        case DIAMOND_ORE:
        case DIRT:
        case ENDER_STONE:
        case GLOWING_REDSTONE_ORE:
        case GLOWSTONE:
        case GOLD_ORE:
        case GRASS:
        case GRAVEL:
        case IRON_ORE:
        case LAPIS_ORE:
        case LOG:
        case MELON_BLOCK:
        case MOSSY_COBBLESTONE:
        case MYCEL:
        case NETHERRACK:
        case OBSIDIAN:
        case PUMPKIN:
        case RED_MUSHROOM:
        case RED_ROSE:
        case REDSTONE_ORE:
        case SAND:
        case SANDSTONE:
        case SOUL_SAND:
        case STONE:
        case SUGAR_CANE_BLOCK:
        case VINE:
        case WATER_LILY:
        case YELLOW_FLOWER:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check if a block should allow for the activation of abilities.
     *
     * @param material The type of Block to check
     * @return true if the block should allow ability activation, false otherwise
     */
    public static boolean abilityBlockCheck(Material material) {
        switch (material) {
        case BED_BLOCK:
        case BREWING_STAND:
        case BOOKSHELF:
        case BURNING_FURNACE:
        case CAKE_BLOCK:
        case CHEST:
        case DISPENSER:
        case ENCHANTMENT_TABLE:
        case FENCE_GATE:
        case FURNACE:
        case IRON_DOOR_BLOCK:
        case JUKEBOX:
        case LEVER:
        case NOTE_BLOCK:
        case STONE_BUTTON:
        case TRAP_DOOR:
        case WALL_SIGN:
        case WOODEN_DOOR:
        case WORKBENCH:
            return false;

        default:
            break;
        }

        if (Material.getMaterial(Config.getInstance().getRepairAnvilId()).equals(material)) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Check if a block type is an ore.
     *
     * @param material The type of Block to check
     * @return true if the Block is an ore, false otherwise
     */
    public static boolean isOre(Material material) {
        switch (material) {
        case COAL_ORE:
        case DIAMOND_ORE:
        case GLOWING_REDSTONE_ORE:
        case GOLD_ORE:
        case IRON_ORE:
        case LAPIS_ORE:
        case REDSTONE_ORE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check if a block can be made mossy.
     *
     * @param material The type of Block to check
     * @return true if the block can be made mossy, false otherwise
     */
    public static boolean makeMossy(Material type) {
        switch (type) {
        case COBBLESTONE:
        case DIRT:
        case SMOOTH_BRICK:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check if a block is affected by Herbalism abilities.
     *
     * @param type The type of Block to check
     * @return true if the block is affected, false otherwise
     */
    public static boolean canBeGreenTerra(Material type){
        switch (type) {
        case BROWN_MUSHROOM:
        case CACTUS:
        case CROPS:
        case MELON_BLOCK:
        case NETHER_WARTS:
        case PUMPKIN:
        case RED_MUSHROOM:
        case RED_ROSE:
        case SUGAR_CANE_BLOCK:
        case VINE:
        case WATER_LILY:
        case YELLOW_FLOWER:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check to see if a block is broken by Super Breaker.
     *
     * @param type The type of Block to check
     * @return true if the block would be broken by Super Breaker, false otherwise
     */
    public static Boolean canBeSuperBroken(Material type) {
        switch (type) {
        case COAL_ORE:
        case DIAMOND_ORE:
        case ENDER_STONE:
        case GLOWING_REDSTONE_ORE:
        case GLOWSTONE:
        case GOLD_ORE:
        case IRON_ORE:
        case LAPIS_ORE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case OBSIDIAN:
        case REDSTONE_ORE:
        case SANDSTONE:
        case STONE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check to see if a block can be broken by Giga Drill Breaker.
     *
     * @param material The type of block to check
     * @return
     */
    public static boolean canBeGigaDrillBroken(Material type) {
        switch (type) {
        case CLAY:
        case DIRT:
        case GRASS:
        case GRAVEL:
        case MYCEL:
        case SAND:
        case SOUL_SAND:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the block is affected by Tree Feller.
     *
     * @param block Block to check
     * @return true if the block is affected by Tree Feller, false otherwise
     */
    public static boolean treeFellerCompatible(Material type) {
        switch (type) {
        case LOG:
        case LEAVES:
        case AIR:
            return true;

        default:
            return false;
        }
    }
}
