package com.gmail.nossr50.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RecipeUtils {
    private RecipeUtils() {
    }

    /**
     * Walks the server's recipes like {@link Server#recipeIterator()}, leaving out the recipes the
     * server fails to convert to their Bukkit form.
     * <p>
     * Spigot 26.3 has no Bukkit counterpart for data-driven brewing recipes and throws
     * {@link AbstractMethodError} from {@link Iterator#next()} for each of them. That also breaks
     * {@link Server#getRecipesFor(org.bukkit.inventory.ItemStack)}, which walks the same iterator.
     *
     * @param server the server whose recipes to walk
     * @return an iterator over every recipe the server can hand out
     */
    public static @NotNull Iterator<Recipe> safeRecipeIterator(@NotNull Server server) {
        return new SafeRecipeIterator(server.recipeIterator());
    }

    private static final class SafeRecipeIterator implements Iterator<Recipe> {
        private final @NotNull Iterator<Recipe> serverRecipes;
        private @Nullable Recipe upcomingRecipe;

        private SafeRecipeIterator(@NotNull Iterator<Recipe> serverRecipes) {
            this.serverRecipes = serverRecipes;
        }

        @Override
        public boolean hasNext() {
            while (upcomingRecipe == null && serverRecipes.hasNext()) {
                try {
                    upcomingRecipe = serverRecipes.next();
                } catch (AbstractMethodError | UnsupportedOperationException ignored) {
                    // CraftBukkit steps past the recipe before converting it, so the failed
                    // entry is already consumed and the walk resumes with the one after it
                }
            }

            return upcomingRecipe != null;
        }

        @Override
        public Recipe next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            final Recipe recipe = upcomingRecipe;
            upcomingRecipe = null;
            return recipe;
        }
    }
}
