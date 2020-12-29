package com.gmail.nossr50.datatypes.treasure;

import org.jetbrains.annotations.NotNull;

public enum Rarity {
    MYTHIC,
    LEGENDARY,
    EPIC,
    RARE,
    UNCOMMON,
    COMMON;

    public static @NotNull Rarity getRarity(@NotNull String string) {
        try {
            return valueOf(string);
        }
        catch (IllegalArgumentException ex) {
            return COMMON;
        }
    }
}
