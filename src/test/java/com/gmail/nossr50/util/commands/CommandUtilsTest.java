package com.gmail.nossr50.util.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class CommandUtilsTest {

    private MockedStatic<mcMMO> mcMMOMock;
    private Player playerA;
    private Player playerB;

    @BeforeEach
    void setUp() {
        mcMMOMock = mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        final Server server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);

        playerA = mock(Player.class);
        when(playerA.getName()).thenReturn("PlayerA");
        playerB = mock(Player.class);
        when(playerB.getName()).thenReturn("PlayerB");
        when(server.getOnlinePlayers()).thenAnswer(invocation -> List.of(playerA, playerB));
    }

    @AfterEach
    void tearDown() {
        mcMMOMock.close();
    }

    /**
     * Regression coverage for console tab completion: the visibility check required a player
     * sender, so the console always got an empty player name list.
     */
    @Test
    void consoleShouldGetAllOnlinePlayerNames() {
        // Given - the command sender is the console
        final ConsoleCommandSender console = mock(ConsoleCommandSender.class);

        // When - online player names are collected for tab completion
        final List<String> names = CommandUtils.getOnlinePlayerNames(console);

        // Then - the console sees every online player
        assertThat(names).containsExactlyInAnyOrder("PlayerA", "PlayerB");
    }

    /** Guard: player senders must keep vanish support and only see players visible to them. */
    @Test
    void playersShouldOnlyGetNamesOfPlayersTheyCanSee() {
        // Given - a player who can see PlayerA but not the vanished PlayerB
        final Player viewer = mock(Player.class);
        when(viewer.canSee(playerA)).thenReturn(true);
        when(viewer.canSee(playerB)).thenReturn(false);

        // When - online player names are collected for tab completion
        final List<String> names = CommandUtils.getOnlinePlayerNames(viewer);

        // Then - only the visible player is listed
        assertThat(names).containsExactly("PlayerA");
    }
}
