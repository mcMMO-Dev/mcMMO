package com.gmail.nossr50.util.scoreboards.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.Test;

class NoopScoreboardBackendTest {
    @Test
    void noopBackendMethodsShouldBeSafeNoOps() {
        // Given - A noop backend with a mocked player and scoreboard
        final NoopScoreboardBackend backend = new NoopScoreboardBackend();
        final Player player = mock(Player.class);
        final Scoreboard scoreboard = mock(Scoreboard.class);
        when(player.getScoreboard()).thenReturn(scoreboard);

        // When - Running no-op operations
        backend.init();
        final PlayerBoard playerBoard = backend.createPlayerBoard(player, scoreboard);

        // Then - No operation throws and all expected no-op values are returned
        assertThatCode(() -> playerBoard.setTitle("test")).doesNotThrowAnyException();
        assertThatCode(() -> playerBoard.draw(java.util.List.of())).doesNotThrowAnyException();
        assertThatCode(() -> playerBoard.show()).doesNotThrowAnyException();
        assertThatCode(() -> playerBoard.hide(player, scoreboard)).doesNotThrowAnyException();
        assertThatCode(playerBoard::close).doesNotThrowAnyException();
        assertThat(playerBoard.isShown()).isFalse();
        assertThat(backend.isPowerLevelTagActive()).isFalse();
        assertThat(backend.getPacketPowerLevelObjective()).isNull();
    }
}
