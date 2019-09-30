package com.gmail.nossr50.config.experience;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceRepair {

    private static final double REPAIR_XP_BASE_DEFAULT = 1000.0;

    private static final HashMap<ItemMaterialCategory, Double> ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT;

    static {
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT = new HashMap<>();
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.WOOD, 0.6);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.STONE, 1.3);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.IRON, 2.5);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.GOLD, 0.3);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.DIAMOND, 5.0);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.LEATHER, 1.6);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.STRING, 1.8);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.OTHER, 1.5);
    }

    @Setting(value = "Item-Material-Category-XP-Multiplier", comment = "The material of your item is determined by mcMMO and used to influence XP, " +
            "if your Item doesn't fit into a known category it will use OTHER." +
            "\nFor the most part, items belong to categories of materials that they are made out of.")
    private HashMap<ItemMaterialCategory, Double> itemMaterialXPMultiplier = ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT;

    @Setting(value = "Repair-XP-Base", comment = "The base amount of XP for repairing an item." +
            "\nThe repair XP formula is a simple multiplication of these 4 values in this order" +
            "\nThe % amount repaired (0.0 to 1.0)" +
            "\nThe Item XP multiplier defined in the Repair config (not this config)" +
            "\nThe Base Repair XP defined here (default 1000.0)" +
            "\nAnd finally, the XP multiplier of the item material category defined in this config." +
            "\nDefault value: " + REPAIR_XP_BASE_DEFAULT)
    private Double repairXPBase = REPAIR_XP_BASE_DEFAULT;

    public double getItemMaterialXPMultiplier(ItemMaterialCategory itemMaterialCategory) {
        return itemMaterialXPMultiplier.get(itemMaterialCategory);
    }

    public Double getRepairXPBase() {
        return repairXPBase;
    }
}