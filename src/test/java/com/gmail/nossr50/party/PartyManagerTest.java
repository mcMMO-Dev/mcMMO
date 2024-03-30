package com.gmail.nossr50.party;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartyManagerTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(PartyManagerTest.class.getName());

    @BeforeEach
    public void setUp() {
        mockBaseEnvironment(logger);

        // currently unnecessary, but may be needed for future tests
        Mockito.when(partyConfig.isPartyEnabled()).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        cleanupBaseEnvironment();

        // disable parties in config for other tests
        Mockito.when(partyConfig.isPartyEnabled()).thenReturn(false);
    }

    @Test
    public void createPartyWithoutPasswordShouldSucceed() {
        // Given
        PartyManager partyManager = new PartyManager(mcMMO.p);
        String partyName = "TestParty";

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
        PartyManager partyManager = new PartyManager(mcMMO.p);
        String partyName = "TestParty";
        String partyPassword = "somePassword";

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
        PartyManager partyManager = new PartyManager(mcMMO.p);
        String partyPassword = "somePassword";

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
        PartyManager partyManager = new PartyManager(mcMMO.p);
        String partyName = "TestParty";
        String partyPassword = "somePassword";

        // When & Then
        assertThrows(NullPointerException.class,
                () -> partyManager.createParty(null, partyName, partyPassword));
    }

}