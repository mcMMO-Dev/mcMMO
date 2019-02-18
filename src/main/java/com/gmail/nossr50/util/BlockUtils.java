package com.gmail.nossr50.util;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.util.random.RandomChanceSkill;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashSet;

public final class BlockUtils {

    private BlockUtils() {}

    /**
     * Mark a block for giving bonus drops, double drops are used if triple is false
     * @param blockState target blockstate
     * @param triple marks the block to give triple drops
     */
    public static void markBlocksForBonusDrops(BlockState blockState, boolean triple)
    {
        if(triple)
            blockState.setMetadata(mcMMO.tripleDropKey, mcMMO.metadataValue);
        else
            blockState.setMetadata(mcMMO.doubleDropKey, mcMMO.metadataValue);
    }

    /**
     * Checks if a player successfully passed the double drop check
     * @param blockState the blockstate
     * @return true if the player succeeded in the check
     */
    public static boolean checkDoubleDrops(Player player, BlockState blockState, PrimarySkillType skillType, SubSkillType subSkillType)
    {
        if(Config.getInstance().getDoubleDropsEnabled(skillType, blockState.getType()) && Permissions.isSubSkillEnabled(player, subSkillType))
        {
            return RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkill(player, subSkillType, true));
        }

        return false;
    }

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
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
            case BREWING_STAND :
            case BOOKSHELF :
            case CAKE:
            case CHEST :
            case DISPENSER :
            case ENCHANTING_TABLE:
            case ENDER_CHEST :
            case OAK_FENCE_GATE:
            case ACACIA_FENCE_GATE :
            case DARK_OAK_FENCE_GATE :
            case SPRUCE_FENCE_GATE :
            case BIRCH_FENCE_GATE :
            case JUNGLE_FENCE_GATE :
            case FURNACE :
            case JUKEBOX :
            case LEVER :
            case NOTE_BLOCK :
            case STONE_BUTTON :
            case OAK_BUTTON:
            case BIRCH_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case SPRUCE_BUTTON:
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case WALL_SIGN :
            case CRAFTING_TABLE:
            case BEACON :
            case ANVIL :
            case DROPPER :
            case HOPPER :
            case TRAPPED_CHEST :
            case IRON_DOOR :
            case IRON_TRAPDOOR :
            case OAK_DOOR:
            case ACACIA_DOOR :
            case SPRUCE_DOOR :
            case BIRCH_DOOR :
            case JUNGLE_DOOR :
            case DARK_OAK_DOOR :
            case OAK_FENCE:
            case ACACIA_FENCE :
            case DARK_OAK_FENCE :
            case BIRCH_FENCE :
            case JUNGLE_FENCE :
            case SPRUCE_FENCE :
            case ARMOR_STAND :
            case BLACK_SHULKER_BOX :
            case BLUE_SHULKER_BOX :
            case BROWN_SHULKER_BOX :
            case CYAN_SHULKER_BOX :
            case GRAY_SHULKER_BOX :
            case GREEN_SHULKER_BOX :
            case LIGHT_BLUE_SHULKER_BOX :
            case LIME_SHULKER_BOX :
            case MAGENTA_SHULKER_BOX :
            case ORANGE_SHULKER_BOX :
            case PINK_SHULKER_BOX :
            case PURPLE_SHULKER_BOX :
            case RED_SHULKER_BOX :
            case LIGHT_GRAY_SHULKER_BOX:
            case WHITE_SHULKER_BOX :
            case YELLOW_SHULKER_BOX :
                return false;

            default :
                return !isMcMMOAnvil(blockState);
                //return !isMcMMOAnvil(blockState) && !mcMMO.getModManager().isCustomAbilityBlock(blockState);
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
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
            case BREWING_STAND :
            case BOOKSHELF :
            case CAKE:
            case CHEST :
            case DISPENSER :
            case ENCHANTING_TABLE:
            case ENDER_CHEST :
            case OAK_FENCE_GATE:
            case ACACIA_FENCE_GATE :
            case DARK_OAK_FENCE_GATE :
            case SPRUCE_FENCE_GATE :
            case BIRCH_FENCE_GATE :
            case JUNGLE_FENCE_GATE :
            case FURNACE :
            case JUKEBOX :
            case LEVER :
            case NOTE_BLOCK :
            case STONE_BUTTON :
            case OAK_BUTTON:
            case BIRCH_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case SPRUCE_BUTTON:
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case WALL_SIGN :
            case CRAFTING_TABLE:
            case BEACON :
            case ANVIL :
            case DROPPER :
            case HOPPER :
            case TRAPPED_CHEST :
            case IRON_DOOR :
            case IRON_TRAPDOOR :
            case OAK_DOOR:
            case ACACIA_DOOR :
            case SPRUCE_DOOR :
            case BIRCH_DOOR :
            case JUNGLE_DOOR :
            case DARK_OAK_DOOR :
            case OAK_FENCE:
            case ACACIA_FENCE :
            case DARK_OAK_FENCE :
            case BIRCH_FENCE :
            case JUNGLE_FENCE :
            case SPRUCE_FENCE :
            case ARMOR_STAND :
            case BLACK_SHULKER_BOX :
            case BLUE_SHULKER_BOX :
            case BROWN_SHULKER_BOX :
            case CYAN_SHULKER_BOX :
            case GRAY_SHULKER_BOX :
            case GREEN_SHULKER_BOX :
            case LIGHT_BLUE_SHULKER_BOX :
            case LIME_SHULKER_BOX :
            case MAGENTA_SHULKER_BOX :
            case ORANGE_SHULKER_BOX :
            case PINK_SHULKER_BOX :
            case PURPLE_SHULKER_BOX :
            case RED_SHULKER_BOX :
            case LIGHT_GRAY_SHULKER_BOX:
            case WHITE_SHULKER_BOX :
            case YELLOW_SHULKER_BOX :
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_ACACIA_WOOD:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_BIRCH_WOOD:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_WOOD:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_JUNGLE_WOOD:
            case STRIPPED_OAK_LOG:
            case STRIPPED_OAK_WOOD:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_SPRUCE_WOOD:
            case ACACIA_LOG:
            case ACACIA_WOOD:
            case BIRCH_LOG:
            case BIRCH_WOOD:
            case DARK_OAK_LOG:
            case DARK_OAK_WOOD:
            case JUNGLE_LOG:
            case JUNGLE_WOOD:
            case OAK_LOG:
            case OAK_WOOD:
            case SPRUCE_LOG:
            case SPRUCE_WOOD:
                return false;

            default :
                return !isMcMMOAnvil(blockState); // && !mcMMO.getModManager().isCustomAbilityBlock(blockState);
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
            case COBBLESTONE :
            case DIRT :
            case GRASS_PATH :
                return true;

            case STONE_BRICKS:
                return true;

            case COBBLESTONE_WALL:
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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.HERBALISM, blockState.getType())) {
            return true;
        }
        else
            return false;

        //return mcMMO.getModManager().isCustomHerbalismBlock(blockState);
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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.MINING, blockState.getType()))
            return true;

        return isOre(blockState); //|| mcMMO.getModManager().isCustomMiningBlock(blockState);
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
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.EXCAVATION, blockState.getType()))
            return true;
        else
            return false;
        //return mcMMO.getModManager().isCustomExcavationBlock(blockState);
    }

    /**
     * Check if a given block is a log
     *
     * @param blockState
     *            The {@link BlockState} of the block to check
     * @return true if the block is a log, false otherwise
     */
    public static boolean isLog(BlockState blockState) {
        if (ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.WOODCUTTING, blockState.getType()))
            return true;
        else
            return false;
        //return mcMMO.getModManager().isCustomLog(blockState);
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
            case OAK_LEAVES:
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case SPRUCE_LEAVES:
                return true;

            default :
                return false;
                //return mcMMO.getModManager().isCustomLeaf(blockState);
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
            case FARMLAND:
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
            case STONE_BRICKS:
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
