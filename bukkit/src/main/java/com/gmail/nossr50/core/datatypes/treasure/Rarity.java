package com.gmail.nossr50.core.datatypes.treasure;

public enum Rarity {
    RECORD,
    LEGENDARY,
    EPIC,
    RARE,
    UNCOMMON,
    COMMON;

    public static Rarity getRarity(String string) {
        try {
            return valueOf(string);
        }
        catch (IllegalArgumentException ex) {
            return COMMON;
        }
    }
}
