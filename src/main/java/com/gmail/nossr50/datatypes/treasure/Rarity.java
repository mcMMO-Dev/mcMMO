package com.gmail.nossr50.datatypes.treasure;

import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Rarity {
    MYTHIC,
    LEGENDARY,
    EPIC,
    RARE,
    UNCOMMON,
    COMMON;

    /**
     * Matches a rarity string to a {@link Rarity}, or returns null when the string is not a
     * recognized rarity. The legacy "Records" name maps to {@link #MYTHIC}. Unlike
     * {@link #getRarity(String)} this never falls back to {@link #COMMON} and has no side
     * effects, so callers can distinguish a misconfigured rarity from a real one.
     */
    public static @Nullable Rarity tryMatch(@NotNull String string) {
        if (string.equalsIgnoreCase("Records")) {
            return MYTHIC;
        }
        try {
            return valueOf(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static @NotNull Rarity getRarity(@NotNull String string) {
        if (string.equalsIgnoreCase("Records")) {
            mcMMO.p.getLogger()
                    .severe("Entries in fishing treasures have Records set as rarity, however Records was renamed to Mythic. Please update your treasures to read MYTHIC instead of RECORDS for rarity, or delete the config file to regenerate a new one.");
            return Rarity.MYTHIC; //People that copy paste their configs will have Records interpretted as Mythic
        }
        try {
            return valueOf(string);
        } catch (IllegalArgumentException ex) {
            return COMMON;
        }
    }
}
