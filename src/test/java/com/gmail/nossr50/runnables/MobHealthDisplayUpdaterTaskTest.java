package com.gmail.nossr50.runnables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.meta.HealthbarSnapshot;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import java.util.logging.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link MobHealthDisplayUpdaterTask} polling logic.
 *
 * <p>A controllable {@link LongSupplier} is injected in place of {@code System::currentTimeMillis}
 * so each test can advance the clock without sleeping. The task's three natural exit conditions are
 * each exercised independently: entity invalid, snapshot absent, and display time elapsed.
 */
class MobHealthDisplayUpdaterTaskTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(MobHealthDisplayUpdaterTaskTest.class.getName());
    private static final long DISPLAY_TIME_MS = 3_000L;

    private LivingEntity target;
    private final AtomicLong fakeTime = new AtomicLong();
    private LongSupplier timeSource;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        target = Mockito.mock(LivingEntity.class);
        fakeTime.set(1_000L);
        timeSource = fakeTime::get;
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private void stubSnapshotPresent(final HealthbarSnapshot snapshot) {
        final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);
        when(target.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                .thenReturn(List.of(metaValue));
    }

    private void stubSnapshotAbsent() {
        when(target.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                .thenReturn(Collections.emptyList());
    }

    @Nested
    class WhenEntityInvalid {

        @BeforeEach
        void arrange() {
            when(target.isValid()).thenReturn(false);
        }

        @Test
        void doesNotRestoreName() {
            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target, never()).setCustomName(any());
            verify(target, never()).setCustomNameVisible(anyBoolean());
        }

        @Test
        void doesNotRemoveMetadata() {
            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target, never()).removeMetadata(any(), any());
        }
    }

    @Nested
    class WhenNoSnapshot {

        @BeforeEach
        void arrange() {
            when(target.isValid()).thenReturn(true);
            stubSnapshotAbsent();
        }

        @Test
        void doesNotRestoreName() {
            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target, never()).setCustomName(any());
            verify(target, never()).setCustomNameVisible(anyBoolean());
        }
    }

    @Nested
    class WhenDisplayTimeNotElapsed {

        @Test
        void doesNotRestoreNameWhenHalfTimeElapsed() {
            // lastHitMs = 0, fakeTime = DISPLAY_TIME_MS/2 — not enough time has passed
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Boss", true, 0L);
            fakeTime.set(DISPLAY_TIME_MS / 2);

            when(target.isValid()).thenReturn(true);
            stubSnapshotPresent(snapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target, never()).setCustomName(any());
            verify(target, never()).removeMetadata(any(), any());
        }

        @Test
        void doesNotRestoreOnMillisecondBeforeBoundary() {
            // elapsed = DISPLAY_TIME_MS - 1 — one millisecond short of the trigger threshold
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Boss", true, 0L);
            fakeTime.set(DISPLAY_TIME_MS - 1);

            when(target.isValid()).thenReturn(true);
            stubSnapshotPresent(snapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target, never()).setCustomName(any());
        }
    }

    @Nested
    class WhenDisplayTimeElapsed {

        @BeforeEach
        void arrangeEntityValid() {
            when(target.isValid()).thenReturn(true);
        }

        @Test
        void restoresNamedMob() {
            // lastHitMs = 0, fakeTime = DISPLAY_TIME_MS → elapsed = 3000ms >= 3000ms
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("BossZombie", true, 0L);
            fakeTime.set(DISPLAY_TIME_MS);
            stubSnapshotPresent(snapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target).setCustomName("BossZombie");
            verify(target).setCustomNameVisible(true);
            verify(target).removeMetadata(
                    eq(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT), eq(mcMMO.p));
        }

        @Test
        void restoresNullName() {
            // Vanilla mob with no custom name — null must be restored, not ""
            final HealthbarSnapshot snapshot = new HealthbarSnapshot(null, false, 0L);
            fakeTime.set(DISPLAY_TIME_MS);
            stubSnapshotPresent(snapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target).setCustomName((String) null);
            verify(target).setCustomNameVisible(false);
            verify(target).removeMetadata(
                    eq(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT), eq(mcMMO.p));
        }

        @Test
        void exactBoundaryTriggersRestore() {
            // elapsed exactly equals displayTimeMs — boundary must trigger restore (>=, not >)
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Mob", false, 0L);
            fakeTime.set(DISPLAY_TIME_MS); // exactly at boundary
            stubSnapshotPresent(snapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            verify(target).setCustomName("Mob");
        }

        @Test
        void doesNotRestoreWhenReHitRefreshedTimestamp() {
            // Simulates a re-hit that updated lastHitMs to fakeTime - 1 (still not elapsed)
            final long reHitMs = fakeTime.get() - 1; // elapsed = 1ms < DISPLAY_TIME_MS
            final HealthbarSnapshot refreshedSnapshot =
                    new HealthbarSnapshot("Boss", true, reHitMs);
            stubSnapshotPresent(refreshedSnapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);
            task.run();

            // lastHitMs was just refreshed — display window extended, no restore yet
            verify(target, never()).setCustomName(any());
        }
    }

    @Nested
    class PollCountInvariants {

        @Test
        void pollIntervalTicksIsPositive() {
            assertThat(MobHealthDisplayUpdaterTask.POLL_INTERVAL_TICKS).isGreaterThan(0);
        }

        @Test
        void stalePollLimitIsSensible() {
            assertThat(MobHealthDisplayUpdaterTask.STALE_POLL_LIMIT).isGreaterThan(0);
        }
    }

    @Nested
    class StalePollGuard {

        @Test
        void restoresAfterStalePollLimit() {
            // lastHitMs = 0; fakeTime = DISPLAY_TIME_MS - 1 so elapsed never reaches displayTimeMs.
            // After STALE_POLL_LIMIT consecutive idle polls the failsafe must restore.
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Boss", true, 0L);
            fakeTime.set(DISPLAY_TIME_MS - 1);

            when(target.isValid()).thenReturn(true);
            stubSnapshotPresent(snapshot);

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);

            // First call initialises lastObservedLastHitMs — stale counting starts on call 2.
            // Stale fires when stalePollCount reaches STALE_POLL_LIMIT, which happens on
            // call (STALE_POLL_LIMIT + 1).
            for (int i = 0; i < MobHealthDisplayUpdaterTask.STALE_POLL_LIMIT; i++) {
                task.run();
            }
            verify(target, never()).setCustomName(any()); // not yet

            task.run(); // this call pushes stalePollCount to STALE_POLL_LIMIT
            verify(target).setCustomName("Boss");
            verify(target).removeMetadata(
                    eq(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT), eq(mcMMO.p));
        }

        @Test
        void resetsCounterOnLastHitMsChange() {
            // Run 50 idle polls, then simulate a re-hit (new lastHitMs), run 50 more.
            // Total: 100 calls, but counter resets at the re-hit, so stale limit is never reached.
            fakeTime.set(DISPLAY_TIME_MS - 1); // elapsed always < displayTimeMs
            when(target.isValid()).thenReturn(true);

            final HealthbarSnapshot firstSnapshot = new HealthbarSnapshot("Boss", true, 0L);
            final HealthbarSnapshot reHitSnapshot = new HealthbarSnapshot("Boss", true, 500L);
            final AtomicReference<HealthbarSnapshot> current = new AtomicReference<>(firstSnapshot);

            when(target.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenAnswer(inv -> List.of(new FixedMetadataValue(mcMMO.p, current.get())));

            final MobHealthDisplayUpdaterTask task =
                    new MobHealthDisplayUpdaterTask(target, DISPLAY_TIME_MS, timeSource);

            for (int i = 0; i < 50; i++) {
                task.run();
            }
            current.set(reHitSnapshot); // simulate re-hit: lastHitMs updated
            for (int i = 0; i < 50; i++) {
                task.run();
            }

            // 100 total calls, but the re-hit reset the counter — stale limit not reached
            verify(target, never()).setCustomName(any());
        }
    }
}
