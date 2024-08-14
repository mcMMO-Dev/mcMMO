package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.api.ItemSpawnReason;
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
import com.gmail.nossr50.util.random.Probability;
import com.gmail.nossr50.util.random.ProbabilityUtil;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gmail.nossr50.util.ItemUtils.isPickaxe;

public class MiningManager extends SkillManager {

    public MiningManager(@NotNull McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.MINING);
    }

    /**
     * Determines if the player can use Demolitions Expertise.
     *
     * @return true if the player can use Demolitions Expertise, false otherwise
     */
    public boolean canUseDemolitionsExpertise() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DEMOLITIONS_EXPERTISE)
                && getSkillLevel() >= BlastMining.getDemolitionExpertUnlockLevel()
                && Permissions.demolitionsExpertise(getPlayer());
    }

    /**
     * Determines if the player can detonate TNT remotely.
     *
     * @return true if the player can detonate TNT remotely, false otherwise
     */
    public boolean canDetonate() {
        Player player = getPlayer();
        return canUseBlastMining() && player.isSneaking()
                && (isPickaxe(player.getInventory().getItemInMainHand())
                || player.getInventory().getItemInMainHand().getType() == mcMMO.p.getGeneralConfig().getDetonatorItem())
                && Permissions.remoteDetonation(player);
    }

    /**
     * Determines if the player can use Blast Mining.
     *
     * @return true if the player can use Blast Mining, false otherwise
     */
    public boolean canUseBlastMining() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BLAST_MINING);
    }

    /**
     * Determines if the player can use Bigger Bombs.
     *
     * @return true if the player can use Bigger Bombs, false otherwise
     */
    public boolean canUseBiggerBombs() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BIGGER_BOMBS)
                && getSkillLevel() >= BlastMining.getBiggerBombsUnlockLevel()
                && Permissions.biggerBombs(getPlayer());
    }

    /**
     * Determines if the player can trigger Double Drops.
     *
     * @return true if the player can trigger Double Drops, false otherwise
     */
    public boolean canDoubleDrop() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS);
    }

    /**
     * Determines if the player can trigger Mother Lode.
     *
     * @return true if the player can trigger Mother Lode, false otherwise
     */
    public boolean canMotherLode() {
        return Permissions.canUseSubSkill(getPlayer(), SubSkillType.MINING_MOTHER_LODE);
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

        if (mmoPlayer.getAbilityMode(mcMMO.p.getSkillTools().getSuperAbility(skill))) {
            SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), mcMMO.p.getGeneralConfig().getAbilityToolDamage());
        }

        if (!mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.MINING, blockState.getType()) || !canDoubleDrop()) {
            return;
        }

        boolean silkTouch = player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);

        if (silkTouch && !mcMMO.p.getAdvancedConfig().getDoubleDropSilkTouchEnabled()) {
            return;
        }
        //Mining mastery allows for a chance of triple drops
        if (canMotherLode()) {
            //Triple Drops failed so do a normal double drops check
            if (!processTripleDrops(blockState)) {
                processDoubleDrops(blockState);
            }
        } else {
            //If the user has no mastery, proceed with normal double drop routine
            processDoubleDrops(blockState);
        }
    }

    /**
     * Processes triple drops for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if triple drops were successful, false otherwise
     */
    private boolean processTripleDrops(@NotNull BlockState blockState) {
        //TODO: Make this readable
        if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.MINING_MOTHER_LODE, mmoPlayer)) {
            BlockUtils.markDropsAsBonus(blockState, 2);
            return true;
        }
        return false;
    }

    /**
     * Processes double drops for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    private void processDoubleDrops(@NotNull BlockState blockState) {
        if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.MINING_DOUBLE_DROPS, mmoPlayer)) {
            boolean useTriple = mmoPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER)
                    && mcMMO.p.getAdvancedConfig().getAllowMiningTripleDrops();
            BlockUtils.markDropsAsBonus(blockState, useTriple);
        }
    }

    /**
     * Detonate TNT for Blast Mining
     */
    public void remoteDetonation() {
        Player player = getPlayer();
        Block targetBlock = player.getTargetBlock(BlockUtils.getTransparentBlocks(), BlastMining.MAXIMUM_REMOTE_DETONATION_DISTANCE);

        //Blast mining cooldown check needs to be first so the player can be messaged
        if (!blastMiningCooldownOver()
                || targetBlock.getType() != Material.TNT
                || !EventUtils.simulateBlockBreak(targetBlock, player)) {
            return;
        }

        TNTPrimed tnt = player.getWorld().spawn(targetBlock.getLocation(), TNTPrimed.class);

        //SkillUtils.sendSkillMessage(player, SuperAbilityType.BLAST_MINING.getAbilityPlayer(player));
        NotificationManager.sendPlayerInformation(player, NotificationType.SUPER_ABILITY, "Mining.Blast.Boom");
        //player.sendMessage(LocaleLoader.getString("Mining.Blast.Boom"));

        tnt.setMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT, mmoPlayer.getPlayerMetadata());
        tnt.setFuseTicks(0);
        if (mcMMO.getCompatibilityManager().getMinecraftGameVersion().isAtLeast(1, 16, 4)) {
            tnt.setSource(player);
        }
        targetBlock.setType(Material.AIR);

        mmoPlayer.setAbilityDATS(SuperAbilityType.BLAST_MINING, System.currentTimeMillis());
        mmoPlayer.setAbilityInformed(SuperAbilityType.BLAST_MINING, false);
        mcMMO.p.getFoliaLib().getImpl().runAtEntityLater(mmoPlayer.getPlayer(), new AbilityCooldownTask(mmoPlayer, SuperAbilityType.BLAST_MINING),
                (long) SuperAbilityType.BLAST_MINING.getCooldown() * Misc.TICK_CONVERSION_FACTOR);
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param yield The % of blocks to drop
     * @param event The {@link EntityExplodeEvent}
     */
    public void blastMiningDropProcessing(float yield, EntityExplodeEvent event) {
        if (yield == 0) return;

        var increasedYieldFromBonuses = yield + (yield * getOreBonus());

        List<BlockState> ores = new ArrayList<>();
        List<BlockState> nonOres = new ArrayList<>();

        for (Block targetBlock : event.blockList()) {
            BlockState blockState = targetBlock.getState();

            if (mcMMO.getUserBlockTracker().isIneligible(targetBlock)) continue;

            if (ExperienceConfig.getInstance().getXp(PrimarySkillType.MINING, targetBlock) != 0) {
                if (BlockUtils.isOre(blockState) && !(targetBlock instanceof Container)) {
                    ores.add(blockState);
                } else {
                    nonOres.add(blockState);
                }
            } else {
                nonOres.add(blockState);
            }
        }

        int xp = 0;
        int dropMultiplier = getDropMultiplier();

        for (BlockState blockState : nonOres) {
            Collection<ItemStack> drops = blockState.getBlock().getDrops(mmoPlayer.getPlayer().getInventory().getItemInMainHand());
            ItemUtils.spawnItems(getPlayer(), Misc.getBlockCenter(blockState), drops, ItemSpawnReason.BLAST_MINING_DEBRIS_NON_ORES);
        }

        for (BlockState blockState : ores) {
            // currentOreYield only used for drop calculations for ores
            float currentOreYield = increasedYieldFromBonuses;

            // Always give XP for every ore destroyed
            xp += Mining.getBlockXp(blockState);
            while (currentOreYield > 0) {
                if (Probability.ofValue(currentOreYield).evaluate()) {
                    Collection<ItemStack> oreDrops = isPickaxe(mmoPlayer.getPlayer().getInventory().getItemInMainHand())
                            ? blockState.getBlock().getDrops(mmoPlayer.getPlayer().getInventory().getItemInMainHand())
                            : List.of(new ItemStack(blockState.getType()));
                    ItemUtils.spawnItems(getPlayer(), Misc.getBlockCenter(blockState),
                            oreDrops, ItemSpawnReason.BLAST_MINING_ORES);

                    if (mcMMO.p.getAdvancedConfig().isBlastMiningBonusDropsEnabled()) {
                        for (int i = 1; i < dropMultiplier; i++) {
                            ItemUtils.spawnItems(getPlayer(),
                                    Misc.getBlockCenter(blockState),
                                    oreDrops,
                                    ItemSpawnReason.BLAST_MINING_ORES_BONUS_DROP);
                        }
                    }
                }
                currentOreYield = Math.max(currentOreYield - 1, 0);
            }
        }

        event.setYield(0F);
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

    /**
     * Processes damage reduction for Demolitions Expertise.
     *
     * @param damage initial damage
     * @return reduced damage
     */
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
     * Gets the ore bonus for Blast Mining.
     *
     * @return the ore bonus as a float value
     */
    public float getOreBonus() {
        return (float) (mcMMO.p.getAdvancedConfig().getOreBonus(getBlastMiningTier()) / 100F);
    }

    @Deprecated(since = "2.2.017", forRemoval = true)
    public static double getOreBonus(int rank) {
        return mcMMO.p.getAdvancedConfig().getOreBonus(rank);
    }

    /**
     * Gets the debris reduction for Blast Mining.
     *
     * @param rank the current rank of Blast Mining
     * @return the debris reduction as a double value
     */
    public static double getDebrisReduction(int rank) {
        return mcMMO.p.getAdvancedConfig().getDebrisReduction(rank);
    }

    /**
     * Gets the debris reduction for the player's current Blast Mining tier.
     *
     * @return the debris reduction as a double value
     */
    public double getDebrisReduction() {
        return getDebrisReduction(getBlastMiningTier());
    }

    /**
     * Gets the drop multiplier for Blast Mining.
     *
     * @param rank the current rank of Blast Mining
     * @return the drop multiplier
     */
    public static int getDropMultiplier(int rank) {
        return mcMMO.p.getAdvancedConfig().getDropMultiplier(rank);
    }

    /**
     * Gets the drop multiplier for the player's current Blast Mining tier.
     *
     * @return the drop multiplier
     */
    public int getDropMultiplier() {
        if (!mcMMO.p.getAdvancedConfig().isBlastMiningBonusDropsEnabled()) {
            return 0;
        }

        return switch (getBlastMiningTier()) {
            case 8, 7 -> 3;
            case 6, 5, 4, 3 -> 2;
            case 2, 1 -> 1;
            default -> 0;
        };
    }

    /**
     * Gets the blast radius modifier for the player's current Blast Mining tier.
     *
     * @return the blast radius modifier
     */
    public double getBlastRadiusModifier() {
        return BlastMining.getBlastRadiusModifier(getBlastMiningTier());
    }

    /**
     * Gets the blast damage modifier for the player's current Blast Mining tier.
     *
     * @return the blast damage modifier
     */
    public double getBlastDamageModifier() {
        return BlastMining.getBlastDamageDecrease(getBlastMiningTier());
    }

    /**
     * Checks if the Blast Mining cooldown is over.
     *
     * @return true if the cooldown is over, false otherwise
     */
    private boolean blastMiningCooldownOver() {
        int timeRemaining = mmoPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING);

        if (timeRemaining > 0) {
            //getPlayer().sendMessage(LocaleLoader.getString("Skills.TooTired", timeRemaining));
            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
            return false;
        }

        return true;
    }
}
