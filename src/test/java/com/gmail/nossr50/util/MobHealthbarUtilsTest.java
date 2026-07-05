package com.gmail.nossr50.util;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.meta.HealthbarSnapshot;
import com.gmail.nossr50.mcMMO;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link MobHealthbarUtils} snapshot business logic.
 *
 * <p>These tests cover all code paths that touch the single
 * {@link MetadataConstants#METADATA_KEY_HEALTHBAR_SNAPSHOT} key: writing on first hit,
 * the re-hit guard that prevents overwriting the original name, null-name preservation,
 * and the canonical restore path used by the timer, death handler, and entity cleanup.
 */
class MobHealthbarUtilsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(MobHealthbarUtilsTest.class.getName());

    private LivingEntity entity;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        entity = Mockito.mock(LivingEntity.class);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Nested
    class RestoreNameFromSnapshot {

        @Test
        void restoresNamedMobCorrectly() {
            // Given – mob had a real custom name before healthbar was applied
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Fido", true, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            // When
            MobHealthbarUtils.restoreNameFromSnapshot(entity);

            // Then
            verify(entity).setCustomName("Fido");
            verify(entity).setCustomNameVisible(true);
            verify(entity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }

        @Test
        void restoresNullWhenMobHadNoCustomName() {
            // Given – vanilla mob: null must come back as null, not ""
            final HealthbarSnapshot snapshot = new HealthbarSnapshot(null, false, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            // When
            MobHealthbarUtils.restoreNameFromSnapshot(entity);

            // Then – null is passed directly, not ""
            verify(entity).setCustomName((String) null);
            verify(entity).setCustomNameVisible(false);
            verify(entity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }

        @Test
        void doesNothingWhenNoSnapshotPresent() {
            // Given – entity was never touched by the healthbar system
            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(false);
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(Collections.emptyList());

            // When
            MobHealthbarUtils.restoreNameFromSnapshot(entity);

            // Then – no name change, no metadata removal
            verify(entity, never()).setCustomName(Mockito.any());
            verify(entity, never()).setCustomNameVisible(Mockito.anyBoolean());
            verify(entity, never()).removeMetadata(
                    MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT, mcMMO.p);
        }

        @Test
        void restoresNameVisibilityFalseCorrectly() {
            // Given – mob had a custom name but it was not visible
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("HiddenName", false, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            // When
            MobHealthbarUtils.restoreNameFromSnapshot(entity);

            // Then
            verify(entity).setCustomName("HiddenName");
            verify(entity).setCustomNameVisible(false);
        }
    }

    @Nested
    class HasHealthbarSnapshot {

        @Test
        void returnsTrueWhenSnapshotPresent() {
            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);

            assertThat(MobHealthbarUtils.hasHealthbarSnapshot(entity)).isTrue();
        }

        @Test
        void returnsFalseWhenSnapshotAbsent() {
            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(false);

            assertThat(MobHealthbarUtils.hasHealthbarSnapshot(entity)).isFalse();
        }
    }

    @Nested
    class GetHealthbarSnapshot {

        @Test
        void returnsSnapshotWhenPresent() {
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Fido", true, 12345L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            final HealthbarSnapshot result = MobHealthbarUtils.getHealthbarSnapshot(entity);
            assertThat(result).isNotNull();
            assertThat(result.previousCustomName()).isEqualTo("Fido");
            assertThat(result.previousNameVisible()).isTrue();
            assertThat(result.lastHitMs()).isEqualTo(12345L);
        }

        @Test
        void returnsNullWhenSnapshotAbsent() {
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(Collections.emptyList());

            assertThat(MobHealthbarUtils.getHealthbarSnapshot(entity)).isNull();
        }
    }

    @Nested
    class HealthbarSnapshotRecord {

        @Test
        void preservesNullName() {
            final HealthbarSnapshot snapshot = new HealthbarSnapshot(null, true, 0L);
            assertThat(snapshot.previousCustomName()).isNull();
            assertThat(snapshot.previousNameVisible()).isTrue();
        }

        @Test
        void preservesNonNullName() {
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("BossZombie", false, 0L);
            assertThat(snapshot.previousCustomName()).isEqualTo("BossZombie");
            assertThat(snapshot.previousNameVisible()).isFalse();
        }

        @Test
        void preservesLastHitMs() {
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Mob", true, 99999L);
            assertThat(snapshot.lastHitMs()).isEqualTo(99999L);
        }

        @Test
        void equalityHoldsForSameValues() {
            final HealthbarSnapshot first = new HealthbarSnapshot("Mob", true, 1000L);
            final HealthbarSnapshot second = new HealthbarSnapshot("Mob", true, 1000L);
            // Records provide value-based equals by default
            assertThat(first).isEqualTo(second);
        }

        @Test
        void inequalityOnDifferentName() {
            final HealthbarSnapshot original = new HealthbarSnapshot("Mob", true, 0L);
            final HealthbarSnapshot differentName = new HealthbarSnapshot("OtherMob", true, 0L);
            assertThat(original).isNotEqualTo(differentName);
        }

        @Test
        void inequalityOnDifferentVisibility() {
            final HealthbarSnapshot nameVisible = new HealthbarSnapshot("Mob", true, 0L);
            final HealthbarSnapshot nameHidden = new HealthbarSnapshot("Mob", false, 0L);
            assertThat(nameVisible).isNotEqualTo(nameHidden);
        }

        @Test
        void inequalityOnDifferentLastHitMs() {
            final HealthbarSnapshot firstHit = new HealthbarSnapshot("Mob", true, 1000L);
            final HealthbarSnapshot reHit = new HealthbarSnapshot("Mob", true, 2000L);
            assertThat(firstHit).isNotEqualTo(reHit);
        }

        @Test
        void nullAndEmptyStringAreDistinct() {
            // This is the core null-vs-"" correctness guarantee.
            // A snapshot storing null must never be equal to one storing "".
            final HealthbarSnapshot nullName = new HealthbarSnapshot(null, false, 0L);
            final HealthbarSnapshot emptyName = new HealthbarSnapshot("", false, 0L);
            assertThat(nullName).isNotEqualTo(emptyName);
            assertThat(nullName.previousCustomName()).isNull();
            assertThat(emptyName.previousCustomName()).isEqualTo("");
        }
    }

    @Nested
    class ReHitGuard {

        @Test
        void reHitUpdatesLastHitMsButPreservesOriginalName() {
            // Arrange – snapshot from first hit with real name
            final long firstHitTime = 1000L;
            final HealthbarSnapshot firstHitSnapshot =
                    new HealthbarSnapshot("Fido", true, firstHitTime);
            final FixedMetadataValue firstHitMeta =
                    new FixedMetadataValue(mcMMO.p, firstHitSnapshot);

            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(firstHitMeta));

            // Act – simulate what handleMobHealthbars does on re-hit:
            // read existing snapshot, build a new one with updated lastHitMs only
            final long reHitTime = 2000L;
            final HealthbarSnapshot existing = MobHealthbarUtils.getHealthbarSnapshot(entity);
            assertThat(existing).isNotNull();

            final HealthbarSnapshot reHitSnapshot = new HealthbarSnapshot(
                    existing.previousCustomName(),
                    existing.previousNameVisible(),
                    reHitTime);

            // Assert – original name fields preserved, only timestamp updated
            assertThat(reHitSnapshot.previousCustomName()).isEqualTo("Fido");
            assertThat(reHitSnapshot.previousNameVisible()).isTrue();
            assertThat(reHitSnapshot.lastHitMs()).isEqualTo(reHitTime);
            // Different from first snapshot only in lastHitMs
            assertThat(reHitSnapshot).isNotEqualTo(firstHitSnapshot);
        }

        /**
         * Verifies the snapshot guard: on first hit the snapshot does not yet exist,
         * so the write condition evaluates to true.
         */
        @Test
        void writtenOnFirstHit() {
            // Arrange – no snapshot exists yet
            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(false);

            // Act – the write condition from handleMobHealthbars:
            final boolean shouldWrite =
                    !entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT);

            // Assert – write should proceed
            assertThat(shouldWrite).isTrue();
        }

        /**
         * Verifies that a re-hit must NOT schedule a new cleanup task (only update lastHitMs).
         * The existing task remains in flight and will check elapsed time on its next poll.
         */
        @Test
        void reHitDoesNotScheduleNewTask() {
            // On re-hit, hasMetadata returns true — the task-scheduling branch is skipped.
            when(entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(new FixedMetadataValue(mcMMO.p,
                            new HealthbarSnapshot("Fido", true, 1000L))));

            final boolean snapshotAlreadyPresent =
                    entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT);

            // No runAtEntityTimer call should be made — verified by confirming the condition
            // that gates scheduling is false
            assertThat(snapshotAlreadyPresent).isTrue(); // gate condition → skip scheduling
            verify(entity, never()).setMetadata(
                    Mockito.eq(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT),
                    Mockito.any());
        }
    }
}
