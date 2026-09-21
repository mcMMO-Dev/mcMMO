package com.gmail.nossr50.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Spigot 26.3 throws AbstractMethodError out of its recipe iterator for every data-driven brewing
 * recipe, so a walk over the server's recipes has to survive entries that cannot be converted.
 */
class RecipeUtilsTest {
    private static final Recipe FIRST = mock(Recipe.class, "first");
    private static final Recipe SECOND = mock(Recipe.class, "second");
    private static final Recipe THIRD = mock(Recipe.class, "third");
    private static final String SKIPPED_TWO_MESSAGE = LogUtils.DEBUG_STR
            + "Skipped 2 recipes the server could not convert to Bukkit recipes, first failure: "
            + "java.lang.AbstractMethodError: BrewingRecipe.toBukkitRecipe is abstract";

    private final Logger logger = mock(Logger.class);

    private static Server serverWithRecipes(List<Recipe> recipes) {
        final Server server = mock(Server.class);
        when(server.recipeIterator()).thenReturn(new ThrowingRecipeIterator(recipes));
        return server;
    }

    private static List<Recipe> drain(Iterator<Recipe> recipeIterator) {
        final List<Recipe> drained = new ArrayList<>();
        recipeIterator.forEachRemaining(drained::add);
        return drained;
    }

    /** A {@code null} in the server list is a recipe the server fails to convert. */
    static Stream<Arguments> recipeLists() {
        return Stream.of(
                Arguments.of("no recipes at all", List.of(), List.of()),
                Arguments.of("all convertible", List.of(FIRST, SECOND), List.of(FIRST, SECOND)),
                Arguments.of("unconvertible first", Arrays.asList(null, FIRST), List.of(FIRST)),
                Arguments.of("unconvertible in the middle", Arrays.asList(FIRST, null, SECOND),
                        List.of(FIRST, SECOND)),
                Arguments.of("unconvertible last", Arrays.asList(FIRST, null), List.of(FIRST)),
                Arguments.of("consecutive unconvertible run",
                        Arrays.asList(FIRST, null, null, null, SECOND, null, THIRD),
                        List.of(FIRST, SECOND, THIRD)),
                Arguments.of("nothing convertible", Arrays.asList(null, null), List.of())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("recipeLists")
    void safeRecipeIteratorShouldSkipRecipesTheServerCannotConvert(String scenario,
            List<Recipe> serverRecipes, List<Recipe> expectedRecipes) {
        // Given - a server whose recipe iterator throws for some of its recipes
        final Server server = serverWithRecipes(serverRecipes);

        // When - every recipe is walked
        final List<Recipe> walkedRecipes = drain(RecipeUtils.safeRecipeIterator(server, logger));

        // Then - only the convertible recipes are seen, in server order
        assertThat(walkedRecipes).containsExactlyElementsOf(expectedRecipes);
    }

    @Test
    void safeRecipeIteratorShouldServeNextWithoutHasNext() {
        // Given - an unconvertible recipe ahead of the only usable one
        final Server server = serverWithRecipes(Arrays.asList(null, FIRST));

        // When - next is called without asking hasNext first
        final Recipe recipe = RecipeUtils.safeRecipeIterator(server, logger).next();

        // Then - the usable recipe is returned
        assertThat(recipe).isSameAs(FIRST);
    }

    @Test
    void safeRecipeIteratorShouldNotSkipRecipesWhenHasNextIsRepeated() {
        // Given - two usable recipes
        final Server server = serverWithRecipes(List.of(FIRST, SECOND));
        final Iterator<Recipe> recipeIterator = RecipeUtils.safeRecipeIterator(server, logger);

        // When - hasNext is asked several times before each next
        assertThat(recipeIterator.hasNext()).isTrue();
        assertThat(recipeIterator.hasNext()).isTrue();

        // Then - no recipe is consumed by the repeated checks
        assertThat(recipeIterator.next()).isSameAs(FIRST);
        assertThat(recipeIterator.hasNext()).isTrue();
        assertThat(recipeIterator.next()).isSameAs(SECOND);
        assertThat(recipeIterator.hasNext()).isFalse();
    }

    @Test
    void safeRecipeIteratorShouldThrowWhenExhausted() {
        // Given - a server whose only recipe cannot be converted
        final Server server = serverWithRecipes(Arrays.asList((Recipe) null));
        final Iterator<Recipe> recipeIterator = RecipeUtils.safeRecipeIterator(server, logger);

        // When / Then - asking for a recipe that does not exist follows the Iterator contract
        assertThatThrownBy(recipeIterator::next).isInstanceOf(NoSuchElementException.class);
    }

    /**
     * Only the failure Spigot actually produces is tolerated. Anything else is a different
     * problem and has to surface instead of quietly shrinking the recipe list.
     */
    @Test
    void safeRecipeIteratorShouldNotSwallowOtherFailures() {
        // Given - a server iterator that fails in a way unrelated to recipe conversion
        final Iterator<Recipe> brokenServerRecipes = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Recipe next() {
                throw new UnsupportedOperationException("unrelated failure");
            }
        };
        final Server server = mock(Server.class);
        when(server.recipeIterator()).thenReturn(brokenServerRecipes);
        final Iterator<Recipe> recipeIterator = RecipeUtils.safeRecipeIterator(server, logger);

        // When / Then - the failure reaches the caller
        assertThatThrownBy(recipeIterator::hasNext)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("unrelated failure");
    }

    @Nested
    class SkippedRecipeReport {
        @Test
        void reportsTheSkippedCountOnceTheWalkCompletes() {
            // Given - two unconvertible recipes among usable ones
            final Server server = serverWithRecipes(Arrays.asList(FIRST, null, SECOND, null));

            // When - the walk runs to the end, with hasNext asked again afterwards
            final Iterator<Recipe> recipeIterator = RecipeUtils.safeRecipeIterator(server, logger);
            drain(recipeIterator);
            recipeIterator.hasNext();

            // Then - one debug line names the count and the first failure
            verify(logger, times(1)).info(SKIPPED_TWO_MESSAGE);
        }

        @Test
        void staysQuietWhenEveryRecipeConverts() {
            // Given - a server that converts everything, the normal case on Paper
            final Server server = serverWithRecipes(List.of(FIRST, SECOND));

            // When - the walk runs to the end
            drain(RecipeUtils.safeRecipeIterator(server, logger));

            // Then - nothing is logged
            verify(logger, never()).info(anyString());
        }

        @Test
        void staysQuietWhenTheCallerStopsEarly() {
            // Given - a caller that stops at the first usable recipe, as isSmelted does
            final Server server = serverWithRecipes(Arrays.asList(null, FIRST, null, SECOND));

            // When - only the first recipe is taken
            RecipeUtils.safeRecipeIterator(server, logger).next();

            // Then - a partial walk reports nothing, its count would be misleading
            verify(logger, never()).info(anyString());
        }
    }
}
