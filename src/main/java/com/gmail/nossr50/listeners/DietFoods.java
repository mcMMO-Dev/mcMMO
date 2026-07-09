package com.gmail.nossr50.listeners;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Pure mapping from eaten food to the diet sub-skill that boosts it, extracted from the food
 * level change listener so the tables are testable. Historically some foods were grouped by
 * how "common" they are (3-rank vs 5-rank foods); both groups receive the same Farmer's Diet
 * treatment today, so the split is not preserved here.
 */
final class DietFoods {

    enum Diet {
        FARMERS,
        FISHERMANS,
        NONE
    }

    private DietFoods() {
    }

    /**
     * Maps a food material to the diet sub-skill that applies to it.
     */
    static @NotNull Diet dietFor(@NotNull Material food) {
        return switch (food) {
            case GLOW_BERRIES,
                 BAKED_POTATO,
                 BEETROOT,
                 BREAD,
                 CARROT,
                 GOLDEN_CARROT,
                 MUSHROOM_STEW,
                 PUMPKIN_PIE,
                 COOKIE,
                 MELON_SLICE,
                 POISONOUS_POTATO,
                 POTATO -> Diet.FARMERS;
            case COD,
                 SALMON,
                 TROPICAL_FISH,
                 COOKED_COD,
                 COOKED_SALMON -> Diet.FISHERMANS;
            default -> Diet.NONE;
        };
    }

    /**
     * Determines which held item is being eaten. The main hand wins when both hands hold food,
     * matching vanilla's eating priority.
     *
     * @return the eaten food material, or null when neither hand holds food
     */
    static @Nullable Material eatenFood(@NotNull Material mainHand, @NotNull Material offHand,
            @NotNull Predicate<Material> isFood) {
        if (isFood.test(mainHand)) {
            return mainHand;
        }

        if (isFood.test(offHand)) {
            return offHand;
        }

        return null;
    }
}
