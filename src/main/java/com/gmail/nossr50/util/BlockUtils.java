package com.gmail.nossr50.util;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.util.random.RandomChanceSkill;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashSet;

public final class BlockUtils {

    private BlockUtils() {
    }

    /**
     * Mark a block for giving bonus drops, double drops are used if triple is false
     *
     * @param blockState target blockstate
     * @param triple     marks the block to give triple drops
     */
    public static void markDropsAsBonus(BlockState blockState, boolean triple) {
        if (triple)
            blockState.setMetadata(MetadataConstants.BONUS_DROPS_METAKEY, new BonusDropMeta(2, mcMMO.p));
        else
            blockState.setMetadata(MetadataConstants.BONUS_DROPS_METAKEY, new BonusDropMeta(1, mcMMO.p));
    }

    /**
     * Marks a block to drop extra copies of items
     *
     * @param blockState target blockstate
     * @param amount     amount of extra items to drop
     */
    public static void markDropsAsBonus(BlockState blockState, int amount) {
        blockState.setMetadata(MetadataConstants.BONUS_DROPS_METAKEY, new BonusDropMeta(amount, mcMMO.p));
    }

    /**
     * Checks if a player successfully passed the double drop check
     *
     * @param blockState the blockstate
     * @return true if the player succeeded in the check
     */
    public static boolean checkDoubleDrops(Player player, BlockState blockState, SubSkillType subSkillType) {
        if (mcMMO.getDynamicSettingsManager().isBonusDropsEnabled(blockState.getType()) && Permissions.isSubSkillEnabled(player, subSkillType)) {
            return RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkill(player, subSkillType, true));
        }

