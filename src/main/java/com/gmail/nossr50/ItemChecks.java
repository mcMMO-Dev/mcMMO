package com.gmail.nossr50;

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
}
