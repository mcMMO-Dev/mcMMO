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
    OTHER;

    public Material getDefaultMaterial() {
        switch (this) {
            case STRING:
                return Material.STRING;

            case LEATHER:
                return Material.LEATHER;

            case WOOD:
                return Material.WOOD;

            case STONE:
                return Material.COBBLESTONE;

            case IRON:
                return Material.IRON_INGOT;

            case GOLD:
                return Material.GOLD_INGOT;

            case DIAMOND:
                return Material.DIAMOND;

            case OTHER:
            default:
                return null;
        }
    }
}
