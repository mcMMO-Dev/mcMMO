package com.gmail.nossr50.util.sounds;

import static com.gmail.nossr50.util.sounds.SkillUnlockSoundThrottle.QUIET_WINDOW_MILLIS;
import static com.gmail.nossr50.util.sounds.SkillUnlockSoundThrottle.clearAll;
import static com.gmail.nossr50.util.sounds.SkillUnlockSoundThrottle.trackedPlayerCount;
import static com.gmail.nossr50.util.sounds.SkillUnlockSoundThrottle.tryPlaySound;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the quiet window logic keeping batched unlock notifications from spamming the
 * unlock sound. The window must slide with every notification, so a stream staggered five
 * seconds apart stays quiet end to end no matter how long it runs.
 */
class SkillUnlockSoundThrottleTest {
    /** Far enough from zero that the first opportunistic sweep gate always opens. */
    private static final long START = 1_000_000L;
    /** The scheduler staggers batched unlock notifications this far apart. */
    private static final long BATCH_STAGGER_MILLIS = 5_000L;

    private final UUID playerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        clearAll();
    }

    @AfterEach
    void tearDown() {
        clearAll();
    }

    @Test
    void firstNotificationShouldPlayTheSound() {
        // Given - the player has received no unlock notifications

        // When / Then - the first notification plays the sound
        assertThat(tryPlaySound(playerId, START)).isTrue();
    }

    @Test
    void notificationInsideTheQuietWindowShouldStayQuiet() {
        // Given - an unlock notification just played the sound
        tryPlaySound(playerId, START);

        // When / Then - another notification inside the quiet window stays quiet
        assertThat(tryPlaySound(playerId, START + BATCH_STAGGER_MILLIS)).isFalse();
    }

    @Test
    void steadyStreamShouldStayQuietUntilItEnds() {
        // Given - the first notification of a long batch played the sound
        assertThat(tryPlaySound(playerId, START)).isTrue();

        // When - the batch keeps arriving at the scheduler stagger, longer than the window
        for (int notification = 1; notification <= 10; notification++) {
            long arrival = START + notification * BATCH_STAGGER_MILLIS;
            // Then - every later batch member stays quiet, the window slides along
            assertThat(tryPlaySound(playerId, arrival)).isFalse();
        }

        // And - once the stream has been over for a full window, sound plays again
        long lastArrival = START + 10 * BATCH_STAGGER_MILLIS;
        assertThat(tryPlaySound(playerId, lastArrival + QUIET_WINDOW_MILLIS)).isTrue();
    }

    @Test
    void notificationAfterTheQuietWindowShouldPlayAgain() {
        // Given - an unlock notification played the sound a full window ago
        tryPlaySound(playerId, START);

        // When / Then - the next notification plays the sound again
        assertThat(tryPlaySound(playerId, START + QUIET_WINDOW_MILLIS)).isTrue();
    }

    @Test
    void playersShouldNotShareTheQuietWindow() {
        // Given - one player just received an unlock notification
        tryPlaySound(playerId, START);

        // When / Then - another player's notification still plays the sound
        final UUID otherPlayerId = UUID.randomUUID();
        assertThat(tryPlaySound(otherPlayerId, START + 1_000)).isTrue();
    }

    @Test
    void staleEntriesShouldBeSweptOut() {
        // Given - a player whose quiet window expired long ago
        tryPlaySound(playerId, START);

        // When - another notification arrives much later, opening the sweep gate
        tryPlaySound(UUID.randomUUID(), START + QUIET_WINDOW_MILLIS * 3);

        // Then - only the fresh entry is still tracked
        assertThat(trackedPlayerCount()).isEqualTo(1);
    }
}
