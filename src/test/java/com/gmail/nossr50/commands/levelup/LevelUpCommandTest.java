package com.gmail.nossr50.commands.levelup;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.MINING;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.WOODCUTTING;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.LevelUpCommandAPI;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.TestPlayerMock;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Behavior tests for level up commands and API registrations, driven through real XP gains so
 * the whole chain (XP gain, level up event, SelfListener, manager, dispatch) is exercised.
 */
class LevelUpCommandTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(LevelUpCommandTest.class.getName());
    private static final String PLAYER_NAME = "Momshroom";

    private SelfListener selfListener;
    private ConsoleCommandSender consoleSender;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        selfListener = Mockito.spy(new SelfListener(mcMMO.p));
        consoleSender = mock(ConsoleCommandSender.class);
        when(Bukkit.getConsoleSender()).thenReturn(consoleSender);

        // Route level up events fired by XP gains into the listener under test
        Mockito.doAnswer(invocation -> {
            selfListener.onPlayerLevelUp(invocation.getArgument(0));
            return null;
        }).when(pluginManager).callEvent(any(McMMOPlayerLevelUpEvent.class));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void commandShouldRunForEachMilestoneWhenLevelingThroughThem() {
        // Given - a command watching Mining levels 1 through 5
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say hello", Set.of(MINING), Set.of(1, 2, 3, 4, 5));

        // When - the player levels Mining five times through XP gains
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 5);

        // Then - the command dispatched once per milestone, as console
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say hello")),
                times(5));
    }

    @Test
    void commandShouldInjectSkillPlaceholdersWhenFiring() {
        // Given - a command using the skill milestone placeholders
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say {@player} hit {@level} in {@skill} ({@mining_level})",
                Set.of(MINING), Set.of(2));

        // When - the player levels Mining to 2
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 2);

        // Then - every placeholder resolved against the milestone and current levels
        // ({@skill} uses the display name, whatever the locale wiring resolves it to)
        final String expected = "say " + PLAYER_NAME + " hit 2 in " + MINING.getName() + " (2)";
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq(expected)),
                times(1));
    }

    @Test
    void eachRegisteredCommandShouldFireOnlyAtItsMilestone() {
        // Given - three commands watching Mining levels 1, 2 and 3 respectively
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say one", Set.of(MINING), Set.of(1));
        registerConfigCommand("say two", Set.of(MINING), Set.of(2));
        registerConfigCommand("say three", Set.of(MINING), Set.of(3));

        // When - the player levels Mining five times
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 5);

        // Then - each command fired exactly once
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say one")),
                times(1));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say two")),
                times(1));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say three")),
                times(1));
    }

    @Test
    void commandShouldFireTwiceWhenTwoOfItsMilestonesAreCrossed() {
        // Given - one command watching Mining levels 1 and 4
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say milestone", Set.of(MINING), Set.of(1, 4));

        // When - the player levels Mining five times
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 5);

        // Then - the command fired at level 1 and level 4 only
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say milestone")),
                times(2));
    }

    @Test
    void commandShouldNotFireForUnlistedSkill() {
        // Given - a command watching Woodcutting
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say chop", Set.of(WOODCUTTING), Set.of(1, 2, 3));

        // When - the player levels Mining instead
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 3);

        // Then - nothing dispatched
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any(String.class)), never());
    }

    @Test
    void commandShouldFireOncePerMilestoneWhenManyLevelsGainedAtOnce() {
        // Given - a command watching Mining levels 2 and 4, and a player already at level 5
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final McMMOPlayer mmoPlayer = playerMock.mmoPlayer();
        registerConfigCommand("say jump", Set.of(MINING), Set.of(2, 4));
        mmoPlayer.modifySkill(MINING, 5);

        // When - a single level up event reports five levels gained at once
        final McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(),
                MINING, 5, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // Then - the command fired once for level 2 and once for level 4
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say jump")),
                times(2));
    }

    @Test
    void powerLevelMilestoneShouldFireOnceWhenCrossed() {
        // Given - a command watching power level 3
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        levelUpCommandManager.register(LevelUpCommand.builder()
                        .withPowerLevels(Set.of(3))
                        .command("say power {@power_level}")
                        .build(),
                RegistrationSource.CONFIG);

        // When - the player levels Mining five times, passing power level 3 once
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 5);

        // Then - the command fired exactly once with the milestone injected
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say power 3")),
                times(1));
    }

    @Test
    void commandShouldStopFiringAfterUnregister() {
        // Given - an API-registered command that fires on every Mining level
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final UUID id = LevelUpCommandAPI.registerCommand(mcMMO.p, LevelUpCommand.builder()
                .withSkill(MINING)
                .withLevels(Set.of(1, 2, 3, 4, 5))
                .command("say hi")
                .build());

        // When - the player levels once, the command is unregistered, then levels again
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 1);
        final boolean removed = LevelUpCommandAPI.unregister(id);
        final boolean removedAgain = LevelUpCommandAPI.unregister(id);
        playerMock.mmoPlayer().applyXpGain(MINING,
                playerMock.mmoPlayer().getProfile().getXpToLevel(MINING), XPGainReason.COMMAND,
                XPGainSource.COMMAND);

        // Then - only the first level up dispatched, and only the first unregister succeeded
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say hi")),
                times(1));
        assertThat(removed).isTrue();
        assertThat(removedAgain).isFalse();
    }

    @Test
    void playerRunAsShouldDispatchAsThePlayer() {
        // Given - a command configured to run as the player
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        levelUpCommandManager.register(LevelUpCommand.builder()
                        .withSkill(MINING)
                        .withLevels(Set.of(1))
                        .command("me leveled up")
                        .runAs(LevelUpCommand.RunAs.PLAYER)
                        .build(),
                RegistrationSource.CONFIG);

        // When - the player levels Mining once
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 1);

        // Then - the command dispatched with the player as sender, not console
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(playerMock.player()),
                eq("me leveled up")), times(1));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), any(String.class)),
                never());
    }

    @Test
    void onPlayerLevelUpShouldSkipPowerLevelWorkWhenNothingIsRegistered() {
        // Given - a level up with no registrations of any kind
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final McMMOPlayer mmoPlayer = playerMock.mmoPlayer();
        assertThat(levelUpCommandManager.hasRegistrations()).isFalse();
        Mockito.clearInvocations(mmoPlayer);

        // When - the level up event reaches the listener
        final McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(),
                MINING, 1, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // Then - the listener bailed out before computing the power level
        verify(mmoPlayer, never()).getPowerLevel();
        // And - nothing was dispatched
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any(String.class)), never());
    }

    @Test
    void handlerShouldReceiveHighestPowerLevelWhenManyLevelsGainedAtOnce() {
        // Given - two handlers and a player who reaches Mining 5 in a single level up event
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final McMMOPlayer mmoPlayer = playerMock.mmoPlayer();
        final List<Integer> firstHandlerPowerLevels = new ArrayList<>();
        final List<Integer> secondHandlerPowerLevels = new ArrayList<>();
        LevelUpCommandAPI.registerHandler(mcMMO.p, (player, skill, levelsGained, powerLevel) ->
                firstHandlerPowerLevels.add(powerLevel));
        LevelUpCommandAPI.registerHandler(mcMMO.p, (player, skill, levelsGained, powerLevel) ->
                secondHandlerPowerLevels.add(powerLevel));
        mmoPlayer.modifySkill(MINING, 5);
        Mockito.clearInvocations(mmoPlayer);

        // When - a single level up event reports five levels gained at once
        final McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(),
                MINING, 5, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // Then - both handlers received the highest power level reached
        assertThat(firstHandlerPowerLevels).containsExactly(5);
        assertThat(secondHandlerPowerLevels).containsExactly(5);
        // And - the power level was computed once by the listener, not once more per handler
        verify(mmoPlayer, times(1)).getPowerLevel();
    }

    @Test
    void handlerShouldReceiveLevelUpDetailsAndStopAfterUnregister() {
        // Given - an API handler capturing every level up
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final List<String> captured = new ArrayList<>();
        final UUID id = LevelUpCommandAPI.registerHandler(mcMMO.p,
                (player, skill, levelsGained, powerLevel) -> captured.add(
                        skill + ":" + levelsGained + ":power=" + powerLevel));

        // When - the player levels Mining twice, then the handler is unregistered
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 2);
        LevelUpCommandAPI.unregister(id);
        playerMock.mmoPlayer().applyXpGain(MINING,
                playerMock.mmoPlayer().getProfile().getXpToLevel(MINING), XPGainReason.COMMAND,
                XPGainSource.COMMAND);

        // Then - the handler saw both level ups with skill, levels and power level details
        assertThat(captured).containsExactly(
                "MINING:[1]:power=1",
                "MINING:[2]:power=2");
    }

    @Test
    void throwingHandlerShouldNotBreakOtherRegistrationsOrTheLevelUp() {
        // Given - a third party handler that throws and a healthy command registration
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        LevelUpCommandAPI.registerHandler(mcMMO.p, (player, skill, levelsGained, powerLevel) -> {
            throw new IllegalStateException("third party bug");
        });
        registerConfigCommand("say resilient", Set.of(MINING), Set.of(1));

        // When - the player levels up (levelPlayerViaXP also asserts the level was applied,
        // proving the exception never escaped into the XP pipeline)
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 1);

        // Then - the healthy command still dispatched despite the broken handler
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say resilient")),
                times(1));
    }

    @Test
    void configRegistrationsShouldClearWithoutTouchingApiRegistrations() {
        // Given - one config command and one API handler
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say config", Set.of(MINING), Set.of(1));
        final List<Integer> handlerCalls = new ArrayList<>();
        LevelUpCommandAPI.registerHandler(mcMMO.p,
                (player, skill, levelsGained, powerLevel) -> handlerCalls.add(powerLevel));

        // When - config registrations are cleared (what a config reload does)
        levelUpCommandManager.clearConfigRegistrations();
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 1);

        // Then - the config command is gone but the API handler still fires
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq("say config")), never());
        assertThat(handlerCalls).containsExactly(1);
        assertThat(levelUpCommandManager.registrationCount()).isEqualTo(1);
    }

    @Test
    void pluginDisableShouldPurgeOnlyThatPluginsRegistrations() {
        // Given - registrations from two different plugins plus a config command
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final Plugin disabledPlugin = mock(Plugin.class);
        when(disabledPlugin.getName()).thenReturn("DisabledPlugin");
        final Plugin survivingPlugin = mock(Plugin.class);
        LevelUpCommandAPI.registerCommand(disabledPlugin, LevelUpCommand.builder()
                .withSkill(MINING)
                .withLevels(Set.of(1))
                .command("say doomed")
                .build());
        final List<Integer> survivingHandlerCalls = new ArrayList<>();
        LevelUpCommandAPI.registerHandler(survivingPlugin,
                (player, skill, levelsGained, powerLevel) ->
                        survivingHandlerCalls.add(powerLevel));
        registerConfigCommand("say config", Set.of(MINING), Set.of(1));

        // When - the first plugin is disabled and the player levels up afterwards
        selfListener.onPluginDisable(new PluginDisableEvent(disabledPlugin));
        levelPlayerViaXP(playerMock.mmoPlayer(), MINING, 1);

        // Then - the disabled plugin's command is gone and never dispatched
        assertThat(levelUpCommandManager.registrationCount()).isEqualTo(2);
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq("say doomed")), never());
        // And - the other plugin's handler and the config command still fired
        assertThat(survivingHandlerCalls).containsExactly(1);
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(eq(consoleSender), eq("say config")),
                times(1));
    }

    @Test
    void pluginDisableForMcMMOItselfShouldLeaveRegistrationsAlone() {
        // Given - an API registration owned by mcMMO itself
        LevelUpCommandAPI.registerHandler(mcMMO.p, (player, skill, levelsGained, powerLevel) -> {
        });

        // When - the disable event for mcMMO itself reaches the listener
        selfListener.onPluginDisable(new PluginDisableEvent(mcMMO.p));

        // Then - the registration survives; mcMMO's own shutdown cleanup happens in onDisable
        assertThat(levelUpCommandManager.registrationCount()).isEqualTo(1);
    }

    @Test
    void offlinePlayerShouldNotTriggerAnyRegistrations() {
        // Given - a registered command and a player who logged off
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        registerConfigCommand("say bye", Set.of(MINING), Set.of(1));
        when(playerMock.player().isOnline()).thenReturn(false);

        // When - a level up is applied for the offline player
        levelUpCommandManager.applyLevelUp(playerMock.mmoPlayer(), MINING, Set.of(1), Set.of(1));

        // Then - nothing dispatched
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any(String.class)), never());
    }

    @Test
    void injectPlaceholdersShouldHandleRepeatedAndUnknownTokens() {
        // Given - a player at Mining level 7 and a command with repeated and unknown tokens
        final TestPlayerMock playerMock = mockPlayer(UUID.randomUUID(), PLAYER_NAME, 0);
        final McMMOPlayer mmoPlayer = playerMock.mmoPlayer();
        mmoPlayer.modifySkill(MINING, 7);
        final String command = "say {@player} {@player} {@bogus} {@mining_level} {@power_level}";

        // When - placeholders are injected outside any milestone context
        final String injected = LevelUpCommand.injectPlaceholders(command, mmoPlayer, null,
                null, null);

        // Then - repeats are replaced, unknown tokens survive, power level is the current one
        assertThat(injected).isEqualTo(
                "say " + PLAYER_NAME + " " + PLAYER_NAME + " {@bogus} 7 7");
    }

    @Test
    void commandShouldRejectEmptyCommandList() {
        // Given - a valid condition but no commands
        final LevelUpCondition condition = LevelUpCondition.skillLevels(Set.of(MINING),
                Set.of(1));

        // When / Then - construction fails fast
        assertThatThrownBy(() -> new LevelUpCommand(condition, List.of(),
                LevelUpCommand.RunAs.CONSOLE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void registerConfigCommand(@NotNull String command,
            @NotNull Set<PrimarySkillType> skills, @NotNull Set<Integer> levels) {
        levelUpCommandManager.register(LevelUpCommand.builder()
                        .withSkills(skills)
                        .withLevels(levels)
                        .command(command)
                        .build(),
                RegistrationSource.CONFIG);
    }

    private void levelPlayerViaXP(@NotNull McMMOPlayer mmoPlayer,
            @NotNull PrimarySkillType skill, int levelsToGain) {
        final int startingLevel = mmoPlayer.getSkillLevel(skill);
        for (int i = 0; i < levelsToGain; i++) {
            mmoPlayer.applyXpGain(skill, mmoPlayer.getProfile().getXpToLevel(skill),
                    XPGainReason.COMMAND, XPGainSource.COMMAND);
        }
        assertThat(mmoPlayer.getSkillLevel(skill)).isEqualTo(startingLevel + levelsToGain);
    }
}