        return false;
    }

    /**
     * Checks to see if a given block awards XP.
     *
     * @param block The {@link Block} of the block to check
     * @return true if the block awards XP, false otherwise
     */
    public static boolean shouldBeWatched(Block block) {
        return affectedByGigaDrillBreaker(block.getType()) || affectedByGreenTerra(block.getType()) || affectedBySuperBreaker(block.getType()) || isLog(block.getType());
    }

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
     * Checks to see if a given block awards XP.
     *
     * @param material The {@link Material} of the block to check
     * @return true if the block awards XP, false otherwise
     */
    public static boolean shouldBeWatched(Material material) {
        return affectedByGigaDrillBreaker(material) || affectedByGreenTerra(material) || affectedBySuperBreaker(material) || isLog(material);
    }

    /**
     * Check if a given block should allow for the activation of abilities
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should allow ability activation, false
     * otherwise
     */
    public static boolean canActivateAbilities(BlockState blockState) {
        return !mcMMO.getMaterialMapStore().isAbilityActivationBlackListed(blockState.getType());
    }

    /**
     * Check if a given block should allow for the activation of tools
     * Activating a tool is step 1 of a 2 step process for super ability activation
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should allow ability activation, false
     * otherwise
     */
    public static boolean canActivateTools(BlockState blockState) {
        return !mcMMO.getMaterialMapStore().isToolActivationBlackListed(blockState.getType());
    }

    /**
     * Check if a given block is an ore
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is an ore, false otherwise
     */
    public static boolean isOre(BlockState blockState) {
        return MaterialUtils.isOre(blockState.getType());
    }

    /**
     * Determine if a given block can be made mossy
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be made mossy, false otherwise
     */
    public static boolean canMakeMossy(BlockState blockState) {
        return mcMMO.getMaterialMapStore().isMossyWhiteListed(blockState.getType());
    }

    /**
     * Determine if a given block should be affected by Green Terra
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Green Terra, false otherwise
     */
    public static boolean affectedByGreenTerra(BlockState blockState) {
        return mcMMO.getDynamicSettingsManager().getExperienceManager().hasHerbalismXp(blockState.getType());
    }

    /**
     * Determine if a given block should be affected by Green Terra
     *
     * @param material The {@link Material} of the block to check
     * @return true if the block should affected by Green Terra, false otherwise
     */
    public static boolean affectedByGreenTerra(Material material) {
        return mcMMO.getDynamicSettingsManager().getExperienceManager().hasHerbalismXp(material);
    }

    /**
     * Determine if a given block should be affected by Super Breaker
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Super Breaker, false
     * otherwise
     */
    public static Boolean affectedBySuperBreaker(BlockState blockState) {
        if (mcMMO.getDynamicSettingsManager().getExperienceManager().hasMiningXp(blockState.getType()))
            return true;

        return isMineable(blockState);
    }

    /**
     * Determine if a given block should be affected by Super Breaker
     *
     * @param material The {@link Material} of the block to check
     * @return true if the block should affected by Super Breaker, false
     * otherwise
     */
    public static Boolean affectedBySuperBreaker(Material material) {
        if (mcMMO.getDynamicSettingsManager().getExperienceManager().hasMiningXp(material))
            return true;

        return isMineable(material);
    }

    /**
     * Whether or not a block is gathered via Pickaxes
     *
     * @param material target blocks material
     * @return
     */
    public static boolean isMineable(Material material) {
        switch (material) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case END_STONE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case NETHER_QUARTZ_ORE:
            case REDSTONE_ORE:
            case ANDESITE:
            case DIORITE:
            case GRANITE:
            case STONE:
            case PRISMARINE:
            case DARK_PRISMARINE:
            case SANDSTONE:
            case NETHERRACK:
            case ICE:
            case PACKED_ICE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMineable(BlockState blockState) {
        return isMineable(blockState.getType());
    }

    /**
     * Determine if a given block should be affected by Giga Drill Breaker
     *
     * @param material The {@link Material} of the block to check
     * @return true if the block should affected by Giga Drill Breaker, false
     * otherwise
     */
    public static boolean affectedByGigaDrillBreaker(Material material) {
        if (mcMMO.getDynamicSettingsManager().getExperienceManager().hasExcavationXp(material))
            return true;

        return isDiggable(material);
    }

    /**
     * Determine if a given block should be affected by Giga Drill Breaker
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Giga Drill Breaker, false
     * otherwise
     */
    public static boolean affectedByGigaDrillBreaker(BlockState blockState) {
        if (mcMMO.getDynamicSettingsManager().getExperienceManager().hasExcavationXp(blockState.getType()))
            return true;

        return isDiggable(blockState);
    }

    /**
     * Returns true if a shovel is used for digging this block
     *
     * @param blockState target blockstate
     * @return true if a shovel is typically used for digging this block
     */
    @Deprecated
    public static boolean isDiggable(BlockState blockState) {
        return isDiggable(blockState.getType());
    }

    /**
     * Returns true if a shovel is used for digging this block
     *
     * @param material target blocks material
     * @return true if a shovel is typically used for digging this block
     */
    public static boolean isDiggable(Material material) {
        switch (material) {
            case CLAY:
            case FARMLAND:
            case GRASS_BLOCK:
            case GRASS_PATH:
            case GRAVEL:
            case MYCELIUM:
            case PODZOL:
            case COARSE_DIRT:
            case DIRT:
            case RED_SAND:
            case SAND:
            case SOUL_SAND:
            case SNOW:
            case SNOW_BLOCK:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if a given block is a log
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is a log, false otherwise
     */
    public static boolean isLog(BlockState blockState) {
        if (mcMMO.getDynamicSettingsManager().getExperienceManager().hasWoodcuttingXp(blockState.getType()))
            return true;

        return isLoggingRelated(blockState);
        //return mcMMO.getModManager().isCustomLog(blockState);
    }

    /**
     * Check if a given block is a log
     *
     * @param material The {@link Material} of the block to check
     * @return true if the block is a log, false otherwise
     */
    public static boolean isLog(Material material) {
        if (mcMMO.getDynamicSettingsManager().getExperienceManager().hasWoodcuttingXp(material))
            return true;

        return isLoggingRelated(material);
        //return mcMMO.getModManager().isCustomLog(blockState);
    }

    /**
     * Determines if this particular block is typically gathered using an Axe
     *
     * @param material target material
     * @return true if the block is gathered via axe
     */
    public static boolean isLoggingRelated(Material material) {
        switch (material) {
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
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
            case ACACIA_WOOD:
            case BIRCH_WOOD:
            case DARK_OAK_WOOD:
            case JUNGLE_WOOD:
            case OAK_WOOD:
            case SPRUCE_WOOD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determines if this particular block is typically gathered using an Axe
     *
     * @param blockState target blockstate
     * @return true if the block is gathered via axe
     */
    public static boolean isLoggingRelated(BlockState blockState) {
        return isLoggingRelated(blockState.getType());
    }

    /**
     * Check if a given block is a leaf
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is a leaf, false otherwise
     */
    public static boolean isLeaves(BlockState blockState) {
        return mcMMO.getMaterialMapStore().isLeavesWhiteListed(blockState.getType());
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
            //return mcMMO.getModManager().isCustomLeaf(blockState);
        }
    }

    /**
     * Determine if a given block can activate Herbalism abilities
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be activate Herbalism abilities, false
     * otherwise
     */
    public static boolean canActivateHerbalism(BlockState blockState) {
        return mcMMO.getMaterialMapStore().isHerbalismAbilityWhiteListed(blockState.getType());
    }

    /**
     * Determine if a given block should be affected by Block Cracker
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block should affected by Block Cracker, false
     * otherwise
     */
    public static boolean affectedByBlockCracker(BlockState blockState) {
        return mcMMO.getMaterialMapStore().isBlockCrackerWhiteListed(blockState.getType());
    }

    /**
     * Determine if a given block can be made into Mycelium
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block can be made into Mycelium, false otherwise
     */
    public static boolean canMakeShroomy(BlockState blockState) {
        return mcMMO.getMaterialMapStore().isShroomyWhiteListed(blockState.getType());
    }

    /**
     * Determine if a given block is an mcMMO anvil
     *
     * @param blockState The {@link BlockState} of the block to check
     * @return true if the block is an mcMMO anvil, false otherwise
     */
    public static boolean isMcMMOAnvil(BlockState blockState) {
        Material type = blockState.getType();

        return type == Repair.getInstance().getAnvilMaterial() || type == Salvage.anvilMaterial;
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
        HashSet<Material> transparentBlocks = new HashSet<>();

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
        if (data instanceof Ageable) {
            Ageable ageable = (Ageable) data;
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return true;
    }
}
