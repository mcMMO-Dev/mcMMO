package com.gmail.nossr50.datatypes.skills;

import org.bukkit.Material;

public enum MaterialType {
    STRING,
    LEATHER,
    WOOD,
    STONE,
    IRON,
    GOLD,
    DIAMOND,
    NETHER,
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

            case NETHER:
                if(Material.getMaterial("netherite_scrap") != null)
                    return Material.getMaterial("netherite_scrap");
                else
                    return Material.GOLD_INGOT;

            case OTHER:
            default:
                return null;
        }
    }
}
