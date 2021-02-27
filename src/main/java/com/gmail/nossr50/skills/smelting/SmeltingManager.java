package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
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
                && SkillUtils.isSkillRNGSuccessful(SubSkillType.SMELTING_SECOND_SMELT, getPlayer());
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

    public void smeltProcessing(@NotNull FurnaceSmeltEvent furnaceSmeltEvent) {
        ItemStack sourceItemStack = furnaceSmeltEvent.getSource();
        ItemStack resultItemStack = furnaceSmeltEvent.getResult();

        applyXpGain(Smelting.getResourceXp(sourceItemStack), XPGainReason.PVE, XPGainSource.PASSIVE); //Add XP
        int itemLimit = resultItemStack.getMaxStackSize();

        processDoubleSmelt(furnaceSmeltEvent, resultItemStack, itemLimit);
    }

    private void processDoubleSmelt(@NotNull FurnaceSmeltEvent furnaceSmeltEvent, @NotNull ItemStack resultItemStack, int itemLimit) {
        //TODO: Permission check work around, could store it as NBT on the furnace
        //We don't do permission checks because this can be for an offline player and Bukkit has nothing to grab permissions for offline players

        //Process double smelt
        if (Config.getInstance().getDoubleDropsEnabled(PrimarySkillType.SMELTING, resultItemStack.getType())
                && resultItemStack.getAmount() < itemLimit
                && isSecondSmeltSuccessful()) {

            ItemStack newResult = resultItemStack.clone();
            newResult.setAmount(Math.min(resultItemStack.getAmount() + 1, itemLimit)); //Don't go over max stack limits
            furnaceSmeltEvent.setResult(newResult);
        }
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