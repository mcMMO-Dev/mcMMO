package com.gmail.nossr50.util.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.McMMOMessageType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

/**
 * Verifies that sub-skill unlock notifications honor the
 * Feedback.ActionBarNotifications.SubSkillUnlocked settings from advanced.yml instead of
 * being hardcoded to the chat system.
 */
class NotificationManagerTest {

    private static final SubSkillType UNLOCKED_SUB_SKILL = SubSkillType.ACROBATICS_ROLL;

    private MockedStatic<mcMMO> mockedMcMMO;
    private MockedStatic<Bukkit> mockedBukkit;
    private MockedStatic<TextComponentFactory> mockedTextComponentFactory;
    private MockedStatic<SoundManager> mockedSoundManager;

    private AdvancedConfig advancedConfig;
    private Server server;
    private PluginManager pluginManager;
    private BukkitAudiences audiences;
    private Audience audience;
    private Player player;
    private Location playerLocation;
    private McMMOPlayer mmoPlayer;
    private TextComponent unlockMessage;

    @BeforeEach
    void setUp() {
        mockedMcMMO = mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        advancedConfig = mock(AdvancedConfig.class);
        when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);

        player = mock(Player.class);
        playerLocation = mock(Location.class);
        when(player.getLocation()).thenReturn(playerLocation);
        mmoPlayer = mock(McMMOPlayer.class);
        when(mmoPlayer.getPlayer()).thenReturn(player);
        when(mmoPlayer.useChatNotifications()).thenReturn(true);

        audiences = mock(BukkitAudiences.class);
        audience = mock(Audience.class);
        when(audiences.player(player)).thenReturn(audience);
        mockedMcMMO.when(mcMMO::getAudiences).thenReturn(audiences);

