package com.gmail.nossr50.util;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.CropState;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.NetherWarts;

import com.gmail.nossr50.skills.repair.Repair;

public final class BlockUtils {
    private BlockUtils() {}

    /**
     * Checks to see if a given block awards XP.
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block awards XP, false otherwise
     */
    public static boolean shouldBeWatched(BlockState blockState) {
        return affectedByGigaDrillBreaker(blockState) || affectedByGreenTerra(blockState) || affectedBySuperBreaker(blockState) || isLog(blockState);
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
            case DROPPER:
            case HOPPER:
            case TRAPPED_CHEST:
                return false;

            default:
                return !isMcMMOAnvil(blockState) && !ModUtils.isCustomAbilityBlock(blockState);
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
            case QUARTZ_ORE:
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
                return ((NetherWarts) blockState.getData()).getState() == NetherWartsState.RIPE;

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
            case ENDER_STONE:
            case GLOWSTONE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case SANDSTONE:
            case STONE:
                return true;

            default:
                return isOre(blockState) || ModUtils.isCustomMiningBlock(blockState);
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
            case SNOW:
            case SNOW_BLOCK:
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
        return isLog(blockState) || isLeaves(blockState);
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

    /**
     * Determine if a given block is an mcMMO anvil
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is an mcMMO anvil, false otherwise
     */
    public static boolean isMcMMOAnvil(BlockState blockState) {
        int blockId = blockState.getTypeId();

        return blockId == Repair.repairAnvilId || blockId == Repair.salvageAnvilId;
    }

    /**
     * Get a HashSet containing every transparent block
     *
     * @return HashSet with the IDs of every transparent block
     */
    public static HashSet<Byte> getTransparentBlocks() {
        return new HashSet<Byte>(Arrays.asList((byte) 0, (byte) 6, (byte) 18, (byte) 20, (byte) 27, (byte) 28, (byte) 31, (byte) 32, (byte) 32, (byte) 34, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 55, (byte) 59, (byte) 63, (byte) 64, (byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 81, (byte) 83, (byte) 85, (byte) 92, (byte) 93, (byte) 94, (byte) 96, (byte) 101, (byte) 102, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 111, (byte) 113, (byte) 114, (byte) 115, (byte) 119, (byte) 126, (byte) 128, (byte) 131, (byte) 132, (byte) 134, (byte) 135, (byte) 136, (byte) 139, (byte) 141, (byte) 142, (byte) 143, (byte) 145, (byte) 147, (byte) 148, (byte) 149, (byte) 150, (byte) 151, (byte) 156, (byte) 157, (byte) 171));
    }
}
