package com.gmail.nossr50.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
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
     * @param logger receives a debug line when a completed walk had to leave recipes out
     * @return an iterator over every recipe the server can hand out
     */
    public static @NotNull Iterator<Recipe> safeRecipeIterator(@NotNull Server server,
            @NotNull Logger logger) {
        return new SafeRecipeIterator(server.recipeIterator(), logger);
    }

    private static final class SafeRecipeIterator implements Iterator<Recipe> {
        private final @NotNull Iterator<Recipe> serverRecipes;
        private final @NotNull Logger logger;
        private @Nullable Recipe upcomingRecipe;
        private @Nullable AbstractMethodError firstFailure;
        private int skippedRecipes;

        private SafeRecipeIterator(@NotNull Iterator<Recipe> serverRecipes,
                @NotNull Logger logger) {
            this.serverRecipes = serverRecipes;
            this.logger = logger;
        }

        @Override
        public boolean hasNext() {
            while (upcomingRecipe == null && serverRecipes.hasNext()) {
                try {
                    upcomingRecipe = serverRecipes.next();
                } catch (AbstractMethodError failure) {
                    // CraftBukkit steps past the recipe before converting it, so the failed
                    // entry is already consumed and the walk resumes with the one after it
                    if (firstFailure == null) {
                        firstFailure = failure;
                    }

                    skippedRecipes++;
                }
            }

            if (upcomingRecipe == null) {
                reportSkippedRecipes();
            }

            return upcomingRecipe != null;
        }

        /**
         * Without this line a server that fails to convert a crafting recipe would quietly lower
         * Repair and Salvage quantities with nothing in the log to explain it.
         */
        private void reportSkippedRecipes() {
            if (firstFailure == null) {
                return;
            }

            LogUtils.debug(logger, "Skipped " + skippedRecipes
                    + " recipes the server could not convert to Bukkit recipes, first failure: "
                    + firstFailure);
            // hasNext keeps being legal on an exhausted iterator, report the walk only once
            firstFailure = null;
            skippedRecipes = 0;
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
