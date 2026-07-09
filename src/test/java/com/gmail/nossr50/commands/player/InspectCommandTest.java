package com.gmail.nossr50.commands.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies that /inspect applies the inspect.far permission gate consistently: inspecting a
 * player whose profile has to be loaded from the database (offline) or a vanished player must
 * not bypass the checks that gate inspecting distant online players (GitHub issue #4399).
 */
class InspectCommandTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(InspectCommandTest.class.getName());
    private static final String OFFLINE_TARGET_NAME = "OfflineTarget";

    private InspectCommand inspectCommand;
    private DatabaseManager databaseManager;
    private Command command;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        databaseManager = mock(DatabaseManager.class);
        mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(databaseManager);

        inspectCommand = new InspectCommand();
        command = mock(Command.class);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void inspectShouldDenyOfflineTargetWhenSenderLacksInspectFarPermission() {
        // Given - the target is offline and the sender lacks mcmmo.commands.inspect.far
        when(UserManager.getOfflinePlayer(OFFLINE_TARGET_NAME)).thenReturn(null);
        when(Permissions.inspectFar(player)).thenReturn(false);

        // When - the sender inspects the offline target
        final boolean handled = inspectCommand.onCommand(player, command, "inspect",
                new String[]{OFFLINE_TARGET_NAME});

        // Then - the command is denied with the offline permission message
        assertThat(handled).isTrue();
        verify(player).sendMessage(LocaleLoader.getString("Inspect.Offline"));

        // And - the target profile is never loaded from the database
        verify(databaseManager, never()).loadPlayerProfile(anyString());
    }

    @Test
    void inspectShouldShowOfflineStatsWhenSenderHasInspectFarPermission() {
        // Given - the target is offline and the sender has mcmmo.commands.inspect.far
        when(UserManager.getOfflinePlayer(OFFLINE_TARGET_NAME)).thenReturn(null);
        when(Permissions.inspectFar(player)).thenReturn(true);
        when(databaseManager.loadPlayerProfile(OFFLINE_TARGET_NAME))
                .thenReturn(new PlayerProfile(OFFLINE_TARGET_NAME, true, 0));

        // When - the sender inspects the offline target
        final boolean handled = inspectCommand.onCommand(player, command, "inspect",
                new String[]{OFFLINE_TARGET_NAME});

        // Then - the offline stats are shown
        assertThat(handled).isTrue();
        verify(player).sendMessage(
                LocaleLoader.getString("Inspect.OfflineStats", OFFLINE_TARGET_NAME));
    }

    @Test
    void inspectShouldDenyVanishedTargetWhenSenderLacksFarAndHiddenPermissions() {
        // Given - the target is online but vanished from a sender who lacks both
        // mcmmo.commands.inspect.hidden and mcmmo.commands.inspect.far
        final Player sender = mock(Player.class);
        when(UserManager.getOfflinePlayer("testPlayer")).thenReturn(mmoPlayer);
        when(sender.canSee(player)).thenReturn(false);
        when(Permissions.inspectHidden(sender)).thenReturn(false);
        when(Permissions.inspectFar(sender)).thenReturn(false);

        // When - the sender inspects the vanished target
        final boolean handled = inspectCommand.onCommand(sender, command, "inspect",
                new String[]{"testPlayer"});

        // Then - the vanished target masquerades as offline and inspection is denied
        assertThat(handled).isTrue();
        verify(sender).sendMessage(LocaleLoader.getString("Inspect.Offline"));

        // And - no stats are leaked to the sender
        verify(sender, never()).sendMessage(any(String[].class));
    }

    @Test
    void inspectShouldShowMaskedStatsForVanishedTargetWhenSenderHasInspectFarPermission() {
        // Given - the target is vanished from a sender who lacks inspect.hidden but holds
        // inspect.far, so the target is still shown while masquerading as offline
        final Player sender = mock(Player.class);
        when(UserManager.getOfflinePlayer("testPlayer")).thenReturn(mmoPlayer);
        when(sender.canSee(player)).thenReturn(false);
        when(Permissions.inspectHidden(sender)).thenReturn(false);
        when(Permissions.inspectFar(sender)).thenReturn(true);

        // When - the sender inspects the vanished target
        final boolean handled = inspectCommand.onCommand(sender, command, "inspect",
                new String[]{"testPlayer"});

        // Then - stats are shown under the offline header to hide the vanish status
        assertThat(handled).isTrue();
        verify(sender).sendMessage(LocaleLoader.getString("Inspect.OfflineStats", "testPlayer"));
    }
}
