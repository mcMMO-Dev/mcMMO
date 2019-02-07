package com.gmail.nossr50.util;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtils {
    private ItemUtils() {}

    /**
     * Checks if the item is a bow.
     *
     * @param item Item to check
     * @return true if the item is a bow, false otherwise
     */
    public static boolean isBow(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.BOW:
                return true;

            default:
                return mcMMO.getModManager().isCustomBow(type);
        }
    }

    /**
     * Checks if the item is a sword.
     *
     * @param item Item to check
     * @return true if the item is a sword, false otherwise
     */
    public static boolean isSword(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_SWORD:
            case Material.GOLDEN_SWORD:
            case Material.IRON_SWORD:
            case Material.STONE_SWORD:
            case Material.WOODEN_SWORD:
                return true;

            default:
                return mcMMO.getModManager().isCustomSword(type);
        }
    }

    /**
     * Checks if the item is a hoe.
     *
     * @param item Item to check
     * @return true if the item is a hoe, false otherwise
     */
    public static boolean isHoe(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_HOE:
            case Material.GOLDEN_HOE:
            case Material.IRON_HOE:
            case Material.STONE_HOE:
            case Material.WOODEN_HOE:
                return true;

            default:
                return mcMMO.getModManager().isCustomHoe(type);
        }
    }

    /**
     * Checks if the item is a shovel.
     *
     * @param item Item to check
     * @return true if the item is a shovel, false otherwise
     */
    public static boolean isShovel(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_SHOVEL:
            case Material.GOLDEN_SHOVEL:
            case Material.IRON_SHOVEL:
            case Material.STONE_SHOVEL:
            case Material.WOODEN_SHOVEL:
                return true;

            default:
                return mcMMO.getModManager().isCustomShovel(type);
        }
    }

    /**
     * Checks if the item is an axe.
     *
     * @param item Item to check
     * @return true if the item is an axe, false otherwise
     */
    public static boolean isAxe(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_AXE:
            case Material.GOLDEN_AXE:
            case Material.IRON_AXE:
            case Material.STONE_AXE:
            case Material.WOODEN_AXE:
                return true;

            default:
                return mcMMO.getModManager().isCustomAxe(type);
        }
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param item Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public static boolean isPickaxe(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_PICKAXE:
            case Material.GOLDEN_PICKAXE:
            case Material.IRON_PICKAXE:
            case Material.STONE_PICKAXE:
            case Material.WOODEN_PICKAXE:
                return true;

            default:
                return mcMMO.getModManager().isCustomPickaxe(type);
        }
    }

    /**
     * Checks if the item counts as unarmed.
     *
     * @param item Item to check
     * @return true if the item counts as unarmed, false otherwise
     */
    public static boolean isUnarmed(ItemStack item) {
        if (Config.getInstance().getUnarmedItemsAsUnarmed()) {
            return !isMinecraftTool(item);
        }

        return item.getType() == Material.AIR;
    }

    /**
     * Checks if the item is a helmet.
     *
     * @param item Item to check
     * @return true if the item is a helmet, false otherwise
     */
    public static boolean isHelmet(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_HELMET:
            case Material.GOLDEN_HELMET:
            case Material.IRON_HELMET:
            case Material.CHAINMAIL_HELMET:
            case Material.LEATHER_HELMET:
                return true;

            default:
                return mcMMO.getModManager().isCustomHelmet(type);
        }
    }

    /**
     * Checks if the item is a chestplate.
     *
     * @param item Item to check
     * @return true if the item is a chestplate, false otherwise
     */
    public static boolean isChestplate(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_CHESTPLATE:
            case Material.GOLDEN_CHESTPLATE:
            case Material.IRON_CHESTPLATE:
            case Material.CHAINMAIL_CHESTPLATE:
            case Material.LEATHER_CHESTPLATE:
                return true;

            default:
                return mcMMO.getModManager().isCustomChestplate(type);
        }
    }

    /**
     * Checks if the item is a pair of pants.
     *
     * @param item Item to check
     * @return true if the item is a pair of pants, false otherwise
     */
    public static boolean isLeggings(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_LEGGINGS:
            case Material.GOLDEN_LEGGINGS:
            case Material.IRON_LEGGINGS:
            case Material.CHAINMAIL_LEGGINGS:
            case Material.LEATHER_LEGGINGS:
                return true;

            default:
                return mcMMO.getModManager().isCustomLeggings(type);
        }
    }

    /**
     * Checks if the item is a pair of boots.
     *
     * @param item Item to check
     * @return true if the item is a pair of boots, false otherwise
     */
    public static boolean isBoots(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case Material.DIAMOND_BOOTS:
            case Material.GOLDEN_BOOTS:
            case Material.IRON_BOOTS:
            case Material.CHAINMAIL_BOOTS:
            case Material.LEATHER_BOOTS:
                return true;

            default:
                return mcMMO.getModManager().isCustomBoots(type);
        }
    }

    /**
     * Checks to see if an item is a wearable armor piece.
     *
     * @param item Item to check
     * @return true if the item is armor, false otherwise
     */
    public static boolean isArmor(ItemStack item) {
        return isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item);
    }

    /**
     * Checks to see if an item is a wearable *vanilla* armor piece.
     *
     * @param item Item to check
     * @return true if the item is armor, false otherwise
     */
    public static boolean isMinecraftArmor(ItemStack item) {
        return isLeatherArmor(item) || isGoldArmor(item) || isIronArmor(item) || isDiamondArmor(item) || isChainmailArmor(item);
    }

    /**
     * Checks to see if an item is a leather armor piece.
     *
     * @param item Item to check
     * @return true if the item is leather armor, false otherwise
     */
    public static boolean isLeatherArmor(ItemStack item) {
        switch (item.getType()) {
            case Material.LEATHER_BOOTS:
            case Material.LEATHER_CHESTPLATE:
            case Material.LEATHER_HELMET:
            case Material.LEATHER_LEGGINGS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a gold armor piece.
     *
     * @param item Item to check
     * @return true if the item is gold armor, false otherwise
     */
    public static boolean isGoldArmor(ItemStack item) {
        switch (item.getType()) {
            case Material.GOLDEN_BOOTS:
            case Material.GOLDEN_CHESTPLATE:
            case Material.GOLDEN_HELMET:
            case Material.GOLDEN_LEGGINGS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is an iron armor piece.
     *
     * @param item Item to check
     * @return true if the item is iron armor, false otherwise
     */
    public static boolean isIronArmor(ItemStack item) {
        switch (item.getType()) {
            case Material.IRON_BOOTS:
            case Material.IRON_CHESTPLATE:
            case Material.IRON_HELMET:
            case Material.IRON_LEGGINGS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a diamond armor piece.
     *
     * @param item Item to check
     * @return true if the item is diamond armor, false otherwise
     */
    public static boolean isDiamondArmor(ItemStack item) {
        switch (item.getType()) {
            case Material.DIAMOND_BOOTS:
            case Material.DIAMOND_CHESTPLATE:
            case Material.DIAMOND_HELMET:
            case Material.DIAMOND_LEGGINGS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a chainmail armor piece.
     *
     * @param item Item to check
     * @return true if the item is chainmail armor, false otherwise
     */
    public static boolean isChainmailArmor(ItemStack item) {
        switch (item.getType()) {
            case Material.CHAINMAIL_BOOTS:
            case Material.CHAINMAIL_CHESTPLATE:
            case Material.CHAINMAIL_HELMET:
            case Material.CHAINMAIL_LEGGINGS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a *vanilla* tool.
     *
     * @param item Item to check
     * @return true if the item is a tool, false otherwise
     */
    public static boolean isMinecraftTool(ItemStack item) {
        return isStoneTool(item) || isWoodTool(item) || isGoldTool(item) || isIronTool(item) || isDiamondTool(item) || isStringTool(item) || item.getType() == Material.TRIDENT;
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isStoneTool(ItemStack item) {
        switch (item.getType()) {
            case Material.STONE_AXE:
            case Material.STONE_HOE:
            case Material.STONE_PICKAXE:
            case Material.STONE_SHOVEL:
            case Material.STONE_SWORD:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a wooden tool.
     *
     * @param item Item to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public static boolean isWoodTool(ItemStack item) {
        switch (item.getType()) {
            case Material.WOODEN_AXE:
            case Material.WOODEN_HOE:
            case Material.WOODEN_PICKAXE:
            case Material.WOODEN_SHOVEL:
            case Material.WOODEN_SWORD:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a string tool.
     *
     * @param item Item to check
     * @return true if the item is a string tool, false otherwise
     */
    public static boolean isStringTool(ItemStack item) {
        switch (item.getType()) {
            case Material.BOW:
            case Material.CARROT_ON_A_STICK:
            case Material.FISHING_ROD:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a gold tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isGoldTool(ItemStack item) {
        switch (item.getType()) {
            case Material.GOLDEN_AXE:
            case Material.GOLDEN_HOE:
            case Material.GOLDEN_PICKAXE:
            case Material.GOLDEN_SHOVEL:
            case Material.GOLDEN_SWORD:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is an iron tool.
     *
     * @param item Item to check
     * @return true if the item is an iron tool, false otherwise
     */
    public static boolean isIronTool(ItemStack item) {
        switch (item.getType()) {
            case Material.BUCKET:
            case Material.FLINT_AND_STEEL:
            case Material.IRON_AXE:
            case Material.IRON_HOE:
            case Material.IRON_PICKAXE:
            case Material.IRON_SHOVEL:
            case Material.IRON_SWORD:
            case Material.SHEARS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a diamond tool.
     *
     * @param item Item to check
     * @return true if the item is a diamond tool, false otherwise
     */
    public static boolean isDiamondTool(ItemStack item) {
        switch (item.getType()) {
            case Material.DIAMOND_AXE:
            case Material.DIAMOND_HOE:
            case Material.DIAMOND_PICKAXE:
            case Material.DIAMOND_SHOVEL:
            case Material.DIAMOND_SWORD:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is enchantable.
     *
     * @param item Item to check
     * @return true if the item is enchantable, false otherwise
     */
    public static boolean isEnchantable(ItemStack item) {
        switch (item.getType()) {
            case Material.ENCHANTED_BOOK:
            case Material.SHEARS:
            case Material.FISHING_ROD:
            case Material.CARROT_ON_A_STICK:
            case Material.FLINT_AND_STEEL:
            case Material.TRIDENT:
                return true;

            default:
                return isArmor(item) || isSword(item) || isAxe(item) || isShovel(item) || isPickaxe(item) || isBow(item);
        }
    }

    public static boolean isSmeltable(ItemStack item) {
        return item != null && item.getType().isBlock() && MaterialUtils.isOre(item.getType());
    }

    public static boolean isSmelted(ItemStack item) {
        if (item == null) {
            return false;
        }

        for (Recipe recipe : mcMMO.p.getServer().getRecipesFor(item)) {
            if (recipe instanceof FurnaceRecipe
                    && ((FurnaceRecipe) recipe).getInput().getType().isBlock()
                    && MaterialUtils.isOre(((FurnaceRecipe) recipe).getInput().getType())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if an item is sharable.
     *
     * @param item Item that will get shared
     * @return True if the item can be shared.
     */
    public static boolean isSharable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return isMiningDrop(item) || isWoodcuttingDrop(item) || isMobDrop(item) || isHerbalismDrop(item) || isMiscDrop(item);
    }

    /**
     * Checks to see if an item is a mining drop.
     *
     * @param item Item to check
     * @return true if the item is a mining drop, false otherwise
     */
    public static boolean isMiningDrop(ItemStack item) {
        switch (item.getType()) {
            case Material.COAL:
            case Material.COAL_ORE:
            case Material.DIAMOND:
            case Material.DIAMOND_ORE:
            case Material.EMERALD:
            case Material.EMERALD_ORE:
            case Material.GOLD_ORE:
            case Material.IRON_ORE:
            case Material.LAPIS_ORE:
            case Material.REDSTONE_ORE: // Should we also have Glowing Redstone Ore here?
            case Material.REDSTONE:
            case Material.GLOWSTONE_DUST: // Should we also have Glowstone here?
            case Material.QUARTZ:
            case Material.NETHER_QUARTZ_ORE:
            case Material.LAPIS_LAZULI:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a herbalism drop.
     *
     * @param item Item to check
     * @return true if the item is a herbalism drop, false otherwise
     */
    public static boolean isHerbalismDrop(ItemStack item) {
        switch (item.getType()) {
            case Material.WHEAT:
            case Material.WHEAT_SEEDS:
            case Material.CARROT:
            case Material.CHORUS_FRUIT:
            case Material.CHORUS_FLOWER:
            case Material.POTATO:
            case Material.BEETROOT:
            case Material.BEETROOT_SEEDS:
            case Material.NETHER_WART:
            case Material.BROWN_MUSHROOM:
            case Material.RED_MUSHROOM:
            case Material.ROSE_RED:
            case Material.DANDELION_YELLOW:
            case Material.CACTUS:
            case Material.SUGAR_CANE:
            case Material.MELON:
            case Material.MELON_SEEDS:
            case Material.PUMPKIN:
            case Material.PUMPKIN_SEEDS:
            case Material.LILY_PAD:
            case Material.VINE:
            case Material.TALL_GRASS:
            case Material.COCOA_BEANS:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a mob drop.
     *
     * @param item Item to check
     * @return true if the item is a mob drop, false otherwise
     */
    public static boolean isMobDrop(ItemStack item) {
        switch (item.getType()) {
            case Material.STRING:
            case Material.FEATHER:
            case Material.CHICKEN:
            case Material.COOKED_CHICKEN:
            case Material.LEATHER:
            case Material.BEEF:
            case Material.COOKED_BEEF:
            case Material.PORKCHOP:
            case Material.COOKED_PORKCHOP:
            case Material.WHITE_WOOL:
            case Material.BLACK_WOOL:
            case Material.BLUE_WOOL:
            case Material.BROWN_WOOL:
            case Material.CYAN_WOOL:
            case Material.GRAY_WOOL:
            case Material.GREEN_WOOL:
            case Material.LIGHT_BLUE_WOOL:
            case Material.LIGHT_GRAY_WOOL:
            case Material.LIME_WOOL:
            case Material.MAGENTA_WOOL:
            case Material.ORANGE_WOOL:
            case Material.PINK_WOOL:
            case Material.PURPLE_WOOL:
            case Material.RED_WOOL:
            case Material.YELLOW_WOOL:
            case Material.IRON_INGOT:
            case Material.SNOWBALL:
            case Material.BLAZE_ROD:
            case Material.SPIDER_EYE:
            case Material.GUNPOWDER:
            case Material.ENDER_PEARL:
            case Material.GHAST_TEAR:
            case Material.MAGMA_CREAM:
            case Material.BONE:
            case Material.ARROW:
            case Material.SLIME_BALL:
            case Material.NETHER_STAR:
            case Material.ROTTEN_FLESH:
            case Material.GOLD_NUGGET:
            case Material.EGG:
            case Material.ROSE_RED:
            case Material.COAL:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a woodcutting drop.
     *
     * @param item Item to check
     * @return true if the item is a woodcutting drop, false otherwise
     */
    public static boolean isWoodcuttingDrop(ItemStack item) {
        switch (item.getType()) {
            case Material.ACACIA_LOG:
            case Material.BIRCH_LOG:
            case Material.DARK_OAK_LOG:
            case Material.JUNGLE_LOG:
            case Material.OAK_LOG:
            case Material.SPRUCE_LOG:
            case Material.STRIPPED_ACACIA_LOG:
            case Material.STRIPPED_BIRCH_LOG:
            case Material.STRIPPED_DARK_OAK_LOG:
            case Material.STRIPPED_JUNGLE_LOG:
            case Material.STRIPPED_OAK_LOG:
            case Material.STRIPPED_SPRUCE_LOG:
            case Material.ACACIA_SAPLING:
            case Material.SPRUCE_SAPLING:
            case Material.BIRCH_SAPLING:
            case Material.DARK_OAK_SAPLING:
            case Material.JUNGLE_SAPLING:
            case Material.OAK_SAPLING:
            case Material.ACACIA_LEAVES:
            case Material.BIRCH_LEAVES:
            case Material.DARK_OAK_LEAVES:
            case Material.JUNGLE_LEAVES:
            case Material.OAK_LEAVES:
            case Material.SPRUCE_LEAVES:
            case Material.APPLE:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks to see if an item is a miscellaneous drop. These items are read from the config file
     *
     * @param item Item to check
     * @return true if the item is a miscellaneous drop, false otherwise
     */
    public static boolean isMiscDrop(ItemStack item) {
        return ItemWeightConfig.getInstance().getMiscItems().contains(item.getType());
    }

    public static boolean isMcMMOItem(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.hasLore() && itemMeta.getLore().contains("mcMMO Item");
    }

    public static boolean isChimaeraWing(ItemStack item) {
        if (!isMcMMOItem(item)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(ChatColor.GOLD + LocaleLoader.getString("Item.ChimaeraWing.Name"));
    }
}
