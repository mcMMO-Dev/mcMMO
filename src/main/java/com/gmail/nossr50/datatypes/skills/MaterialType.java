package com.gmail.nossr50.datatypes.skills;

import org.bukkit.Material;

public enum MaterialType {
    STRING,
    LEATHER,
    WOOD,
    STONE,
    IRON,
    COPPER,
    GOLD,
    DIAMOND,
    NETHERITE,
    PRISMARINE,
    OTHER;

    public Material getDefaultMaterial() {
        switch (this) {
            case STRING:
                return Material.STRING;

            case LEATHER:
                return Material.LEATHER;

            case WOOD:
                return Material.OAK_PLANKS;

            case STONE:
                return Material.COBBLESTONE;

            case IRON:
                return Material.IRON_INGOT;

            case GOLD:
                return Material.GOLD_INGOT;

            case DIAMOND:
                return Material.DIAMOND;

            case NETHERITE:
                if (Material.getMaterial("NETHERITE_SCRAP") != null) {
                    return Material.getMaterial("NETHERITE_SCRAP");
                } else {
                    return Material.DIAMOND;
                }
            case PRISMARINE:
                return Material.PRISMARINE_CRYSTALS;
            case COPPER:
                return Material.COPPER_INGOT;

            case OTHER:
            default:
                return null;
        }
    }
}
