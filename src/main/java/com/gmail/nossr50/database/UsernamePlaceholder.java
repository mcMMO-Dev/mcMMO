package com.gmail.nossr50.database;

import org.jetbrains.annotations.Nullable;

/**
 * The name a stored player is given once another player holds their name. It is longer than the
 * 16 characters a Minecraft name allows, so no player can have it, and looking a player up by it
 * finds no one.
 */
public final class UsernamePlaceholder {
    public static final String INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_";

    /** The spelling FlatFile wrote before both databases shared one placeholder. */
    static final String LEGACY_FLATFILE_INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_'";

    private UsernamePlaceholder() {
    }

    /**
     * @return true for either spelling of the placeholder, ignoring case, since names are
     *         matched ignoring case everywhere else
     */
    public static boolean isInvalidOldUsername(@Nullable String playerName) {
        return INVALID_OLD_USERNAME.equalsIgnoreCase(playerName)
                || LEGACY_FLATFILE_INVALID_OLD_USERNAME.equalsIgnoreCase(playerName);
    }
}
