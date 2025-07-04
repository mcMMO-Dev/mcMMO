package com.gmail.nossr50.party;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyLeader;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PartyManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(PartyManagerTest.class.getName());

    @BeforeEach
    public void setUp() {
        mockBaseEnvironment(logger);

        // currently unnecessary, but may be needed for future tests
        when(partyConfig.isPartyEnabled()).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        cleanUpStaticMocks();

        // disable parties in config for other tests
        when(partyConfig.isPartyEnabled()).thenReturn(false);
    }

    @Test
    public void createPartyWithoutPasswordShouldSucceed() {
        // Given
        PartyManager partyManager = new PartyManager(mcMMO.p);
        String partyName = "TestParty";

        Player player = mock(Player.class);
        final McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
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
        final McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
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
        final McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
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

    @Test
    public void checkPartyPasswordFailsWithIncorrectPassword() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        Party party = Mockito.mock(Party.class);
        Player player = Mockito.mock(Player.class);

        when(party.isLocked()).thenReturn(true);
        when(party.getPassword()).thenReturn("correctPassword");

        boolean result = partyManager.checkPartyPassword(player, party, "wrongPassword");

        assertThat(result).isFalse();
        verify(player).sendMessage(contains("Party password is incorrect"));
    }

    @Test
    public void checkPartyPasswordFailsWithNullInput() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        Party party = Mockito.mock(Party.class);
        Player player = Mockito.mock(Player.class);

        when(party.isLocked()).thenReturn(true);
        when(party.getPassword()).thenReturn("secure");

        boolean result = partyManager.checkPartyPassword(player, party, null);

        assertThat(result).isFalse();
        verify(player).sendMessage(
                contains("This party is password protected. Please provide a password to join."));
    }

    @Test
    public void checkPartyExistenceReturnsTrueIfExists() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        Party party = Mockito.mock(Party.class);
        Mockito.when(party.getName()).thenReturn("ExistingParty");

        partyManager.getParties().add(party);

        boolean result = partyManager.checkPartyExistence(player, "ExistingParty");

        assertThat(result).isTrue();
        Mockito.verify(player).sendMessage(Mockito.contains("Party ExistingParty already exists!"));
    }

    @Test
    public void inSamePartyShouldReturnTrueIfSameParty() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        Party party = Mockito.mock(Party.class);
        Player playerA = mock(Player.class);
        Player playerB = mock(Player.class);

        McMMOPlayer mmoA = mock(McMMOPlayer.class);
        McMMOPlayer mmoB = mock(McMMOPlayer.class);

        mockedUserManager.when(() -> UserManager.getPlayer(playerA)).thenReturn(mmoA);
        mockedUserManager.when(() -> UserManager.getPlayer(playerB)).thenReturn(mmoB);

        when(mmoA.getParty()).thenReturn(party);
        when(mmoB.getParty()).thenReturn(party);

        assertThat(partyManager.inSameParty(playerA, playerB)).isTrue();
    }

    @Test
    public void areAlliesShouldReturnTrueIfMutuallyAllied() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);

        McMMOPlayer mmo1 = mock(McMMOPlayer.class);
        McMMOPlayer mmo2 = mock(McMMOPlayer.class);

        Party party1 = mock(Party.class);
        Party party2 = mock(Party.class);

        mockedUserManager.when(() -> UserManager.getPlayer(p1)).thenReturn(mmo1);
        mockedUserManager.when(() -> UserManager.getPlayer(p2)).thenReturn(mmo2);

        when(mmo1.getParty()).thenReturn(party1);
        when(mmo2.getParty()).thenReturn(party2);
        when(party1.getAlly()).thenReturn(party2);
        when(party2.getAlly()).thenReturn(party1);

        assertTrue(partyManager.areAllies(p1, p2));
    }

    @Test
    public void removeFromPartyDoesNothing() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        when(mmoPlayer.getParty()).thenReturn(null);

        partyManager.removeFromParty(mmoPlayer);
    }

    @Test
    public void removeFromPartyWithPartyRemovesCorrectly() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        Player player = mock(Player.class);
        Party party = mock(Party.class);
        UUID uuid = UUID.randomUUID();

        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("PlayerName");
        when(player.isOnline()).thenReturn(true);
        when(player.getPlayer()).thenReturn(player);

        when(mmoPlayer.getPlayer()).thenReturn(player);
        when(mmoPlayer.getParty()).thenReturn(party);

        when(party.getMembers()).thenReturn(new LinkedHashMap<>(Map.of(uuid, "PlayerName")));
        when(party.getOnlineMembers()).thenReturn(new ArrayList<>(List.of(player)));
        when(party.getLeader()).thenReturn(new PartyLeader(uuid, "PlayerName"));

        partyManager.getParties().add(party);
        partyManager.removeFromParty(mmoPlayer);

        // Party should be removed since it had only one member
        assertFalse(partyManager.getParties().contains(party));
    }

    @Test
    public void changeOrJoinPartyNotInPartyTriggersEventAndReturnsTrue() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        Player player = mock(Player.class);

        when(mmoPlayer.getPlayer()).thenReturn(player);
        when(mmoPlayer.inParty()).thenReturn(false);

        assertTrue(partyManager.changeOrJoinParty(mmoPlayer, "NewParty"));
    }

    @Test
    public void removeFromPartyLeaderLeavesNewLeaderIsAssigned() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        UUID oldLeaderUUID = UUID.randomUUID();
        UUID newLeaderUUID = UUID.randomUUID();

        // Setup players
        OfflinePlayer oldLeader = mock(OfflinePlayer.class);
        when(oldLeader.getUniqueId()).thenReturn(oldLeaderUUID);
        when(oldLeader.getName()).thenReturn("OldLeader");
        when(oldLeader.isOnline()).thenReturn(true);
        when(oldLeader.getPlayer()).thenReturn(
                mock(Player.class)); // required for party.getOnlineMembers()

        OfflinePlayer newLeader = mock(OfflinePlayer.class);
        when(newLeader.getUniqueId()).thenReturn(newLeaderUUID);
        when(newLeader.getName()).thenReturn("NewLeader");

        // Setup party and members
        Party party = new Party(new PartyLeader(oldLeaderUUID, "OldLeader"), "SomeParty", null);
        party.getMembers().put(oldLeaderUUID, "OldLeader");
        party.getMembers().put(newLeaderUUID, "NewLeader");

        Player newLeaderOnline = mock(Player.class);
        when(newLeaderOnline.getUniqueId()).thenReturn(newLeaderUUID);
        party.getOnlineMembers().add(newLeaderOnline); // simulate second member online

        partyManager.getParties().add(party);

        // Act
        partyManager.removeFromParty(oldLeader, party);

        // Assert
        PartyLeader newLeaderObj = party.getLeader();
        assertThat(newLeaderUUID).isEqualTo(newLeaderObj.getUniqueId());
        assertThat("NewLeader").isEqualTo(newLeaderObj.getPlayerName());
    }

    @Test
    public void joinInvitedPartyPartyDoesNotExistDoesNotJoin() {
        PartyManager partyManager = new PartyManager(mcMMO.p);

        McMMOPlayer mmoPlayer = mock(McMMOPlayer.class);
        Player player = mock(Player.class);
        Party partyWhichNoLongerExists = mock(Party.class);

        when(mmoPlayer.getPartyInvite()).thenReturn(partyWhichNoLongerExists);
        when(mmoPlayer.getPlayer()).thenReturn(player);

        assertFalse(partyManager.getParties().contains(partyWhichNoLongerExists));

        partyManager.joinInvitedParty(mmoPlayer);

        // Should have sent disband message
        notificationManager.verify(() ->
                NotificationManager.sendPlayerInformation(player, NotificationType.PARTY_MESSAGE,
                        "Party.Disband"));
    }

}