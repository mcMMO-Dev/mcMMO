package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a 'Salvageable' item
 * Includes all the data needed for determining rewards from Salvage
 */
public class Salvageable {
    private final Material itemMaterial, salvagedItemMaterial;
    private final int maximumQuantity, minimumLevel;
    private final short maximumDurability, baseSalvageDurability;
    private final ItemType salvageItemType;
    private final ItemMaterialCategory salvageItemMaterialCategory;
    private final double xpMultiplier;

    public Salvageable(String itemRegisterKey, String salvagedMaterialRegisterKey) {
        this(Material.matchMaterial(itemRegisterKey), Material.matchMaterial(salvagedMaterialRegisterKey), 0, 1);
    }

    public Salvageable(String itemRegisterKey, String salvagedMaterialRegisterKey, int minimumLevel, int maximumQuantity) {
        this(Material.matchMaterial(itemRegisterKey), Material.matchMaterial(salvagedMaterialRegisterKey), minimumLevel, maximumQuantity);
    }

    public Salvageable(Material itemMaterial, Material salvagedItemMaterial, int minimumLevel, int maximumQuantity) {
        this.itemMaterial = itemMaterial;
        this.salvagedItemMaterial = salvagedItemMaterial;
        this.salvageItemType = determineItemType(itemMaterial);
        this.salvageItemMaterialCategory = determineMaterialType(salvagedItemMaterial);
        this.minimumLevel = Math.max(0, minimumLevel);
        this.maximumQuantity = Math.max(1, maximumQuantity);
        this.maximumDurability = itemMaterial.getMaxDurability();
        this.baseSalvageDurability = (short) (maximumDurability / maximumQuantity);
        this.xpMultiplier = Math.max(0, 1.0D);
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public Material getSalvagedItemMaterial() {
        return salvagedItemMaterial;
    }

    public ItemType getSalvageItemType() {
        return salvageItemType;
    }

    public ItemMaterialCategory getSalvageItemMaterialCategory() {
        return salvageItemMaterialCategory;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    public short getMaximumDurability() {
        return maximumDurability;
    }

    public short getBaseSalvageDurability() {
        return baseSalvageDurability;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    //TODO: Hacky work around below, it disgusts me
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
        Material type = item.getType();

        switch (type) {
            case BOW:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomBow(type);
        }
    }

    /**
     * Checks if the item is a sword.
     *
     * @param item Item to check
     * @return true if the item is a sword, false otherwise
     */
    public boolean isSword(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_SWORD:
            case GOLDEN_SWORD:
            case IRON_SWORD:
            case STONE_SWORD:
            case WOODEN_SWORD:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomSword(type);
        }
    }

    /**
     * Checks if the item is a hoe.
     *
     * @param item Item to check
     * @return true if the item is a hoe, false otherwise
     */
    public boolean isHoe(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_HOE:
            case GOLDEN_HOE:
            case IRON_HOE:
            case STONE_HOE:
            case WOODEN_HOE:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomHoe(type);
        }
    }

    /**
     * Checks if the item is a shovel.
     *
     * @param item Item to check
     * @return true if the item is a shovel, false otherwise
     */
    public boolean isShovel(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_SHOVEL:
            case GOLDEN_SHOVEL:
            case IRON_SHOVEL:
            case STONE_SHOVEL:
            case WOODEN_SHOVEL:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomShovel(type);
        }
    }

    /**
     * Checks if the item is an axe.
     *
     * @param item Item to check
     * @return true if the item is an axe, false otherwise
     */
    public boolean isAxe(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_AXE:
            case GOLDEN_AXE:
            case IRON_AXE:
            case STONE_AXE:
            case WOODEN_AXE:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomAxe(type);
        }
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param item Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public boolean isPickaxe(ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case DIAMOND_PICKAXE:
            case GOLDEN_PICKAXE:
            case IRON_PICKAXE:
            case STONE_PICKAXE:
            case WOODEN_PICKAXE:
                return true;

            default:
                return false;
            //return mcMMO.getModManager().isCustomPickaxe(type);
        }
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
        return isLeatherArmor(item) || isGoldArmor(item) || isIronArmor(item) || isDiamondArmor(item) || isChainmailArmor(item);
    }

    /**
     * Checks to see if an item is a leather armor piece.
     *
     * @param item Item to check
     * @return true if the item is leather armor, false otherwise
     */
    public boolean isLeatherArmor(ItemStack item) {
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
    public boolean isGoldArmor(ItemStack item) {
        switch (item.getType()) {
            case GOLDEN_BOOTS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
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
    public boolean isIronArmor(ItemStack item) {
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
    public boolean isDiamondArmor(ItemStack item) {
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
    public boolean isChainmailArmor(ItemStack item) {
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
    public boolean isMinecraftTool(ItemStack item) {
        return isStoneTool(item) || isWoodTool(item) || isGoldTool(item) || isIronTool(item) || isDiamondTool(item) || isStringTool(item) || item.getType() == Material.TRIDENT;
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public boolean isStoneTool(ItemStack item) {
        switch (item.getType()) {
            case STONE_AXE:
            case STONE_HOE:
            case STONE_PICKAXE:
            case STONE_SHOVEL:
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
    public boolean isWoodTool(ItemStack item) {
        switch (item.getType()) {
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
        switch (item.getType()) {
            case BOW:
            case CARROT_ON_A_STICK:
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
    public boolean isGoldTool(ItemStack item) {
        switch (item.getType()) {
            case GOLDEN_AXE:
            case GOLDEN_HOE:
            case GOLDEN_PICKAXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_SWORD:
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
    public boolean isIronTool(ItemStack item) {
        switch (item.getType()) {
            case BUCKET:
            case FLINT_AND_STEEL:
            case IRON_AXE:
            case IRON_HOE:
            case IRON_PICKAXE:
            case IRON_SHOVEL:
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
    public boolean isDiamondTool(ItemStack item) {
        switch (item.getType()) {
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_PICKAXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_SWORD:
                return true;

            default:
                return false;
        }
    }


}
