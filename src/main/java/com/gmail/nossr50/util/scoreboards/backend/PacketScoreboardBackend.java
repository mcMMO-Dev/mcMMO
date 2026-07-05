package com.gmail.nossr50.util.scoreboards.backend;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import java.util.ArrayList;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketScoreboardBackend implements ScoreboardBackend {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    // Randomized per server start so it can never collide with a leftover objective of the same
    // name on a client - e.g. the old Bukkit implementation's 'mcmmo_pwrlvl' lingering after an
    // in-place upgrade, or another plugin's objective. Nothing is persisted server-side, so a
    // fresh name each startup leaves no orphans behind. Kept within 16 chars ("mcmmo_pwr_" (10)
    // + 5 hex = 15): Java 1.18 (21w37a) removed the objective name length cap, but older
    // clients connecting through ViaVersion still enforce it.
    private static final String POWER_OBJECTIVE = "mcmmo_pwr_" + java.util.UUID.randomUUID()
            .toString().replace("-", "").substring(0, 5);

    private @Nullable ScoreboardLibrary scoreboardLibrary;
    private @Nullable ObjectiveManager powerLevelObjectiveManager;
    private @Nullable ScoreboardObjective powerLevelObjective;

    @Override
    public @NotNull ScoreboardBackendType getType() {
        return ScoreboardBackendType.PACKET;
    }

    @Override
    public void init() {
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(mcMMO.p);
        } catch (NoPacketAdapterAvailableException e) {
            throw new RuntimeException("No packet adapter available for scoreboard-library", e);
        }
    }

    @Override
    public @NotNull Scoreboard createEventTargetBoard(final @NotNull Player player) {
        return player.getScoreboard();
    }

    @Override
    public @NotNull PlayerBoard createPlayerBoard(final @NotNull Player player,
            final @NotNull Scoreboard eventTargetBoard) {
        if (scoreboardLibrary == null || scoreboardLibrary.closed()) {
            return new NoopPlayerBoard(player);
        }

        return new PacketPlayerBoard(player, scoreboardLibrary.createSidebar(15));
    }

    @Override
    public void setupPowerLevelTag(final @NotNull Player player) {
        final ScoreboardObjective objective = getOrCreatePowerLevelObjective();

        if (objective == null || powerLevelObjectiveManager == null || powerLevelObjectiveManager.closed()) {
            return;
        }

        powerLevelObjectiveManager.addPlayer(player);
    }

    @Override
    public void removePowerLevelTag(final @NotNull Player player) {
        if (powerLevelObjectiveManager == null || powerLevelObjectiveManager.closed()) {
            return;
        }

        // removePlayer() needs a live connection; skipping it when the player has already
        // disconnected avoids an NPE in scoreboard-library's async tick (weak-key playerMap
        // GC race).
        if (player.isOnline()) {
            powerLevelObjectiveManager.removePlayer(player);
        }

        // Score removal is name-based and safe for offline players; always run it so departed
        // players don't accumulate in the objective's score map and flood joining players with
        // a large initial packet burst.
        if (powerLevelObjective != null) {
            powerLevelObjective.removeScore(player.getName());
        }
    }

    @Override
    public void setPowerLevel(final @NotNull String playerName, final int powerLevel) {
        final ScoreboardObjective objective = getOrCreatePowerLevelObjective();

        if (objective != null) {
            objective.score(playerName, powerLevel);
        }
    }

    @Override
    public boolean isPowerLevelTagActive() {
        return getOrCreatePowerLevelObjective() != null;
    }

    @Override
    public void onPlayerBoardClosed(final @NotNull String playerName) {
    }

    @Override
    public @Nullable ScoreboardObjective getPacketPowerLevelObjective() {
        return getOrCreatePowerLevelObjective();
    }

    private @Nullable ScoreboardObjective getOrCreatePowerLevelObjective() {
        if (!mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled()) {
            if (powerLevelObjectiveManager != null && !powerLevelObjectiveManager.closed()) {
                powerLevelObjectiveManager.close();
            }

            powerLevelObjectiveManager = null;
            powerLevelObjective = null;
            return null;
        }

        if (scoreboardLibrary == null || scoreboardLibrary.closed()) {
            return null;
        }

        if (powerLevelObjectiveManager == null || powerLevelObjectiveManager.closed()) {
            powerLevelObjectiveManager = scoreboardLibrary.createObjectiveManager();
            powerLevelObjective = powerLevelObjectiveManager.create(POWER_OBJECTIVE);
            powerLevelObjective.value(LEGACY.deserialize(
                    LocaleLoader.getString("Scoreboard.Header.PowerLevel")));
            powerLevelObjectiveManager.display(ObjectiveDisplaySlot.belowName(), powerLevelObjective);
            powerLevelObjectiveManager.addPlayers(new ArrayList<>(mcMMO.p.getServer().getOnlinePlayers()));
        }

        return powerLevelObjective;
    }

    @Override
    public void shutdown() {
        if (powerLevelObjectiveManager != null && !powerLevelObjectiveManager.closed()) {
            powerLevelObjectiveManager.close();
        }

        powerLevelObjectiveManager = null;
        powerLevelObjective = null;

        if (scoreboardLibrary != null && !scoreboardLibrary.closed()) {
            scoreboardLibrary.close();
        }

        scoreboardLibrary = null;
    }
}
