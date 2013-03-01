package com.gmail.nossr50.util;

import org.bukkit.CropState;
import org.bukkit.block.BlockState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

import com.gmail.nossr50.config.Config;

public final class BlockUtils {
    private BlockUtils() {}

    /**
     * Checks to see if a given block awards XP.
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block awards XP, false otherwise
     */
    public static boolean shouldBeWatched(BlockState blockState) {
        switch (blockState.getType()) {
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
            case COCOA:
            case EMERALD_ORE:
            case CARROT:
            case POTATO:
                return true;

            default:
                return ModUtils.getCustomBlock(blockState) != null;
        }
    }

    /**
     * Check if a given block should allow for the activation of abilities
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should allow ability activation, false otherwise
     */
    public static boolean canActivateAbilities(BlockState blockState) {
        switch (blockState.getType()) {
            case BED_BLOCK:
            case BREWING_STAND:
            case BOOKSHELF:
            case BURNING_FURNACE:
            case CAKE_BLOCK:
            case CHEST:
            case DISPENSER:
            case ENCHANTMENT_TABLE:
            case ENDER_CHEST:
            case FENCE_GATE:
            case FURNACE:
            case IRON_DOOR_BLOCK:
            case JUKEBOX:
            case LEVER:
            case NOTE_BLOCK:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case TRAP_DOOR:
            case WALL_SIGN:
            case WOODEN_DOOR:
            case WORKBENCH:
            case BEACON:
            case ANVIL:
                return false;

            default:
                int blockId = blockState.getTypeId();

                if (blockId == Config.getInstance().getRepairAnvilId() || blockId == Config.getInstance().getSalvageAnvilId()) {
                    return false;
                }

                if (ModUtils.isCustomAbilityBlock(blockState)) {
                    return false;
                }

                return true;
        }
    }

    /**
     * Check if a given block is an ore
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is an ore, false otherwise
     */
    public static boolean isOre(BlockState blockState) {
        switch (blockState.getType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case GLOWING_REDSTONE_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case EMERALD_ORE:
                return true;

            default:
                return ModUtils.isCustomOreBlock(blockState);
        }
    }

    /**
     * Determine if a given block can be made mossy
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be made mossy, false otherwise
     */
    public static boolean canMakeMossy(BlockState blockState) {
        switch (blockState.getType()) {
            case COBBLESTONE:
            case DIRT:
                return true;

            case SMOOTH_BRICK:
            case COBBLE_WALL:
                return blockState.getRawData() == (byte) 0x0;

            default:
                return false;
        }
    }

    /**
     * Determine if a given block should be affected by Green Terra
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Green Terra, false otherwise
     */
    public static boolean affectedByGreenTerra(BlockState blockState) {
        switch (blockState.getType()) {
            case BROWN_MUSHROOM:
            case CACTUS:
            case MELON_BLOCK:
            case PUMPKIN:
            case RED_MUSHROOM:
            case RED_ROSE:
            case SUGAR_CANE_BLOCK:
            case VINE:
            case WATER_LILY:
            case YELLOW_FLOWER:
                return true;

            case CARROT:
            case CROPS:
            case POTATO:
                return blockState.getRawData() == CropState.RIPE.getData();

            case NETHER_WARTS:
                return blockState.getRawData() == (byte) 0x3;

            case COCOA:
                return ((CocoaPlant) blockState.getData()).getSize() == CocoaPlantSize.LARGE;

            default:
                return ModUtils.isCustomHerbalismBlock(blockState);
        }
    }

    /**
     * Determine if a given block should be affected by Super Breaker
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Super Breaker, false otherwise
     */
    public static Boolean affectedBySuperBreaker(BlockState blockState) {
        switch (blockState.getType()) {
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
            case EMERALD_ORE:
                return true;

            default:
                return ModUtils.isCustomMiningBlock(blockState);
        }
    }

    /**
     * Determine if a given block should be affected by Giga Drill Breaker
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Giga Drill Breaker, false otherwise
     */
    public static boolean affectedByGigaDrillBreaker(BlockState blockState) {
        switch (blockState.getType()) {
            case CLAY:
            case DIRT:
            case GRASS:
            case GRAVEL:
            case MYCEL:
            case SAND:
            case SOUL_SAND:
                return true;

            default:
                return ModUtils.isCustomExcavationBlock(blockState);
        }
    }

    /**
     * Determine if a given block should be affected by Tree Feller
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Tree Feller, false otherwise
     */
    public static boolean affectedByTreeFeller(BlockState blockState) {
        switch (blockState.getType()) {
            case LOG:
            case LEAVES:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                return true;

            default:
                return ModUtils.isCustomWoodcuttingBlock(blockState);
        }
    }

    /**
     * Check if a given block is a log
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is a log, false otherwise
     */
    public static boolean isLog(BlockState blockState) {
        switch (blockState.getType()) {
            case LOG:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                return true;

            default:
                return ModUtils.isCustomLogBlock(blockState);
        }
    }

    /**
     * Check if a given block is a leaf
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is a leaf, false otherwise
     */
    public static boolean isLeaves(BlockState blockState) {
        switch (blockState.getType()) {
            case LEAVES:
                return true;

            default:
                return ModUtils.isCustomLeafBlock(blockState);
        }
    }

    /**
     * Determine if a given block should be affected by Flux Mining
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Flux Mining, false otherwise
     */
    public static boolean affectedByFluxMining(BlockState blockState) {
        switch (blockState.getType()) {
            case IRON_ORE:
            case GOLD_ORE:
                return true;

            default:
                return false;
        }
    }

    /**
     * Determine if a given block can activate Herbalism abilities
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be activate Herbalism abilities, false otherwise
     */
    public static boolean canActivateHerbalism(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT:
            case GRASS:
            case SOIL:
                return false;

            default:
                return true;
        }
    }

    /**
     * Determine if a given block should be affected by Block Cracker
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Block Cracker, false otherwise
     */
    public static boolean affectedByBlockCracker(BlockState blockState) {
        switch (blockState.getType()) {
            case SMOOTH_BRICK:
                return blockState.getRawData() == (byte) 0x0;

            default:
                return false;
        }
    }

    /**
     * Determine if a given block can be made into Mycelium
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be made in Mycelium, false otherwise
     */
    public static boolean canMakeShroomy(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT:
            case GRASS:
                return true;

            default:
                return false;
        }
    }
}
