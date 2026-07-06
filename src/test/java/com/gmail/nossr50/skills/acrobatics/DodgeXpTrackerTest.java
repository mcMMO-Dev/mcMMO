package com.gmail.nossr50.skills.acrobatics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pure unit tests for the per-mob Dodge XP reward tracker, using injected timestamps so the
 * idle window and sweep behavior are deterministic.
 */
class DodgeXpTrackerTest {
    private static final long START = 1_000_000L;

    @BeforeEach
    void setUp() {
        DodgeXpTracker.clearAll();
    }

    @AfterEach
    void tearDown() {
        DodgeXpTracker.clearAll();
    }

    @Test
    void tryConsumeXpRewardShouldGrantXpOnlyUpToCapForASingleMob() {
        // Given - a mob that has not been dodged yet
        final UUID mobId = UUID.randomUUID();

        // When / Then - only the first MAX_XP_REWARDS_PER_MOB dodges grant XP
        for (int i = 0; i < DodgeXpTracker.MAX_XP_REWARDS_PER_MOB; i++) {
            assertThat(DodgeXpTracker.tryConsumeXpReward(mobId, START + i)).isTrue();
        }

        // And - further dodges inside the idle window grant nothing
        assertThat(DodgeXpTracker.tryConsumeXpReward(mobId, START + 1_000)).isFalse();
        assertThat(DodgeXpTracker.tryConsumeXpReward(mobId, START + 2_000)).isFalse();
    }

    @Test
    void tryConsumeXpRewardShouldTrackMobsIndependently() {
        // Given - one mob already at its reward cap
        final UUID exhaustedMobId = UUID.randomUUID();
        for (int i = 0; i <= DodgeXpTracker.MAX_XP_REWARDS_PER_MOB; i++) {
            DodgeXpTracker.tryConsumeXpReward(exhaustedMobId, START);
        }

        // When - a fresh mob is dodged at the same time
        final UUID freshMobId = UUID.randomUUID();

        // Then - the exhausted mob grants nothing while the fresh mob still rewards
        assertThat(DodgeXpTracker.tryConsumeXpReward(exhaustedMobId, START)).isFalse();
        assertThat(DodgeXpTracker.tryConsumeXpReward(freshMobId, START)).isTrue();
    }

    @Test
    void tryConsumeXpRewardShouldResetWhenMobNotDodgedForIdleWindow() {
        // Given - a mob at its reward cap
        final UUID mobId = UUID.randomUUID();
        for (int i = 0; i <= DodgeXpTracker.MAX_XP_REWARDS_PER_MOB; i++) {
            DodgeXpTracker.tryConsumeXpReward(mobId, START);
        }
        assertThat(DodgeXpTracker.tryConsumeXpReward(mobId, START)).isFalse();

        // When - the mob goes a full idle window without being dodged
        final long afterIdleWindow = START + DodgeXpTracker.IDLE_RESET_MILLIS;

        // Then - the mob rewards XP again
        assertThat(DodgeXpTracker.tryConsumeXpReward(mobId, afterIdleWindow)).isTrue();
    }

    @Test
    void tryConsumeXpRewardShouldNotResetWhileMobKeepsBeingDodged() {
        // Given - a mob at its reward cap
        final UUID mobId = UUID.randomUUID();
        long now = START;
        for (int i = 0; i < DodgeXpTracker.MAX_XP_REWARDS_PER_MOB; i++) {
            DodgeXpTracker.tryConsumeXpReward(mobId, now);
        }

        // When - dodges keep arriving just inside the idle window for a long stretch
        for (int i = 0; i < 10; i++) {
            now += DodgeXpTracker.IDLE_RESET_MILLIS - 1;
            // Then - every dodge refreshes the idle timer, so the cap never resets
            assertThat(DodgeXpTracker.tryConsumeXpReward(mobId, now)).isFalse();
        }
    }

    @Test
    void sweepShouldRemoveOnlyStaleEntries() {
        // Given - a mob dodged long ago and a mob dodged recently
        final UUID staleMobId = UUID.randomUUID();
        DodgeXpTracker.tryConsumeXpReward(staleMobId, START);
        final UUID recentMobId = UUID.randomUUID();
        DodgeXpTracker.tryConsumeXpReward(recentMobId,
                START + DodgeXpTracker.IDLE_RESET_MILLIS - 1);

        // When - a dodge lands after the idle window, which triggers a sweep
        final UUID newMobId = UUID.randomUUID();
        DodgeXpTracker.tryConsumeXpReward(newMobId,
                START + DodgeXpTracker.IDLE_RESET_MILLIS + 10_000);

        // Then - only the stale mob's entry was removed
        assertThat(DodgeXpTracker.trackedMobCount()).isEqualTo(2);
    }

    @Test
    void freshEntriesShouldNeverBeEvictedRegardlessOfMapSize() {
        // Given - a player being attacked by a large pack of mobs at once
        final int mobCount = 100;

        // When - every mob is dodged within the idle window
        for (int i = 0; i < mobCount; i++) {
            DodgeXpTracker.tryConsumeXpReward(UUID.randomUUID(), START + i);
        }

        // Then - every mob keeps its own entry; there is no size-based eviction
        assertThat(DodgeXpTracker.trackedMobCount()).isEqualTo(mobCount);
    }
}
