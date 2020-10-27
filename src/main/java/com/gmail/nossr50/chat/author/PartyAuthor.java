package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.config.Config;
import com.google.common.base.Objects;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PartyAuthor implements Author {

    private final @NotNull Player player;
    private @Nullable String overrideName;

    public PartyAuthor(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public @NotNull String getAuthoredName() {
        if(overrideName != null) {
            return overrideName;
        } else {
            if(Config.getInstance().getPartyDisplayNames()) {
                return player.getDisplayName();
            } else {
                return player.getName();
            }
        }
    }

    /**
     * Set the name of this author
     * @param newName value of the new name
     */
    public void setName(@NotNull String newName) {
        overrideName = newName;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NonNull UUID uuid() {
        return player.getUniqueId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyAuthor that = (PartyAuthor) o;
        return Objects.equal(player, that.player) &&
                Objects.equal(overrideName, that.overrideName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player, overrideName);
    }
}
