package com.gmail.nossr50.skills.smelting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.skills.smelting.Smelting.Tier;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SmeltingManager extends SkillManager {
    public SmeltingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.SMELTING);
    }

    public boolean canUseFluxMining(BlockState blockState) {
        Player player = getPlayer();
        ItemStack heldItem = player.getItemInHand();

        return BlockUtils.affectedByFluxMining(blockState) && ItemUtils.isPickaxe(heldItem) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH) && Permissions.fluxMining(player) && !mcMMO.placeStore.isTrue(blockState);
    }

    public boolean canUseVanillaXpBoost() {
        Player player = getPlayer();

        return SkillUtils.unlockLevelReached(player, skill, Smelting.Tier.ONE.getLevel()) && Permissions.vanillaXpBoost(player, skill);
    }

    /**
     * Process the Flux Mining ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processFluxMining(BlockState blockState) {
        Player player = getPlayer();

        if (SkillUtils.unlockLevelReached(player, skill, Smelting.fluxMiningUnlockLevel) && SkillUtils.activationSuccessful(player, skill, Smelting.fluxMiningChance)) {
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

            Location location = blockState.getLocation();

            Misc.dropItem(location, item);

            if (Permissions.doubleDrops(player, skill) && SkillUtils.activationSuccessful(player, skill, Mining.doubleDropsMaxChance, Mining.doubleDropsMaxLevel)) {
                Misc.dropItem(location, item);
            }

            blockState.setRawData((byte) 0x0);
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

    public ItemStack smeltProcessing(Material resourceType, ItemStack result) {
        Player player = getPlayer();

        applyXpGain(Smelting.getResourceXp(resourceType));

        if (Permissions.doubleDrops(player, skill) && SkillUtils.activationSuccessful(player, skill, Smelting.secondSmeltMaxChance, Smelting.secondSmeltMaxLevel)) {
            ItemStack newResult = new ItemStack(result.getType(), result.getAmount() + 1);
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

        return 0;
    }
}
