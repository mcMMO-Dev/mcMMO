package com.gmail.nossr50.skills.archery;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

/**
 * Covers the static Archery helpers: the Skill Shot damage math with its max bonus cap, and
 * the arrow tracker that pays back arrows stuck in an entity when Arrow Retrieval fires.
 */
class ArcheryTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(ArcheryTest.class.getName());

    private double originalSkillShotMaxBonus;
    private double originalDazeBonusDamage;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        // Must be stubbed before anything touches the Archery class: its static initializer
        // freezes the distance multiplier from the config at class load
        when(ExperienceConfig.getInstance().getArcheryDistanceMultiplier()).thenReturn(0.025);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(mock(PlatformScheduler.class));

        originalSkillShotMaxBonus = Archery.skillShotMaxBonusDamage;
        originalDazeBonusDamage = Archery.dazeBonusDamage;
        Archery.skillShotMaxBonusDamage = 9.0;
        Archery.dazeBonusDamage = 4.0;
    }

    @AfterEach
    void tearDown() {
        Archery.skillShotMaxBonusDamage = originalSkillShotMaxBonus;
        Archery.dazeBonusDamage = originalDazeBonusDamage;
        cleanUpStaticMocks();
    }

    private LivingEntity mockTrackedTarget() {
        final LivingEntity target = mock(LivingEntity.class);
        when(target.getUniqueId()).thenReturn(UUID.randomUUID());
        when(target.getLocation()).thenReturn(new Location(world, 0, 64, 0));
        when(target.isValid()).thenReturn(true);
        return target;
    }

    @Nested
    class SkillShotDamage {
        @Test
        void damageBonusPercentShouldScaleWithRank() {
            // Given - Skill Shot rank 5 with a 10% per-rank damage multiplier
            when(RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)).thenReturn(5);
            when(advancedConfig.getSkillShotRankDamageMultiplier()).thenReturn(10.0);

            // When - the damage bonus percent is computed
            // Then - five ranks of 10% make +50%
            assertThat(Archery.getDamageBonusPercent(player)).isCloseTo(0.5, within(1e-9));
        }

        @Test
        void bonusDamageShouldAddThePercentOnTop() {
            // Given - a +50% damage bonus and a 10 damage arrow
            when(RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)).thenReturn(5);
            when(advancedConfig.getSkillShotRankDamageMultiplier()).thenReturn(10.0);

            // When - the bonus damage is computed
            // Then - the arrow deals 15 damage, still under the +9 cap
            assertThat(Archery.getSkillShotBonusDamage(player, 10.0))
                    .isCloseTo(15.0, within(1e-9));
        }

        @Test
        void bonusDamageShouldCapAtTheConfiguredMaximum() {
            // Given - a +200% damage bonus that would blow far past the cap
            when(RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)).thenReturn(20);
            when(advancedConfig.getSkillShotRankDamageMultiplier()).thenReturn(10.0);

            // When - the bonus damage is computed for a 10 damage arrow
            // Then - the result caps at the raw damage plus the configured max bonus
            assertThat(Archery.getSkillShotBonusDamage(player, 10.0))
                    .isCloseTo(19.0, within(1e-9));
        }
    }

    @Nested
    class ArrowTracking {
        @Test
        void retrievalShouldSpawnOneArrowPerTrackedHit() {
            // Given - an entity that has been hit by two tracked arrows
            final LivingEntity target = mockTrackedTarget();
            final Location targetLocation = target.getLocation();
            Archery.incrementTrackerValue(target);
            Archery.incrementTrackerValue(target);

            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                // When - the entity dies and arrow retrieval fires
                Archery.arrowRetrievalCheck(target);

                // Then - both arrows drop at the entity's location
                final ArgumentCaptor<ItemStack> droppedArrows =
                        ArgumentCaptor.forClass(ItemStack.class);
                mockedItemUtils.verify(() -> ItemUtils.spawnItems(isNull(),
                        eq(targetLocation), droppedArrows.capture(), eq(2),
                        eq(ItemSpawnReason.ARROW_RETRIEVAL_ACTIVATED)));
                assertThat(droppedArrows.getValue().getType()).isEqualTo(Material.ARROW);
            }
        }

        @Test
        void retrievalShouldForgetTheEntityAfterPayingOut() {
            // Given - a tracked entity whose arrows were already paid out
            final LivingEntity target = mockTrackedTarget();
            Archery.incrementTrackerValue(target);

            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                Archery.arrowRetrievalCheck(target);

                // When - retrieval fires again for the same entity
                Archery.arrowRetrievalCheck(target);

                // Then - the second check pays nothing
                mockedItemUtils.verify(() -> ItemUtils.spawnItems(isNull(), any(Location.class),
                        any(ItemStack.class), anyInt(), any(ItemSpawnReason.class)));
            }
        }

        @Test
        void untrackedEntityShouldPayNothing() {
            // Given - an entity no tracked arrow ever hit
            final LivingEntity target = mockTrackedTarget();

            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                // When - arrow retrieval fires
                Archery.arrowRetrievalCheck(target);

                // Then - nothing drops
                mockedItemUtils.verifyNoInteractions();
            }
        }

        @Test
        void cleanupTaskShouldDropInvalidEntitiesAndCancelItself() {
            // Given - a tracked entity that has since become invalid (despawned or dead)
            final LivingEntity target = mockTrackedTarget();
            Archery.incrementTrackerValue(target);
            when(target.isValid()).thenReturn(false);
            final TrackedEntity cleanupTracker = new TrackedEntity(target);
            final WrappedTask wrappedTask = mock(WrappedTask.class);

            // When - the periodic cleanup runs
            cleanupTracker.accept(wrappedTask);

            // Then - the timer cancels itself and the entity is no longer tracked
            verify(wrappedTask).cancel();
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                Archery.arrowRetrievalCheck(target);
                mockedItemUtils.verifyNoInteractions();
            }
        }

        @Test
        void cleanupTaskShouldKeepValidEntitiesTracked() {
            // Given - a tracked entity that is still alive
            final LivingEntity target = mockTrackedTarget();
            final Location targetLocation = target.getLocation();
            Archery.incrementTrackerValue(target);
            final TrackedEntity cleanupTracker = new TrackedEntity(target);
            final WrappedTask wrappedTask = mock(WrappedTask.class);

            // When - the periodic cleanup runs
            cleanupTracker.accept(wrappedTask);

            // Then - the timer keeps running and the arrows still pay out
            verify(wrappedTask, never()).cancel();
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                Archery.arrowRetrievalCheck(target);
                mockedItemUtils.verify(() -> ItemUtils.spawnItems(isNull(),
                        eq(targetLocation), any(ItemStack.class), eq(1),
                        eq(ItemSpawnReason.ARROW_RETRIEVAL_ACTIVATED)));
            }
        }
    }
}
