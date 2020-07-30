package com.gmail.nossr50.util;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        return mcMMO.getMaterialMapStore().isBow(item.getType().getKey().getKey());
    }

    public static boolean hasItemInEitherHand(Player player, Material material) {
        return player.getInventory().getItemInMainHand().getType() == material || player.getInventory().getItemInOffHand().getType() == material;
    }

    /**
     * Checks if the item is a sword.
     *
     * @param item Item to check
     * @return true if the item is a sword, false otherwise
     */
    public static boolean isSword(ItemStack item) {
        return mcMMO.getMaterialMapStore().isSword(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a hoe.
     *
     * @param item Item to check
     * @return true if the item is a hoe, false otherwise
     */
    public static boolean isHoe(ItemStack item) {
        return mcMMO.getMaterialMapStore().isHoe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a shovel.
     *
     * @param item Item to check
     * @return true if the item is a shovel, false otherwise
     */
    public static boolean isShovel(ItemStack item) {
        return mcMMO.getMaterialMapStore().isShovel(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is an axe.
     *
     * @param item Item to check
     * @return true if the item is an axe, false otherwise
     */
    public static boolean isAxe(ItemStack item) {
        return mcMMO.getMaterialMapStore().isAxe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param item Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public static boolean isPickaxe(ItemStack item) {
        return mcMMO.getMaterialMapStore().isPickAxe(item.getType().getKey().getKey());
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
     * Checks to see if an item is a wearable armor piece.
     *
     * @param item Item to check
     * @return true if the item is armor, false otherwise
     */
    public static boolean isArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isArmor(item.getType());
    }

    /**
     * Checks to see if an item is a leather armor piece.
     *
     * @param item Item to check
     * @return true if the item is leather armor, false otherwise
     */
    public static boolean isLeatherArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isLeatherArmor(item.getType());
    }

    /**
     * Checks to see if an item is a gold armor piece.
     *
     * @param item Item to check
     * @return true if the item is gold armor, false otherwise
     */
    public static boolean isGoldArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isGoldArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is an iron armor piece.
     *
     * @param item Item to check
     * @return true if the item is iron armor, false otherwise
     */
    public static boolean isIronArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isIronArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a diamond armor piece.
     *
     * @param item Item to check
     * @return true if the item is diamond armor, false otherwise
     */
    public static boolean isDiamondArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isDiamondArmor(item.getType().getKey().getKey());
    }

    public static boolean isNetheriteArmor(ItemStack itemStack) {
        return mcMMO.getMaterialMapStore().isNetheriteArmor(itemStack.getType().getKey().getKey());
    }

    public static boolean isNetheriteTool(ItemStack itemStack) {
        return mcMMO.getMaterialMapStore().isNetheriteTool(itemStack.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a chainmail armor piece.
     *
     * @param item Item to check
     * @return true if the item is chainmail armor, false otherwise
     */
    public static boolean isChainmailArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isChainmailArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a *vanilla* tool.
     *
     * @param item Item to check
     * @return true if the item is a tool, false otherwise
     */
    public static boolean isMinecraftTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isStoneTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isStoneTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a wooden tool.
     *
     * @param item Item to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public static boolean isWoodTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isWoodTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a string tool.
     *
     * @param item Item to check
     * @return true if the item is a string tool, false otherwise
     */
    public static boolean isStringTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isStringTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a gold tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isGoldTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isGoldTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is an iron tool.
     *
     * @param item Item to check
     * @return true if the item is an iron tool, false otherwise
     */
    public static boolean isIronTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isIronTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a diamond tool.
     *
     * @param item Item to check
     * @return true if the item is a diamond tool, false otherwise
     */
    public static boolean isDiamondTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isDiamondTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is enchantable.
     *
     * @param item Item to check
     * @return true if the item is enchantable, false otherwise
     */
    public static boolean isEnchantable(ItemStack item) {
        return mcMMO.getMaterialMapStore().isEnchantable(item.getType().getKey().getKey());
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
        //TODO: 1.14 This needs to be updated
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
            case NETHER_QUARTZ_ORE:
            case LAPIS_LAZULI:
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
        //TODO: 1.14 This needs to be updated
        switch (item.getType()) {
            case WHEAT:
            case WHEAT_SEEDS:
            case CARROT:
            case CHORUS_FRUIT:
            case CHORUS_FLOWER:
            case POTATO:
            case BEETROOT:
            case BEETROOT_SEEDS:
            case NETHER_WART:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case ROSE_BUSH:
            case DANDELION:
            case CACTUS:
            case SUGAR_CANE:
            case MELON:
            case MELON_SEEDS:
            case PUMPKIN:
            case PUMPKIN_SEEDS:
            case LILY_PAD:
            case VINE:
            case TALL_GRASS:
            case COCOA_BEANS:
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
        //TODO: 1.14 This needs to be updated
        switch (item.getType()) {
            case STRING:
            case FEATHER:
            case CHICKEN:
            case COOKED_CHICKEN:
            case LEATHER:
            case BEEF:
            case COOKED_BEEF:
            case PORKCHOP:
            case COOKED_PORKCHOP:
            case WHITE_WOOL:
            case BLACK_WOOL:
            case BLUE_WOOL:
            case BROWN_WOOL:
            case CYAN_WOOL:
            case GRAY_WOOL:
            case GREEN_WOOL:
            case LIGHT_BLUE_WOOL:
            case LIGHT_GRAY_WOOL:
            case LIME_WOOL:
            case MAGENTA_WOOL:
            case ORANGE_WOOL:
            case PINK_WOOL:
            case PURPLE_WOOL:
            case RED_WOOL:
            case YELLOW_WOOL:
            case IRON_INGOT:
            case SNOWBALL:
            case BLAZE_ROD:
            case SPIDER_EYE:
            case GUNPOWDER:
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
            case ROSE_BUSH:
            case COAL:
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
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_OAK_LOG:
            case STRIPPED_SPRUCE_LOG:
            case ACACIA_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case DARK_OAK_SAPLING:
            case JUNGLE_SAPLING:
            case OAK_SAPLING:
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
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

        if(itemMeta == null)
            return false;

        return itemMeta.getLore() != null
                && itemMeta.getLore().contains("mcMMO Item");
    }

    public static boolean isChimaeraWing(ItemStack item) {
        if (!isMcMMOItem(item)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        if(itemMeta == null)
            return false;

        return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(ChatColor.GOLD + LocaleLoader.getString("Item.ChimaeraWing.Name"));
    }
}
