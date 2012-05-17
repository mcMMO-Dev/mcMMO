package com.gmail.nossr50.skills.misc;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.mods.CustomArmorConfig;
import com.gmail.nossr50.config.mods.CustomToolsConfig;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.mods.CustomItem;
import com.gmail.nossr50.events.skills.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;

public class Repair {

    private static Random random = new Random();
    private static Config configInstance = Config.getInstance();
    private static Permissions permInstance = Permissions.getInstance();

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
            if (ItemChecks.isArmor(is) && permInstance.armorRepair(player)) {
                if (ItemChecks.isDiamondArmor(is) && inventory.contains(configInstance.getRepairDiamondMaterial()) && skillLevel >= configInstance.getRepairDiamondLevelRequirement() && permInstance.diamondRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairDiamondMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 6);
                }
                else if (ItemChecks.isIronArmor(is) && inventory.contains(configInstance.getRepairIronMaterial()) && skillLevel >= configInstance.getRepairIronLevelRequirement() && permInstance.ironRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairIronMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 2);
                }
                else if (ItemChecks.isGoldArmor(is) && inventory.contains(configInstance.getRepairGoldMaterial()) && skillLevel >= configInstance.getRepairGoldLevelRequirement() && permInstance.goldRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairGoldMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 4);
                }
                else if (ItemChecks.isLeatherArmor(is) && inventory.contains(configInstance.getRepairLeatherMaterial()) && permInstance.leatherRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairLeatherMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 1);
                }
                else {
                    needMoreVespeneGas(is, player); //UNABLE TO REPAIR
                }
            }

            /*
             * REPAIR TOOLS
             */
            else if (ItemChecks.isTool(is) && permInstance.toolRepair(player)) {
                if (ItemChecks.isStoneTool(is) && inventory.contains(configInstance.getRepairStoneMaterial()) && skillLevel >= configInstance.getRepairStoneLevelRequirement() && permInstance.stoneRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairStoneMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, .5);
                }
                else if (ItemChecks.isWoodTool(is) && inventory.contains(configInstance.getRepairWoodMaterial()) && permInstance.woodRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairWoodMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, .5);
                }
                else if (ItemChecks.isIronTool(is) && inventory.contains(configInstance.getRepairIronMaterial()) && skillLevel >= configInstance.getRepairIronLevelRequirement() && permInstance.ironRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairIronMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 1);
                }
                else if (ItemChecks.isDiamondTool(is) && inventory.contains(configInstance.getRepairDiamondMaterial()) && skillLevel >= configInstance.getRepairDiamondLevelRequirement() && permInstance.diamondRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairDiamondMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 1);
                }
                else if (ItemChecks.isGoldTool(is) && inventory.contains(configInstance.getRepairGoldMaterial()) && skillLevel >= configInstance.getRepairGoldLevelRequirement() && permInstance.goldRepair(player)) {
                    repairItem(player, is, new ItemStack(configInstance.getRepairGoldMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, 8);
                }
                else if (ItemChecks.isStringTool(is) && inventory.contains(configInstance.getRepairStringMaterial()) && permInstance.stringRepair(player)){
                    repairItem(player, is, new ItemStack(configInstance.getRepairStringMaterial()));
                    xpHandler(player, PP, is, durabilityBefore, .5);
                }
                else {
                    needMoreVespeneGas(is, player); //UNABLE TO REPAIR
                }
            }

            /*
             * REPAIR CUSTOM TOOLS
             */
            else if (ItemChecks.isCustomTool(is) && permInstance.toolRepair(player)) {
                CustomToolsConfig toolsInstance = CustomToolsConfig.getInstance();

                for (CustomItem tool : toolsInstance.customItems) {
                    if (tool.getItemID() == is.getTypeId()) {
                        ItemStack repairMaterial = tool.getRepairMaterial();

                        if (inventory.contains(repairMaterial)) {
                            repairCustomItem(player, is, repairMaterial);
                            xpHandler(player, PP, is, durabilityBefore, 1);
                        }
                        else {
                            needMoreVespeneGas(is, player);
                        }

                        break;
                    }
                }
            }

            /*
             * REPAIR CUSTOM ARMOR
             */
            else if (ItemChecks.isCustomArmor(is) && permInstance.armorRepair(player)) {
                CustomArmorConfig armorInstance = CustomArmorConfig.getInstance();

                for (CustomItem armor : armorInstance.customItems) {
                    if (armor.getItemID() == is.getTypeId()) {
                        ItemStack repairMaterial = armor.getRepairMaterial();

                        if (inventory.contains(repairMaterial)) {
                            repairCustomItem(player, is, repairMaterial);
                            xpHandler(player, PP, is, durabilityBefore, 1);
                        }
                        else {
                            needMoreVespeneGas(is, player);
                        }

                        break;
                    }
                }
            }
        }
        else {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.FullDurability"));
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
    private static void xpHandler(Player player, PlayerProfile PP, ItemStack is, short durabilityBefore, double modify) {
        short durabilityAfter = is.getDurability();
        short dif = (short) (durabilityBefore - durabilityAfter);

        dif = (short) (dif * modify);

        //TODO: What exactly is this for, and should we have it for armor as well?
        if (ItemChecks.isShovel(is)) {
            dif = (short) (dif / 3);
        }
        else if(ItemChecks.isSword(is)) {
            dif = (short) (dif / 2);
        }
        else if(ItemChecks.isHoe(is)) {
            dif = (short) (dif / 2);
        }

        PP.addXP(player, SkillType.REPAIR, dif * 10);
        Skills.XpCheckSkill(SkillType.REPAIR, player);

        //CLANG CLANG
        if (configInstance.spoutEnabled) {
            SpoutSounds.playRepairNoise(player, mcMMO.p);
        }
    }

    /**
     * Get current Arcane Forging rank.
     * 
     * @param skillLevel The skill level of the player whose rank is being checked
     * @return The player's current Arcane Forging rank
     */
    public static int getArcaneForgingRank(PlayerProfile PP) {
        int skillLevel = PP.getSkillLevel(SkillType.REPAIR);

        if (skillLevel >= configInstance.getArcaneForgingRankLevels4()) {
            return 4;
        }
        else if (skillLevel >= configInstance.getArcaneForgingRankLevels3()) {
            return 3;
        }
        else if (skillLevel >= configInstance.getArcaneForgingRankLevels2()) {
            return 2;
        }
        else if (skillLevel >= configInstance.getArcaneForgingRankLevels1()) {
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

        int rank = getArcaneForgingRank(Users.getProfile(player));

        if (rank == 0 || !permInstance.arcaneForging(player)) {
            for (Enchantment x : enchants.keySet()) {
                is.removeEnchantment(x);
            }
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Lost"));
            return;
        }

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            Enchantment enchantment = enchant.getKey();

            if (random.nextInt(100) <= getEnchantChance(rank)) {
                int enchantLevel = enchant.getValue();

                if (configInstance.getArcaneForgingDowngradeEnabled() && enchantLevel > 1) {
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
            return configInstance.getArcaneForgingKeepEnchantsChanceRank4();

        case 3:
            return configInstance.getArcaneForgingKeepEnchantsChanceRank3();

        case 2:
            return configInstance.getArcaneForgingKeepEnchantsChanceRank2();

        case 1:
            return configInstance.getArcaneForgingKeepEnchantsChanceRank1();

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
            return configInstance.getArcaneForgingDowngradeChanceRank4();

        case 3:
            return configInstance.getArcaneForgingDowngradeChanceRank3();

        case 2:
            return configInstance.getArcaneForgingDowngradeChanceRank2();

        case 1:
            return configInstance.getArcaneForgingDowngradeChanceRank1();

        default:
            return 100;
        }
    }

    /**
     * Computes repair bonuses.
     *
     * @param player The player repairing an item
     * @param durability The durability of the item being repaired
     * @param repairAmount The base amount of durability repaired to the item 
     * @return The final amount of durability repaired to the item
     */
    private static short repairCalculate(Player player, short durability, int repairAmount) {
        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);
        float bonus = (float) skillLevel / 500;

        if (permInstance.repairMastery(player)) {
            bonus = (repairAmount * bonus);
            repairAmount += bonus;
        }

        if (checkPlayerProcRepair(player)) {
            repairAmount = (short) (repairAmount * 2);
        }

        durability -= repairAmount;

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
        int repairAmount = 0;

        if (ItemChecks.isShovel(is)) {
            repairAmount = maxDurability;
        }
        else if (ItemChecks.isHoe(is) || ItemChecks.isSword(is) || is.getType().equals(Material.SHEARS) || is.getType().equals(Material.FISHING_ROD)) {
            repairAmount = maxDurability / 2;
        }
        else if (ItemChecks.isAxe(is) || ItemChecks.isPickaxe(is) || is.getType().equals(Material.BOW)) {
            repairAmount = maxDurability / 3;
        }
        else if (ItemChecks.isBoots(is)) {
            repairAmount = maxDurability / 4;
        }
        else if (ItemChecks.isHelmet(is)) {
            repairAmount = maxDurability / 5;
        }
        else if (ItemChecks.isPants(is)) {
            repairAmount = maxDurability / 7;
        }
        else if (ItemChecks.isChestplate(is)) {
            repairAmount = maxDurability / 8;
        }

        return repairCalculate(player, is.getDurability(), repairAmount);
    }

    /**
     * Gets the base durability amount to repair a custom item.
     *
     * @param is The custom item being repaired
     * @param player The player repairing the custom item
     * @return The final amount of durability repaired to the custom item
     */
    private static short getCustomRepairAmount(ItemStack is, Player player) {
        short maxDurability = 0;
        int materialsRequired = 0;
        int repairAmount = 0;

        CustomToolsConfig toolInstance = CustomToolsConfig.getInstance();
        CustomArmorConfig armorInstance = CustomArmorConfig.getInstance();

        if (ModChecks.getToolFromItemStack(is) != null) {
            for (CustomItem tool : toolInstance.customItems) {
                if (tool.getItemID() == is.getTypeId()) {
                    maxDurability = tool.getDurability();
                    materialsRequired = tool.getRepairQuantity();
                    break;
                }
            }
        }
        else if (ModChecks.getArmorFromItemStack(is) != null) {
            for (CustomItem armor : armorInstance.customItems) {
                if (armor.getItemID() == is.getTypeId()) {
                    maxDurability = armor.getDurability();
                    materialsRequired = armor.getRepairQuantity();
                    break;
                }
            }
        }

        repairAmount = maxDurability / materialsRequired;

        return repairCalculate(player, is.getDurability(), repairAmount);
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
            player.sendMessage(LocaleLoader.getString("Repair.Skills.StackedItems"));
        }
        else {
            if (ItemChecks.isDiamondTool(is) || ItemChecks.isDiamondArmor(is)) {
                if (skillLevel < configInstance.getRepairDiamondLevelRequirement()) {
                    player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptDiamond"));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.BLUE + Misc.prettyItemString(configInstance.getRepairDiamondMaterial()));
                }
            }
            else if (ItemChecks.isIronTool(is) || ItemChecks.isIronArmor(is)) {
                if (skillLevel < configInstance.getRepairIronLevelRequirement()) {
                    player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptIron"));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Skills.NeedMore")+ " " + ChatColor.GRAY + Misc.prettyItemString(configInstance.getRepairIronMaterial()));
                }
            }
            else if (ItemChecks.isGoldTool(is) || ItemChecks.isGoldArmor(is)) {
                if (skillLevel < configInstance.getRepairGoldLevelRequirement()) {
                    player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptGold"));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.GOLD + Misc.prettyItemString(configInstance.getRepairGoldMaterial()));
                }
            }
            else if (ItemChecks.isStoneTool(is)) {
                if (skillLevel < configInstance.getRepairStoneLevelRequirement()) {
                    player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptStone"));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.GRAY + Misc.prettyItemString(configInstance.getRepairStoneMaterial()));
                }
            }
            else if (ItemChecks.isWoodTool(is)) {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.DARK_GREEN + Misc.prettyItemString(configInstance.getRepairWoodMaterial()));
            }
            else if (ItemChecks.isLeatherArmor(is)) {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.YELLOW + Misc.prettyItemString(configInstance.getRepairLeatherMaterial()));
            }
            else if (ItemChecks.isStringTool(is)) {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.YELLOW + Misc.prettyItemString(configInstance.getRepairStringMaterial()));
            }
            else {
                player.sendMessage("You do not have the material needed to repair this item!"); //TODO: Use locale
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

        if ((skillLevel > MAX_BONUS_LEVEL || random.nextInt(1000) <= skillLevel) && permInstance.repairBonus(player)) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.FeltEasy"));
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
    private static void repairItem(Player player, ItemStack item, ItemStack repairMaterial) {
        short initialDurability = item.getDurability();
        short newDurability = getRepairAmount(item, player);
        PlayerInventory inventory = player.getInventory();

        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, (short) (initialDurability - newDurability), repairMaterial, item);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

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
        if (configInstance.getArcaneForgingEnchantLossEnabled() && !permInstance.arcaneBypass(player)) {
            addEnchants(player, item);
        }

        item.setDurability(newDurability);
    }

    /**
     * Repairs a custom item.
     *
     * @param player The player repairing an item
     * @param item The custom item being repaired
     * @param repairMaterial The repair reagent
     */
    private static void repairCustomItem(Player player, ItemStack item, ItemStack repairMaterial) {
        short initialDurability = item.getDurability();
        short newDurability = getCustomRepairAmount(item, player);
        PlayerInventory inventory = player.getInventory();

        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, (short) (initialDurability - newDurability), repairMaterial, item);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        inventory.removeItem(repairMaterial);

        if (configInstance.getArcaneForgingEnchantLossEnabled() && !permInstance.arcaneBypass(player)) {
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
            if (configInstance.spoutEnabled) {
                SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

                if (sPlayer.isSpoutCraftEnabled()) {
                    sPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to repair!", Material.getMaterial(anvilID)); //TODO: Use Locale
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Repair.Listener.Anvil"));
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
