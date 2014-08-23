package com.gmail.nossr50.skills.smelting;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityWeightedActivationCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.smelting.Smelting.Tier;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SmeltingManager extends SkillManager {
    public SmeltingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.smelting);
    }

    public boolean canUseFluxMining(BlockState blockState) {
        return getSkillLevel() >= Smelting.fluxMiningUnlockLevel && BlockUtils.affectedByFluxMining(blockState) && Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.FLUX_MINING) && !mcMMO.getPlaceStore().isTrue(blockState);
    }

    public boolean isSecondSmeltSuccessful() {
        return Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.SECOND_SMELT) && SkillUtils.activationSuccessful(SecondaryAbility.SECOND_SMELT, getPlayer(), getSkillLevel(), activationChance);
    }

    /**
     * Process the Flux Mining ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processFluxMining(BlockState blockState) {
        Player player = getPlayer();

        SecondaryAbilityWeightedActivationCheckEvent event = new SecondaryAbilityWeightedActivationCheckEvent(getPlayer(), SecondaryAbility.FLUX_MINING, Smelting.fluxMiningChance / activationChance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        if ((event.getChance() * activationChance) > Misc.getRandom().nextInt(activationChance)) {
            ItemStack item = null;

            switch (blockState.getType()) {
                case IRON_ORE:
                    item = new ItemStack(Material.IRON_INGOT);
                    break;

                case GOLD_ORE:
                    item = new ItemStack(Material.GOLD_INGOT);
                    break;

                default:
                    break;
            }

            if (item == null) {
                return false;
            }

            Misc.dropItems(blockState.getLocation(), item, isSecondSmeltSuccessful() ? 2 : 1);

            blockState.setType(Material.AIR);
            player.sendMessage(LocaleLoader.getString("Smelting.FluxMining.Success"));
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
        applyXpGain(Smelting.getResourceXp(smelting), XPGainReason.PVE);

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
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getVanillaXPBoostModifier();
            }
        }

        return 1;
    }
}
