package com.gmail.nossr50.util.skills;

import static com.gmail.nossr50.util.skills.SkillUnlockNotificationPacer.SLOT_SPACING_MILLIS;
import static com.gmail.nossr50.util.skills.SkillUnlockNotificationPacer.SWEEP_INTERVAL_MILLIS;
import static com.gmail.nossr50.util.skills.SkillUnlockNotificationPacer.reserveSlotDelayTicks;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the per-player pacing schedule for skill unlock notifications: every notification is
 * delivered on a uniform cadence of one per spacing window, so bursts from mass level changes
 * (like /mmoedit all) stream out evenly instead of dumping at once, and players whose
 * schedule has drained get evicted.
 */
class SkillUnlockNotificationPacerTest {
    private static final long START = 1_000_000L;
    private static final long SPACING_TICKS = SLOT_SPACING_MILLIS / 50;
    private final UUID playerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        SkillUnlockNotificationPacer.clearAll();
    }

    @AfterEach
    void tearDown() {
        SkillUnlockNotificationPacer.clearAll();
    }

    @Test
    void firstNotificationShouldFireImmediately() {
        // Given - a player with no notifications scheduled
        // When - the first slot is reserved
        final long delay = reserveSlotDelayTicks(playerId, START);

        // Then - the notification fires right away
        assertThat(delay).isZero();
    }

    @Test
    void burstShouldSpaceNotificationsUniformly() {
        // Given - a burst of notifications arriving in the same instant
        // When - three slots are reserved back to back
        final long first = reserveSlotDelayTicks(playerId, START);
        final long second = reserveSlotDelayTicks(playerId, START);
        final long third = reserveSlotDelayTicks(playerId, START);

        // Then - each notification gets its own slot, one spacing window apart
        assertThat(first).isZero();
        assertThat(second).isEqualTo(SPACING_TICKS);
        assertThat(third).isEqualTo(2 * SPACING_TICKS);
    }

    @Test
    void largeBurstShouldScheduleEveryNotification() {
        // Given - a mass level change queueing a large burst in the same instant
        // When - sixty slots are reserved back to back
        long lastDelay = -1;
        for (int slot = 0; slot < 60; slot++) {
            lastDelay = reserveSlotDelayTicks(playerId, START);
        }

        // Then - nothing is dropped; the final notification takes the sixtieth slot
        assertThat(lastDelay).isEqualTo(59 * SPACING_TICKS);
    }

    @Test
    void lateArrivalShouldContinueTheUniformCadence() {
        // Given - three notifications scheduled at the start, occupying slots up to +2 windows
        reserveSlotDelayTicks(playerId, START);
        reserveSlotDelayTicks(playerId, START);
        reserveSlotDelayTicks(playerId, START);

        // When - another notification arrives midway between the last slot and the next
        final long delay = reserveSlotDelayTicks(playerId,
                START + 2 * SLOT_SPACING_MILLIS + SLOT_SPACING_MILLIS / 2);

        // Then - it takes the next slot on the same cadence rather than firing immediately
        assertThat(delay).isEqualTo(SPACING_TICKS / 2);
    }

    @Test
    void notificationAfterAQuietGapShouldFireImmediately() {
        // Given - a single notification whose slot has already passed
        reserveSlotDelayTicks(playerId, START);

        // When - the next notification arrives one full spacing window later
        final long delay = reserveSlotDelayTicks(playerId, START + SLOT_SPACING_MILLIS);

        // Then - the schedule has drained, so it fires right away
        assertThat(delay).isZero();
    }

    @Test
    void playersShouldPaceIndependently() {
        // Given - one player with a busy notification schedule
        reserveSlotDelayTicks(playerId, START);
        reserveSlotDelayTicks(playerId, START);

        // When - another player's first notification arrives at the same time
        final UUID otherPlayerId = UUID.randomUUID();
        final long delay = reserveSlotDelayTicks(otherPlayerId, START);

        // Then - the other player's notification fires immediately
        assertThat(delay).isZero();
    }

    @Test
    void sweepShouldEvictPlayersWithDrainedSchedules() {
        // Given - a player whose only notification slot has long passed
        reserveSlotDelayTicks(playerId, START);
        assertThat(SkillUnlockNotificationPacer.trackedPlayerCount()).isEqualTo(1);

        // When - another player reserves a slot far enough in the future to trigger a sweep
        reserveSlotDelayTicks(UUID.randomUUID(), START + SWEEP_INTERVAL_MILLIS);

        // Then - the drained player's entry is gone, only the fresh reservation remains
        assertThat(SkillUnlockNotificationPacer.trackedPlayerCount()).isEqualTo(1);
    }
}
