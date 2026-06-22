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
    private static final String POWER_OBJECTIVE = "mcmmo_power_" + java.util.UUID.randomUUID()
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
        if (powerLevelObjectiveManager == null || powerLevelObjectiveManager.closed() || !player.isOnline()) {
            return;
        }

        powerLevelObjectiveManager.removePlayer(player);

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
