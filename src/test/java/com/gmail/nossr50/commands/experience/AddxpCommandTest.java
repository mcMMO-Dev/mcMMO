package com.gmail.nossr50.commands.experience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Covers /addxp sender feedback. Other plugins award XP by dispatching this command from the
 * console, so the -s flag must silence the command completely: the "has been modified"
 * confirmation echoed to the sender otherwise floods the server log on every dispatch.
 */
class AddxpCommandTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(AddxpCommandTest.class.getName());

    private AddxpCommand addxpCommand;
    private CommandSender sender;
    private Command command;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        sender = mock(CommandSender.class);
        command = mock(Command.class);
        when(Permissions.addxpOthers(sender)).thenReturn(true);
        when(UserManager.getOfflinePlayer("testPlayer")).thenReturn(mmoPlayer);
        addxpCommand = new AddxpCommand();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void addingXpToAnotherPlayerShouldConfirmToTheSender() {
        // Given - a sender with permission to add XP to other players

        // When - the sender awards Herbalism XP without the silent flag
        final boolean handled = addxpCommand.onCommand(sender, command, "addxp",
                new String[]{"testPlayer", "herbalism", "50"});

        // Then - the XP gain is processed and the sender gets the confirmation message
        assertThat(handled).isTrue();
        verify(mmoPlayer).applyXpGain(PrimarySkillType.HERBALISM, 50F, XPGainReason.COMMAND,
                XPGainSource.COMMAND);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender).sendMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains("has been modified");
    }

    @Test
    void addingXpWithTheSilentFlagShouldNotMessageTheSender() {
        // Given - a sender with permission to add XP to other players

        // When - the sender awards Herbalism XP with the -s silent flag
        final boolean handled = addxpCommand.onCommand(sender, command, "addxp",
                new String[]{"testPlayer", "herbalism", "50", "-s"});

        // Then - the XP gain is still processed but the sender hears nothing
        assertThat(handled).isTrue();
        verify(mmoPlayer).applyXpGain(PrimarySkillType.HERBALISM, 50F, XPGainReason.COMMAND,
                XPGainSource.COMMAND);
        verify(sender, never()).sendMessage(anyString());
    }
}
