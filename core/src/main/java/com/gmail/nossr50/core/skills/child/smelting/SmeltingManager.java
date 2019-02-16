package com.gmail.nossr50.core.skills.child.smelting;

import com.gmail.nossr50.core.config.MainConfig;
import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.datatypes.experience.XPGainSource;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.mcmmo.block.BlockState;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.SkillManager;
import com.gmail.nossr50.core.skills.SubSkillType;
import com.gmail.nossr50.core.skills.primary.mining.Mining;
import com.gmail.nossr50.core.util.BlockUtils;
import com.gmail.nossr50.core.util.EventUtils;
import com.gmail.nossr50.core.util.Misc;
import com.gmail.nossr50.core.util.Permissions;
import com.gmail.nossr50.core.util.random.RandomChanceUtil;
import com.gmail.nossr50.core.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.core.util.skills.RankUtils;
import com.gmail.nossr50.core.util.skills.SkillActivationType;
import com.gmail.nossr50.core.util.skills.SkillUtils;
import com.gmail.nossr50.core.util.sounds.SoundManager;
import com.gmail.nossr50.core.util.sounds.SoundType;

public class SmeltingManager extends SkillManager {
    public SmeltingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.SMELTING);
    }

    public boolean canUseFluxMining(BlockState blockState) {
        return getSkillLevel() >= Smelting.fluxMiningUnlockLevel
                && BlockUtils.affectedByFluxMining(blockState)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SMELTING_FLUX_MINING)
                && !mcMMO.getPlaceStore().isTrue(blockState);
    }

    public boolean isSecondSmeltSuccessful() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SMELTING_SECOND_SMELT)
                && RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SMELTING_SECOND_SMELT, getPlayer());
    }

    /**
     * Process the Flux Mining ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processFluxMining(BlockState blockState) {
        Player player = getPlayer();

        if (RandomChanceUtil.checkRandomChanceExecutionSuccess(getPlayer(), SubSkillType.SMELTING_FLUX_MINING, true)) {
            ItemStack item = null;

            switch (blockState.getType()) {
                case Material.IRON_ORE:
                    item = new ItemStack(Material.IRON_INGOT);
                    break;

                case Material.GOLD_ORE:
                    item = new ItemStack(Material.GOLD_INGOT);
                    break;

                default:
                    break;
            }

            if (item == null) {
                return false;
            }

            if (!EventUtils.simulateBlockBreak(blockState.getBlock(), player, true)) {
                return false;
            }

            // We need to distribute Mining XP here, because the block break event gets cancelled
            applyXpGain(Mining.getBlockXp(blockState), XPGainReason.PVE, XPGainSource.PASSIVE);

            SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), MainConfig.getInstance().getAbilityToolDamage());

            Misc.dropItems(Misc.getBlockCenter(blockState), item, isSecondSmeltSuccessful() ? 2 : 1);

            blockState.setType(Material.AIR);

            if (MainConfig.getInstance().getFluxPickaxeSoundEnabled()) {
                SoundManager.sendSound(player, blockState.getLocation(), SoundType.FIZZ);
            }

            ParticleEffectUtils.playFluxEffect(blockState.getLocation());
            return true;
        }

        return false;
    }

    /**
     * Increases burn time for furnace fuel.
     *
     * @param burnTime The initial burn time from the {@link FurnaceBurnEvent}
     */
    public int fuelEfficiency(int burnTime) {
        double burnModifier = 1 + (((double) getSkillLevel() / Smelting.burnModifierMaxLevel) * Smelting.burnTimeMultiplier);

        return (int) (burnTime * burnModifier);
    }

    public ItemStack smeltProcessing(ItemStack smelting, ItemStack result) {
        applyXpGain(Smelting.getResourceXp(smelting), XPGainReason.PVE, XPGainSource.PASSIVE);

        if (isSecondSmeltSuccessful()) {
            ItemStack newResult = result.clone();

            newResult.setAmount(result.getAmount() + 1);
            return newResult;
        }

        return result;
    }

    public int vanillaXPBoost(int experience) {
        return experience * getVanillaXpMultiplier();
    }

    /**
     * Gets the vanilla XP multiplier
     *
     * @return the vanilla XP multiplier
     */
    public int getVanillaXpMultiplier() {
        return RankUtils.getRank(getPlayer(), SubSkillType.SMELTING_UNDERSTANDING_THE_ART);
    }
}