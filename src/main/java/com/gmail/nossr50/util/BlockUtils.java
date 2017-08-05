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
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;

public final class BlockUtils {

    private BlockUtils() {}

    /**
     * Checks to see if a given block awards XP.
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block awards XP, false otherwise
     */
    public static boolean shouldBeWatched(BlockState blockState) {
        return affectedByGigaDrillBreaker(blockState) || affectedByGreenTerra(blockState) || affectedBySuperBreaker(blockState) || isLog(blockState);
    }

    /**
     * Check if a given block should allow for the activation of abilities
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should allow ability activation, false
     *         otherwise
     */
    public static boolean canActivateAbilities(BlockState blockState) {
        switch (blockState.getType()) {
            case BED_BLOCK :
            case BREWING_STAND :
            case BOOKSHELF :
            case BURNING_FURNACE :
            case CAKE_BLOCK :
            case CHEST :
            case DISPENSER :
            case ENCHANTMENT_TABLE :
            case ENDER_CHEST :
            case FENCE_GATE :
            case ACACIA_FENCE_GATE :
            case DARK_OAK_FENCE_GATE :
            case SPRUCE_FENCE_GATE :
            case BIRCH_FENCE_GATE :
            case JUNGLE_FENCE_GATE :
            case FURNACE :
            case IRON_DOOR_BLOCK :
            case JUKEBOX :
            case LEVER :
            case NOTE_BLOCK :
            case STONE_BUTTON :
            case WOOD_BUTTON :
            case TRAP_DOOR :
            case WALL_SIGN :
            case WOODEN_DOOR :
            case WORKBENCH :
            case BEACON :
            case ANVIL :
            case DROPPER :
            case HOPPER :
            case TRAPPED_CHEST :
            case IRON_DOOR :
            case IRON_TRAPDOOR :
            case ACACIA_DOOR :
            case SPRUCE_DOOR :
            case BIRCH_DOOR :
            case JUNGLE_DOOR :
            case DARK_OAK_DOOR :
            case FENCE :
            case ACACIA_FENCE :
            case DARK_OAK_FENCE :
            case BIRCH_FENCE :
            case JUNGLE_FENCE :
            case SPRUCE_FENCE :
            case ARMOR_STAND :
                return false;

            default :
                return !isMcMMOAnvil(blockState) && !mcMMO.getModManager().isCustomAbilityBlock(blockState);
        }
    }

    /**
     * Check if a given block is an ore
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block is an ore, false otherwise
     */
    public static boolean isOre(BlockState blockState) {
        return MaterialUtils.isOre(blockState.getData());
    }

    /**
     * Determine if a given block can be made mossy
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block can be made mossy, false otherwise
     */
    public static boolean canMakeMossy(BlockState blockState) {
        switch (blockState.getType()) {
            case COBBLESTONE :
            case DIRT :
            case GRASS_PATH :
                return true;

            case SMOOTH_BRICK :
                return ((SmoothBrick) blockState.getData()).getMaterial() == Material.STONE;

            case COBBLE_WALL :
                return blockState.getRawData() == (byte) 0x0;

            default :
                return false;
        }
    }

    /**
     * Determine if a given block should be affected by Green Terra
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should affected by Green Terra, false otherwise
     */
    public static boolean affectedByGreenTerra(BlockState blockState) {
        if (ExperienceConfig.getInstance().isSkillBlock(SkillType.HERBALISM, blockState.getData()))
            return true;

        return mcMMO.getModManager().isCustomHerbalismBlock(blockState);
    }

    /**
     * Determine if a given block should be affected by Super Breaker
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should affected by Super Breaker, false
     *         otherwise
     */
    public static Boolean affectedBySuperBreaker(BlockState blockState) {
        if (ExperienceConfig.getInstance().isSkillBlock(SkillType.MINING, blockState.getData()))
            return true;

        return isOre(blockState) || mcMMO.getModManager().isCustomMiningBlock(blockState);
    }

    /**
     * Determine if a given block should be affected by Giga Drill Breaker
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should affected by Giga Drill Breaker, false
     *         otherwise
     */
    public static boolean affectedByGigaDrillBreaker(BlockState blockState) {
        if (ExperienceConfig.getInstance().isSkillBlock(SkillType.EXCAVATION, blockState.getData()))
            return true;
        return mcMMO.getModManager().isCustomExcavationBlock(blockState);
    }

    /**
     * Check if a given block is a log
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block is a log, false otherwise
     */
    public static boolean isLog(BlockState blockState) {
        if (ExperienceConfig.getInstance().isSkillBlock(SkillType.WOODCUTTING, blockState.getData()))
            return true;
        return mcMMO.getModManager().isCustomLog(blockState);
    }

    /**
     * Check if a given block is a leaf
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block is a leaf, false otherwise
     */
    public static boolean isLeaves(BlockState blockState) {
        switch (blockState.getType()) {
            case LEAVES :
            case LEAVES_2 :
                return true;

            default :
                return mcMMO.getModManager().isCustomLeaf(blockState);
        }
    }

    /**
     * Determine if a given block should be affected by Flux Mining
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should affected by Flux Mining, false otherwise
     */
    public static boolean affectedByFluxMining(BlockState blockState) {
        switch (blockState.getType()) {
            case IRON_ORE :
            case GOLD_ORE :
                return true;

            default :
                return false;
        }
    }

    /**
     * Determine if a given block can activate Herbalism abilities
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block can be activate Herbalism abilities, false
     *         otherwise
     */
    public static boolean canActivateHerbalism(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT :
            case GRASS :
            case GRASS_PATH :
            case SOIL :
                return false;

            default :
                return true;
        }
    }

    /**
     * Determine if a given block should be affected by Block Cracker
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should affected by Block Cracker, false
     *         otherwise
     */
    public static boolean affectedByBlockCracker(BlockState blockState) {
        switch (blockState.getType()) {
            case SMOOTH_BRICK :
                return ((SmoothBrick) blockState.getData()).getMaterial() == Material.STONE;

            default :
                return false;
        }
    }

    /**
     * Determine if a given block can be made into Mycelium
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block can be made into Mycelium, false otherwise
     */
    public static boolean canMakeShroomy(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT :
            case GRASS :
            case GRASS_PATH :
                return true;

            default :
                return false;
        }
    }

    /**
     * Determine if a given block is an mcMMO anvil
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
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
    public static HashSet<Material> getTransparentBlocks() {
        HashSet<Material> transparentBlocks = new HashSet<Material>();

        for (Material material : Material.values()) {
            if (material.isTransparent()) {
                transparentBlocks.add(material);
            }
        }

        return transparentBlocks;
    }
}
