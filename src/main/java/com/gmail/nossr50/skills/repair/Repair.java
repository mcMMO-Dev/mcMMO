package com.gmail.nossr50.skills.repair;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.PerksUtils;

public class Repair {
    public static int    repairMasteryMaxBonusLevel = AdvancedConfig.getInstance().getRepairMasteryMaxLevel();
    public static double repairMasteryMaxBonus      = AdvancedConfig.getInstance().getRepairMasteryMaxBonus();

    public static int    superRepairMaxBonusLevel = AdvancedConfig.getInstance().getSuperRepairMaxLevel();
    public static double superRepairMaxChance     = AdvancedConfig.getInstance().getSuperRepairChanceMax();

    public static boolean arcaneForgingDowngrades  = AdvancedConfig.getInstance().getArcaneForgingDowngradeEnabled();
    public static boolean arcaneForgingEnchantLoss = AdvancedConfig.getInstance().getArcaneForgingEnchantLossEnabled();

    public static int anvilID = Config.getInstance().getRepairAnvilId();
    public static boolean anvilMessagesEnabled = Config.getInstance().getRepairAnvilMessagesEnabled();

    /**
     * Get current Arcane Forging rank.
     *
     * @param profile The PlayerProfile of the player to get the rank for
     * @return The player's current Arcane Forging rank
     */
    public static int getArcaneForgingRank(PlayerProfile profile) {
        int skillLevel = profile.getSkillLevel(SkillType.REPAIR);

        if (skillLevel >= AdvancedConfig.getInstance().getArcaneForgingRankLevels4()) {
            return 4;
        }
        else if (skillLevel >= AdvancedConfig.getInstance().getArcaneForgingRankLevels3()) {
            return 3;
        }
        else if (skillLevel >= AdvancedConfig.getInstance().getArcaneForgingRankLevels2()) {
            return 2;
        }
        else if (skillLevel >= AdvancedConfig.getInstance().getArcaneForgingRankLevels1()) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * Handles removing & downgrading enchants.
     *
     * @param player Player repairing the item
     * @param is Item being repaired
     */
    protected static void addEnchants(Player player, ItemStack is) {
        if (Permissions.arcaneBypass(player)) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Perfect"));
            return;
        }
        Map<Enchantment, Integer> enchants = is.getEnchantments();

        if (enchants.size() == 0) {
            return;
        }

        int rank = getArcaneForgingRank(UserManager.getPlayer(player).getProfile());

        if (rank == 0 || !Permissions.arcaneForging(player)) {
            for (Enchantment x : enchants.keySet()) {
                is.removeEnchantment(x);
            }
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Lost"));
            return;
        }

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            Enchantment enchantment = enchant.getKey();

            int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.REPAIR);

            if (Misc.getRandom().nextInt(activationChance) <= getEnchantChance(rank)) {
                int enchantLevel = enchant.getValue();

                if (arcaneForgingDowngrades && enchantLevel > 1) {
                    if (Misc.getRandom().nextInt(activationChance) < getDowngradeChance(rank)) {
                        is.addEnchantment(enchantment, --enchantLevel);
                        downgraded = true;
                    }
                }
            }
            else {
                is.removeEnchantment(enchantment);
            }
        }

        Map<Enchantment, Integer> newEnchants = is.getEnchantments();

        if (newEnchants.isEmpty()) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Fail"));
        }
        else if (downgraded || newEnchants.size() < enchants.size()) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Downgrade"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Perfect"));
        }
    }

    /**
     * Gets chance of keeping enchantment during repair.
     *
     * @param rank Arcane Forging rank
     * @return The chance of keeping the enchantment
     */
    public static int getEnchantChance(int rank) {
        switch (rank) {
            case 4:
                return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank4();

            case 3:
                return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank3();

            case 2:
                return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank2();

            case 1:
                return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank1();

            default:
                return 0;
        }
    }

    /**
     * Gets chance of enchantment being downgraded during repair.
     *
     * @param rank Arcane Forging rank
     * @return The chance of the enchantment being downgraded
     */
    public static int getDowngradeChance(int rank) {
        switch (rank) {
            case 4:
                return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank4();

            case 3:
                return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank3();

            case 2:
                return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank2();

            case 1:
                return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank1();

            default:
                return 100;
        }
    }

    /**
     * Checks for Super Repair bonus.
     *
     * @param player The player repairing an item
     * @return true if bonus granted, false otherwise
     */
    public static boolean checkPlayerProcRepair(Player player) {
        int skillLevel = UserManager.getPlayer(player).getProfile().getSkillLevel(SkillType.REPAIR);

        int chance = (int) ((superRepairMaxChance / superRepairMaxBonusLevel) * skillLevel);
        if (skillLevel >= superRepairMaxBonusLevel) {
            chance = (int) superRepairMaxChance;
        }

        int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.REPAIR);

        if (chance > Misc.getRandom().nextInt(activationChance) && Permissions.superRepair(player)) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.FeltEasy"));
            return true;
        }
        return false;
    }

    /**
     * Handles notifications for placing an anvil.
     *
     * @param player The player placing the anvil
     * @param anvilID The item ID of the anvil block
     */
    public static void placedAnvilCheck(Player player, int anvilID) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (!mcMMOPlayer.getPlacedAnvil()) {
            if (mcMMO.spoutEnabled) {
                SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

                if (spoutPlayer.isSpoutCraftEnabled()) {
                    spoutPlayer.sendNotification(LocaleLoader.getString("Repair.AnvilPlaced.Spout1"), LocaleLoader.getString("Repair.AnvilPlaced.Spout2"), Material.getMaterial(anvilID));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Repair.Listener.Anvil"));
            }

            player.playSound(player.getLocation(), Sound.ANVIL_LAND, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
            mcMMOPlayer.togglePlacedAnvil();
        }
    }

    /**
     * Handle the Xp gain for repair events.
     *
     * @param mcMMOPlayer Player repairing the item
     * @param durabilityBefore Durability of the item before repair
     * @param modify Amount to modify the durability by
     */
    protected static void xpHandler(McMMOPlayer mcMMOPlayer, short durabilityBefore, short durabilityAfter, double modify) {
        short dif = (short) ((durabilityBefore - durabilityAfter) * modify);
        Player player = mcMMOPlayer.getPlayer();

        player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        mcMMOPlayer.beginXpGain(SkillType.REPAIR, dif * 10);
    }

    /**
     * Computes repair bonuses.
     *
     * @param player The player repairing an item
     * @param skillLevel the skillLevel of the player in Repair
     * @param durability The durability of the item being repaired
     * @param repairAmount The base amount of durability repaired to the item
     * @return The final amount of durability repaired to the item
     */
    protected static short repairCalculate(Player player, int skillLevel, short durability, int repairAmount) {
        float bonus;
        if (skillLevel >= repairMasteryMaxBonusLevel) {
            bonus = (float) (repairMasteryMaxBonus / 100F);
        }
        else {
            bonus = (((float) skillLevel) / ((float) repairMasteryMaxBonusLevel)) * (float) ((repairMasteryMaxBonus) / 100F);
        }

        if (Permissions.repairMastery(player)) {
            bonus = repairAmount * bonus;
            repairAmount += (int) bonus;
        }

        if (checkPlayerProcRepair(player)) {
            repairAmount = (int) (repairAmount * 2D);
        }

        if (repairAmount <= 0 || repairAmount > 32767) {
            repairAmount = 32767;
        }

        durability -= repairAmount;

        if (durability < 0) {
            durability = 0;
        }

        return durability;
    }
}
