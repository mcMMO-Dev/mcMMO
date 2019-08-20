package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.behaviours.MiningBehaviour;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AbilityCooldownTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiningManager extends SkillManager {
    
    private final MiningBehaviour miningBehaviour;
    
    public MiningManager(mcMMO pluginRef, McMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.MINING);
        
        //Init behaviour
        miningBehaviour = pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getMiningBehaviour();
    }

    public double getOreBonus(int rank) {
        return pluginRef.getConfigManager().getConfigMining().getBlastMining().getOreBonus(rank);
    }

    public double getDebrisReduction(int rank) {
        return pluginRef.getConfigManager().getConfigMining().getBlastMining().getDebrisReduction(rank);
    }

    public int getDropMultiplier(int rank) {
        return pluginRef.getConfigManager().getConfigMining().getBlastMining().getDropMultiplier(rank);
    }

    public boolean canUseDemolitionsExpertise() {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DEMOLITIONS_EXPERTISE))
            return false;

        return getSkillLevel() >= miningBehaviour.getDemolitionExpertUnlockLevel() && pluginRef.getPermissionTools().demolitionsExpertise(getPlayer());
    }

    public boolean canDetonate() {
        Player player = getPlayer();

        return canUseBlastMining() && player.isSneaking()
                && miningBehaviour.isDetonator(player.getInventory().getItemInMainHand())
                && pluginRef.getPermissionTools().remoteDetonation(player);
    }

    public boolean canUseBlastMining() {
        //Not checking permissions?
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BLAST_MINING);
    }

    public boolean canUseBiggerBombs() {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BIGGER_BOMBS))
            return false;

        return getSkillLevel() >= miningBehaviour.getBiggerBombsUnlockLevel() && pluginRef.getPermissionTools().biggerBombs(getPlayer());
    }

    public boolean canDoubleDrop() {
        return pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS) && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS);
    }

    /**
     * Process double drops & XP gain for miningBehaviour.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void miningBlockCheck(BlockState blockState) {
        Player player = getPlayer();

        applyXpGain(miningBehaviour.getBlockXp(blockState), XPGainReason.PVE);

        if (mcMMOPlayer.getAbilityMode(skill.getSuperAbility())) {
            pluginRef.getSkillTools().handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), pluginRef.getConfigManager().getConfigSuperAbilities().getSuperAbilityLimits().getToolDurabilityDamage());
        }

        if (!canDoubleDrop() || !pluginRef.getDynamicSettingsManager().getBonusDropManager().isBonusDropWhitelisted(blockState.getType()))
            return;

        boolean silkTouch = player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);

        if (silkTouch && !pluginRef.getConfigManager().getConfigMining().getMiningSubskills().getDoubleDrops().isAllowSilkTouchDoubleDrops())
            return;

        //TODO: Make this readable
        if (pluginRef.getRandomChanceTools().checkRandomChanceExecutionSuccess(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS)) {
            pluginRef.getBlockTools().markDropsAsBonus(blockState, mcMMOPlayer.getAbilityMode(skill.getSuperAbility()));
        }
    }

    /**
     * Detonate TNT for Blast Mining
     */
    public void remoteDetonation() {
        Player player = getPlayer();
        Block targetBlock = player.getTargetBlock(pluginRef.getBlockTools().getTransparentBlocks(), miningBehaviour.MAXIMUM_REMOTE_DETONATION_DISTANCE);

        //Blast mining cooldown check needs to be first so the player can be messaged
        if (!blastMiningCooldownOver() || targetBlock.getType() != Material.TNT || !pluginRef.getEventManager().simulateBlockBreak(targetBlock, player, true)) {
            return;
        }

        TNTPrimed tnt = player.getWorld().spawn(targetBlock.getLocation(), TNTPrimed.class);

        //SkillUtils.sendSkillMessage(player, SuperAbilityType.BLAST_miningBehaviour.getAbilityPlayer(player));
        pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUPER_ABILITY, "miningBehaviour.Blast.Boom");
        //player.sendMessage(pluginRef.getLocaleManager().getString("miningBehaviour.Blast.Boom"));

        tnt.setMetadata(MetadataConstants.TNT_TRACKING_METAKEY, mcMMOPlayer.getPlayerMetadata());
        tnt.setFuseTicks(0);
        targetBlock.setType(Material.AIR);

        mcMMOPlayer.setAbilityDATS(SuperAbilityType.BLAST_MINING, System.currentTimeMillis());
        mcMMOPlayer.setAbilityInformed(SuperAbilityType.BLAST_MINING, false);
        new AbilityCooldownTask(pluginRef, mcMMOPlayer, SuperAbilityType.BLAST_MINING).runTaskLater(pluginRef, SuperAbilityType.BLAST_MINING.getCooldown() * Misc.TICK_CONVERSION_FACTOR);
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param yield     The % of blocks to drop
     * @param blockList The list of blocks to drop
     */
    public void blastMiningDropProcessing(float yield, List<Block> blockList) {
        List<BlockState> ores = new ArrayList<>();
        List<BlockState> debris = new ArrayList<>();
        int xp = 0;

        float oreBonus = (float) (getOreBonus() / 100);
        float debrisReduction = (float) (getDebrisReduction() / 100);
        int dropMultiplier = getDropMultiplier();

        float debrisYield = yield - debrisReduction;

        for (Block block : blockList) {
            BlockState blockState = block.getState();

            if (pluginRef.getBlockTools().isOre(blockState)) {
                ores.add(blockState);
            } else {
                debris.add(blockState);
            }
        }

        for (BlockState blockState : ores) {
            if (Misc.getRandom().nextFloat() < (yield + oreBonus)) {
                if (!pluginRef.getPlaceStore().isTrue(blockState)) {
                    xp += miningBehaviour.getBlockXp(blockState);
                }

                Misc.dropItem(Misc.getBlockCenter(blockState), new ItemStack(blockState.getType())); // Initial block that would have been dropped

                if (!pluginRef.getPlaceStore().isTrue(blockState)) {
                    for (int i = 1; i < dropMultiplier; i++) {
                        miningBehaviour.handleSilkTouchDrops(blockState); // Bonus drops - should drop the block & not the items
                    }
                }
            }
        }

        if (debrisYield > 0) {
            for (BlockState blockState : debris) {
                if (Misc.getRandom().nextFloat() < debrisYield) {
                    Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
                }
            }
        }

        applyXpGain(xp, XPGainReason.PVE);
    }

    /**
     * Increases the blast radius of the explosion.
     *
     * @param radius to modify
     * @return modified radius
     */
    public float biggerBombs(float radius) {
        return (float) (radius + getBlastRadiusModifier());
    }

    public double processDemolitionsExpertise(double damage) {
        return damage * ((100.0D - getBlastDamageModifier()) / 100.0D);
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public int getBlastMiningTier() {
        return pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.MINING_BLAST_MINING);
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getOreBonus() {
        return getOreBonus(getBlastMiningTier());
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getDebrisReduction() {
        return getDebrisReduction(getBlastMiningTier());
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public int getDropMultiplier() {
        return getDropMultiplier(getBlastMiningTier());
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getBlastRadiusModifier() {
        return miningBehaviour.getBlastRadiusModifier(getBlastMiningTier());
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getBlastDamageModifier() {
        return miningBehaviour.getBlastDamageDecrease(getBlastMiningTier());
    }

    private boolean blastMiningCooldownOver() {
        int timeRemaining = mcMMOPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING);

        if (timeRemaining > 0) {
            //getPlayer().sendMessage(pluginRef.getLocaleManager().getString("Skills.TooTired", timeRemaining));
            pluginRef.getNotificationManager().sendPlayerInformation(getPlayer(), NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
            return false;
        }

        return true;
    }
}
