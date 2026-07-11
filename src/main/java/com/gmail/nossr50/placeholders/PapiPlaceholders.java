package com.gmail.nossr50.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Applies PlaceholderAPI placeholders to arbitrary text for a player. Callable whether or not
 * PlaceholderAPI is installed; without it the text is returned unchanged.
 */
public final class PapiPlaceholders {
    private PapiPlaceholders() {
    }

    /**
     * Replaces PlaceholderAPI placeholders in the given text for the given player.
     *
     * @param player the player providing the placeholder context
     * @param text the text to resolve
     * @return the text with placeholders resolved, or the unchanged text when PlaceholderAPI
     * is not enabled
     */
    public static @NotNull String replace(@NotNull Player player, @NotNull String text) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return text;
        }
        return Bridge.setPlaceholders(player, text);
    }

    /**
     * PlaceholderAPI types stay behind this nested class so they are only loaded after the
     * plugin check has passed; touching them earlier would fail on servers without
     * PlaceholderAPI installed.
     */
    private static final class Bridge {
        private static String setPlaceholders(Player player, String text) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
    }
}
