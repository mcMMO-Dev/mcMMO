package com.gmail.nossr50.util;

import java.util.HashSet;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;
import org.bukkit.material.SmoothBrick;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.util.temp.DualSupport;

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
            case IRON_DOOR:
                return false;

            default:
                return DualSupport.canActivateAbilities(blockState) && !isMcMMOAnvil(blockState) && !mcMMO.getModManager().isCustomAbilityBlock(blockState);
        }
    }

    /**
     * Check if a given block is an ore
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is an ore, false otherwise
     */
    public static boolean isOre(BlockState blockState) {
        return MaterialUtils.isOre(blockState.getData());
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
                return ((SmoothBrick) blockState.getData()).getMaterial() == Material.STONE;

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
            case DOUBLE_PLANT:
            case MELON_BLOCK:
            case LONG_GRASS:
            case PUMPKIN:
            case RED_MUSHROOM:
            case RED_ROSE:
            case SUGAR_CANE_BLOCK:
            case VINE:
            case WATER_LILY:
            case YELLOW_FLOWER:
                return true;

            case CARROT:
            case POTATO:
                return blockState.getRawData() == CropState.RIPE.getData();

            case CROPS:
                return ((Crops) blockState.getData()).getState() == CropState.RIPE;

            case NETHER_WARTS:
                return ((NetherWarts) blockState.getData()).getState() == NetherWartsState.RIPE;

            case COCOA:
                return ((CocoaPlant) blockState.getData()).getSize() == CocoaPlantSize.LARGE;

            default:
                return mcMMO.getModManager().isCustomHerbalismBlock(blockState);
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
            case HARD_CLAY:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case PACKED_ICE:
            case SANDSTONE:
            case STAINED_CLAY:
            case STONE:
                return true;

            default:
                return DualSupport.affectedBySuperBreaker(blockState) || isOre(blockState) || mcMMO.getModManager().isCustomMiningBlock(blockState);
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
                return mcMMO.getModManager().isCustomExcavationBlock(blockState);
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
            case LOG_2:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                return true;

            default:
                return mcMMO.getModManager().isCustomLog(blockState);
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
            case LEAVES_2:
                return true;

            default:
                return mcMMO.getModManager().isCustomLeaf(blockState);
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
                return ((SmoothBrick) blockState.getData()).getMaterial() == Material.STONE;

            default:
                return false;
        }
    }

    /**
     * Determine if a given block can be made into Mycelium
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be made into Mycelium, false otherwise
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
        Material type = blockState.getType();

        return type == Repair.anvilMaterial || type == Salvage.anvilMaterial;
    }

    public static boolean isPistonPiece(BlockState blockState) {
        Material type = blockState.getType();

        return type == Material.PISTON_MOVING_PIECE || type == Material.AIR;
    }

    /**
     * Get a HashSet containing every transparent block
     *
     * @return HashSet with the IDs of every transparent block
     */
    public static HashSet<Byte> getTransparentBlocks() {
        HashSet<Byte> transparentBlocks = new HashSet<Byte>();

        for (Material material : Material.values()) {
            if (material.isTransparent()) {
                transparentBlocks.add((byte) material.getId());
            }
        }

        return transparentBlocks;
    }
}
