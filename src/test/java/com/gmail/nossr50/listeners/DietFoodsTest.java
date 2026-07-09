package com.gmail.nossr50.listeners;

import static org.assertj.core.api.Assertions.assertThat;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Contract coverage for the eaten-food resolution and the food-to-diet-skill mapping used by
 * the food level change listener.
 */
class DietFoodsTest {

    @ParameterizedTest
    @CsvSource({
            "GLOW_BERRIES, FARMERS",
            "BAKED_POTATO, FARMERS",
            "BEETROOT, FARMERS",
            "BREAD, FARMERS",
            "CARROT, FARMERS",
            "GOLDEN_CARROT, FARMERS",
            "MUSHROOM_STEW, FARMERS",
            "PUMPKIN_PIE, FARMERS",
            "COOKIE, FARMERS",
            "MELON_SLICE, FARMERS",
            "POISONOUS_POTATO, FARMERS",
            "POTATO, FARMERS",
            "COD, FISHERMANS",
            "SALMON, FISHERMANS",
            "TROPICAL_FISH, FISHERMANS",
            "COOKED_COD, FISHERMANS",
            "COOKED_SALMON, FISHERMANS",
            "COOKED_BEEF, NONE",
            "GOLDEN_APPLE, NONE",
            "STONE, NONE",
    })
    void dietForShouldMapFoodsToTheirDietSkill(Material food, DietFoods.Diet expected) {
        // Given - a material a player just ate
        // When - the diet mapping is resolved
        // Then - it matches the historical listener table
        assertThat(DietFoods.dietFor(food)).isEqualTo(expected);
    }

    @Test
    void eatenFoodShouldPreferTheMainHandWhenBothHandsHoldFood() {
        // Given - food in both hands
        // When - the eaten food is resolved
        final Material eaten = DietFoods.eatenFood(Material.BREAD, Material.COOKED_COD,
                material -> true);

        // Then - the main hand wins, matching vanilla eating priority
        assertThat(eaten).isEqualTo(Material.BREAD);
    }

    @Test
    void eatenFoodShouldFallBackToTheOffHandWhenTheMainHandHoldsNoFood() {
        // Given - a sword in the main hand and food in the off hand
        // When - the eaten food is resolved
        final Material eaten = DietFoods.eatenFood(Material.IRON_SWORD, Material.COOKED_COD,
                material -> material == Material.COOKED_COD);

        // Then - the off hand food is used
        assertThat(eaten).isEqualTo(Material.COOKED_COD);
    }

    @Test
    void eatenFoodShouldReturnNullWhenNeitherHandHoldsFood() {
        // Given - no food in either hand
        // When - the eaten food is resolved
        // Then - null signals the event is not about eating
        assertThat(DietFoods.eatenFood(Material.IRON_SWORD, Material.SHIELD, material -> false))
                .isNull();
    }
}
