package com.gmail.nossr50.datatypes.skills;

public enum ModConfigType {
    BLOCKS,
    TOOLS,
    ARMOR,
    UNKNOWN;

    public static ModConfigType getModConfigType(String materialName) {
        if (materialName.contains("HELM") || (materialName.contains("CHEST") && !materialName.contains("CHESTNUT")) || materialName.contains("LEGS") || materialName.contains("LEGGINGS") || materialName.contains("BOOT")) {
            return ARMOR;
        } else if (materialName.contains("PICKAXE") || materialName.contains("AXE") || (materialName.contains("BOW") && !materialName.contains("BOWL")) || materialName.contains("HOE") || materialName.contains("SHOVEL") || materialName.contains("SWORD")) {
            return TOOLS;
        } else if (materialName.contains("LOG") || materialName.contains("LEAVES") || materialName.contains("FLOWER") || materialName.contains("PLANT") || materialName.contains("CROP") || materialName.contains("ORE") || materialName.contains("DIRT") || materialName.contains("SAND") || materialName.contains("GRASS")) {
            return BLOCKS;
        }

        return UNKNOWN;
    }
}
