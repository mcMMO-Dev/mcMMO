package com.gmail.nossr50.util;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.ACROBATICS;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventUtilsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(EventUtilsTest.class.getName());

    @BeforeEach
    void setUpEnvironment() {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /** Simulates another plugin cancelling every mcMMO level event. */
    private void cancelLevelEvents() {
        doAnswer(invocation -> {
            final Object event = invocation.getArgument(0);
            if (event instanceof Cancellable cancellable) {
                cancellable.setCancelled(true);
            }
            return event;
        }).when(pluginManager).callEvent(any(Event.class));
    }

    /**
     * Regression coverage for cancelled /mmoedit level ups: the cancel rollback previously
     * subtracted the absolute new level instead of restoring the old one, resetting the skill
     * to zero.
     */
    @Test
    void cancelledLevelUpEditShouldRestoreOldLevel() {
        // Given - the player's Acrobatics was raised from level 5 to level 10 by /mmoedit
        mmoPlayer.modifySkill(ACROBATICS, 10);
        // And - another plugin cancels the level event
        cancelLevelEvents();

        // When - the level edit event is fired
        final boolean cancelled = EventUtils.tryLevelEditEvent(mmoPlayer, ACROBATICS, 10, 0F,
                true, XPGainReason.COMMAND, 5);

        // Then - the edit reports cancelled and the skill is restored to its old level
        assertThat(cancelled).isTrue();
        assertThat(mmoPlayer.getSkillLevel(ACROBATICS)).isEqualTo(5);
    }

    /**
     * Regression coverage for cancelled /mmoedit level downs: the cancel rollback previously
     * added the absolute new level back on top of it instead of restoring the old level.
     */
    @Test
    void cancelledLevelDownEditShouldRestoreOldLevel() {
        // Given - the player's Acrobatics was lowered from level 10 to level 4 by /mmoedit
        mmoPlayer.modifySkill(ACROBATICS, 4);
        // And - another plugin cancels the level event
        cancelLevelEvents();

        // When - the level edit event is fired
        final boolean cancelled = EventUtils.tryLevelEditEvent(mmoPlayer, ACROBATICS, 4, 0F,
                false, XPGainReason.COMMAND, 10);

        // Then - the edit reports cancelled and the skill is restored to its old level
        assertThat(cancelled).isTrue();
        assertThat(mmoPlayer.getSkillLevel(ACROBATICS)).isEqualTo(10);
    }

    /**
     * The level-down event fired for /mmoedit previously reported the absolute new level as the
     * number of levels lost, feeding garbage to plugins listening to the event.
     */
    @Test
    void levelDownEditEventShouldReportLevelsLost() {
        // Given - the player's Acrobatics was lowered from level 10 to level 4 by /mmoedit
        mmoPlayer.modifySkill(ACROBATICS, 4);
        // And - the fired event is captured
        final AtomicReference<McMMOPlayerLevelDownEvent> fired = new AtomicReference<>();
        doAnswer(invocation -> {
            final Object event = invocation.getArgument(0);
            if (event instanceof McMMOPlayerLevelDownEvent downEvent) {
                fired.set(downEvent);
            }
            return event;
        }).when(pluginManager).callEvent(any(Event.class));

        // When - the level edit event is fired
        EventUtils.tryLevelEditEvent(mmoPlayer, ACROBATICS, 4, 0F, false, XPGainReason.COMMAND,
                10);

        // Then - the event reports the 6 levels actually lost, not the absolute new level
        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getLevelsLost()).isEqualTo(6);
    }

    /**
     * Guard: the level-up edit event already reported the correct delta and must keep doing so.
     */
    @Test
    void levelUpEditEventShouldReportLevelsGained() {
        // Given - the player's Acrobatics was raised from level 5 to level 10 by /mmoedit
        mmoPlayer.modifySkill(ACROBATICS, 10);
        // And - the fired event is captured
        final AtomicReference<McMMOPlayerLevelUpEvent> fired = new AtomicReference<>();
        doAnswer(invocation -> {
            final Object event = invocation.getArgument(0);
            if (event instanceof McMMOPlayerLevelUpEvent upEvent) {
                fired.set(upEvent);
            }
            return event;
        }).when(pluginManager).callEvent(any(Event.class));

        // When - the level edit event is fired
        EventUtils.tryLevelEditEvent(mmoPlayer, ACROBATICS, 10, 0F, true, XPGainReason.COMMAND,
                5);

        // Then - the event reports the 5 levels actually gained and the level is untouched
        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getLevelsGained()).isEqualTo(5);
        assertThat(mmoPlayer.getSkillLevel(ACROBATICS)).isEqualTo(10);
    }

    /**
     * Regression coverage for the Player overload: it is only reached when the player's mcMMO
     * data is not loaded, and a cancelled event then dereferenced that missing data and crashed.
     */
    @Test
    void cancelledLevelChangeShouldNotThrowWhenPlayerDataIsNotLoaded() {
        // Given - a player whose mcMMO data is not loaded
        final Player unloadedPlayer = mock(Player.class);
        when(unloadedPlayer.getName()).thenReturn("unloaded");
        // And - another plugin cancels the level event
        cancelLevelEvents();

        // When - the level change event is fired through the Player overload
        // Then - the call reports cancelled without crashing, even though nothing can be reverted
        assertThatCode(() -> {
            final boolean cancelled = EventUtils.tryLevelChangeEvent(unloadedPlayer, ACROBATICS,
                    5, 0F, true, XPGainReason.COMMAND);
            assertThat(cancelled).isTrue();
        }).doesNotThrowAnyException();
    }

    /**
     * Guard: a cancelled level change for a loaded player must keep rolling the level back,
     * including through the Player overload.
     */
    @Test
    void cancelledLevelChangeShouldRestoreLevelsWhenDataIsLoaded() {
        // Given - the player's Acrobatics was raised from level 5 to level 8 by /addlevels
        mmoPlayer.modifySkill(ACROBATICS, 5);
        mmoPlayer.addLevels(ACROBATICS, 3);
        // And - another plugin cancels the level event
        cancelLevelEvents();

        // When - the level change event is fired through the Player overload
        final boolean cancelled = EventUtils.tryLevelChangeEvent(player, ACROBATICS, 3, 0F, true,
                XPGainReason.COMMAND);

        // Then - the change reports cancelled and the levels are rolled back
        assertThat(cancelled).isTrue();
        assertThat(mmoPlayer.getSkillLevel(ACROBATICS)).isEqualTo(5);
    }
}
