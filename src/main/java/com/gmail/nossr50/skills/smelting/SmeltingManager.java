package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SmeltingManager extends SkillManager {
    public SmeltingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.SMELTING);
    }

    public boolean isSecondSmeltSuccessful() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SMELTING_SECOND_SMELT)
                && ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.SMELTING_SECOND_SMELT, mmoPlayer);
    }

    /**
     * Increases burn time for furnace fuel.
     *
     * @param burnTime The initial burn time from the {@link FurnaceBurnEvent}
     */
    public int fuelEfficiency(int burnTime) {
        return Math.min(Short.MAX_VALUE, Math.max(1, burnTime * getFuelEfficiencyMultiplier()));
    }

    public int getFuelEfficiencyMultiplier() {
        return switch (RankUtils.getRank(getPlayer(), SubSkillType.SMELTING_FUEL_EFFICIENCY)) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            default -> 1;
        };
    }

    public void smeltProcessing(@NotNull FurnaceSmeltEvent furnaceSmeltEvent, @NotNull Furnace furnace) {
        applyXpGain(Smelting.getSmeltXP(furnaceSmeltEvent.getSource()), XPGainReason.PVE, XPGainSource.PASSIVE); //Add XP

        processDoubleSmelt(furnaceSmeltEvent, furnace);
    }

    private void processDoubleSmelt(@NotNull FurnaceSmeltEvent furnaceSmeltEvent, @NotNull Furnace furnace) {
        ItemStack resultItemStack = furnaceSmeltEvent.getResult();
        /*
            doubleSmeltCondition should be equal to the max
         */

        //Process double smelt
        if (mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.SMELTING, resultItemStack.getType())
                && canDoubleSmeltItemStack(furnace) //Effectively two less than max stack size
                && isSecondSmeltSuccessful()) {

            ItemStack doubleSmeltStack = resultItemStack.clone(); //TODO: Necessary?
            doubleSmeltStack.setAmount(resultItemStack.getAmount() + 1); //Add one
            furnaceSmeltEvent.setResult(doubleSmeltStack); //Set result
        }
    }

    private boolean canDoubleSmeltItemStack(@NotNull Furnace furnace) {
        FurnaceInventory furnaceInventory = furnace.getInventory();
        ItemStack furnaceResult = furnaceInventory.getResult();

        if (furnaceResult == null)
            return true; //This actually means there is nothing yet in the resulting item slot, which means it should always be okay to double smelt

        int resultAmount = furnaceResult.getAmount(); //Amount before double smelt
        int itemLimit = furnaceResult.getMaxStackSize();
        int doubleSmeltCondition = itemLimit - 2; //Don't double smelt if it would cause an illegal stack size

        return resultAmount <= doubleSmeltCondition;
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
        return Math.max(1, RankUtils.getRank(getPlayer(), SubSkillType.SMELTING_UNDERSTANDING_THE_ART));
    }
}