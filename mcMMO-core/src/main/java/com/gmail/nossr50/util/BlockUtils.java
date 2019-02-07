package com.gmail.nossr50.util;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

import java.util.HashSet;

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
            case Material.BLACK_BED:
            case Material.BLUE_BED:
            case Material.BROWN_BED:
            case Material.CYAN_BED:
            case Material.GRAY_BED:
            case Material.GREEN_BED:
            case Material.LIGHT_BLUE_BED:
            case Material.LIGHT_GRAY_BED:
            case Material.LIME_BED:
            case Material.MAGENTA_BED:
            case Material.ORANGE_BED:
            case Material.PINK_BED:
            case Material.PURPLE_BED:
            case Material.RED_BED:
            case Material.WHITE_BED:
            case Material.YELLOW_BED:
            case Material.BREWING_STAND :
            case Material.BOOKSHELF :
            case Material.CAKE:
            case Material.CHEST :
            case Material.DISPENSER :
            case Material.ENCHANTING_TABLE:
            case Material.ENDER_CHEST :
            case Material.OAK_FENCE_GATE:
            case Material.ACACIA_FENCE_GATE :
            case Material.DARK_OAK_FENCE_GATE :
            case Material.SPRUCE_FENCE_GATE :
            case Material.BIRCH_FENCE_GATE :
            case Material.JUNGLE_FENCE_GATE :
            case Material.FURNACE :
            case Material.JUKEBOX :
            case Material.LEVER :
            case Material.NOTE_BLOCK :
            case Material.STONE_BUTTON :
            case Material.OAK_BUTTON:
            case Material.BIRCH_BUTTON:
            case Material.ACACIA_BUTTON:
            case Material.DARK_OAK_BUTTON:
            case Material.JUNGLE_BUTTON:
            case Material.SPRUCE_BUTTON:
            case Material.ACACIA_TRAPDOOR:
            case Material.BIRCH_TRAPDOOR:
            case Material.DARK_OAK_TRAPDOOR:
            case Material.JUNGLE_TRAPDOOR:
            case Material.OAK_TRAPDOOR:
            case Material.SPRUCE_TRAPDOOR:
            case Material.WALL_SIGN :
            case Material.CRAFTING_TABLE:
            case Material.BEACON :
            case Material.ANVIL :
            case Material.DROPPER :
            case Material.HOPPER :
            case Material.TRAPPED_CHEST :
            case Material.IRON_DOOR :
            case Material.IRON_TRAPDOOR :
            case Material.OAK_DOOR:
            case Material.ACACIA_DOOR :
            case Material.SPRUCE_DOOR :
            case Material.BIRCH_DOOR :
            case Material.JUNGLE_DOOR :
            case Material.DARK_OAK_DOOR :
            case Material.OAK_FENCE:
            case Material.ACACIA_FENCE :
            case Material.DARK_OAK_FENCE :
            case Material.BIRCH_FENCE :
            case Material.JUNGLE_FENCE :
            case Material.SPRUCE_FENCE :
            case Material.ARMOR_STAND :
            case Material.BLACK_SHULKER_BOX :
            case Material.BLUE_SHULKER_BOX :
            case Material.BROWN_SHULKER_BOX :
            case Material.CYAN_SHULKER_BOX :
            case Material.GRAY_SHULKER_BOX :
            case Material.GREEN_SHULKER_BOX :
            case Material.LIGHT_BLUE_SHULKER_BOX :
            case Material.LIME_SHULKER_BOX :
            case Material.MAGENTA_SHULKER_BOX :
            case Material.ORANGE_SHULKER_BOX :
            case Material.PINK_SHULKER_BOX :
            case Material.PURPLE_SHULKER_BOX :
            case Material.RED_SHULKER_BOX :
            case Material.LIGHT_GRAY_SHULKER_BOX:
            case Material.WHITE_SHULKER_BOX :
            case Material.YELLOW_SHULKER_BOX :
                return false;

            default :
                return !isMcMMOAnvil(blockState) && !mcMMO.getModManager().isCustomAbilityBlock(blockState);
        }
    }

    /**
     * Check if a given block should allow for the activation of tools
     * Activating a tool is step 1 of a 2 step process for super ability activation
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block should allow ability activation, false
     *         otherwise
     */
    public static boolean canActivateTools(BlockState blockState) {
        switch (blockState.getType()) {
            case Material.BLACK_BED:
            case Material.BLUE_BED:
            case Material.BROWN_BED:
            case Material.CYAN_BED:
            case Material.GRAY_BED:
            case Material.GREEN_BED:
            case Material.LIGHT_BLUE_BED:
            case Material.LIGHT_GRAY_BED:
            case Material.LIME_BED:
            case Material.MAGENTA_BED:
            case Material.ORANGE_BED:
            case Material.PINK_BED:
            case Material.PURPLE_BED:
            case Material.RED_BED:
            case Material.WHITE_BED:
            case Material.YELLOW_BED:
            case Material.BREWING_STAND :
            case Material.BOOKSHELF :
            case Material.CAKE:
            case Material.CHEST :
            case Material.DISPENSER :
            case Material.ENCHANTING_TABLE:
            case Material.ENDER_CHEST :
            case Material.OAK_FENCE_GATE:
            case Material.ACACIA_FENCE_GATE :
            case Material.DARK_OAK_FENCE_GATE :
            case Material.SPRUCE_FENCE_GATE :
            case Material.BIRCH_FENCE_GATE :
            case Material.JUNGLE_FENCE_GATE :
            case Material.FURNACE :
            case Material.JUKEBOX :
            case Material.LEVER :
            case Material.NOTE_BLOCK :
            case Material.STONE_BUTTON :
            case Material.OAK_BUTTON:
            case Material.BIRCH_BUTTON:
            case Material.ACACIA_BUTTON:
            case Material.DARK_OAK_BUTTON:
            case Material.JUNGLE_BUTTON:
            case Material.SPRUCE_BUTTON:
            case Material.ACACIA_TRAPDOOR:
            case Material.BIRCH_TRAPDOOR:
            case Material.DARK_OAK_TRAPDOOR:
            case Material.JUNGLE_TRAPDOOR:
            case Material.OAK_TRAPDOOR:
            case Material.SPRUCE_TRAPDOOR:
            case Material.WALL_SIGN :
            case Material.CRAFTING_TABLE:
            case Material.BEACON :
            case Material.ANVIL :
            case Material.DROPPER :
            case Material.HOPPER :
            case Material.TRAPPED_CHEST :
            case Material.IRON_DOOR :
            case Material.IRON_TRAPDOOR :
            case Material.OAK_DOOR:
            case Material.ACACIA_DOOR :
            case Material.SPRUCE_DOOR :
            case Material.BIRCH_DOOR :
            case Material.JUNGLE_DOOR :
            case Material.DARK_OAK_DOOR :
            case Material.OAK_FENCE:
            case Material.ACACIA_FENCE :
            case Material.DARK_OAK_FENCE :
            case Material.BIRCH_FENCE :
            case Material.JUNGLE_FENCE :
            case Material.SPRUCE_FENCE :
            case Material.ARMOR_STAND :
            case Material.BLACK_SHULKER_BOX :
            case Material.BLUE_SHULKER_BOX :
            case Material.BROWN_SHULKER_BOX :
            case Material.CYAN_SHULKER_BOX :
            case Material.GRAY_SHULKER_BOX :
            case Material.GREEN_SHULKER_BOX :
            case Material.LIGHT_BLUE_SHULKER_BOX :
            case Material.LIME_SHULKER_BOX :
            case Material.MAGENTA_SHULKER_BOX :
            case Material.ORANGE_SHULKER_BOX :
            case Material.PINK_SHULKER_BOX :
            case Material.PURPLE_SHULKER_BOX :
            case Material.RED_SHULKER_BOX :
            case Material.LIGHT_GRAY_SHULKER_BOX:
            case Material.WHITE_SHULKER_BOX :
            case Material.YELLOW_SHULKER_BOX :
            case Material.STRIPPED_ACACIA_LOG:
            case Material.STRIPPED_ACACIA_WOOD:
            case Material.STRIPPED_BIRCH_LOG:
            case Material.STRIPPED_BIRCH_WOOD:
            case Material.STRIPPED_DARK_OAK_LOG:
            case Material.STRIPPED_DARK_OAK_WOOD:
            case Material.STRIPPED_JUNGLE_LOG:
            case Material.STRIPPED_JUNGLE_WOOD:
            case Material.STRIPPED_OAK_LOG:
            case Material.STRIPPED_OAK_WOOD:
            case Material.STRIPPED_SPRUCE_LOG:
            case Material.STRIPPED_SPRUCE_WOOD:
            case Material.ACACIA_LOG:
            case Material.ACACIA_WOOD:
            case Material.BIRCH_LOG:
            case Material.BIRCH_WOOD:
            case Material.DARK_OAK_LOG:
            case Material.DARK_OAK_WOOD:
            case Material.JUNGLE_LOG:
            case Material.JUNGLE_WOOD:
            case Material.OAK_LOG:
            case Material.OAK_WOOD:
            case Material.SPRUCE_LOG:
            case Material.SPRUCE_WOOD:
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
        return MaterialUtils.isOre(blockState.getType());
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
            case Material.COBBLESTONE :
            case Material.DIRT :
            case Material.GRASS_PATH :
                return true;

            case Material.STONE_BRICKS:
                return true;

            case Material.COBBLESTONE_WALL:
                return true;

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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.HERBALISM, blockState.getBlockData())) {
            return true;
        }

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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.MINING, blockState.getBlockData()))
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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.EXCAVATION, blockState.getBlockData()))
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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.WOODCUTTING, blockState.getBlockData()))
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
            case Material.OAK_LEAVES:
            case Material.ACACIA_LEAVES:
            case Material.BIRCH_LEAVES:
            case Material.DARK_OAK_LEAVES:
            case Material.JUNGLE_LEAVES:
            case Material.SPRUCE_LEAVES:
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
            case Material.IRON_ORE :
            case Material.GOLD_ORE :
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
            case Material.DIRT :
            case Material.GRASS :
            case Material.GRASS_PATH :
            case Material.FARMLAND:
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
            case Material.STONE_BRICKS:
                return true;

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
            case Material.DIRT :
            case Material.GRASS :
            case Material.GRASS_PATH :
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

        return type == Material.MOVING_PISTON || type == Material.AIR;
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

    public static boolean isFullyGrown(BlockState blockState) {
        BlockData data = blockState.getBlockData();
        if (data.getMaterial() == Material.CACTUS || data.getMaterial() == Material.SUGAR_CANE)
            return true;
        if (data instanceof Ageable)
        {
            Ageable ageable = (Ageable) data;
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return true;
    }
}
