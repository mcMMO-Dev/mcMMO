package com.gmail.nossr50.party;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PartyManagerTest {

    static mcMMO mockMcMMO;

    @BeforeAll
    public static void setup() {
        // create a static stub for LocaleLoader.class
        mockStatic(LocaleLoader.class);
        when(LocaleLoader.getString(anyString())).thenReturn("");

        mockMcMMO = mock(mcMMO.class);
        final Server mockServer = mock(Server.class);
        when(mockMcMMO.getServer()).thenReturn(mockServer);
        when(mockServer.getPluginManager()).thenReturn(mock(PluginManager.class));

        // TODO: Add cleanup for static mock
    }

    @Test
    public void createPartyWithoutPasswordShouldSucceed() {
        // Given
        PartyManager partyManager = new PartyManager(mockMcMMO);
        String partyName = "TestParty";

        // TODO: Update this with utils from the other dev branches in the future
        Player player = mock(Player.class);
        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        when(mmoPlayer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(new UUID(0, 0));

        // When & Then
        partyManager.createParty(mmoPlayer, partyName, null);
    }

    @Test
    public void createPartyWithPasswordShouldSucceed() {
        // Given
        PartyManager partyManager = new PartyManager(mockMcMMO);
        String partyName = "TestParty";
        String partyPassword = "somePassword";

        // TODO: Update this with utils from the other dev branches in the future
        Player player = mock(Player.class);
        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        when(mmoPlayer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(new UUID(0, 0));

        // When & Then
        partyManager.createParty(mmoPlayer, partyName, partyPassword);
    }

    @Test
    public void createPartyWithoutNameShouldFail() {
        // Given
        PartyManager partyManager = new PartyManager(mockMcMMO);
        String partyPassword = "somePassword";

        // TODO: Update this with utils from the other dev branches in the future
        Player player = mock(Player.class);
        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        when(mmoPlayer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(new UUID(0, 0));

        // When & Then
        assertThrows(NullPointerException.class,
                () -> partyManager.createParty(mmoPlayer, null, partyPassword));
    }

    @Test
    public void createPartyWithoutPlayerShouldFail() {
        // Given
        PartyManager partyManager = new PartyManager(mockMcMMO);
        String partyName = "TestParty";
        String partyPassword = "somePassword";

        // When & Then
        assertThrows(NullPointerException.class,
                () -> partyManager.createParty(null, partyName, partyPassword));
    }

}