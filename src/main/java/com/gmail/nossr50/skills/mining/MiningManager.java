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
import java.util.Set;

import static com.gmail.nossr50.util.ItemUtils.isPickaxe;

public class MiningManager extends SkillManager {

    public static final String BUDDING_AMETHYST = "budding_amethyst";
    public static final Collection<Material> BLAST_MINING_BLACKLIST = Set.of(Material.SPAWNER);

    public MiningManager(@NotNull McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.MINING);
    }

    public boolean canUseDemolitionsExpertise() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DEMOLITIONS_EXPERTISE))
            return false;

        return getSkillLevel() >= BlastMining.getDemolitionExpertUnlockLevel() && Permissions.demolitionsExpertise(getPlayer());
    }

    public boolean canDetonate() {
        Player player = getPlayer();

        return canUseBlastMining() && player.isSneaking()
                && (isPickaxe(getPlayer().getInventory().getItemInMainHand()) || player.getInventory().getItemInMainHand().getType() == mcMMO.p.getGeneralConfig().getDetonatorItem())
                && Permissions.remoteDetonation(player);
    }

    public boolean canUseBlastMining() {
        //Not checking permissions?
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BLAST_MINING);
    }

    public boolean canUseBiggerBombs() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_BIGGER_BOMBS))
            return false;

        return getSkillLevel() >= BlastMining.getBiggerBombsUnlockLevel() && Permissions.biggerBombs(getPlayer());
    }

    public boolean canDoubleDrop() {
        return RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS) && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.MINING_DOUBLE_DROPS);
    }

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

        if (!mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.MINING, blockState.getType()) || !canDoubleDrop())
            return;

        boolean silkTouch = player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);

        if (silkTouch && !mcMMO.p.getAdvancedConfig().getDoubleDropSilkTouchEnabled())
            return;

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

    private boolean processTripleDrops(@NotNull BlockState blockState) {
        //TODO: Make this readable
        if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.MINING_MOTHER_LODE, mmoPlayer)) {
            BlockUtils.markDropsAsBonus(blockState, 2);
            return true;
        } else {
            return false;
        }
    }

    private void processDoubleDrops(@NotNull BlockState blockState) {
        //TODO: Make this readable
        if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.MINING_DOUBLE_DROPS, mmoPlayer)) {
            boolean useTriple = mmoPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER) && mcMMO.p.getAdvancedConfig().getAllowMiningTripleDrops();
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
        mcMMO.p.getFoliaLib().getImpl().runAtEntityLater(mmoPlayer.getPlayer(), new AbilityCooldownTask(mmoPlayer, SuperAbilityType.BLAST_MINING), (long) SuperAbilityType.BLAST_MINING.getCooldown() * Misc.TICK_CONVERSION_FACTOR);
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param yield The % of blocks to drop
     * @param event The {@link EntityExplodeEvent}
     */
    public void blastMiningDropProcessing(float yield, EntityExplodeEvent event) {
        if (yield == 0)
            return;

        var increasedYieldFromBonuses = yield + (yield * getOreBonus());
        // Strip out only stuff that gives mining XP
        List<BlockState> ores = new ArrayList<>();
        List<BlockState> notOres = new ArrayList<>();
        for (Block targetBlock : event.blockList()) {
            BlockState blockState = targetBlock.getState();

            if(mcMMO.getUserBlockTracker().isIneligible(targetBlock))
                continue;

            if (ExperienceConfig.getInstance().getXp(PrimarySkillType.MINING, targetBlock) != 0) {
                if (BlockUtils.isOre(blockState) && !(targetBlock instanceof Container)) {
                    ores.add(blockState);
                }
            } else {
                notOres.add(blockState);
            }
        }

        int xp = 0;
        int dropMultiplier = getDropMultiplier();

        for(BlockState blockState : notOres) {
            if (isDropIllegal(blockState.getType()))
                continue;

            if (blockState.getType().isItem() && Probability.ofPercent(50).evaluate()) {
                ItemUtils.spawnItem(getPlayer(),
                        Misc.getBlockCenter(blockState),
                        new ItemStack(blockState.getType()),
                        ItemSpawnReason.BLAST_MINING_DEBRIS_NON_ORES); // Initial block that would have been dropped
            }
        }
        for (BlockState blockState : ores) {
            // currentOreYield only used for drop calculations for ores
            float currentOreYield = increasedYieldFromBonuses;

            if (isDropIllegal(blockState.getType())) {
                continue;
            }

            // Always give XP for every ore destroyed
            xp += Mining.getBlockXp(blockState);
            while(currentOreYield > 0) {
                if (Probability.ofValue(currentOreYield).evaluate()) {
                    Collection<ItemStack> oreDrops = isPickaxe(mmoPlayer.getPlayer().getInventory().getItemInMainHand())
                            ? blockState.getBlock().getDrops(mmoPlayer.getPlayer().getInventory().getItemInMainHand())
                            : List.of(new ItemStack(blockState.getType()));
                    ItemUtils.spawnItems(getPlayer(), Misc.getBlockCenter(blockState),
                            oreDrops, BLAST_MINING_BLACKLIST, ItemSpawnReason.BLAST_MINING_ORES);

                    if (mcMMO.p.getAdvancedConfig().isBlastMiningBonusDropsEnabled()) {
                        for (int i = 1; i < dropMultiplier; i++) {
                            ItemUtils.spawnItems(getPlayer(),
                                    Misc.getBlockCenter(blockState),
                                    oreDrops,
                                    BLAST_MINING_BLACKLIST,
                                    ItemSpawnReason.BLAST_MINING_ORES_BONUS_DROP);
                        }
                    }
                }
                currentOreYield = Math.max(currentOreYield - 1, 0);
            }
        }

        // Replace the event blocklist with the newYield list
        event.setYield(0F);
        applyXpGain(xp, XPGainReason.PVE);
    }

    /**
     * Checks if it would be illegal (in vanilla) to obtain the block
     * Certain things should never drop ( such as budding_amethyst )
     *
     * @param material target material
     * @return true if it's not legal to obtain the block through normal gameplay
     */
    public boolean isDropIllegal(@NotNull Material material) {
        return material.getKey().getKey().equalsIgnoreCase(BUDDING_AMETHYST);
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
    public float getOreBonus() {
        return (float) (mcMMO.p.getAdvancedConfig().getOreBonus(getBlastMiningTier()) / 100F);
    }

    @Deprecated(since = "2.2.017", forRemoval = true)
    public static double getOreBonus(int rank) {
        return mcMMO.p.getAdvancedConfig().getOreBonus(rank);
    }

    public static double getDebrisReduction(int rank) {
        return mcMMO.p.getAdvancedConfig().getDebrisReduction(rank);
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
        return mcMMO.p.getAdvancedConfig().getDropMultiplier(rank);
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
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
        int timeRemaining = mmoPlayer.calculateTimeRemaining(SuperAbilityType.BLAST_MINING);

        if (timeRemaining > 0) {
            //getPlayer().sendMessage(LocaleLoader.getString("Skills.TooTired", timeRemaining));
            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
            return false;
        }

        return true;
    }
}
