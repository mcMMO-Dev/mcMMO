package com.gmail.nossr50.skills;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.locale.mcLocale;

public class Repair {

    private static Random random = new Random();

    /**
     * Handle all the item repair checks.
     *
     * @param player Player repairing the item
     * @param is The item being repaired
     */
    public static void repairCheck(Player player, ItemStack is) {
        PlayerProfile PP = Users.getProfile(player);
        short durabilityBefore = is.getDurability();
        PlayerInventory inventory = player.getInventory();
        int skillLevel = PP.getSkillLevel(SkillType.REPAIR);

        if (durabilityBefore > 0 && is.getAmount() == 1) {

            /*
             * REPAIR ARMOR
             */
            if (ItemChecks.isArmor(is) && LoadProperties.repairArmor && mcPermissions.getInstance().armorRepair(player)) {
                if (ItemChecks.isDiamondArmor(is) && inventory.contains(LoadProperties.rDiamond) && skillLevel >= LoadProperties.repairDiamondLevel && mcPermissions.getInstance().diamondRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rDiamond));
                    xpHandler(player, PP, is, durabilityBefore, 6, true);
                }
                else if (ItemChecks.isIronArmor(is) && inventory.contains(LoadProperties.rIron) && skillLevel >= LoadProperties.repairIronLevel && mcPermissions.getInstance().ironRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rIron));
                    xpHandler(player, PP, is, durabilityBefore, 2, true);
                }
                else if (ItemChecks.isGoldArmor(is) && inventory.contains(LoadProperties.rGold) && skillLevel >= LoadProperties.repairGoldLevel && mcPermissions.getInstance().goldRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rGold));
                    xpHandler(player, PP, is, durabilityBefore, 4, true);
                }
                else if (ItemChecks.isLeatherArmor(is) && inventory.contains(LoadProperties.rLeather) && mcPermissions.getInstance().leatherRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rLeather));
                    xpHandler(player, PP, is, durabilityBefore, 1, true);
                }
                else {
                    needMoreVespeneGas(is, player); //UNABLE TO REPAIR
                }
            }

            /*
             * REPAIR TOOLS
             */
            else if (ItemChecks.isTool(is) && LoadProperties.repairTools && mcPermissions.getInstance().toolRepair(player)) {
                if (ItemChecks.isStoneTool(is) && inventory.contains(LoadProperties.rStone) && skillLevel >= LoadProperties.repairStoneLevel && mcPermissions.getInstance().stoneRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rStone));
                    xpHandler(player, PP, is, durabilityBefore, 2, false);
                }
                else if (ItemChecks.isWoodTool(is) && inventory.contains(LoadProperties.rWood) && mcPermissions.getInstance().woodRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rWood));
                    xpHandler(player, PP, is, durabilityBefore, 2, false);
                }
                else if (ItemChecks.isIronTool(is) && inventory.contains(LoadProperties.rIron) && skillLevel >= LoadProperties.repairIronLevel && mcPermissions.getInstance().ironRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rIron));
                    xpHandler(player, PP, is, durabilityBefore, 1, true);
                }
                else if (ItemChecks.isDiamondTool(is) && inventory.contains(LoadProperties.rDiamond) && skillLevel >= LoadProperties.repairDiamondLevel && mcPermissions.getInstance().diamondRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rDiamond));
                    xpHandler(player, PP, is, durabilityBefore, 1, true);
                }
                else if (ItemChecks.isGoldTool(is) && inventory.contains(LoadProperties.rGold) && skillLevel >= LoadProperties.repairGoldLevel && mcPermissions.getInstance().goldRepair(player)) {
                    repairItem(player, is, new ItemStack(LoadProperties.rGold));
                    xpHandler(player, PP, is, durabilityBefore, 8, true);
                }
                else if (ItemChecks.isStringTool(is) && inventory.contains(LoadProperties.rString) && skillLevel >= LoadProperties.repairStringLevel && mcPermissions.getInstance().stringRepair(player)){
                    repairItem(player, is, new ItemStack(LoadProperties.rString));
                    xpHandler(player, PP, is, durabilityBefore, 2, false);
                }
                else {
                    needMoreVespeneGas(is, player); //UNABLE TO REPAIR
                }
            }
        }
        else {
            player.sendMessage(mcLocale.getString("Skills.FullDurability"));
        }
    }

    /**
     * Handle the XP gain for repair events.
     *
     * @param player Player repairing the item
     * @param PP PlayerProfile of the repairing player
     * @param is Item being repaired
     * @param durabilityBefore Durability of the item before repair
     * @param modify Amount to modify the durability by
     * @param boost True if the modifier is a boost, false if the modifier is a reduction
     */
    private static void xpHandler(Player player, PlayerProfile PP, ItemStack is, short durabilityBefore, int modify, boolean boost) {
        short durabilityAfter = is.getDurability();
        short dif = (short) (durabilityBefore - durabilityAfter);

        if (boost) {
            dif = (short) (dif * modify);
        }
        else{
            dif = (short) (dif / modify);
        }

        if (ItemChecks.isShovel(is)) {
            dif = (short) (dif / 3);
        }
        else if(ItemChecks.isSword(is)) {
            dif = (short) (dif / 2);
        }
        else if(ItemChecks.isHoe(is)) {
            dif = (short) (dif / 2);
        }

        PP.addXP(SkillType.REPAIR, dif * 10);
        Skills.XpCheckSkill(SkillType.REPAIR, player);

        //CLANG CLANG
        if (LoadProperties.spoutEnabled) {
            SpoutSounds.playRepairNoise(player);
        }
    }

    /**
     * Get current Arcane Forging rank.
     * 
     * @param skillLevel The skill level of the player whose rank is being checked
     * @return The player's current Arcane Forging rank
     */
    public static int getArcaneForgingRank(int skillLevel) {
        if (skillLevel >= LoadProperties.arcaneRank4) {
            return 4;
        }
        else if (skillLevel >= LoadProperties.arcaneRank3) {
            return 3;
        }
        else if (skillLevel >= LoadProperties.arcaneRank2) {
            return 2;
        }
        else if (skillLevel >= LoadProperties.arcaneRank1) {
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
    private static void addEnchants(Player player, ItemStack is) {
        Map<Enchantment, Integer> enchants = is.getEnchantments();

        if (enchants.size() == 0) {
            return;
        }

        int rank = getArcaneForgingRank(Users.getProfile(player).getSkillLevel(SkillType.REPAIR));

        if (rank == 0 || !mcPermissions.getInstance().arcaneForging(player)) {
            for (Enchantment x : enchants.keySet()) {
                is.removeEnchantment(x);
            }
            player.sendMessage(mcLocale.getString("Repair.LostEnchants"));
            return;
        }

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            Enchantment enchantment = enchant.getKey();

            if (random.nextInt(100) <= getEnchantChance(rank)) {
                int enchantLevel = enchant.getValue();

                if (LoadProperties.mayDowngradeEnchants && enchantLevel > 1) {
                    if (random.nextInt(100) <= getDowngradeChance(rank)) {
                        is.addEnchantment(enchantment, enchantLevel--);
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
            player.sendMessage(mcLocale.getString("Repair.ArcaneFailed"));
        }
        else if (downgraded || newEnchants.size() < enchants.size()) {
            player.sendMessage(mcLocale.getString("Repair.Downgraded"));
        }
        else {
            player.sendMessage(mcLocale.getString("Repair.ArcanePerfect"));
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
            return LoadProperties.keepEnchantsRank4;

        case 3:
            return LoadProperties.keepEnchantsRank3;

        case 2:
            return LoadProperties.keepEnchantsRank2;

        case 1:
            return LoadProperties.keepEnchantsRank1;

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
            return LoadProperties.downgradeRank4;

        case 3:
            return LoadProperties.downgradeRank3;

        case 2:
            return LoadProperties.downgradeRank2;

        case 1:
            return LoadProperties.downgradeRank1;

        default:
            return 100;
        }
    }

    /**
     * Computes repair bonuses.
     *
     * @param player The player repairing an item
     * @param durability The durability of the item being repaired
     * @param ramt The base amount of durability repaired to the item 
     * @return The final amount of durability repaired to the item
     */
    private static short repairCalculate(Player player, short durability, int ramt) {
        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);
        float bonus = (float) skillLevel / 500;

        bonus = (ramt * bonus);
        ramt += bonus;

        if (checkPlayerProcRepair(player)) {
            ramt = (short) (ramt * 2);
        }

        durability -= ramt;

        if (durability < 0) {
            durability = 0;
        }
        return durability;
    }

    /**
     * Gets the base durability amount to repair an item.
     *
     * @param is The item being repaired
     * @param player The player repairing the item
     * @return The final amount of durability repaired to the item
     */
    private static short getRepairAmount(ItemStack is, Player player){
        short maxDurability = is.getType().getMaxDurability();
        int ramt = 0;
        
        if (ItemChecks.isShovel(is)) {
            ramt = maxDurability;
        }
        else if (ItemChecks.isHoe(is) || ItemChecks.isSword(is) || is.getType().equals(Material.SHEARS)) {
            ramt = maxDurability / 2;
        }
        else if (ItemChecks.isAxe(is) || ItemChecks.isMiningPick(is) || ItemChecks.isStringTool(is)) {
            ramt = maxDurability / 3;
        }
        else if (ItemChecks.isBoots(is)) {
            ramt = maxDurability / 4;
        }
        else if (ItemChecks.isHelmet(is)) {
            ramt = maxDurability / 5;
        }
        else if (ItemChecks.isPants(is)) {
            ramt = maxDurability / 7;
        }
        else if (ItemChecks.isChestplate(is)) {
            ramt = maxDurability / 8;
        }

        return repairCalculate(player, is.getDurability(), ramt);
    }

    /**
     * Informs a player that the repair has failed.
     *
     * @param is The item being repaired
     * @param player The player repairing the item
     */
    private static void needMoreVespeneGas(ItemStack is, Player player) {
        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);

        if (is.getAmount() != 1) {
            player.sendMessage(mcLocale.getString("Skills.StackedItems"));
        }
        else {
            if (ItemChecks.isDiamondTool(is) || ItemChecks.isDiamondArmor(is)) {
                if (skillLevel < LoadProperties.repairDiamondLevel) {
                    player.sendMessage(mcLocale.getString("Skills.AdeptDiamond"));
                }
                else {
                    player.sendMessage(mcLocale.getString("Skills.NeedMore") + " " + ChatColor.BLUE + m.prettyItemString(LoadProperties.rDiamond));
                }
            }
            else if (ItemChecks.isIronTool(is) || ItemChecks.isIronArmor(is)) {
                if (skillLevel < LoadProperties.repairIronLevel) {
                    player.sendMessage(mcLocale.getString("Skills.AdeptIron"));
                }
                else {
                    player.sendMessage(mcLocale.getString("Skills.NeedMore")+ " " + ChatColor.GRAY + m.prettyItemString(LoadProperties.rIron));
                }
            }
            else if (ItemChecks.isGoldTool(is) || ItemChecks.isGoldArmor(is)) {
                if (skillLevel < LoadProperties.repairGoldLevel) {
                    player.sendMessage(mcLocale.getString("Skills.AdeptGold"));
                }
                else {
                    player.sendMessage(mcLocale.getString("Skills.NeedMore") + " " + ChatColor.GOLD + m.prettyItemString(LoadProperties.rGold));
                }
            }
            else if (ItemChecks.isStoneTool(is)) {
                if (skillLevel < LoadProperties.repairStoneLevel) {
                    player.sendMessage(mcLocale.getString("Skills.AdeptStone"));
                }
                else {
                    player.sendMessage(mcLocale.getString("Skills.NeedMore") + " " + ChatColor.GRAY + m.prettyItemString(LoadProperties.rStone));
                }
            }
            else if (ItemChecks.isWoodTool(is)) {
                player.sendMessage(mcLocale.getString("Skills.NeedMore") + " " + ChatColor.DARK_GREEN + m.prettyItemString(LoadProperties.rWood));
            }
            else if (ItemChecks.isLeatherArmor(is)) {
                player.sendMessage(mcLocale.getString("Skills.NeedMore") + " " + ChatColor.YELLOW + m.prettyItemString(LoadProperties.rLeather));
            }
            else if (ItemChecks.isStringTool(is)) {
                player.sendMessage(mcLocale.getString("Skills.NeedMore") + " " + ChatColor.YELLOW + m.prettyItemString(LoadProperties.rString));
            }
        }
    }

    /**
     * Checks for Super Repair bonus.
     *
     * @param player The player repairing an item
     * @return true if bonus granted, false otherwise
     */
    public static boolean checkPlayerProcRepair(Player player) {
        final int MAX_BONUS_LEVEL = 1000;

        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);

        if ((skillLevel > MAX_BONUS_LEVEL || random.nextInt(1000) <= skillLevel) && mcPermissions.getInstance().repairBonus(player)) {
            player.sendMessage(mcLocale.getString("Skills.FeltEasy"));
            return true;
        }

        return false;
    }

    /**
     * Repairs an item.
     *
     * @param player The player repairing an item
     * @param item The item being repaired
     * @param repairMaterial The repair reagent
     */
    public static void repairItem(Player player, ItemStack item, ItemStack repairMaterial) {
        short initialDurability = item.getDurability();
        short newDurability = getRepairAmount(item, player);
        PlayerInventory inventory = player.getInventory();

        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, (short) (initialDurability - newDurability), repairMaterial, item);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (repairMaterial.getType().equals(Material.WOOD)) {
            removeWood(inventory);
        }
        else {
            inventory.removeItem(repairMaterial);
        }

        /* Handle the enchants */
        if (LoadProperties.mayLoseEnchants && !mcPermissions.getInstance().arcaneBypass(player)) {
            addEnchants(player, item);
        }

        item.setDurability(newDurability);
    }

    /**
     * Handles notifications for placing an anvil.
     *
     * @param player The player placing the anvil
     * @param anvilID The item ID of the anvil block
     */
    public static void placedAnvilCheck(Player player, int anvilID) {
        PlayerProfile PP = Users.getProfile(player);

        if (!PP.getPlacedAnvil()) {
            if (LoadProperties.spoutEnabled) {
                SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

                if (sPlayer.isSpoutCraftEnabled()) {
                    sPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to repair!", Material.getMaterial(anvilID)); //TODO: Use Locale
                }
            }
            else {
                player.sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil"));
            }

            PP.togglePlacedAnvil();
        }
    }

    /**
     * Removes wood from a player's inventory on repair. Needed due to wood having multiple possible data values.
     *
     * @param inventory The inventory to remove wood from
     */
    private static void removeWood(PlayerInventory inventory) {
        //TODO: Make this less hackish once there's a better way to do it...
        int slot = inventory.first(Material.WOOD);
        ItemStack item = inventory.getItem(slot);
        item.setAmount(item.getAmount() - 1);
        inventory.setItem(slot, item);
    }
}
