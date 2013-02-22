package com.gmail.nossr50.skills.smelting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class Smelting {
    public static int burnModifierMaxLevel = AdvancedConfig.getInstance().getBurnModifierMaxLevel();
    public static double burnTimeMultiplier = AdvancedConfig.getInstance().getBurnTimeMultiplier();

    public static double secondSmeltMaxChance = AdvancedConfig.getInstance().getSecondSmeltMaxChance();
    public static int secondSmeltMaxLevel = AdvancedConfig.getInstance().getSecondSmeltMaxLevel();

    public static int fluxMiningUnlockLevel = AdvancedConfig.getInstance().getFluxMiningUnlockLevel();
    public static double fluxMiningChance = AdvancedConfig.getInstance().getFluxMiningChance();

    public static int vanillaXPBoostRank1Level = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank1Level();
    public static int vanillaXPBoostRank2Level = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank2Level();
    public static int vanillaXPBoostRank3Level = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank3Level();
    public static int vanillaXPBoostRank4Level = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank4Level();
    public static int vanillaXPBoostRank5Level = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank5Level();

    public static int vanillaXPBoostRank1Multiplier = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank1Multiplier();
    public static int vanillaXPBoostRank2Multiplier = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank2Multiplier();
    public static int vanillaXPBoostRank3Multiplier = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank3Multiplier();
    public static int vanillaXPBoostRank4Multiplier = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank4Multiplier();
    public static int vanillaXPBoostRank5Multiplier = AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank5Multiplier();

    /**
     * Process the Flux Mining ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    public static boolean processFluxMining(BlockState blockState, Player player) {
        if (SkillTools.unlockLevelReached(player, SkillType.SMELTING, fluxMiningUnlockLevel) && SkillTools.activationSuccessful(player, SkillType.SMELTING, fluxMiningChance)) {
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

            if (Permissions.doubleDrops(player, SkillType.SMELTING) && SkillTools.activationSuccessful(player, SkillType.SMELTING, Mining.doubleDropsMaxChance, Mining.doubleDropsMaxLevel)) {
                Misc.dropItem(location, item);
            }

            blockState.setRawData((byte) 0x0);
            blockState.setType(Material.AIR);
            player.sendMessage(LocaleLoader.getString("Smelting.FluxMining.Success"));
            return true;
        }

        return false;
    }
}
