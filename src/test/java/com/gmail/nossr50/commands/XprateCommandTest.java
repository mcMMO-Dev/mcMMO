package com.gmail.nossr50.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

/**
 * Covers /xprate argument handling: decimal and integer rates must reach the global XP
 * multiplier unchanged, non-positive or non-finite rates must never touch it, whole-number
 * rates must be announced without a trailing ".0", and reset must restore the multiplier
 * captured when the command was created.
 */
class XprateCommandTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(XprateCommandTest.class.getName());
    private static final double ORIGINAL_RATE = 1.0;

    private XprateCommand xprateCommand;
    private CommandSender sender;
    private Command command;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier())
                .thenReturn(ORIGINAL_RATE);

        sender = mock(CommandSender.class);
        command = mock(Command.class);
        when(command.getPermissionMessage()).thenReturn("permission-denied");
        when(Permissions.xprateSet(sender)).thenReturn(true);
        when(Permissions.xprateReset(sender)).thenReturn(true);

        xprateCommand = new XprateCommand();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private boolean runCommand(String... args) {
        return xprateCommand.onCommand(sender, command, "xprate", args);
    }

    @ParameterizedTest
    @CsvSource({
            "1.5, 1.5",
            "0.5, 0.5",
            "2.25, 2.25",
            "10.75, 10.75",
            "2, 2.0",
            "100, 100.0",
    })
    void onCommandShouldApplyPositiveRateWhenStartingEvent(String input, double expected) {
        // Given - a sender with permission to start an XP event

        // When - the sender starts an event with a positive decimal or integer rate
        final boolean handled = runCommand(input, "true");

        // Then - the exact rate reaches the global XP multiplier
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance()).setExperienceGainsGlobalMultiplier(expected);
        verify(mcMMO.p).setXPEventEnabled(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0.5", "0", "0.0", "NaN", "Infinity", "-Infinity", "abc",
            "1.2.3", ""})
    void onCommandShouldRejectRateWhenValueIsNotAPositiveFiniteNumber(String input) {
        // Given - a sender with permission to start an XP event

        // When - the sender supplies a rate that is negative, zero, non-finite, or garbage
        final boolean handled = runCommand(input, "true");

        // Then - the command is handled with feedback and the multiplier is never touched
        assertThat(handled).isTrue();
        verify(sender, atLeastOnce()).sendMessage(anyString());
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "NaN"})
    void onCommandShouldNotToggleXpEventWhenRateIsInvalid(String input) {
        // Given - a sender with permission to start an XP event

        // When - the sender supplies an invalid rate together with a valid toggle
        runCommand(input, "true");

        // Then - the XP event state is left untouched despite the valid toggle argument
        verify(mcMMO.p, never()).setXPEventEnabled(true);
    }

    @Test
    void onCommandShouldShowUsageWhenToggleArgumentIsInvalid() {
        // Given - a sender with permission and a valid rate

        // When - the toggle argument is neither an enable nor a disable keyword
        final boolean handled = runCommand("2", "maybe");

        // Then - usage is requested and the multiplier is never touched
        assertThat(handled).isFalse();
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
    }

    @Test
    void onCommandShouldAnnounceWholeRateWithoutTrailingZero() {
        // Given - event broadcasts are enabled and the sender sets a whole-number decimal rate
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);

        // When - the rate is set to 2.0
        runCommand("2.0", "true");

        // Then - every broadcast and notification shows "2" rather than "2.0"
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), atLeastOnce()).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("2"))
                .allSatisfy(message -> assertThat(message).doesNotContain("2.0"));
        notificationManager.verify(() -> NotificationManager.processSensitiveCommandNotification(
                sender, SensitiveCommandType.XPRATE_MODIFY, "2"));
    }

    @Test
    void onCommandShouldAnnounceFractionalRateWithDecimals() {
        // Given - event broadcasts are enabled and the sender sets a fractional rate
        when(mcMMO.p.getGeneralConfig().broadcastEventMessages()).thenReturn(true);

        // When - the rate is set to 1.5
        runCommand("1.5", "true");

        // Then - the broadcast and the admin notification both show the decimal rate
        final ArgumentCaptor<String> broadcastCaptor = ArgumentCaptor.forClass(String.class);
        verify(mcMMO.p.getServer(), atLeastOnce()).broadcastMessage(broadcastCaptor.capture());
        assertThat(broadcastCaptor.getAllValues())
                .anySatisfy(message -> assertThat(message).contains("1.5"));
        notificationManager.verify(() -> NotificationManager.processSensitiveCommandNotification(
                sender, SensitiveCommandType.XPRATE_MODIFY, "1.5"));
    }

    @Test
    void onCommandShouldRestoreOriginalRateWhenReset() {
        // Given - the command captured the multiplier that was active when it was created

        // When - the sender resets the XP rate
        final boolean handled = runCommand("reset");

        // Then - the multiplier captured at construction time is restored
        assertThat(handled).isTrue();
        verify(ExperienceConfig.getInstance())
                .setExperienceGainsGlobalMultiplier(ORIGINAL_RATE);
    }

    @Test
    void onCommandShouldDenyRateChangeWithoutSetPermission() {
        // Given - a sender lacking the set permission
        when(Permissions.xprateSet(sender)).thenReturn(false);

        // When - the sender tries to change the rate
        final boolean handled = runCommand("1.5", "true");

        // Then - the permission message is sent and the multiplier is never touched
        assertThat(handled).isTrue();
        verify(sender).sendMessage("permission-denied");
        verify(ExperienceConfig.getInstance(), never())
                .setExperienceGainsGlobalMultiplier(anyDouble());
    }
}
