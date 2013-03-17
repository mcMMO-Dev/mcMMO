package com.gmail.nossr50.skills.mining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.mining.BlastMining.Tier;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class MiningManager extends SkillManager{
    public MiningManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.MINING);
    }

    public boolean canUseDemolitionsExpertise() {
        return getSkillLevel() >= BlastMining.Tier.FOUR.getLevel() && Permissions.demolitionsExpertise(getPlayer());
    }

    public boolean canDetonate() {
        Player player = getPlayer();

        return canUseBlastMining() && player.isSneaking() && player.getItemInHand().getTypeId() == BlastMining.detonatorID && Permissions.remoteDetonation(player);
    }

    public boolean canUseBlastMining() {
        return getSkillLevel() >= BlastMining.Tier.ONE.getLevel();
    }

    public boolean canUseBiggerBombs() {
        return getSkillLevel() >= BlastMining.Tier.TWO.getLevel() && Permissions.biggerBombs(getPlayer());
    }

    /**
     * Process double drops & XP gain for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void miningBlockCheck(BlockState blockState) {
        Player player = getPlayer();

        applyXpGain(Mining.getBlockXp(blockState));

        if (!Permissions.doubleDrops(player, skill)) {
            return;
        }

        Material material = blockState.getType();

        if (material != Material.GLOWING_REDSTONE_ORE && !Config.getInstance().getDoubleDropsEnabled(skill, material)) {
            return;
        }

        boolean silkTouch = player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH);

        for (int i = mcMMOPlayer.getAbilityMode(skill.getAbility()) ? 2 : 1; i != 0; i--) {
            if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Mining.doubleDropsMaxChance, Mining.doubleDropsMaxLevel)) {
                if (silkTouch) {
                    Mining.handleSilkTouchDrops(blockState);
                }
                else {
                    Mining.handleMiningDrops(blockState);
                }
            }
        }
    }

    /**
     * Detonate TNT for Blast Mining
     */
    public void remoteDetonation() {
        Player player = getPlayer();

        HashSet<Byte> transparentBlocks = BlastMining.generateTransparentBlockList();
        Block targetBlock = player.getTargetBlock(transparentBlocks, BlastMining.MAXIMUM_REMOTE_DETONATION_DISTANCE);

        if (targetBlock.getType() != Material.TNT || !SkillUtils.blockBreakSimulate(targetBlock, player, true) || !blastMiningCooldownOver()) {
            return;
        }

        TNTPrimed tnt = player.getWorld().spawn(targetBlock.getLocation(), TNTPrimed.class);

        SkillUtils.sendSkillMessage(player, AbilityType.BLAST_MINING.getAbilityPlayer(player));
        player.sendMessage(LocaleLoader.getString("Mining.Blast.Boom"));

        mcMMO.p.addToTNTTracker(tnt.getEntityId(), player.getName());
        tnt.setFuseTicks(0);
        targetBlock.setData((byte) 0x0);
        targetBlock.setType(Material.AIR);

        getProfile().setSkillDATS(AbilityType.BLAST_MINING, System.currentTimeMillis());
        mcMMOPlayer.setAbilityInformed(AbilityType.BLAST_MINING, false);
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param event Event whose explosion is being processed
     */
    public void blastMiningDropProcessing(float yield, List<Block> blockList) {
        List<BlockState> ores = new ArrayList<BlockState>();
        List<BlockState> debris = new ArrayList<BlockState>();
        int xp = 0;

        float oreBonus = (float) (getOreBonus() / 100);
        float debrisReduction = (float) (getDebrisReduction() / 100);
        int dropMultiplier = getDropMultiplier();

        float debrisYield = yield - debrisReduction;

        for (Block block : blockList) {
            BlockState blockState = block.getState();

            if (BlockUtils.isOre(blockState)) {
                ores.add(blockState);
            }
            else {
                debris.add(blockState);
            }
        }

        for (BlockState blockState : ores) {
            if (Misc.getRandom().nextFloat() < (yield + oreBonus)) {
                if (!mcMMO.placeStore.isTrue(blockState)) {
                    xp += Mining.getBlockXp(blockState);
                }

                Misc.dropItem(blockState.getLocation(), blockState.getData().toItemStack(1)); // Initial block that would have been dropped

                if (!mcMMO.placeStore.isTrue(blockState)) {
                    for (int i = 1; i < dropMultiplier; i++) {
                        Mining.handleSilkTouchDrops(blockState); // Bonus drops - should drop the block & not the items
                    }
                }
            }
        }

        if (debrisYield > 0) {
            for (BlockState blockState : debris) {
                if (Misc.getRandom().nextFloat() < debrisYield) {
                    Misc.dropItem(blockState.getLocation(), blockState.getData().toItemStack(1));
                }
            }
        }

        applyXpGain(xp);
    }

    /**
     * Increases the blast radius of the explosion.
     *
     * @param event Event whose explosion radius is being changed
     */
    public float biggerBombs(float radius) {
        return (float) (radius + getBlastRadiusModifier());
    }

    public int processDemolitionsExpertise(int damage) {
        return (int) (damage * ((100.0D - getBlastDamageModifier()) / 100.0D));
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public int getBlastMiningTier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getOreBonus() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getOreBonus();
            }
        }

        return 0;
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getDebrisReduction() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getDebrisReduction();
            }
        }

        return 0;
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public int getDropMultiplier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getDropMultiplier();
            }
        }

        return 0;
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getBlastRadiusModifier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getBlastRadiusModifier();
            }
        }

        return 0;
    }

    /**
     * Gets the Blast Mining tier
     *
     * @return the Blast Mining tier
     */
    public double getBlastDamageModifier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getBlastDamageDecrease();
            }
        }

        return 0;
    }

    private boolean blastMiningCooldownOver() {
        Player player = getPlayer();
        PlayerProfile profile = getProfile();

        long oldTime = profile.getSkillDATS(AbilityType.BLAST_MINING) * Misc.TIME_CONVERSION_FACTOR;
        int cooldown = AbilityType.BLAST_MINING.getCooldown();

        if (!SkillUtils.cooldownOver(oldTime, cooldown, player)) {
            player.sendMessage(LocaleLoader.getString("Skills.TooTired", SkillUtils.calculateTimeLeft(oldTime, cooldown, player)));
            return false;
        }

        return true;
    }
}
