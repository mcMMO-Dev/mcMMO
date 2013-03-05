package com.gmail.nossr50.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.SpoutToolsAPI;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.mods.CustomArmorConfig;
import com.gmail.nossr50.config.mods.CustomToolConfig;

public class ItemUtils {
    private static Config configInstance = Config.getInstance();
    private static boolean customToolsEnabled = configInstance.getToolModsEnabled();
    private static boolean customArmorEnabled = configInstance.getArmorModsEnabled();

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
                if (customToolsEnabled && CustomToolConfig.getInstance().customSwordIDs.contains(is.getTypeId())) {
                    return true;
                }
                else if (mcMMO.spoutEnabled && SpoutToolsAPI.spoutSwords.contains(is)) {
                    return true;
                }
                else {
                    return false;
                }
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
                if (customToolsEnabled && CustomToolConfig.getInstance().customHoeIDs.contains(is.getTypeId())) {
                    return true;
                }
                else if (mcMMO.spoutEnabled && SpoutToolsAPI.spoutHoes.contains(is)) {
                    return true;
                }
                else {
                    return false;
                }
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
                if (customToolsEnabled && CustomToolConfig.getInstance().customShovelIDs.contains(is.getTypeId())) {
                    return true;
                }
                else if (mcMMO.spoutEnabled && SpoutToolsAPI.spoutShovels.contains(is)) {
                    return true;
                }
                else {
                    return false;
                }
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
                if (customToolsEnabled && CustomToolConfig.getInstance().customAxeIDs.contains(is.getTypeId())) {
                    return true;
                }
                else if (mcMMO.spoutEnabled && SpoutToolsAPI.spoutAxes.contains(is)) {
                    return true;
                }
                else {
                    return false;
                }
        }
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param is Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public static boolean isPickaxe(ItemStack is) {
        switch (is.getType()) {
            case DIAMOND_PICKAXE:
            case GOLD_PICKAXE:
            case IRON_PICKAXE:
            case STONE_PICKAXE:
            case WOOD_PICKAXE:
                return true;

            default:
                if (customToolsEnabled && CustomToolConfig.getInstance().customPickaxeIDs.contains(is.getTypeId())) {
                    return true;
                }
                else if (mcMMO.spoutEnabled && SpoutToolsAPI.spoutPickaxes.contains(is)) {
                    return true;
                }
                else {
                    return false;
                }
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
                if (customArmorEnabled && CustomArmorConfig.getInstance().customHelmetIDs.contains(is.getTypeId())) {
                    return true;
                }

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
                if (customArmorEnabled && CustomArmorConfig.getInstance().customChestplateIDs.contains(is.getTypeId())) {
                    return true;
                }

                return false;
        }
    }

    /**
     * Checks if the item is a pair of pants.
     *
     * @param is Item to check
     * @return true if the item is a pair of pants, false otherwise
     */
    public static boolean isLeggings(ItemStack is) {
        switch (is.getType()) {
            case DIAMOND_LEGGINGS:
            case GOLD_LEGGINGS:
            case IRON_LEGGINGS:
            case LEATHER_LEGGINGS:
                return true;

            default:
                if (customArmorEnabled && CustomArmorConfig.getInstance().customLeggingIDs.contains(is.getTypeId())) {
                    return true;
                }

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
                if (customArmorEnabled && CustomArmorConfig.getInstance().customBootIDs.contains(is.getTypeId())) {
                    return true;
                }

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
        return isHelmet(is) || isChestplate(is) || isLeggings(is) || isBoots(is);
    }

    /**
     * Checks to see if an item is a wearable armor piece.
     *
     * @param is Item to check
     * @return true if the item is armor, false otherwise
     */
    public static boolean isMinecraftArmor(ItemStack is) {
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
    public static boolean isMinecraftTool(ItemStack is) {
        return isStoneTool(is) || isWoodTool(is) || isGoldTool(is) || isIronTool(is) || isDiamondTool(is) || isStringTool(is);
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
     * Checks to see if an item is a string tool.
     *
     * @param is Item to check
     * @return true if the item is a string tool, false otherwise
     */
    public static boolean isStringTool(ItemStack is) {
        switch (is.getType()) {
            case BOW:
            case CARROT_STICK:
            case FISHING_ROD:
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
            case BUCKET:
            case FLINT_AND_STEEL:
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

    /**
     * Checks to see if an item is enchantable.
     *
     * @param is Item to check
     * @return true if the item is enchantable, false otherwise
     */
    public static boolean isEnchantable(ItemStack is) {
        Material type = is.getType();
        return isArmor(is) || isSword(is) || isAxe(is) || isShovel(is) || isPickaxe(is) || type == Material.SHEARS || type == Material.FISHING_ROD || type == Material.CARROT_STICK || type == Material.FLINT_AND_STEEL || type == Material.BOW;
    }

    public static boolean isSmeltable(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case GLOWING_REDSTONE_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case EMERALD_ORE:
                return true;

            default:
                return false;
        }
    }

    public static boolean isSmelted(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case COAL:
            case DIAMOND:
            case REDSTONE:
            case GOLD_INGOT:
            case IRON_INGOT:
            case EMERALD:
                return true;

            case INK_SACK:
                if (itemStack.getData().getData() == DyeColor.BLUE.getDyeData()) {
                    return true;
                }

                return false;

            default:
                return false;
        }
    }

    /**
     * Check if an item is sharable.
     *
     * @param item Item that will get shared
     * @return True if the item can be shared.
     */
    public static boolean isShareable(ItemStack is) {
        return isMiningDrop(is) || isWoodcuttingDrop(is) || isMobDrop(is) || isHerbalismDrop(is);
    }

    /**
     * Checks to see if an item is a mining drop.
     *
     * @param is Item to check
     * @return true if the item is a mining drop, false otherwise
     */
    public static boolean isMiningDrop(ItemStack is) {
        switch (is.getType()) {
            case COAL:
            case COAL_ORE:
            case DIAMOND:
            case DIAMOND_ORE:
            case EMERALD:
            case EMERALD_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case REDSTONE:
            case GLOWSTONE_DUST:
                return true;

            case INK_SACK:
                if (is.getData().getData() == DyeColor.BLUE.getDyeData()) {
                    return true;
                }

                return false;

            default:
                return false;
        }
    }

    public static boolean isHerbalismDrop(ItemStack is) {
        switch (is.getType()) {
            case WHEAT:
            case SEEDS:
            case CARROT:
            case POTATO:
            case COCOA:
            case NETHER_WARTS:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case RED_ROSE:
            case YELLOW_FLOWER:
            case CACTUS:
            case SUGAR_CANE:
            case MELON:
            case PUMPKIN:
            case WATER_LILY:
            case VINE:
                return true;

            case INK_SACK:
                return is.getData().getData() == DyeColor.BROWN.getDyeData();

            default:
                return false;
        }
    }

    public static boolean isMobDrop(ItemStack is) {
        switch (is.getType()) {
            case STRING:
            case FEATHER:
            case RAW_CHICKEN:
            case COOKED_CHICKEN:
            case LEATHER:
            case RAW_BEEF:
            case COOKED_BEEF:
            case PORK:
            case GRILLED_PORK:
            case WOOL:
            case RED_ROSE:
            case IRON_INGOT:
            case SNOW_BALL:
            case BLAZE_ROD:
            case SPIDER_EYE:
            case SULPHUR:
            case ENDER_PEARL:
            case GHAST_TEAR:
            case MAGMA_CREAM:
            case BONE:
            case ARROW:
            case SLIME_BALL:
            case NETHER_STAR:
            case COAL:
            case ROTTEN_FLESH:
            case GOLD_NUGGET:
            case EGG:
                return true;

            default:
                return false;
        }
    }

    public static boolean isWoodcuttingDrop(ItemStack is) {
        switch (is.getType()) {
            case LOG:
            case LEAVES:
            case SAPLING:
            case APPLE:
                return true;

            default:
                return false;
        }
    }
}
