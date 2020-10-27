package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.config.Config;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AdminAuthor implements Author {

    private final @NotNull Player player;
    private @Nullable String overrideName;

    public AdminAuthor(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public @NotNull String getAuthoredName() {
        if(overrideName != null) {
            return overrideName;
        } else {
            if(Config.getInstance().getAdminDisplayNames()) {
                return player.getDisplayName();
            } else {
                return player.getName();
            }
        }
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @Nullable String getOverrideName() {
        return overrideName;
    }

    @Override
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

    @Override
    public @NonNull UUID uuid() {
        return player.getUniqueId();
    }
}
