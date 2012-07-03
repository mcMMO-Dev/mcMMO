package com.gmail.nossr50.util;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.mods.CustomBlocksConfig;

public class BlockChecks {
    private static Config configInstance = Config.getInstance();
    private static boolean customBlocksEnabled = configInstance.getBlockModsEnabled();

    /**
     * Checks to see if a block type awards XP.
     *
     * @param block Block to check
     * @return true if the block type awards XP, false otherwise
     */
    public static boolean shouldBeWatched(Block block) {
        switch (block.getType()) {
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
            if (customBlocksEnabled && CustomBlocksConfig.getInstance().customItems.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Check if a block should allow for the activation of abilities.
     *
     * @param block Block to check
     * @return true if the block should allow ability activation, false otherwise
     */
    public static boolean abilityBlockCheck(Block block) {
        if (customBlocksEnabled && CustomBlocksConfig.getInstance().customAbilityBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            return false;
        }

        switch (block.getType()) {
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
            if (block.getTypeId() == Config.getInstance().getRepairAnvilId()) {
                return false;
            }
            else {
                return true;
            }
        }
    }

    /**
     * Check if a block type is an ore.
     *
     * @param block Block to check
     * @return true if the Block is an ore, false otherwise
     */
    public static boolean isOre(Block block) {
        switch (block.getType()) {
        case COAL_ORE:
        case DIAMOND_ORE:
        case GLOWING_REDSTONE_ORE:
        case GOLD_ORE:
        case IRON_ORE:
        case LAPIS_ORE:
        case REDSTONE_ORE:
            return true;

        default:
            if (customBlocksEnabled && ModChecks.isCustomOreBlock(block)) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Check if a block can be made mossy.
     *
     * @param block The block to check
     * @return true if the block can be made mossy, false otherwise
     */
    public static boolean makeMossy(Block block) {
        switch (block.getType()) {
        case COBBLESTONE:
        case DIRT:
            return true;
        case SMOOTH_BRICK:
            if (block.getData() == 0) {
                return true;
            }

        default:
            return false;
        }
    }

    /**
     * Check if a block is affected by Herbalism abilities.
     *
     * @param block Block to check
     * @return true if the block is affected, false otherwise
     */
    public static boolean canBeGreenTerra(Block block){
        switch (block.getType()) {
        case BROWN_MUSHROOM:
        case CACTUS:
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

        case CROPS:
            if (block.getData() == CropState.RIPE.getData()) {
                return true;
            }
            else {
                return false;
            }

        default:
            if (customBlocksEnabled && CustomBlocksConfig.getInstance().customHerbalismBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Check to see if a block is broken by Super Breaker.
     *
     * @param block Block to check
     * @return true if the block would be broken by Super Breaker, false otherwise
     */
    public static Boolean canBeSuperBroken(Block block) {
        switch (block.getType()) {
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
            if (customBlocksEnabled && CustomBlocksConfig.getInstance().customMiningBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Check to see if a block can be broken by Giga Drill Breaker.
     *
     * @param block Block to check
     * @return true if the block can be broken by Giga Drill Breaker, false otherwise
     */
    public static boolean canBeGigaDrillBroken(Block block) {
        switch (block.getType()) {
        case CLAY:
        case DIRT:
        case GRASS:
        case GRAVEL:
        case MYCEL:
        case SAND:
        case SOUL_SAND:
            return true;

        default:
            if (customBlocksEnabled && CustomBlocksConfig.getInstance().customExcavationBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Checks if the block is affected by Tree Feller.
     *
     * @param block Block to check
     * @return true if the block is affected by Tree Feller, false otherwise
     */
    public static boolean treeFellerCompatible(Block block) {
        switch (block.getType()) {
        case LOG:
        case LEAVES:
        case AIR:
            return true;

        default:
            if (customBlocksEnabled && CustomBlocksConfig.getInstance().customWoodcuttingBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public static boolean isLog (Block block){
        if (block.getType().equals(Material.LOG) || (customBlocksEnabled && ModChecks.isCustomLogBlock(block))) {
            return true;
        }
        else {
            return false;
        }
    }
}
