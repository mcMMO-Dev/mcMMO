package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;

public class SmeltingManager extends SkillManager {
    public SmeltingManager(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.SMELTING);
    }

    public boolean isSecondSmeltSuccessful() {
        return pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.SMELTING_SECOND_SMELT)
                && pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SMELTING_SECOND_SMELT, getPlayer());
    }

    /**
     * Increases burn time for furnace fuel.
     *
     * @param burnTime The initial burn time from the {@link FurnaceBurnEvent}
     */
    public int fuelEfficiency(int burnTime) {
        return burnTime * getFuelEfficiencyMultiplier();
    }

    public int getFuelEfficiencyMultiplier() {
        switch (pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.SMELTING_FUEL_EFFICIENCY)) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                return 1;
        }
    }

    public ItemStack smeltProcessing(ItemStack smelting, ItemStack result) {
        applyXpGain(getResourceXp(smelting), XPGainReason.PVE, XPGainSource.PASSIVE);

        if (isSecondSmeltSuccessful()) {
            ItemStack newResult = result.clone();

            newResult.setAmount(result.getAmount() + 1);
            return newResult;
        }

        return result;
    }

    public int getResourceXp(ItemStack smelting) {
        return pluginRef.getDynamicSettingsManager().getExperienceManager().getFurnaceItemXP(smelting.getType());
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
        return Math.max(1, pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.SMELTING_UNDERSTANDING_THE_ART));
    }
}