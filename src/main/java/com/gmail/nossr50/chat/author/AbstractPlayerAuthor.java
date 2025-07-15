package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.util.text.TextUtils;
import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlayerAuthor implements Author {
    private final @NotNull Player player;
    private final @NotNull Map<ChatChannel, String> sanitizedNameCache;
    private @NotNull String lastKnownDisplayName;

    public AbstractPlayerAuthor(@NotNull Player player) {
        this.player = player;
        this.lastKnownDisplayName = player.getDisplayName();
        this.sanitizedNameCache = new HashMap<>();
    }

    /**
     * Returns true if a players display name has changed
     *
     * @return true if the players display name has changed
     */
    private boolean hasPlayerDisplayNameChanged() {
        return !player.getDisplayName().equals(lastKnownDisplayName);
    }

    /**
     * Player display names can change and this method will update the last known display name of
     * this player
     */
    private void updateLastKnownDisplayName() {
        lastKnownDisplayName = player.getDisplayName();
    }

    /**
     * Gets a sanitized name for a channel Sanitized names are names that are friendly to the
     * {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer} Sanitized names
     * for authors are cached by channel and are only created as needed Sanitized names will update
     * if a players display name has updated
     *
     * @param chatChannel target chat channel
     * @return the sanitized name for a player
     */
    protected @NotNull String getSanitizedName(@NotNull ChatChannel chatChannel,
            boolean useDisplayName) {
        //Already in cache
        if (sanitizedNameCache.containsKey(chatChannel)) {
            //Update cache
            if (useDisplayName && hasPlayerDisplayNameChanged()) {
                updateLastKnownDisplayName();
                updateSanitizedNameCache(chatChannel, true);
            }
        } else {
            //Update last known display name
            if (useDisplayName && hasPlayerDisplayNameChanged()) {
                updateLastKnownDisplayName();
            }

            //Add cache entry
            updateSanitizedNameCache(chatChannel, useDisplayName);
        }

        return sanitizedNameCache.get(chatChannel);
    }

    /**
     * Update the sanitized name cache This will add entries if one didn't exit Sanitized names are
     * associated with a {@link ChatChannel} as different chat channels have different chat name
     * settings
     *
     * @param chatChannel target chat channel
     * @param useDisplayName whether to use this authors display name
     */
    private void updateSanitizedNameCache(@NotNull ChatChannel chatChannel,
            boolean useDisplayName) {
        if (useDisplayName) {
            sanitizedNameCache.put(chatChannel,
                    TextUtils.sanitizeForSerializer(player.getDisplayName()));
        } else {
            //No need to sanitize a basic String
            sanitizedNameCache.put(chatChannel, player.getName());
        }
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NonNull UUID uuid() {
        return player.getUniqueId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractPlayerAuthor that = (AbstractPlayerAuthor) o;
        return Objects.equal(player, that.player) && Objects.equal(
                lastKnownDisplayName,
                that.lastKnownDisplayName) && Objects.equal(
                sanitizedNameCache, that.sanitizedNameCache);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player, lastKnownDisplayName, sanitizedNameCache);
    }
}
