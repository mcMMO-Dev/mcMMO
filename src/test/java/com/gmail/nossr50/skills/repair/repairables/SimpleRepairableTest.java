package com.gmail.nossr50.skills.repair.repairables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SimpleRepairableTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(SimpleRepairableTest.class.getName());
    private static final short DIAMOND_PICKAXE_DURABILITY = 1561;
    private static final int NOT_CONFIGURED = -1;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private static SimpleRepairable diamondPickaxeRepairable(int configuredMinimumQuantity) {
        return new SimpleRepairable(Material.DIAMOND_PICKAXE, Material.DIAMOND, "Diamond", 0,
                DIAMOND_PICKAXE_DURABILITY, ItemType.TOOL, MaterialType.DIAMOND, 1.0,
                configuredMinimumQuantity);
    }

    /** Serves a fresh iterator per walk, the way a real server does. */
    private void serverRecipesAre(List<Recipe> recipes) {
        when(server.recipeIterator()).thenAnswer(invocation -> recipes.iterator());
    }

    private static ShapedRecipe diamondPickaxeRecipe() {
        final RecipeChoice diamond = new RecipeChoice.MaterialChoice(Material.DIAMOND);
        final RecipeChoice stick = new RecipeChoice.MaterialChoice(Material.STICK);
        final ShapedRecipe pickaxeRecipe = mock(ShapedRecipe.class);
        when(pickaxeRecipe.getResult()).thenReturn(new ItemStack(Material.DIAMOND_PICKAXE));
        when(pickaxeRecipe.getChoiceMap()).thenReturn(
                Map.of('a', diamond, 'b', diamond, 'c', diamond, 'd', stick, 'e', stick));
        return pickaxeRecipe;
    }

    /**
     * Counting ingredients converts every recipe on the server, thousands of allocations per
     * walk, and it used to happen on each repair. The count is worked out once per repairable
     * and kept.
     */
    @Nested
    class GetMinimumQuantity {
        @Test
        void walksTheServerRecipesOnlyOnceAcrossRepeatedCalls() {
            // Given - a repairable with no configured minimum quantity
            serverRecipesAre(List.of(diamondPickaxeRecipe()));
            final SimpleRepairable repairable = diamondPickaxeRepairable(NOT_CONFIGURED);

            // When - the quantity is asked for repeatedly, as every repair does
            final int firstQuantity = repairable.getMinimumQuantity();
            final int secondQuantity = repairable.getMinimumQuantity();
            final short baseRepairDurability = repairable.getBaseRepairDurability(
                    new ItemStack(Material.DIAMOND_PICKAXE));

            // Then - the recipe count is right each time and the server was walked once
            assertThat(firstQuantity).isEqualTo(3);
            assertThat(secondQuantity).isEqualTo(3);
            assertThat(baseRepairDurability).isEqualTo((short) (DIAMOND_PICKAXE_DURABILITY / 3));
            verify(server, times(1)).recipeIterator();
        }

        @Test
        void remembersTheFallbackOfOneWhenNoRecipeUsesTheRepairMaterial() {
            // Given - a server with no recipe for the item; the count of zero becomes one,
            // and that fallback must be remembered too or every repair walks again
            serverRecipesAre(List.of());
            final SimpleRepairable repairable = diamondPickaxeRepairable(NOT_CONFIGURED);

            // When - the quantity is asked for twice
            final int firstQuantity = repairable.getMinimumQuantity();
            final int secondQuantity = repairable.getMinimumQuantity();

            // Then - the fallback is served both times from a single walk
            assertThat(firstQuantity).isEqualTo(1);
            assertThat(secondQuantity).isEqualTo(1);
            verify(server, times(1)).recipeIterator();
        }

        @Test
        void usesTheConfiguredQuantityWithoutWalkingTheServerRecipes() {
            // Given - MinimumQuantity set in the repair config
            serverRecipesAre(List.of(diamondPickaxeRecipe()));
            final SimpleRepairable repairable = diamondPickaxeRepairable(2);

            // When - the quantity is asked for
            final int quantity = repairable.getMinimumQuantity();

            // Then - the config wins over the recipe and the server is never walked
            assertThat(quantity).isEqualTo(2);
            verify(server, never()).recipeIterator();
        }

        /**
         * The quantity divides the item's durability to get the base repair amount, so a zero
         * or negative value from a config or another plugin must never be served.
         */
        @ParameterizedTest(name = "configured quantity {0}")
        @ValueSource(ints = {0, -1, -4, Integer.MIN_VALUE})
        void fallsBackToTheRecipeWhenTheConfiguredQuantityIsNotPositive(int configuredQuantity) {
            // Given - an unusable configured quantity
            serverRecipesAre(List.of(diamondPickaxeRecipe()));
            final SimpleRepairable repairable = diamondPickaxeRepairable(configuredQuantity);

            // When - the quantity and the base repair amount are asked for
            final int quantity = repairable.getMinimumQuantity();
            final short baseRepairDurability = repairable.getBaseRepairDurability(
                    new ItemStack(Material.DIAMOND_PICKAXE));

            // Then - the recipe count is used and the division is safe
            assertThat(quantity).isEqualTo(3);
            assertThat(baseRepairDurability).isEqualTo((short) (DIAMOND_PICKAXE_DURABILITY / 3));
        }

        @Test
        void doesNotShareTheRememberedQuantityBetweenRepairables() {
            // Given - two repairables for different items
            serverRecipesAre(List.of(diamondPickaxeRecipe()));
            final SimpleRepairable pickaxe = diamondPickaxeRepairable(NOT_CONFIGURED);
            final SimpleRepairable shovel = new SimpleRepairable(Material.DIAMOND_SHOVEL,
                    Material.DIAMOND, "Diamond", 0, (short) 1561, ItemType.TOOL,
                    MaterialType.DIAMOND, 1.0, NOT_CONFIGURED);

            // When - both are asked
            final int pickaxeQuantity = pickaxe.getMinimumQuantity();
            final int shovelQuantity = shovel.getMinimumQuantity();

            // Then - each reflects its own recipe; the shovel has none here, so it falls back
            assertThat(pickaxeQuantity).isEqualTo(3);
            assertThat(shovelQuantity).isEqualTo(1);
        }
    }
}
