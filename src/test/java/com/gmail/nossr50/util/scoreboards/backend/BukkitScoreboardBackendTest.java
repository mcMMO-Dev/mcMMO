package com.gmail.nossr50.util.scoreboards.backend;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Covers removal of the persisted main-scoreboard power level objective. The objective and its
 * below-name display slot persist in the world's scoreboard data, so a leftover from an older
 * run keeps rendering below nametags until something unregisters it.
 */
class BukkitScoreboardBackendTest {
    private static final String POWER_OBJECTIVE_NAME = "mcmmo_pwrlvl";

    private MockedStatic<Bukkit> bukkitMock;
    private ScoreboardManager scoreboardManager;
    private Scoreboard mainScoreboard;

    @BeforeEach
    void setUp() {
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(Logger.getLogger("mcMMOTestLogger"));

        scoreboardManager = mock(ScoreboardManager.class);
        mainScoreboard = mock(Scoreboard.class);
        when(scoreboardManager.getMainScoreboard()).thenReturn(mainScoreboard);

        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(Bukkit::getScoreboardManager).thenReturn(scoreboardManager);
    }

    @AfterEach
    void tearDown() {
        bukkitMock.close();
        mcMMO.p = null;
    }

    @Test
    void removeLeftoverPowerObjectiveShouldUnregisterPersistedObjective() {
        // Given - a leftover power level objective persisted on the main scoreboard
        final Objective leftoverObjective = mock(Objective.class);
        when(mainScoreboard.getObjective(POWER_OBJECTIVE_NAME)).thenReturn(leftoverObjective);

        // When - the leftover cleanup runs
        BukkitScoreboardBackend.removeLeftoverPowerObjective();

        // Then - the persisted objective is unregistered so it stops rendering below nametags
        verify(leftoverObjective).unregister();
    }

    @Test
    void removeLeftoverPowerObjectiveShouldDoNothingWhenNoLeftoverExists() {
        // Given - no power level objective on the main scoreboard
        when(mainScoreboard.getObjective(POWER_OBJECTIVE_NAME)).thenReturn(null);

        // When / Then - the cleanup completes without touching anything
        assertThatCode(BukkitScoreboardBackend::removeLeftoverPowerObjective)
                .doesNotThrowAnyException();
        verify(mainScoreboard, never()).clearSlot(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void removeLeftoverPowerObjectiveShouldBeSafeWhenScoreboardManagerIsUnavailable() {
        // Given - the server's scoreboard manager is not available (e.g. no world loaded yet)
        bukkitMock.when(Bukkit::getScoreboardManager).thenReturn(null);

        // When / Then - the cleanup is a safe no-op
        assertThatCode(BukkitScoreboardBackend::removeLeftoverPowerObjective)
                .doesNotThrowAnyException();
    }

    @Test
    void removeLeftoverPowerObjectiveShouldIgnoreAlreadyUnregisteredObjective() {
        // Given - a leftover objective that another plugin or a race already unregistered
        final Objective staleObjective = mock(Objective.class);
        when(mainScoreboard.getObjective(POWER_OBJECTIVE_NAME)).thenReturn(staleObjective);
        doThrow(new IllegalStateException("Objective is unregistered"))
                .when(staleObjective).unregister();

        // When / Then - the stale objective is tolerated instead of breaking initialization
        assertThatCode(BukkitScoreboardBackend::removeLeftoverPowerObjective)
                .doesNotThrowAnyException();
    }
}
