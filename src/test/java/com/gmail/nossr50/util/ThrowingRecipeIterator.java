package com.gmail.nossr50.util;

import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Mimics CraftBukkit's RecipeIterator on a server that cannot convert every recipe to its Bukkit
 * form: a {@code null} entry stands for such a recipe, and {@link #next()} steps past it before
 * throwing, exactly as the real iterator advances before calling toBukkitRecipe.
 */
public final class ThrowingRecipeIterator implements Iterator<Recipe> {
    private final Iterator<@Nullable Recipe> recipes;

    public ThrowingRecipeIterator(@NotNull List<@Nullable Recipe> recipes) {
        this.recipes = recipes.iterator();
    }

    @Override
    public boolean hasNext() {
        return recipes.hasNext();
    }

    @Override
    public Recipe next() {
        final Recipe recipe = recipes.next();

        if (recipe == null) {
            throw new AbstractMethodError("BrewingRecipe.toBukkitRecipe is abstract");
        }

        return recipe;
    }
}
