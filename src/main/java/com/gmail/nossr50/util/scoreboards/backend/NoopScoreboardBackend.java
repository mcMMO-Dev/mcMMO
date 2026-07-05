package com.gmail.nossr50.util.scoreboards.backend;

import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoopScoreboardBackend implements ScoreboardBackend {
    @Override
    public @NotNull ScoreboardBackendType getType() {
        return ScoreboardBackendType.NOOP;
    }

    @Override
    public void init() {
    }

    @Override
    public @NotNull Scoreboard createEventTargetBoard(final @NotNull Player player) {
        return player.getScoreboard();
    }

    @Override
    public @NotNull PlayerBoard createPlayerBoard(final @NotNull Player player,
            final @NotNull Scoreboard eventTargetBoard) {
        return new NoopPlayerBoard(player);
    }

    @Override
    public void setupPowerLevelTag(final @NotNull Player player) {
    }

    @Override
    public void removePowerLevelTag(final @NotNull Player player) {
    }

    @Override
    public void setPowerLevel(final @NotNull String playerName, final int powerLevel) {
    }

    @Override
    public boolean isPowerLevelTagActive() {
        return false;
    }

    @Override
    public void onPlayerBoardClosed(final @NotNull String playerName) {
    }

    @Override
    public @Nullable ScoreboardObjective getPacketPowerLevelObjective() {
        return null;
    }

    @Override
    public void shutdown() {
    }
}
