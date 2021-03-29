package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
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

    /*public boolean canUseFluxMining(BlockState blockState) {
        return getSkillLevel() >= Smelting.fluxMiningUnlockLevel
                && BlockUtils.affectedByFluxMining(blockState)
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SMELTING_FLUX_MINING)
                && !mcMMO.getPlaceStore().isTrue(blockState);
    }*/

    public boolean isSecondSmeltSuccessful() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SMELTING_SECOND_SMELT)
                && RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SMELTING_SECOND_SMELT, getPlayer());
    }

    /*
      Process the Flux Mining ability.

      @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    /*public boolean processFluxMining(BlockState blockState) {
        Player player = getPlayer();

        if (RandomChanceUtil.checkRandomChanceExecutionSuccess(getPlayer(), SubSkillType.SMELTING_FLUX_MINING, true)) {
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

            if (!EventUtils.simulateBlockBreak(blockState.getBlock(), player, true)) {
                return false;
            }

            // We need to distribute Mining XP here, because the block break event gets cancelled
            applyXpGain(Mining.getBlockXp(blockState), XPGainReason.PVE, XPGainSource.PASSIVE);

            SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), Config.getInstance().getAbilityToolDamage());

            Misc.dropItems(Misc.getBlockCenter(blockState), item, isSecondSmeltSuccessful() ? 2 : 1);

            blockState.setType(Material.AIR);

            if (Config.getInstance().getFluxPickaxeSoundEnabled()) {
                SoundManager.sendSound(player, blockState.getLocation(), SoundType.FIZZ);
            }

            ParticleEffectUtils.playFluxEffect(blockState.getLocation());
            return true;
        }

        return false;
    }*/

    /**
     * Increases burn time for furnace fuel.
     *
     * @param burnTime The initial burn time from the {@link FurnaceBurnEvent}
     */
    public int fuelEfficiency(int burnTime) {
        return burnTime * getFuelEfficiencyMultiplier();
    }

    public int getFuelEfficiencyMultiplier()
    {
        switch(RankUtils.getRank(getPlayer(), SubSkillType.SMELTING_FUEL_EFFICIENCY))
        {
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

    public void smeltProcessing(@NotNull FurnaceSmeltEvent furnaceSmeltEvent, @NotNull Furnace furnace) {
        applyXpGain(Smelting.getResourceXp(furnaceSmeltEvent.getSource()), XPGainReason.PVE, XPGainSource.PASSIVE); //Add XP

        processDoubleSmelt(furnaceSmeltEvent, furnace);
    }

    private void processDoubleSmelt(@NotNull FurnaceSmeltEvent furnaceSmeltEvent, @NotNull Furnace furnace) {
        ItemStack resultItemStack = furnaceSmeltEvent.getResult();
        /*
            doubleSmeltCondition should be equal to the max
         */

        //Process double smelt
        if (Config.getInstance().getDoubleDropsEnabled(PrimarySkillType.SMELTING, resultItemStack.getType())
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

        if(furnaceResult == null)
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