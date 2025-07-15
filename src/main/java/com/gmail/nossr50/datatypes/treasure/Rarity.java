package com.gmail.nossr50.datatypes.treasure;

import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;

public enum Rarity {
    MYTHIC,
    LEGENDARY,
    EPIC,
    RARE,
    UNCOMMON,
    COMMON;

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