        server = mock(Server.class);
        pluginManager = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(pluginManager);
        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);

        unlockMessage = Component.text("unlock notification");
        mockedTextComponentFactory = mockStatic(TextComponentFactory.class);
        mockedTextComponentFactory.when(() -> TextComponentFactory
                        .getSubSkillUnlockedNotificationComponents(player, UNLOCKED_SUB_SKILL))
                .thenReturn(unlockMessage);

        mockedSoundManager = mockStatic(SoundManager.class);
    }

    @AfterEach
    void tearDown() {
        mockedSoundManager.close();
        mockedTextComponentFactory.close();
        mockedBukkit.close();
        mockedMcMMO.close();
    }

    /**
     * Covers every advanced.yml combination, including the shipped default (action bar
     * disabled) which must keep the unlock message in chat like before the setting worked.
     */
    @ParameterizedTest
    @CsvSource({
            "true, false, 1, 0",
            "true, true, 1, 1",
            "false, false, 0, 1",
            "false, true, 0, 1",
    })
    void unlockNotificationShouldRouteBasedOnActionBarConfig(boolean useActionBar,
            boolean sendCopyToChat, int expectedActionBarSends, int expectedChatSends) {
        // Given - advanced.yml configures where SubSkillUnlocked notifications are shown
        stubUnlockNotificationConfig(useActionBar, sendCopyToChat);

        // When - the player is notified about a newly unlocked sub-skill
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, UNLOCKED_SUB_SKILL);

        // Then - the message goes to the destination(s) chosen in the config
        verify(audience, times(expectedActionBarSends)).sendActionBar(unlockMessage);
        verify(audience, times(expectedChatSends)).sendMessage(unlockMessage);
    }

    @Test
    void unlockNotificationShouldFireEventWithSubSkillUnlockedType() {
        // Given - the action bar is enabled for SubSkillUnlocked notifications
        stubUnlockNotificationConfig(true, true);

        // When - the player is notified about a newly unlocked sub-skill
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, UNLOCKED_SUB_SKILL);

        // Then - a notification event is fired so other plugins can inspect or cancel it
        final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(pluginManager).callEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue())
                .isInstanceOfSatisfying(McMMOPlayerNotificationEvent.class, event -> {
                    assertThat(event.getEventNotificationType())
                            .isEqualTo(NotificationType.SUBSKILL_UNLOCKED);
                    assertThat(event.getChatMessageType())
                            .isEqualTo(McMMOMessageType.ACTION_BAR);
                    assertThat(event.isMessageAlsoBeingSentToChat()).isTrue();
                });
    }

    @Test
    void unlockNotificationShouldNotSendMessagesWhenEventIsCancelled() {
        // Given - another plugin cancels the notification event
        stubUnlockNotificationConfig(true, true);
        doAnswer(invocation -> {
            invocation.getArgument(0, McMMOPlayerNotificationEvent.class).setCancelled(true);
            return null;
        }).when(pluginManager).callEvent(any(McMMOPlayerNotificationEvent.class));

        // When - the player would be notified about a newly unlocked sub-skill
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, UNLOCKED_SUB_SKILL);

        // Then - no message is delivered to any destination
        verify(audience, never()).sendActionBar(unlockMessage);
        verify(audience, never()).sendMessage(unlockMessage);
    }

    @Test
    void unlockNotificationShouldPlayUnlockSoundRegardlessOfDestination() {
        // Given - the unlock message is routed to chat (shipped default)
        stubUnlockNotificationConfig(false, true);

        // When - the player is notified about a newly unlocked sub-skill
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, UNLOCKED_SUB_SKILL);

        // Then - the level up jingle still plays like it always has
        mockedSoundManager.verify(() -> SoundManager.sendCategorizedSound(player, playerLocation,
                SoundType.SKILL_UNLOCKED, SoundCategory.MASTER));
    }

    @Test
    void unlockNotificationShouldDoNothingWhenPlayerDisabledChatNotifications() {
        // Given - the player has toggled mcMMO notifications off
        stubUnlockNotificationConfig(true, true);
        when(mmoPlayer.useChatNotifications()).thenReturn(false);

        // When - the player would be notified about a newly unlocked sub-skill
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, UNLOCKED_SUB_SKILL);

        // Then - no event is fired, nothing is sent, and no sound plays
        verifyNoInteractions(pluginManager);
        verifyNoInteractions(audience);
        mockedSoundManager.verifyNoInteractions();
    }

    /**
     * Pins the admin notification identity format: admins need to know who ran a sensitive
     * command, but chat messages must never print the sender's UUID. The UUID lives on a
     * hover event attached to the sender's name instead. The console has no hover, so its
     * copy keeps the UUID inline in a readable format.
     */
    @ParameterizedTest
    @EnumSource(value = SensitiveCommandType.class, names = {"XPRATE_MODIFY", "XPRATE_END"})
    void sensitiveCommandNotificationShouldHideSenderUuidBehindHover(
            SensitiveCommandType commandType) throws IOException {
        // Given - locale bootstrap plus admin notifications enabled with one op online
        final GeneralConfig generalConfig = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(generalConfig.adminNotifications()).thenReturn(true);
        mockedMcMMO.when(mcMMO::getLocalesDirectory)
                .thenReturn(Files.createTempDirectory("mcmmo-test-locales") + File.separator);
        final Logger logger = mock(Logger.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        final Player adminViewer = mock(Player.class);
        when(adminViewer.isOp()).thenReturn(true);
        doReturn(List.of(adminViewer)).when(server).getOnlinePlayers();
        final Audience adminAudience = mock(Audience.class);
        when(audiences.player(adminViewer)).thenReturn(adminAudience);

        // And - a player who runs the sensitive command
        final UUID senderUuid = UUID.fromString("588fe472-1c82-4c4e-9aa1-7eefccb277e3");
        final Player commandSender = mock(Player.class);
        when(commandSender.getDisplayName()).thenReturn("nossr50");
        when(commandSender.getUniqueId()).thenReturn(senderUuid);

        // When - the admin notification for that command goes out
        NotificationManager.processSensitiveCommandNotification(commandSender, commandType,
                "1.5");

        // Then - the chat message marks the sender's name as hoverable with an @ prefix and
        // never prints the UUID
        final ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(adminAudience).sendMessage(messageCaptor.capture());
        final Component chatMessage = messageCaptor.getValue();
        assertThat(LegacyComponentSerializer.legacySection().serialize(chatMessage))
                .contains("@nossr50")
                .doesNotContain(senderUuid.toString());

        // And - the UUID is available to admins on a hover attached to the sender's name
        assertThat(hoverContents(chatMessage)).anySatisfy(hover -> assertThat(
                LegacyComponentSerializer.legacySection().serialize(hover))
                .contains("UUID of ")
                .contains("nossr50")
                .contains(senderUuid.toString()));

        // And - the console has no hover, so its copy keeps the UUID inline but readable
        final ArgumentCaptor<String> consoleCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger).info(consoleCaptor.capture());
        assertThat(consoleCaptor.getValue())
                .contains("nossr50 (" + senderUuid + ")");
    }

    private static List<Component> hoverContents(Component root) {
        final List<Component> hovers = new ArrayList<>();
        collectHoverContents(root, hovers);
        return hovers;
    }

    private static void collectHoverContents(Component component, List<Component> hovers) {
        final HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null && hoverEvent.value() instanceof Component) {
            hovers.add((Component) hoverEvent.value());
        }
        for (final Component child : component.children()) {
            collectHoverContents(child, hovers);
        }
    }

    private void stubUnlockNotificationConfig(boolean useActionBar, boolean sendCopyToChat) {
        when(advancedConfig.doesNotificationUseActionBar(NotificationType.SUBSKILL_UNLOCKED))
                .thenReturn(useActionBar);
        when(advancedConfig.doesNotificationSendCopyToChat(NotificationType.SUBSKILL_UNLOCKED))
                .thenReturn(sendCopyToChat);
    }
}
