package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemUtils;

public class Repair {
    public static int    repairMasteryMaxBonusLevel = AdvancedConfig.getInstance().getRepairMasteryMaxLevel();
    public static double repairMasteryMaxBonus      = AdvancedConfig.getInstance().getRepairMasteryMaxBonus();

    public static int    superRepairMaxBonusLevel = AdvancedConfig.getInstance().getSuperRepairMaxLevel();
    public static double superRepairMaxChance     = AdvancedConfig.getInstance().getSuperRepairChanceMax();

    public static int salvageUnlockLevel = AdvancedConfig.getInstance().getSalvageUnlockLevel();

    public static int     salvageAnvilId       = Config.getInstance().getSalvageAnvilId();
    public static int     repairAnvilId        = Config.getInstance().getRepairAnvilId();
    public static boolean anvilMessagesEnabled = Config.getInstance().getRepairAnvilMessagesEnabled();

    /**
     * Checks if the item is salvageable.
     *
     * @param item Item to check
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(ItemStack item) {
        if (Config.getInstance().getSalvageTools() && (ItemUtils.isMinecraftTool(item) || ItemUtils.isStringTool(item) || item.getType() == Material.BUCKET)) {
            return true;
        }

        if (Config.getInstance().getSalvageArmor() && ItemUtils.isMinecraftArmor(item)) {
            return true;
        }

        return false;
    }

    public static String getAnvilMessage(int blockId) {
        if (blockId == repairAnvilId) {
            return LocaleLoader.getString("Repair.Listener.Anvil");
        }

        if (blockId == salvageAnvilId) {
            return LocaleLoader.getString("Repair.Listener.Anvil2");
        }

        return "";
    }

    public static String[] getSpoutAnvilMessages(int blockId) {
        if (blockId == repairAnvilId) {
            return new String[] {LocaleLoader.getString("Repair.AnvilPlaced.Spout1"), LocaleLoader.getString("Repair.AnvilPlaced.Spout2")};
        }

        if (blockId == salvageAnvilId) {
            return new String[] {"[mcMMO] Anvil Placed", "Right click to salvage!"};
        }

        return new String[] {"", ""};
    }

    /**
     * Search the inventory for an item and return the index.
     *
     * @param inventory PlayerInventory to scan
     * @param itemId Item id to look for
     * @return index location where the item was found, or -1 if not found
     */
    protected static int findInInventory(PlayerInventory inventory, int itemId) {
        int location = inventory.first(itemId);

        // VALIDATE
        if (inventory.getItem(location).getTypeId() == itemId) {
            return location;
        }

        return -1;
    }

    /**
     * Search the inventory for an item and return the index.
     *
     * @param inventory PlayerInventory to scan
     * @param itemId Item id to look for
     * @param metadata Metadata to look for
     * @return index location where the item was found, or -1 if not found
     */
    protected static int findInInventory(PlayerInventory inventory, int itemId, byte metadata) {
        int location = -1;

        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null) {
                continue;
            }

            if (item.getTypeId() == itemId && item.getData().getData() == metadata) {
                return i;
            }
        }

        return location;
    }

    /**
     * Decrease the amount of items in this slot by one
     *
     * @param inventory PlayerInventory to work in
     * @param index Item index to decrement
     */
    protected static void removeOneFrom(PlayerInventory inventory, int index) {
        ItemStack item = inventory.getItem(index).clone();
        item.setAmount(1);

        inventory.removeItem(item);
    }

    protected static Material getSalvagedItem(ItemStack inHand) {
        if (ItemUtils.isDiamondTool(inHand) || ItemUtils.isDiamondArmor(inHand)) {
            return Material.DIAMOND;
        }
        else if (ItemUtils.isGoldTool(inHand) || ItemUtils.isGoldArmor(inHand)) {
            return Material.GOLD_INGOT;
        }
        else if (ItemUtils.isIronTool(inHand) || ItemUtils.isIronArmor(inHand)) {
            return Material.IRON_INGOT;
        }
        else if (ItemUtils.isStoneTool(inHand)) {
            return Material.COBBLESTONE;
        }
        else if (ItemUtils.isWoodTool(inHand)) {
            return Material.WOOD;
        }
        else if (ItemUtils.isLeatherArmor(inHand)) {
            return Material.LEATHER;
        }
        else if (ItemUtils.isStringTool(inHand)) {
            return Material.STRING;
        }
        else {
            return null;
        }
    }

    protected static int getSalvagedAmount(ItemStack inHand) {
        if (ItemUtils.isPickaxe(inHand) || ItemUtils.isAxe(inHand) || inHand.getType() == Material.BOW || inHand.getType() == Material.BUCKET) {
            return 3;
        }
        else if (ItemUtils.isShovel(inHand) || inHand.getType() == Material.FLINT_AND_STEEL) {
            return 1;
        }
        else if (ItemUtils.isSword(inHand) || ItemUtils.isHoe(inHand) || inHand.getType() == Material.CARROT_STICK || inHand.getType() == Material.FISHING_ROD || inHand.getType() == Material.SHEARS) {
            return 2;
        }
        else if (ItemUtils.isHelmet(inHand)) {
            return 5;
        }
        else if (ItemUtils.isChestplate(inHand)) {
            return 8;
        }
        else if (ItemUtils.isLeggings(inHand)) {
            return 7;
        }
        else if (ItemUtils.isBoots(inHand)) {
            return 4;
        }
        else {
            return 0;
        }
    }
}
