package com.gmail.nossr50.util.player;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Regression coverage for shutdown-time user cleanup. Clearing users during server shutdown
 * must tolerate players whose mcMMO data was never loaded (no player-data metadata), which
 * previously caused a NullPointerException in {@link UserManager#remove(Player)} when called
 * from {@link UserManager#clearAll()} during onDisable.
 */
class UserManagerTest {

    private Server server;

    @BeforeEach
    void setUp() {
        mcMMO.p = mock(mcMMO.class);
        server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);
    }

    @AfterEach
    void tearDown() {
        mcMMO.p = null;
    }

    @Test
    void clearAllShouldNotThrowWhenNoPlayersOnline() {
        // Given - no players are online during shutdown
        doReturn(List.of()).when(server).getOnlinePlayers();

        // When - all users are cleared on plugin disable
        // Then - no exception is thrown
        assertThatCode(UserManager::clearAll).doesNotThrowAnyException();
    }

    @Test
    void clearAllShouldNotThrowWhenOnlinePlayerHasNoMcMMOData() {
        // Given - an online player whose mcMMO data was never loaded (no metadata attached)
        final Player untrackedPlayer = mock(Player.class);
        when(untrackedPlayer.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA))
                .thenReturn(false);
        doReturn(List.of(untrackedPlayer)).when(server).getOnlinePlayers();

        // When - all users are cleared on plugin disable
        // Then - the untracked player is skipped without a NullPointerException
        assertThatCode(UserManager::clearAll).doesNotThrowAnyException();
    }

    @Test
    void removeShouldReturnQuietlyWhenPlayerHasNoMcMMOData() {
        // Given - a player without mcMMO player-data metadata
        final Player untrackedPlayer = mock(Player.class);
        when(untrackedPlayer.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA))
                .thenReturn(false);

        // When - the player is removed from user tracking
        assertThatCode(() -> UserManager.remove(untrackedPlayer)).doesNotThrowAnyException();

        // Then - no metadata cleanup is attempted for the untracked player
        verify(untrackedPlayer, never()).removeMetadata(anyString(), any(Plugin.class));
    }
}
