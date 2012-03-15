package com.gmail.nossr50;

import org.bukkit.Material;

import com.gmail.nossr50.config.LoadProperties;

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
        case JACK_O_LANTERN:
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

        if (Material.getMaterial(LoadProperties.anvilID).equals(material)) {
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
}
