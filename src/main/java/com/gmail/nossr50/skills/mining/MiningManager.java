package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AbilityCooldownTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiningManager extends SkillManager {
    public MiningManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.MINING);
    }

    public boolean canUseDemolitionsExpertise() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DEMOLITIONS_EXPERTISE))
            return false;

        return getSkillLevel() >= BlastMining.getDemolitionExpertUnlockLevel() && Permissions.demolitionsExpertise(getPlayer());
    }

    public boolean canDetonate() {
        Player player = getPlayer();

        return canUseBlastMining() && player.isSneaking()
                && (ItemUtils.isPickaxe(getPlayer().getInventory().getItemInMainHand()) || player.getInventory().getItemInMainHand().getType() == Config.getInstance().getDetonatorItem())
                && Permissions.remoteDetonation(player);
    }

    public boolean canUseBlastMining() {
        //Not checking permissions?
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BLAST_MINING);
    }

    public boolean canUseBiggerBombs() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BIGGER_BOMBS))
            return false;

        return getSkillLevel() >= BlastMining.getBiggerBombsUnlockLevel() && Permissions.biggerBombs(getPlayer());
    }

    public boolean canDoubleDrop() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS) && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS);
    }

    /**
     * Process double drops & XP gain for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void miningBlockCheck(BlockState blockState) {
        Player player = getPlayer();

        applyXpGain(Mining.getBlockXp(blockState), XPGainReason.PVE);

        if (!Permissions.isSubSkillEnabled(player, SubSkillType.MINING_DOUBLE_DROPS)) {
            return;
        }

        if (mcMMOPlayer.getAbilityMode(skill.getAbility())) {
            SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), Config.getInstance().getAbilityToolDamage());
        }

        if(!Config.getInstance().getDoubleDropsEnabled(PrimarySkillType.MINING, blockState.getType()) || !canDoubleDrop())
            return;

        boolean silkTouch = player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);

        if(silkTouch && !AdvancedConfig.getInstance().getDoubleDropSilkTouchEnabled())
            return;

        //TODO: Make this readable
        if (RandomChanceUtil.checkRandomChanceExecutionSuccess(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS, true)) {
            BlockUtils.markDropsAsBonus(blockState, mcMMOPlayer.getAbilityMode(skill.getAbility()));
        }
    }

    /**
     * Detonate TNT for Blast Mining
     */
    public void remoteDetonation() {
        Player player = getPlayer();
        Block targetBlock = player.getTargetBlock(BlockUtils.getTransparentBlocks(), BlastMining.MAXIMUM_REMOTE_DETONATION_DISTANCE);

        //Blast mining cooldown check needs to be first so the player can be messaged
        if (!blastMiningCooldownOver() || targetBlock.getType() != Material.TNT || !EventUtils.simulateBlockBreak(targetBlock, player, true)) {
            return;
        }

        TNTPrimed tnt = player.getWorld().spawn(targetBlock.getLocation(), TNTPrimed.class);

        //SkillUtils.sendSkillMessage(player, SuperAbilityType.BLAST_MINING.getAbilityPlayer(player));
        NotificationManager.sendPlayerInformation(player, NotificationType.SUPER_ABILITY, "Mining.Blast.Boom");
        //player.sendMessage(LocaleLoader.getString("Mining.Blast.Boom"));

        tnt.setMetadata(mcMMO.tntMetadataKey, mcMMOPlayer.getPlayerMetadata());
        tnt.setFuseTicks(0);
        targetBlock.setType(Material.AIR);

        mcMMOPlayer.setAbilityDATS(SuperAbilityType.BLAST_MINING, System.currentTimeMillis());
        mcMMOPlayer.setAbilityInformed(SuperAbilityType.BLAST_MINING, false);
        new AbilityCooldownTask(mcMMOPlayer, SuperAbilityType.BLAST_MINING).runTaskLater(mcMMO.p, SuperAbilityType.BLAST_MINING.getCooldown() * Misc.TICK_CONVERSION_FACTOR);
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param yield The % of blocks to drop
     * @param event The {@link EntityExplodeEvent}
     */
    public void blastMiningDropProcessing(float yield, EntityExplodeEvent event) {
        //Strip out only stuff that gives mining XP

        List<BlockState> ores = new ArrayList<BlockState>();

        List<Block> notOres = new ArrayList<>();
        for (Block targetBlock : event.blockList()) {
            //Containers usually have 0 XP unless someone edited their config in a very strange way
            if (ExperienceConfig.getInstance().getXp(PrimarySkillType.MINING, targetBlock) == 0 || targetBlock instanceof Container || mcMMO.getPlaceStore().isTrue(targetBlock)) {
                notOres.add(targetBlock);
            } else {
                ores.add(targetBlock.getState());
            }
        }

        int xp = 0;

        float oreBonus = (float) (getOreBonus() / 100);
        //TODO: Pretty sure something is fucked with debrisReduction stuff
//        float debrisReduction = (float) (getDebrisReduction() / 100);
        int dropMultiplier = getDropMultiplier();

//        float debrisYield = yield - debrisReduction;

        for (BlockState blockState : ores) {
            if (Misc.getRandom().nextFloat() < (notOres.size() + oreBonus)) {
                xp += Mining.getBlockXp(blockState);

                Misc.dropItem(Misc.getBlockCenter(blockState), new ItemStack(blockState.getType())); // Initial block that would have been dropped

                for (int i = 1; i < dropMultiplier; i++) {
                    Mining.handleSilkTouchDrops(blockState); // Bonus drops - should drop the block & not the items
                }
            }
        }

        //Replace the event blocklist with the newYield list
        event.setYield(0F);
//        event.blockList().clear();
//        event.blockList().addAll(notOres);

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
        return RankUtils.getRank(getPlayer(), SubSkillType.MINING_BLAST_MINING);
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getOreBonus() {
        return getOreBonus(getBlastMiningTier());
    }

    public static double getOreBonus(int rank) {
        return AdvancedConfig.getInstance().getOreBonus(rank);
    }

    public static double getDebrisReduction(int rank) {
        return AdvancedConfig.getInstance().getDebrisReduction(rank);
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getDebrisReduction() {
        return getDebrisReduction(getBlastMiningTier());
    }

    public static int getDropMultiplier(int rank) {
        return AdvancedConfig.getInstance().getDropMultiplier(rank);
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
        return BlastMining.getBlastRadiusModifier(getBlastMiningTier());
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getBlastDamageModifier() {
        return BlastMining.getBlastDamageDecrease(getBlastMiningTier());
    }

    private boolean blastMiningCooldownOver() {
        int timeRemaining = mcMMOPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING);

        if (timeRemaining > 0) {
            //getPlayer().sendMessage(LocaleLoader.getString("Skills.TooTired", timeRemaining));
            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
            return false;
        }

        return true;
    }
}
