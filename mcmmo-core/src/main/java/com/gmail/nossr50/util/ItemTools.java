package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemTools {
    private final mcMMO pluginRef;

    public ItemTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public ArrayList<String> getRepairItemMaterials(List<Material> repairItemList) {
        ArrayList<String> repairMaterialList = new ArrayList<>();

        for (Material m : repairItemList) {
            repairMaterialList.add(m.getKey().toString());
        }

        return repairMaterialList;
    }

    public ArrayList<Material> matchMaterials(List<String> ItemBlockRegistryKeyList) {
        ArrayList<Material> matchedMaterials = new ArrayList<>();

        for (String s : ItemBlockRegistryKeyList) {
            matchedMaterials.add(Material.matchMaterial(s));
        }

        return matchedMaterials;
    }

    /**
     * Determines the item type, currently used for repairables/salvageables
     *
     * @param material target material
     * @return the matching ItemType returns OTHER if no match
     */
    public ItemType determineItemType(Material material) {
        if (isMinecraftTool(new ItemStack(material))) {
            return ItemType.TOOL;
        } else if (isArmor(new ItemStack((material)))) {
            return ItemType.ARMOR;
        } else {
            return ItemType.OTHER;
        }
    }

    /**
     * Determines the material category, currently used for repairables/salvageables
     *
     * @param material target material
     * @return the matching ItemMaterialCategory, return OTHER if no match
     */
    public ItemMaterialCategory determineMaterialType(Material material) {
        switch (material) {
            case STRING:
                return ItemMaterialCategory.STRING;

            case LEATHER:
                return ItemMaterialCategory.LEATHER;

            case ACACIA_PLANKS:
            case BIRCH_PLANKS:
            case DARK_OAK_PLANKS:
            case JUNGLE_PLANKS:
            case OAK_PLANKS:
            case SPRUCE_PLANKS:
                return ItemMaterialCategory.WOOD;

            case STONE:
                return ItemMaterialCategory.STONE;

            case IRON_INGOT:
                return ItemMaterialCategory.IRON;

            case GOLD_INGOT:
                return ItemMaterialCategory.GOLD;

            case DIAMOND:
                return ItemMaterialCategory.DIAMOND;

            default:
                return ItemMaterialCategory.OTHER;
        }
    }

    /**
     * Checks if the item is a bow.
     *
     * @param item Item to check
     * @return true if the item is a bow, false otherwise
     */
    public boolean isBow(ItemStack item) {
        return pluginRef.getMaterialMapStore().isBow(item.getType().getKey().getKey());
    }

    public boolean hasItemInEitherHand(Player player, Material material) {
        return player.getInventory().getItemInMainHand().getType() == material || player.getInventory().getItemInOffHand().getType() == material;
    }

    /**
     * Checks if the item is a sword.
     *
     * @param item Item to check
     * @return true if the item is a sword, false otherwise
     */
    public boolean isSword(ItemStack item) {
        return pluginRef.getMaterialMapStore().isSword(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a hoe.
     *
     * @param item Item to check
     * @return true if the item is a hoe, false otherwise
     */
    public boolean isHoe(ItemStack item) {
        return pluginRef.getMaterialMapStore().isHoe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a shovel.
     *
     * @param item Item to check
     * @return true if the item is a shovel, false otherwise
     */
    public boolean isShovel(ItemStack item) {
        return pluginRef.getMaterialMapStore().isShovel(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is an axe.
     *
     * @param item Item to check
     * @return true if the item is an axe, false otherwise
     */
    public boolean isAxe(ItemStack item) {
        return pluginRef.getMaterialMapStore().isAxe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param item Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public boolean isPickaxe(ItemStack item) {
        return pluginRef.getMaterialMapStore().isPickAxe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item counts as unarmed.
     *
     * @param item Item to check
     * @return true if the item counts as unarmed, false otherwise
     */
    public boolean isUnarmed(ItemStack item) {
        if (pluginRef.getConfigManager().getConfigUnarmed().doItemsCountAsUnarmed()) {
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
    public boolean isHelmet(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_HELMET:
            case GOLDEN_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case LEATHER_HELMET:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomHelmet(type);
        }
    }

    /**
     * Checks if the item is a chestplate.
     *
     * @param item Item to check
     * @return true if the item is a chestplate, false otherwise
     */
    public boolean isChestplate(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomChestplate(type);
        }
    }

    /**
     * Checks if the item is a pair of pants.
     *
     * @param item Item to check
     * @return true if the item is a pair of pants, false otherwise
     */
    public boolean isLeggings(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case LEATHER_LEGGINGS:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomLeggings(type);
        }
    }

    /**
     * Checks if the item is a pair of boots.
     *
     * @param item Item to check
     * @return true if the item is a pair of boots, false otherwise
     */
    public boolean isBoots(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_BOOTS:
            case GOLDEN_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case LEATHER_BOOTS:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomBoots(type);
        }
    }

    /**
     * Checks to see if an item is a wearable armor piece.
     *
     * @param item Item to check
     * @return true if the item is armor, false otherwise
     */
    public boolean isArmor(ItemStack item) {
        return isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item);
    }

    /**
     * Checks to see if an item is a wearable *vanilla* armor piece.
     *
     * @param item Item to check
     * @return true if the item is armor, false otherwise
     */
    public boolean isMinecraftArmor(ItemStack item) {
        return pluginRef.getMaterialMapStore().isArmor(item.getType());
    }

    /**
     * Checks to see if an item is a leather armor piece.
     *
     * @param item Item to check
     * @return true if the item is leather armor, false otherwise
     */
    public boolean isLeatherArmor(ItemStack item) {
        return pluginRef.getMaterialMapStore().isLeatherArmor(item.getType());
    }

    /**
     * Checks to see if an item is a gold armor piece.
     *
     * @param item Item to check
     * @return true if the item is gold armor, false otherwise
     */
    public boolean isGoldArmor(ItemStack item) {
        return pluginRef.getMaterialMapStore().isGoldArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is an iron armor piece.
     *
     * @param item Item to check
     * @return true if the item is iron armor, false otherwise
     */
    public boolean isIronArmor(ItemStack item) {
        return pluginRef.getMaterialMapStore().isIronArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a diamond armor piece.
     *
     * @param item Item to check
     * @return true if the item is diamond armor, false otherwise
     */
    public boolean isDiamondArmor(ItemStack item) {
        return pluginRef.getMaterialMapStore().isDiamondArmor(item.getType().getKey().getKey());
    }

    public boolean isNetheriteArmor(ItemStack itemStack) {
        return pluginRef.getMaterialMapStore().isNetheriteArmor(itemStack.getType().getKey().getKey());
    }

    public boolean isNetheriteTool(ItemStack itemStack) {
        return pluginRef.getMaterialMapStore().isNetheriteTool(itemStack.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a chainmail armor piece.
     *
     * @param item Item to check
     * @return true if the item is chainmail armor, false otherwise
     */
    public boolean isChainmailArmor(ItemStack item) {
        return pluginRef.getMaterialMapStore().isChainmailArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a *vanilla* tool.
     *
     * @param item Item to check
     * @return true if the item is a tool, false otherwise
     */
    public boolean isMinecraftTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public boolean isStoneTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isStoneTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a wooden tool.
     *
     * @param item Item to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public boolean isWoodTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isWoodTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a wooden tool.
     *
     * @param material Material to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public boolean isWoodTool(Material material) {
        switch (material) {
            case WOODEN_AXE:
            case WOODEN_HOE:
            case WOODEN_PICKAXE:
            case WOODEN_SHOVEL:
            case WOODEN_SWORD:
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
    public boolean isStringTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isStringTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a gold tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public boolean isGoldTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isGoldTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is an iron tool.
     *
     * @param item Item to check
     * @return true if the item is an iron tool, false otherwise
     */
    public boolean isIronTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isIronTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a diamond tool.
     *
     * @param item Item to check
     * @return true if the item is a diamond tool, false otherwise
     */
    public boolean isDiamondTool(ItemStack item) {
        return pluginRef.getMaterialMapStore().isDiamondTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is enchantable.
     *
     * @param item Item to check
     * @return true if the item is enchantable, false otherwise
     */
    public boolean isEnchantable(ItemStack item) {
        return pluginRef.getMaterialMapStore().isEnchantable(item.getType().getKey().getKey());
    }

    public boolean isSmeltable(ItemStack item) {
        return item != null && item.getType().isBlock() && pluginRef.getMaterialMapStore().isOre(item.getType());
    }

    public boolean isSmelted(ItemStack item) {
        if (item == null) {
            return false;
        }

        for (Recipe recipe : pluginRef.getServer().getRecipesFor(item)) {
            if (recipe instanceof FurnaceRecipe
                    && ((FurnaceRecipe) recipe).getInput().getType().isBlock()
                    && pluginRef.getMaterialMapStore().isOre(((FurnaceRecipe) recipe).getInput().getType())) {
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
    public boolean isSharable(ItemStack item) {
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
    public boolean isMiningDrop(ItemStack item) {
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
    public boolean isHerbalismDrop(ItemStack item) {
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
    public boolean isMobDrop(ItemStack item) {
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
    public boolean isWoodcuttingDrop(ItemStack item) {
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
    public boolean isMiscDrop(ItemStack item) {
        return pluginRef.getConfigManager().getConfigParty().getPartyItemShare().getItemShareMap().get(item.getType()) != null;
    }

    public boolean isMcMMOItem(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.hasLore() && itemMeta.getLore().contains("mcMMO Item");
    }

    public boolean isChimaeraWing(ItemStack item) {
        if (!isMcMMOItem(item)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(ChatColor.GOLD + pluginRef.getLocaleManager().getString("Item.ChimaeraWing.Name"));
    }
}
