package com.gmail.nossr50.util.scoreboards.backend;

import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScoreboardBackend {
    @NotNull ScoreboardBackendType getType();

    void init();

    @NotNull Scoreboard createEventTargetBoard(@NotNull Player player);

    @NotNull PlayerBoard createPlayerBoard(@NotNull Player player, @NotNull Scoreboard eventTargetBoard);

    void setupPowerLevelTag(@NotNull Player player);

    void removePowerLevelTag(@NotNull Player player);

    void setPowerLevel(@NotNull String playerName, int powerLevel);

    boolean isPowerLevelTagActive();

    void onPlayerBoardClosed(@NotNull String playerName);

    @Nullable ScoreboardObjective getPacketPowerLevelObjective();

    void shutdown();
}
