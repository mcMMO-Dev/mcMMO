package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceRepair {

    private static final double REPAIR_XP_BASE_DEFAULT = 1000.0D;

    private static final HashMap<String, Double> ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT;

    static {
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT = new HashMap<>();
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.WOOD.toString(), 0.6D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.STONE.toString(), 1.3D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.IRON.toString(), 2.5D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.GOLD.toString(), 0.3D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.DIAMOND.toString(), 5.0D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.LEATHER.toString(), 1.6D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.STRING.toString(), 1.8D);
        ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT.put(ItemMaterialCategory.OTHER.toString(), 1.5D);
    }

    @Setting(value = "Item-Material-Category-XP-Multiplier", comment = "The material of your item is determined by mcMMO and used to influence XP, " +
            "if your Item doesn't fit into a known category it will use OTHER." +
            "\nFor the most part, items belong to categories of materials that they are made out of.")
    private HashMap<String, Double> itemMaterialXPMultiplier = ITEM_MATERIAL_XP_MULTIPLIER_DEFAULT;

    @Setting(value = "Repair-XP-Base", comment = "The base amount of XP for repairing an item." +
            "\nThe repair XP formula is a simple multiplication of these 4 values in this order" +
            "\nThe % amount repaired (0.0 to 1.0)" +
            "\nThe Item XP multiplier defined in the Repair config (not this config)" +
            "\nThe Base Repair XP defined here (default 1000.0)" +
            "\nAnd finally, the XP multiplier of the item material category defined in this config." +
            "\nDefault value: " + REPAIR_XP_BASE_DEFAULT)
    private double repairXPBase = REPAIR_XP_BASE_DEFAULT;

    public HashMap<String, Double> getItemMaterialXPMultiplier() {
        return itemMaterialXPMultiplier;
    }

    public double getRepairXPBase() {
        return repairXPBase;
    }
}