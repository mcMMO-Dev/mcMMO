package com.gmail.nossr50;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemChecks {

    /**
     * Checks if the item is a sword.
     *
     * @param is Item to check
     * @return true if the item is a sword, false otherwise
     */
    public static boolean isSword(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_SWORD:
        case GOLD_SWORD:
        case IRON_SWORD:
        case STONE_SWORD:
        case WOOD_SWORD:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a hoe.
     *
     * @param is Item to check
     * @return true if the item is a hoe, false otherwise
     */
    public static boolean isHoe(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_HOE:
        case GOLD_HOE:
        case IRON_HOE:
        case STONE_HOE:
        case WOOD_HOE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a shovel.
     *
     * @param is Item to check
     * @return true if the item is a shovel, false otherwise
     */
    public static boolean isShovel(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_SPADE:
        case GOLD_SPADE:
        case IRON_SPADE:
        case STONE_SPADE:
        case WOOD_SPADE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is an axe.
     *
     * @param is Item to check
     * @return true if the item is an axe, false otherwise
     */
    public static boolean isAxe(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_AXE:
        case GOLD_AXE:
        case IRON_AXE:
        case STONE_AXE:
        case WOOD_AXE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param is Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public static boolean isMiningPick(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_PICKAXE:
        case GOLD_PICKAXE:
        case IRON_PICKAXE:
        case STONE_PICKAXE:
        case WOOD_PICKAXE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a helmet.
     *
     * @param is Item to check
     * @return true if the item is a helmet, false otherwise
     */
    public static boolean isHelmet(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_HELMET:
        case GOLD_HELMET:
        case IRON_HELMET:
        case LEATHER_HELMET:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a chestplate.
     *
     * @param is Item to check
     * @return true if the item is a chestplate, false otherwise
     */
    public static boolean isChestplate(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_CHESTPLATE:
        case GOLD_CHESTPLATE:
        case IRON_CHESTPLATE:
        case LEATHER_CHESTPLATE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a pair of pants.
     *
     * @param is Item to check
     * @return true if the item is a pair of pants, false otherwise
     */
    public static boolean isPants(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_LEGGINGS:
        case GOLD_LEGGINGS:
        case IRON_LEGGINGS:
        case LEATHER_LEGGINGS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks if the item is a pair of boots.
     *
     * @param is Item to check
     * @return true if the item is a pair of boots, false otherwise
     */
    public static boolean isBoots(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_BOOTS:
        case GOLD_BOOTS:
        case IRON_BOOTS:
        case LEATHER_BOOTS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a wearable armor piece.
     *
     * @param is Item to check
     * @return true if the item is armor, false otherwise
     */
    public static boolean isArmor(ItemStack is) {
        return isLeatherArmor(is) || isGoldArmor(is) || isIronArmor(is) || isDiamondArmor(is);
    }

    /**
     * Checks to see if an item is a leather armor piece.
     *
     * @param is Item to check
     * @return true if the item is leather armor, false otherwise
     */
    public static boolean isLeatherArmor(ItemStack is) {
        switch (is.getType()) {
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a gold armor piece.
     *
     * @param is Item to check
     * @return true if the item is gold armor, false otherwise
     */
    public static boolean isGoldArmor(ItemStack is) {
        switch (is.getType()) {
        case GOLD_BOOTS:
        case GOLD_CHESTPLATE:
        case GOLD_HELMET:
        case GOLD_LEGGINGS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is an iron armor piece.
     *
     * @param is Item to check
     * @return true if the item is iron armor, false otherwise
     */
    public static boolean isIronArmor(ItemStack is) {
        switch (is.getType()) {
        case IRON_BOOTS:
        case IRON_CHESTPLATE:
        case IRON_HELMET:
        case IRON_LEGGINGS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a diamond armor piece.
     *
     * @param is Item to check
     * @return true if the item is diamond armor, false otherwise
     */
    public static boolean isDiamondArmor(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_BOOTS:
        case DIAMOND_CHESTPLATE:
        case DIAMOND_HELMET:
        case DIAMOND_LEGGINGS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a tool.
     *
     * @param is Item to check
     * @return true if the item is a tool, false otherwise
     */
    public static boolean isTool(ItemStack is) {
        return isStoneTool(is) || isWoodTool(is) || isGoldTool(is) || isIronTool(is) || isDiamondTool(is) || is.getType().equals(Material.BOW);
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param is Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isStoneTool(ItemStack is) {
        switch (is.getType()) {
        case STONE_AXE:
        case STONE_HOE:
        case STONE_PICKAXE:
        case STONE_SPADE:
        case STONE_SWORD:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a wooden tool.
     *
     * @param is Item to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public static boolean isWoodTool(ItemStack is) {
        switch (is.getType()) {
        case WOOD_AXE:
        case WOOD_HOE:
        case WOOD_PICKAXE:
        case WOOD_SPADE:
        case WOOD_SWORD:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a gold tool.
     *
     * @param is Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isGoldTool(ItemStack is) {
        switch (is.getType()) {
        case GOLD_AXE:
        case GOLD_HOE:
        case GOLD_PICKAXE:
        case GOLD_SPADE:
        case GOLD_SWORD:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is an iron tool.
     *
     * @param is Item to check
     * @return true if the item is an iron tool, false otherwise
     */
    public static boolean isIronTool(ItemStack is) {
        switch (is.getType()) {
        case IRON_AXE:
        case IRON_HOE:
        case IRON_PICKAXE:
        case IRON_SPADE:
        case IRON_SWORD:
        case SHEARS:
            return true;

        default:
            return false;
        }
    }

    /**
     * Checks to see if an item is a diamond tool.
     *
     * @param is Item to check
     * @return true if the item is a diamond tool, false otherwise
     */
    public static boolean isDiamondTool(ItemStack is) {
        switch (is.getType()) {
        case DIAMOND_AXE:
        case DIAMOND_HOE:
        case DIAMOND_PICKAXE:
        case DIAMOND_SPADE:
        case DIAMOND_SWORD:
            return true;

        default:
            return false;
        }
    }
}
