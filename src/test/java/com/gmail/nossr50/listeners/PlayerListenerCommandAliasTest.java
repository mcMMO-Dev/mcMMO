package com.gmail.nossr50.listeners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import java.lang.reflect.Method;
import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the localized skill command alias handling in
 * {@link PlayerListener#onPlayerCommandPreprocess(PlayerCommandPreprocessEvent)}.
 *
 * <p>Localized skill names (e.g. German "faustkampf" for "unarmed") are not registered commands,
 * so the listener rewrites the command message to the English skill command. Command filtering
 * plugins typically inspect the very same event at LOWEST priority, so the listener must run
 * after them (LOW) and must leave the message untouched when an earlier handler already
 * cancelled the command. Otherwise server owners are forced to allow both the localized and the
 * English command names.
 */
@TestInstance(Lifecycle.PER_CLASS)
class PlayerListenerCommandAliasTest {

    private static MockedStatic<mcMMO> mockedMcMMO;
    private PlayerListener playerListener;
    private GeneralConfig generalConfig;
    private Player player;

    @BeforeAll
    void setUpAll() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);

        generalConfig = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getLocale()).thenReturn("de_DE");

        // Localized skill names: UNARMED maps to the German "Faustkampf", every other skill
        // just echoes its English name so it can never accidentally match the tested alias
        final SkillTools skillTools = mock(SkillTools.class);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);
        when(skillTools.getLocalizedSkillName(any(PrimarySkillType.class))).thenAnswer(
                invocation -> {
                    final PrimarySkillType skill = invocation.getArgument(0);
                    return skill == PrimarySkillType.UNARMED ? "Faustkampf" : skill.toString();
                });

        player = mock(Player.class);
        playerListener = new PlayerListener(mcMMO.p);
    }

    /**
     * Builds the event with an explicit recipient set; the two-arg constructor would call
     * {@code player.getServer()} which is not stubbed on the mocked player.
     */
    private PlayerCommandPreprocessEvent newCommandEvent(String message) {
        return new PlayerCommandPreprocessEvent(player, message, new HashSet<>());
    }

    @AfterAll
    void tearDownAll() {
        mockedMcMMO.close();
    }

    @ParameterizedTest
    @CsvSource({
            "/faustkampf, /unarmed",
            "/Faustkampf, /unarmed",
            "/FAUSTKAMPF, /unarmed",
            "'/faustkampf ?', '/unarmed ?'",
            "'/faustkampf ? 2', '/unarmed ? 2'",
    })
    void localizedAliasShouldBeRewrittenToEnglishCommand(String typed, String expected) {
        // Given - a non-English locale and a player using the localized skill command alias
        final PlayerCommandPreprocessEvent event = newCommandEvent(typed);

        // When - the alias listener processes the command
        playerListener.onPlayerCommandPreprocess(event);

        // Then - the message is rewritten to the registered English skill command
        assertThat(event.getMessage()).isEqualTo(expected);

        // And - the event stays uncancelled so the server executes the rewritten command
        // normally and later handlers (command loggers, etc.) still observe it
        assertThat(event.isCancelled()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/unarmed", "/unarmed ?", "/help", "/faust", "/faustkampfx"})
    void commandShouldBeLeftUntouchedWhenItIsNotALocalizedAlias(String typed) {
        // Given - a non-English locale and a command that is not a localized skill alias
        final PlayerCommandPreprocessEvent event = newCommandEvent(typed);

        // When - the alias listener processes the command
        playerListener.onPlayerCommandPreprocess(event);

        // Then - the message is passed through unchanged and the event is not cancelled
        assertThat(event.getMessage()).isEqualTo(typed);
        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void localizedAliasShouldNotBeRewrittenWhenLocaleIsEnglish() {
        // Given - the default en_US locale where no localized aliases exist
        when(generalConfig.getLocale()).thenReturn("en_US");
        final PlayerCommandPreprocessEvent event = newCommandEvent("/faustkampf");

        try {
            // When - the alias listener processes the command
            playerListener.onPlayerCommandPreprocess(event);

            // Then - the message is passed through unchanged
            assertThat(event.getMessage()).isEqualTo("/faustkampf");
        } finally {
            when(generalConfig.getLocale()).thenReturn("de_DE");
        }
    }

    /**
     * Command filtering plugins commonly check commands at LOWEST priority on this very event.
     * Running the alias rewrite at LOW guarantees they see the command exactly as the player
     * typed it, so server owners only need to allow the localized command name. Ignoring
     * cancelled events guarantees a command blocked by such a plugin is not rewritten back into
     * an executable form for later handlers.
     */
    @Test
    void aliasListenerShouldRunAtLowPriorityAndIgnoreCancelledEvents() throws Exception {
        // Given - the registered alias handler method
        final Method handler = PlayerListener.class.getMethod("onPlayerCommandPreprocess",
                PlayerCommandPreprocessEvent.class);

        // When - reading its event handler registration
        final EventHandler eventHandler = handler.getAnnotation(EventHandler.class);

        // Then - it runs after LOWEST priority command filtering plugins
        assertThat(eventHandler).isNotNull();
        assertThat(eventHandler.priority()).isEqualTo(EventPriority.LOW);

        // And - it does not touch commands an earlier handler already blocked
        assertThat(eventHandler.ignoreCancelled()).isTrue();
    }
}
