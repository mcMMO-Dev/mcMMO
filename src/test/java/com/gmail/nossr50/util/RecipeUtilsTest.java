package com.gmail.nossr50.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
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
        final List<Recipe> walkedRecipes = drain(RecipeUtils.safeRecipeIterator(server));

        // Then - only the convertible recipes are seen, in server order
        assertThat(walkedRecipes).containsExactlyElementsOf(expectedRecipes);
    }

    @Test
    void safeRecipeIteratorShouldServeNextWithoutHasNext() {
        // Given - an unconvertible recipe ahead of the only usable one
        final Server server = serverWithRecipes(Arrays.asList(null, FIRST));

        // When - next is called without asking hasNext first
        final Recipe recipe = RecipeUtils.safeRecipeIterator(server).next();

        // Then - the usable recipe is returned
        assertThat(recipe).isSameAs(FIRST);
    }

    @Test
    void safeRecipeIteratorShouldNotSkipRecipesWhenHasNextIsRepeated() {
        // Given - two usable recipes
        final Iterator<Recipe> recipeIterator =
                RecipeUtils.safeRecipeIterator(serverWithRecipes(List.of(FIRST, SECOND)));

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
        final Iterator<Recipe> recipeIterator = RecipeUtils.safeRecipeIterator(server);

        // When / Then - asking for a recipe that does not exist follows the Iterator contract
        assertThatThrownBy(recipeIterator::next).isInstanceOf(NoSuchElementException.class);
    }
}
