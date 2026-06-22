package com.gmail.nossr50.util.scoreboards.backend;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.locale.LocaleLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitScoreboardBackend implements ScoreboardBackend {
    private static final String POWER_OBJECTIVE = "mcmmo_" + java.util.UUID.randomUUID()
            .toString().replace("-", "").substring(0, 8);
    private static final String DISPLAY_NAME = "powerLevel";

    private final Map<String, BukkitPlayerBoard> activeBoards = new ConcurrentHashMap<>();
    private @Nullable Objective powerObjective;

    @Override
    public @NotNull ScoreboardBackendType getType() {
        return ScoreboardBackendType.BUKKIT;
    }

    @Override
    public void init() {
    }

    @Override
    public @NotNull Scoreboard createEventTargetBoard(final @NotNull Player player) {
        final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return player.getScoreboard();
        }
        return scoreboardManager.getNewScoreboard();
    }

    @Override
    public @NotNull PlayerBoard createPlayerBoard(final @NotNull Player player,
            final @NotNull Scoreboard eventTargetBoard) {
        final BukkitPlayerBoard playerBoard = new BukkitPlayerBoard(player, eventTargetBoard);
        activeBoards.put(player.getName(), playerBoard);
        return playerBoard;
    }

    @Override
    public void setupPowerLevelTag(final @NotNull Player player) {
        // No action required. BukkitPlayerBoard creates its local below-name objective during setup.
    }

    @Override
    public void removePowerLevelTag(final @NotNull Player player) {
        final Objective objective = getOrCreatePowerObjective();

        if (objective != null) {
            objective.getScoreboard().resetScores(player.getName());
        }
    }

    @Override
    public void setPowerLevel(final @NotNull String playerName, final int powerLevel) {
        final Objective objective = getOrCreatePowerObjective();

        if (objective != null) {
            objective.getScore(playerName).setScore(powerLevel);
        }

        for (BukkitPlayerBoard board : activeBoards.values()) {
            board.updatePowerLevel(playerName, powerLevel);
        }
    }

    @Override
    public boolean isPowerLevelTagActive() {
        return getOrCreatePowerObjective() != null;
    }

    @Override
    public void onPlayerBoardClosed(final @NotNull String playerName) {
        activeBoards.remove(playerName);
    }

    @Override
    public @Nullable ScoreboardObjective getPacketPowerLevelObjective() {
        return null;
    }

    private @Nullable Objective getOrCreatePowerObjective() {
        if (!mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled()) {
            if (powerObjective != null) {
                try {
                    powerObjective.unregister();
                } catch (IllegalStateException ignored) {
                }
            }
            powerObjective = null;
            return null;
        }

        final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            return null;
        }

        final Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
        Objective objective = mainScoreboard.getObjective(POWER_OBJECTIVE);

        if (objective == null) {
            objective = mainScoreboard.registerNewObjective(POWER_OBJECTIVE, "dummy", DISPLAY_NAME);
            objective.setDisplayName(LocaleLoader.getString("Scoreboard.Header.PowerLevel"));
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        powerObjective = objective;
        return objective;
    }

    @Override
    public void shutdown() {
        for (BukkitPlayerBoard board : activeBoards.values()) {
            board.close();
        }
        activeBoards.clear();

        if (powerObjective != null) {
            try {
                powerObjective.unregister();
            } catch (IllegalStateException e) {
                LogUtils.debug(mcMMO.p.getLogger(), "Power objective was already unregistered.");
            }
        }
        powerObjective = null;
    }
}
