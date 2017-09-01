package com.gmail.nossr50.util;

import org.bukkit.ChatColor;
import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Coal;
import org.bukkit.material.Dye;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;

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
            case BOW:
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
            case DIAMOND_SWORD:
            case GOLD_SWORD:
            case IRON_SWORD:
            case STONE_SWORD:
            case WOOD_SWORD:
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
            case DIAMOND_HOE:
            case GOLD_HOE:
            case IRON_HOE:
            case STONE_HOE:
            case WOOD_HOE:
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
            case DIAMOND_SPADE:
            case GOLD_SPADE:
            case IRON_SPADE:
            case STONE_SPADE:
            case WOOD_SPADE:
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
            case DIAMOND_AXE:
            case GOLD_AXE:
            case IRON_AXE:
            case STONE_AXE:
            case WOOD_AXE:
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
            case DIAMOND_PICKAXE:
            case GOLD_PICKAXE:
            case IRON_PICKAXE:
            case STONE_PICKAXE:
            case WOOD_PICKAXE:
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
            case DIAMOND_HELMET:
            case GOLD_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case LEATHER_HELMET:
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
            case DIAMOND_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case LEATHER_CHESTPLATE:
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
            case DIAMOND_LEGGINGS:
            case GOLD_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case LEATHER_LEGGINGS:
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
            case DIAMOND_BOOTS:
            case GOLD_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case LEATHER_BOOTS:
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
     * @param item Item to check
     * @return true if the item is gold armor, false otherwise
     */
    public static boolean isGoldArmor(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is iron armor, false otherwise
     */
    public static boolean isIronArmor(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is diamond armor, false otherwise
     */
    public static boolean isDiamondArmor(ItemStack item) {
        switch (item.getType()) {
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
     * Checks to see if an item is a chainmail armor piece.
     *
     * @param item Item to check
     * @return true if the item is chainmail armor, false otherwise
     */
    public static boolean isChainmailArmor(ItemStack item) {
        switch (item.getType()) {
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
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
        return isStoneTool(item) || isWoodTool(item) || isGoldTool(item) || isIronTool(item) || isDiamondTool(item) || isStringTool(item);
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isStoneTool(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public static boolean isWoodTool(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is a string tool, false otherwise
     */
    public static boolean isStringTool(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isGoldTool(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is an iron tool, false otherwise
     */
    public static boolean isIronTool(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is a diamond tool, false otherwise
     */
    public static boolean isDiamondTool(ItemStack item) {
        switch (item.getType()) {
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
     * @param item Item to check
     * @return true if the item is enchantable, false otherwise
     */
    public static boolean isEnchantable(ItemStack item) {
        switch (item.getType()) {
            case SHEARS:
            case FISHING_ROD:
            case CARROT_STICK:
            case FLINT_AND_STEEL:
                return true;

            default:
                return isArmor(item) || isSword(item) || isAxe(item) || isShovel(item) || isPickaxe(item) || isBow(item);
        }
    }

    public static boolean isSmeltable(ItemStack item) {
        return item != null && MaterialUtils.isOre(item.getData());
    }

    public static boolean isSmelted(ItemStack item) {
        if (item == null) {
            return false;
        }

        for (Recipe recipe : mcMMO.p.getServer().getRecipesFor(item)) {
            if (recipe instanceof FurnaceRecipe && MaterialUtils.isOre(((FurnaceRecipe) recipe).getInput().getData())) {
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
            case COAL:
            case COAL_ORE:
            case DIAMOND:
            case DIAMOND_ORE:
            case EMERALD:
            case EMERALD_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE: // Should we also have Glowing Redstone Ore here?
            case REDSTONE:
            case GLOWSTONE_DUST: // Should we also have Glowstone here?
            case QUARTZ:
            case QUARTZ_ORE:
                return true;

            case INK_SACK:
                return ((Dye) item.getData()).getColor() == DyeColor.BLUE;

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
            case WHEAT:
            case SEEDS:
            case CARROT_ITEM:
            case CHORUS_FRUIT:
            case CHORUS_FLOWER:
            case POTATO_ITEM:
            case BEETROOT:
            case BEETROOT_SEEDS:
            case NETHER_WARTS:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case RED_ROSE:
            case YELLOW_FLOWER:
            case CACTUS:
            case SUGAR_CANE:
            case MELON:
            case MELON_SEEDS:
            case PUMPKIN:
            case PUMPKIN_SEEDS:
            case WATER_LILY:
            case VINE:
            case LONG_GRASS:
            case DOUBLE_PLANT:
                return true;

            case INK_SACK:
                return ((Dye) item.getData()).getColor() == DyeColor.BROWN;

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
            case ROTTEN_FLESH:
            case GOLD_NUGGET:
            case EGG:
                return true;

            case COAL: // Not sure we should include this, as it will also trigger when mining
                return (((Coal) item.getData()).getType() == CoalType.COAL);

            case RED_ROSE: // Not sure we should include this, as it will also trigger from herbalism
                return (item.getData().getData() == 0x0);

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
            case LOG:
            case LOG_2:
            case LEAVES:
            case LEAVES_2:
            case SAPLING:
            case APPLE:
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

    public static boolean isFluxPickaxe(ItemStack item) {
        if (!isMcMMOItem(item)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(ChatColor.GOLD + LocaleLoader.getString("Item.FluxPickaxe.Name"));
    }
}
