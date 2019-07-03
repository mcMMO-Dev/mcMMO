package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class MiningBehaviour {

    private final mcMMO pluginRef;
    private List<Material> detonators;

    public final int MAXIMUM_REMOTE_DETONATION_DISTANCE = 100;

    public MiningBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.detonators = ItemUtils.matchMaterials(pluginRef.getConfigManager().getConfigMining().getDetonators());
    }

    public double getBlastRadiusModifier(int rank) {
        return pluginRef.getConfigManager().getConfigMining().getBlastMining().getRadius(rank);
    }


    public double getBlastDamageDecrease(int rank) {
        return pluginRef.getConfigManager().getConfigMining().getBlastMining().getDamageDecrease(rank);
    }

    public int getDemolitionExpertUnlockLevel() {

        for (int i = 0; i < SubSkillType.MINING_BLAST_MINING.getNumRanks() - 1; i++) {
            if (getBlastDamageDecrease(i + 1) > 0)
                return RankUtils.getRankUnlockLevel(SubSkillType.MINING_BLAST_MINING, i + 1);
        }

        return 0;
    }

    public int getBiggerBombsUnlockLevel() {

        for (int i = 0; i < SubSkillType.MINING_BLAST_MINING.getNumRanks() - 1; i++) {
            if (getBlastRadiusModifier(i + 1) > 0)
                return RankUtils.getRankUnlockLevel(SubSkillType.MINING_BLAST_MINING, i + 1);
        }

        return 0;
    }

    public boolean processBlastMiningExplosion(EntityDamageByEntityEvent event, TNTPrimed tnt, Player defender) {
        if (!tnt.hasMetadata(MetadataConstants.TNT_TRACKING_METAKEY) || !pluginRef.getUserManager().hasPlayerDataKey(defender)) {
            return false;
        }

        // We can make this assumption because we (should) be the only ones using this exact metadata
        Player player = pluginRef.getServer().getPlayerExact(tnt.getMetadata(MetadataConstants.TNT_TRACKING_METAKEY).get(0).asString());

        if (!player.equals(defender)) {
            return false;
        }

        if (pluginRef.getUserManager().getPlayer(defender) == null) {
            return false;
        }

        MiningManager miningManager = pluginRef.getUserManager().getPlayer(defender).getMiningManager();

        if (!miningManager.canUseDemolitionsExpertise()) {
            return false;
        }

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, miningManager.processDemolitionsExpertise(event.getDamage()));

        if (event.getFinalDamage() == 0) {
            event.setCancelled(true);
            return false;
        }

        return true;
    }

    /**
     * Calculate XP gain for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public int getBlockXp(BlockState blockState) {
        int xp = pluginRef.getDynamicSettingsManager().getExperienceManager().getMiningXp(blockState.getType());

        return xp;
    }

    /**
     * Handle double drops when using Silk Touch.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void handleSilkTouchDrops(BlockState blockState) {
        Material blockType = blockState.getType();

        switch (blockType) {
            case ANDESITE:
            case DIORITE:
            case GRANITE:
            case END_STONE:
            case TERRACOTTA:
            case CLAY:
            case IRON_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case SANDSTONE:
            case BLACK_GLAZED_TERRACOTTA:
            case BLACK_TERRACOTTA:
            case BLUE_GLAZED_TERRACOTTA:
            case BLUE_TERRACOTTA:
            case BROWN_GLAZED_TERRACOTTA:
            case BROWN_TERRACOTTA:
            case CYAN_GLAZED_TERRACOTTA:
            case CYAN_TERRACOTTA:
            case GRAY_GLAZED_TERRACOTTA:
            case GRAY_TERRACOTTA:
            case GREEN_GLAZED_TERRACOTTA:
            case GREEN_TERRACOTTA:
            case LIGHT_BLUE_GLAZED_TERRACOTTA:
            case LIGHT_BLUE_TERRACOTTA:
            case LIGHT_GRAY_GLAZED_TERRACOTTA:
            case LIGHT_GRAY_TERRACOTTA:
            case LIME_GLAZED_TERRACOTTA:
            case LIME_TERRACOTTA:
            case MAGENTA_GLAZED_TERRACOTTA:
            case MAGENTA_TERRACOTTA:
            case ORANGE_GLAZED_TERRACOTTA:
            case ORANGE_TERRACOTTA:
            case PINK_GLAZED_TERRACOTTA:
            case PINK_TERRACOTTA:
            case PURPLE_GLAZED_TERRACOTTA:
            case PURPLE_TERRACOTTA:
            case RED_GLAZED_TERRACOTTA:
            case RED_TERRACOTTA:
            case WHITE_GLAZED_TERRACOTTA:
            case WHITE_TERRACOTTA:
            case YELLOW_GLAZED_TERRACOTTA:
            case YELLOW_TERRACOTTA:
                handleMiningDrops(blockState);
                return;

            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GLOWSTONE:
            case LAPIS_ORE:
            case PACKED_ICE:
            case NETHER_QUARTZ_ORE:
            case REDSTONE_ORE:
            case STONE:
            case PRISMARINE:
                Misc.dropItem(Misc.getBlockCenter(blockState), new ItemStack(blockState.getType()));
                return;

            default:
                /*if (mcMMO.getModManager().isCustomMiningBlock(blockState)) {
                    Misc.dropItem(Misc.getBlockCenter(blockState), new ItemStack(blockState.getType()));
                }*/
        }
    }

    /**
     * Handle double drops from Mining & Blast Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void handleMiningDrops(BlockState blockState) {
        switch (blockState.getType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case END_STONE:
            case GLOWSTONE:
            case GOLD_ORE:
            case TERRACOTTA:
            case IRON_ORE:
            case LAPIS_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case PACKED_ICE:
            case REDSTONE_ORE:
            case SANDSTONE:
            case BLACK_GLAZED_TERRACOTTA:
            case BLACK_TERRACOTTA:
            case BLUE_GLAZED_TERRACOTTA:
            case BLUE_TERRACOTTA:
            case BROWN_GLAZED_TERRACOTTA:
            case BROWN_TERRACOTTA:
            case CYAN_GLAZED_TERRACOTTA:
            case CYAN_TERRACOTTA:
            case GRAY_GLAZED_TERRACOTTA:
            case GRAY_TERRACOTTA:
            case GREEN_GLAZED_TERRACOTTA:
            case GREEN_TERRACOTTA:
            case LIGHT_BLUE_GLAZED_TERRACOTTA:
            case LIGHT_BLUE_TERRACOTTA:
            case LIGHT_GRAY_GLAZED_TERRACOTTA:
            case LIGHT_GRAY_TERRACOTTA:
            case LIME_GLAZED_TERRACOTTA:
            case LIME_TERRACOTTA:
            case MAGENTA_GLAZED_TERRACOTTA:
            case MAGENTA_TERRACOTTA:
            case ORANGE_GLAZED_TERRACOTTA:
            case ORANGE_TERRACOTTA:
            case PINK_GLAZED_TERRACOTTA:
            case PINK_TERRACOTTA:
            case PURPLE_GLAZED_TERRACOTTA:
            case PURPLE_TERRACOTTA:
            case RED_GLAZED_TERRACOTTA:
            case RED_TERRACOTTA:
            case WHITE_GLAZED_TERRACOTTA:
            case WHITE_TERRACOTTA:
            case YELLOW_GLAZED_TERRACOTTA:
            case YELLOW_TERRACOTTA:
            case STONE:
            case NETHER_QUARTZ_ORE:
                Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
                return;

            default:
                /*if (mcMMO.getModManager().isCustomMiningBlock(blockState)) {
                    Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
                }*/
        }
    }

    /**
     * Retrieve a list of Blast Mining detonator types
     *
     * @return blast mining detonator materials
     */
    public List<Material> getDetonators() {
        return detonators;
    }

    /**
     * Check if an itemStack is a valid blast mining detonator
     *
     * @param itemStack target itemstack
     * @return true if valid blast mining detonator
     */
    public Boolean isDetonator(ItemStack itemStack) {
        return getDetonators().contains(itemStack.getType());
    }
}
