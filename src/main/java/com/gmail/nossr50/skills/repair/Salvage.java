package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.ItemUtils;

public class Salvage {
    public static int salvageUnlockLevel = Config.getInstance().getSalvageUnlockLevel();
    public static int anvilID = Config.getInstance().getSalvageAnvilId();

    /**
     * Checks if the item is salvageable.
     *
     * @param is Item to check
     * @return true if the item is salvageable, false otherwise
     */
    public static boolean isSalvageable(final ItemStack is) {
        if (Config.getInstance().getSalvageTools() && (ItemUtils.isMinecraftTool(is) || ItemUtils.isStringTool(is) || is.getType() == Material.BUCKET)) {
            return true;
        }

        if (Config.getInstance().getSalvageArmor() && ItemUtils.isMinecraftArmor(is)) {
            return true;
        }

        return false;
    }

    protected static Material getSalvagedItem(final ItemStack inHand) {
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

    protected static int getSalvagedAmount(final ItemStack inHand) {
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
