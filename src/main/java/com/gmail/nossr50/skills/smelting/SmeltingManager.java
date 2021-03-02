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
        ItemStack sourceItemStack = furnaceSmeltEvent.getSource();
        ItemStack resultItemStack = furnaceSmeltEvent.getResult();

        applyXpGain(Smelting.getResourceXp(sourceItemStack), XPGainReason.PVE, XPGainSource.PASSIVE); //Add XP


        processDoubleSmelt(furnaceSmeltEvent, resultItemStack, furnace);
    }

    private void processDoubleSmelt(@NotNull FurnaceSmeltEvent furnaceSmeltEvent, @NotNull ItemStack resultItemStack, @NotNull Furnace furnace) {
        int itemLimit = resultItemStack.getMaxStackSize();

        //Check for viewers
        if(furnace.getInventory().getViewers().size() > 0) {
            itemLimit = itemLimit - 1; //Lower max size to prevent exploit, the exploit involves an open window and a stack at 63 which is about to double smelt, instructions to replicate below
            /*
                Momshroom02/26/2021
                    1) have max (or close to it) smelting.
                    2) put more than half a stack of an ore in a furnace and wait for it to smelt.
                    3) have your inv full except for one items worth of whatever the furnace product is.  (so like a stack of 63 iron and the rest of your inv is full).
                    4) shift click on the furnace output to remove only one item-- leaving 63.
                    5) let one more item smelt so the furnace appears to have 64
                    6) manually drag (don't shift click) the stack of product out of the furnace- it'll be 65 items
                    when you drop that and pick it up with only one free space in your inv, it'll look like there's more than one left on the ground, but for me, there was only one, and the "dupe" was visual only-- idk about VK's player.
             */
        }

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