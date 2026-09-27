package com.gmail.nossr50.datatypes.database;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A player stored in the database. Only the UUID tells players apart: names change hands, and
 * everyone who lost theirs to another player shares the same placeholder name.
 *
 * @param playerName the name stored for the player
 * @param uuid the player's UUID, or null when none can be read: data stored before mcMMO kept
 *         UUIDs, a malformed value, or a database manager that does not list UUIDs
 */
public record PlayerNameAndUUID(@NotNull String playerName, @Nullable UUID uuid) {
}
