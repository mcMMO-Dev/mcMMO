package com.gmail.nossr50.commands;

import static com.gmail.nossr50.database.UsernamePlaceholder.INVALID_OLD_USERNAME;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.commands.database.McremoveCommand;
import com.gmail.nossr50.commands.experience.AddlevelsCommand;
import com.gmail.nossr50.commands.experience.AddxpCommand;
import com.gmail.nossr50.commands.experience.MmoeditCommand;
import com.gmail.nossr50.commands.experience.SkillresetCommand;
import com.gmail.nossr50.commands.player.InspectCommand;
import com.gmail.nossr50.commands.player.McRankCommand;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Players who lost their name to someone else are stored under a placeholder name. An admin
 * command given the placeholder must not act on whichever of them the database finds first:
 * /mcremove would delete that player.
 */
class PlaceholderNameCommandsTest extends MMOTestEnvironment {
    private static final Logger logger =
            Logger.getLogger(PlaceholderNameCommandsTest.class.getName());
    /** The spelling FlatFile wrote before it shared the SQL placeholder. */
    private static final String LEGACY_FLATFILE_PLACEHOLDER = INVALID_OLD_USERNAME + "'";

    private DatabaseManager databaseManager;
    private Command command;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        databaseManager = mock(DatabaseManager.class);
        mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(databaseManager);
        command = mock(Command.class);

        // The sender may use every command on other players, including offline ones
        when(Permissions.addlevelsOthers(player)).thenReturn(true);
        when(Permissions.addxpOthers(player)).thenReturn(true);
        when(Permissions.mmoeditOthers(player)).thenReturn(true);
        when(Permissions.skillresetOthers(player)).thenReturn(true);
        when(Permissions.inspectFar(player)).thenReturn(true);
        when(Permissions.mcrankOthers(player)).thenReturn(true);
        when(player.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA)).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /** Each command that names another player, with the arguments after the name. */
    static Stream<Arguments> commandsThatLookUpAPlayerByName() {
        return Stream.of(
                Arguments.of("addlevels", (Supplier<CommandExecutor>) AddlevelsCommand::new,
                        List.of("mining", "10")),
                Arguments.of("addxp", (Supplier<CommandExecutor>) AddxpCommand::new,
                        List.of("mining", "10")),
                Arguments.of("mmoedit", (Supplier<CommandExecutor>) MmoeditCommand::new,
                        List.of("mining", "10")),
                Arguments.of("skillreset", (Supplier<CommandExecutor>) SkillresetCommand::new,
                        List.of("mining")),
                Arguments.of("inspect", (Supplier<CommandExecutor>) InspectCommand::new,
                        List.of()),
                Arguments.of("mcrank", (Supplier<CommandExecutor>) McRankCommand::new,
                        List.of()),
                Arguments.of("mcremove", (Supplier<CommandExecutor>) McremoveCommand::new,
                        List.of())
        );
    }

    static Stream<Arguments> commandsGivenEachPlaceholderSpelling() {
        return commandsThatLookUpAPlayerByName().flatMap(commandCase -> Stream.of(
                        INVALID_OLD_USERNAME, INVALID_OLD_USERNAME.toLowerCase(Locale.ROOT),
                        LEGACY_FLATFILE_PLACEHOLDER,
                        LEGACY_FLATFILE_PLACEHOLDER.toLowerCase(Locale.ROOT))
                .map(placeholder -> Arguments.of(commandCase.get()[0], commandCase.get()[1],
                        commandCase.get()[2], placeholder)));
    }

    /**
     * Makes the database find a player under the placeholder, as it did before lookups by name
     * refused it, so a command without its own guard goes on to act on that player.
     */
    private void storePlayerUnderThePlaceholder(String placeholder) {
        final PlayerProfile playerWhoLostTheirName =
                new PlayerProfile(placeholder, randomUUID(), true, 0);
        final OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(server.getOfflinePlayer(placeholder)).thenReturn(offlinePlayer);
        when(databaseManager.loadPlayerProfile(offlinePlayer)).thenReturn(playerWhoLostTheirName);
        when(databaseManager.loadPlayerProfile(placeholder)).thenReturn(playerWhoLostTheirName);
        when(databaseManager.removeUser(placeholder, null)).thenReturn(true);
    }

    @ParameterizedTest(name = "/{0} {3}")
    @MethodSource("commandsGivenEachPlaceholderSpelling")
    void commandShouldFindNoPlayerUnderThePlaceholder(String label,
            Supplier<CommandExecutor> commandExecutor, List<String> argumentsAfterTheName,
            String placeholder) {
        // Given - a player stored under the placeholder
        storePlayerUnderThePlaceholder(placeholder);
        final String[] arguments = Stream.concat(Stream.of(placeholder),
                argumentsAfterTheName.stream()).toArray(String[]::new);

        // When - the command is given the placeholder as the player's name
        final boolean handled = commandExecutor.get().onCommand(player, command, label,
                arguments);

        // Then - the sender is told no such player exists
        assertThat(handled).isTrue();
        verify(player).sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));

        // And - the database is not used
        verifyNoInteractions(databaseManager);
    }
}
