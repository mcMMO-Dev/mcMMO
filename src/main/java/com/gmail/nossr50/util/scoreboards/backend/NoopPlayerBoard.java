package com.gmail.nossr50.util.scoreboards.backend;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoopPlayerBoard implements PlayerBoard {
    public NoopPlayerBoard(final @NotNull Player player) {
    }

    @Override
    public @Nullable Scoreboard show() {
        return null;
    }

    @Override
    public void hide(final @NotNull Player targetPlayer, final @Nullable Scoreboard targetBoard) {
    }

    @Override
    public boolean isShown() {
        return false;
    }

    @Override
    public void setTitle(final @NotNull String displayName) {
    }

    @Override
    public void draw(final @NotNull List<SidebarLine> lines) {
    }

    @Override
    public void close() {
    }
}
