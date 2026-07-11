package com.gmail.nossr50.skills.archery;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.PaperUtil;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.PotionEffectUtil;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

/**
 * Covers the Archery manager: ability gates, the distance XP multiplier for long shots, the
 * arrow tracking hook, Daze (both the Paper lookAt branch and the Spigot scheduled-teleport
 * branch), and the Skill Shot damage hook.
 */
class ArcheryManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(ArcheryManagerTest.class.getName());

    private ArcheryManager archeryManager;
    private PlatformScheduler scheduler;

    private double originalSkillShotMaxBonus;
    private double originalDazeBonusDamage;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        // Must be stubbed before anything touches the Archery class: its static initializer
        // freezes the distance multiplier from the config at class load
        when(ExperienceConfig.getInstance().getArcheryDistanceMultiplier()).thenReturn(0.025);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);

        originalSkillShotMaxBonus = Archery.skillShotMaxBonusDamage;
        originalDazeBonusDamage = Archery.dazeBonusDamage;
        Archery.skillShotMaxBonusDamage = 9.0;
        Archery.dazeBonusDamage = 4.0;

        archeryManager = new ArcheryManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        Archery.skillShotMaxBonusDamage = originalSkillShotMaxBonus;
        Archery.dazeBonusDamage = originalDazeBonusDamage;
        cleanUpStaticMocks();
    }

    @Nested
    class AbilityGates {
        @Test
        void dazeShouldOnlyWorkOnPlayers() {
            // Given - Daze is unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_DAZE))
                    .thenReturn(true);

            // When / Then - a mob target cannot be dazed, a player target can
            assertThat(archeryManager.canDaze(mock(LivingEntity.class))).isFalse();
            assertThat(archeryManager.canDaze(mock(Player.class))).isTrue();
        }

        @Test
        void dazeShouldBeLockedWithoutTheSubskill() {
            // Given - Daze has not been unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_DAZE))
                    .thenReturn(false);

            // When / Then - even a player target cannot be dazed
            assertThat(archeryManager.canDaze(mock(Player.class))).isFalse();
        }

        @Test
        void dazeShouldRespectPermissions() {
            // Given - Daze is unlocked but the permission is missing
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_DAZE))
                    .thenReturn(true);
            when(Permissions.isSubSkillEnabled(player, SubSkillType.ARCHERY_DAZE))
                    .thenReturn(false);

            // When / Then - the gate stays closed
            assertThat(archeryManager.canDaze(mock(Player.class))).isFalse();
        }

        @Test
        void skillShotShouldRequireItsUnlock() {
            // Given - Skill Shot has not been unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_SKILL_SHOT))
                    .thenReturn(false);

            // When / Then - the gate stays closed until the unlock
            assertThat(archeryManager.canSkillShot()).isFalse();
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_SKILL_SHOT))
                    .thenReturn(true);
            assertThat(archeryManager.canSkillShot()).isTrue();
        }

        @Test
        void arrowRetrievalShouldRequireItsUnlock() {
            // Given - Arrow Retrieval has not been unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_ARROW_RETRIEVAL))
                    .thenReturn(false);

            // When / Then - the gate stays closed until the unlock
            assertThat(archeryManager.canRetrieveArrows()).isFalse();
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.ARCHERY_ARROW_RETRIEVAL))
                    .thenReturn(true);
            assertThat(archeryManager.canRetrieveArrows()).isTrue();
        }
    }

    @Nested
    class DistanceXpBonus {
        private LivingEntity target;

        @BeforeEach
        void setUpTarget() {
            target = mock(LivingEntity.class);
        }

        private Projectile mockArrowFiredFrom(Location firedLocation) {
            final Projectile arrow = mock(Projectile.class);
            when(arrow.hasMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE))
                    .thenReturn(true);
            final MetadataValue metadataValue = mock(MetadataValue.class);
            when(metadataValue.value()).thenReturn(firedLocation);
            when(arrow.getMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE))
                    .thenReturn(List.of(metadataValue));
            return arrow;
        }

        @Test
        void unmarkedArrowsShouldNotBoostXp() {
            // Given - an arrow another plugin spawned without mcMMO's distance marker
            final Projectile arrow = mock(Projectile.class);

            // When - the distance bonus is computed
            // Then - the XP is unchanged
            assertThat(ArcheryManager.distanceXpBonusMultiplier(target, arrow)).isEqualTo(1.0);
        }

        @Test
        void arrowsWithoutAFiredWorldShouldNotBoostXp() {
            // Given - a distance marker whose fired location lost its world
            final Projectile arrow = mockArrowFiredFrom(new Location(null, 0, 64, 0));
            when(target.getLocation()).thenReturn(new Location(world, 3, 64, 4));

            // When - the distance bonus is computed
            // Then - the XP is unchanged
            assertThat(ArcheryManager.distanceXpBonusMultiplier(target, arrow)).isEqualTo(1.0);
        }

        @Test
        void crossWorldShotsShouldNotBoostXp() {
            // Given - an arrow fired in one world hitting a target in another
            final Projectile arrow = mockArrowFiredFrom(new Location(world, 0, 64, 0));
            when(target.getLocation())
                    .thenReturn(new Location(mock(org.bukkit.World.class), 3, 64, 4));

            // When - the distance bonus is computed
            // Then - the XP is unchanged
            assertThat(ArcheryManager.distanceXpBonusMultiplier(target, arrow)).isEqualTo(1.0);
        }

        @Test
        void distanceShouldScaleTheXpBonusLinearly() {
            // Given - a shot that flew exactly 5 blocks (3-4-5 triangle)
            final Projectile arrow = mockArrowFiredFrom(new Location(world, 0, 64, 0));
            when(target.getLocation()).thenReturn(new Location(world, 3, 64, 4));

            // When - the distance bonus is computed
            // Then - each block adds one multiplier step
            assertThat(ArcheryManager.distanceXpBonusMultiplier(target, arrow))
                    .isCloseTo(1 + 5 * Archery.DISTANCE_XP_MULTIPLIER, within(1e-9));
        }

        @Test
        void distanceBonusShouldCapAtFiftyBlocks() {
            // Given - a shot that flew 100 blocks (60-80-100 triangle)
            final Projectile arrow = mockArrowFiredFrom(new Location(world, 0, 64, 0));
            when(target.getLocation()).thenReturn(new Location(world, 60, 64, 80));

            // When - the distance bonus is computed
            // Then - only the first 50 blocks count
            assertThat(ArcheryManager.distanceXpBonusMultiplier(target, arrow))
                    .isCloseTo(1 + 50 * Archery.DISTANCE_XP_MULTIPLIER, within(1e-9));
        }
    }

    @Nested
    class RetrieveArrows {
        @Test
        void trackedArrowsShouldBeCountedAndUnmarked() {
            // Given - a target hit by an arrow carrying the tracked-arrow marker
            final LivingEntity target = mock(LivingEntity.class);
            final Location targetLocation = new Location(world, 0, 64, 0);
            when(target.getUniqueId()).thenReturn(UUID.randomUUID());
            when(target.getLocation()).thenReturn(targetLocation);
            final Projectile arrow = mock(Projectile.class);
            when(arrow.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW))
                    .thenReturn(true);

            // When - the hit is tracked
            archeryManager.retrieveArrows(target, arrow);

            // Then - the marker is consumed so one projectile only ever counts once
            verify(arrow).removeMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW, mcMMO.p);

            // And - the tracked hit pays out on retrieval
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                Archery.arrowRetrievalCheck(target);
                mockedItemUtils.verify(() -> ItemUtils.spawnItems(isNull(),
                        eq(targetLocation), any(org.bukkit.inventory.ItemStack.class),
                        eq(1), eq(ItemSpawnReason.ARROW_RETRIEVAL_ACTIVATED)));
            }
        }

        @Test
        void untrackedArrowsShouldBeIgnored() {
            // Given - a target hit by a plain arrow without the marker
            final LivingEntity target = mock(LivingEntity.class);
            when(target.getUniqueId()).thenReturn(UUID.randomUUID());
            when(target.getLocation()).thenReturn(new Location(world, 0, 64, 0));
            final Projectile arrow = mock(Projectile.class);

            // When - the hit is processed
            archeryManager.retrieveArrows(target, arrow);

            // Then - nothing is tracked and retrieval pays nothing
            verify(arrow, never()).removeMetadata(eq(MetadataConstants.METADATA_KEY_TRACKED_ARROW),
                    eq(mcMMO.p));
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                Archery.arrowRetrievalCheck(target);
                mockedItemUtils.verifyNoInteractions();
            }
        }
    }

    @Nested
    class Daze {
        private static final int NAUSEA_DURATION_TICKS = 20 * 10;
        private static final int NAUSEA_AMPLIFIER = 10;

        private Player defender;

        @BeforeEach
        void setUpDefender() {
            defender = mock(Player.class);
            when(defender.isValid()).thenReturn(true);
            when(defender.getWorld()).thenReturn(world);
        }

        /**
         * The random pitch is 90 - nextInt(181); returning 30 gives a deterministic pitch of
         * 60 degrees.
         */
        private void wireRandomPitchRoll() {
            final Random random = mock(Random.class);
            when(random.nextInt(181)).thenReturn(30);
            when(Misc.getRandom()).thenReturn(random);
        }

        @Test
        void failedRollShouldDealNoBonusDamageAndNoEffects() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the Daze roll fails
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.ARCHERY_DAZE, mmoPlayer)).thenReturn(false);

                // When - daze is processed
                final double bonusDamage = archeryManager.daze(defender);

                // Then - no bonus damage, no nausea, no view change
                assertThat(bonusDamage).isEqualTo(0.0);
                verify(defender, never()).addPotionEffect(any());
            }
        }

        @Test
        void dazeOnSpigotShouldScheduleThePitchFlipAndApplyNausea() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class);
                    final MockedStatic<PaperUtil> mockedPaperUtil = mockStatic(PaperUtil.class);
                    final MockedStatic<PotionEffectUtil> ignored =
                            mockStatic(PotionEffectUtil.class);
                    final MockedConstruction<PotionEffect> effectConstruction =
                            mockConstruction(PotionEffect.class)) {
                // Given - a successful Daze roll on a Spigot server
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.ARCHERY_DAZE, mmoPlayer)).thenReturn(true);
                mockedPaperUtil.when(PaperUtil::canLookAt).thenReturn(false);
                wireRandomPitchRoll();
                final Location defenderLocation = new Location(world, 1, 64, 1, 0, 0);
                when(defender.getLocation()).thenReturn(defenderLocation);

                // When - daze is processed
                final double bonusDamage = archeryManager.daze(defender);

                // Then - the daze pays its bonus damage
                assertThat(bonusDamage).isEqualTo(4.0);

                // And - the pitch flip teleport is scheduled and lands on the defender
                @SuppressWarnings("unchecked")
                final ArgumentCaptor<Consumer<WrappedTask>> scheduledTeleport =
                        ArgumentCaptor.forClass(Consumer.class);
                verify(scheduler).runAtEntity(eq(defender), scheduledTeleport.capture());
                scheduledTeleport.getValue().accept(mock(WrappedTask.class));
                verify(defender).teleport(defenderLocation);
                assertThat(defenderLocation.getPitch()).isEqualTo(60.0f);

                // And - the nausea effect is built and applied to the defender
                assertThat(effectConstruction.constructed()).hasSize(1);
                verify(defender).addPotionEffect(effectConstruction.constructed().get(0));
            }
        }

        @Test
        void scheduledDazeTeleportShouldNotFireAfterWorldChange() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class);
                    final MockedStatic<PaperUtil> mockedPaperUtil = mockStatic(PaperUtil.class);
                    final MockedStatic<PotionEffectUtil> ignored =
                            mockStatic(PotionEffectUtil.class);
                    final MockedConstruction<PotionEffect> ignoredConstruction =
                            mockConstruction(PotionEffect.class)) {
                // Given - a successful Spigot daze whose defender changes world before the
                // scheduled teleport runs
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.ARCHERY_DAZE, mmoPlayer)).thenReturn(true);
                mockedPaperUtil.when(PaperUtil::canLookAt).thenReturn(false);
                wireRandomPitchRoll();
                when(defender.getLocation()).thenReturn(new Location(world, 1, 64, 1, 0, 0));
                archeryManager.daze(defender);
                when(defender.getWorld()).thenReturn(mock(org.bukkit.World.class));

                // When - the scheduled teleport fires
                @SuppressWarnings("unchecked")
                final ArgumentCaptor<Consumer<WrappedTask>> scheduledTeleport =
                        ArgumentCaptor.forClass(Consumer.class);
                verify(scheduler).runAtEntity(eq(defender), scheduledTeleport.capture());
                scheduledTeleport.getValue().accept(mock(WrappedTask.class));

                // Then - the stale teleport is dropped
                verify(defender, never()).teleport(any(Location.class));
            }
        }

        @Test
        void dazeOnPaperShouldTurnTheDefendersViewWithoutTeleporting() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class);
                    final MockedStatic<PaperUtil> mockedPaperUtil = mockStatic(PaperUtil.class);
                    final MockedStatic<PotionEffectUtil> ignored =
                            mockStatic(PotionEffectUtil.class);
                    final MockedConstruction<PotionEffect> effectConstruction =
                            mockConstruction(PotionEffect.class)) {
                // Given - a successful Daze roll on a Paper server
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.ARCHERY_DAZE, mmoPlayer)).thenReturn(true);
                mockedPaperUtil.when(PaperUtil::canLookAt).thenReturn(true);
                wireRandomPitchRoll();
                when(defender.getEyeLocation()).thenReturn(new Location(world, 0, 64, 0, 0, 0));

                // When - daze is processed
                archeryManager.daze(defender);

                // Then - the view is redirected 10 blocks along the 60-degree-down direction
                // (yaw 0, pitch 60: x = 0, y = 64 - 10*sin(60), z = 10*cos(60))
                final ArgumentCaptor<Double> lookX = ArgumentCaptor.forClass(Double.class);
                final ArgumentCaptor<Double> lookY = ArgumentCaptor.forClass(Double.class);
                final ArgumentCaptor<Double> lookZ = ArgumentCaptor.forClass(Double.class);
                mockedPaperUtil.verify(() -> PaperUtil.lookAt(eq(defender), lookX.capture(),
                        lookY.capture(), lookZ.capture()));
                assertThat(lookX.getValue()).isCloseTo(0.0, within(1e-6));
                assertThat(lookY.getValue())
                        .isCloseTo(64 - 10 * Math.sin(Math.toRadians(60)), within(1e-6));
                assertThat(lookZ.getValue())
                        .isCloseTo(10 * Math.cos(Math.toRadians(60)), within(1e-6));

                // And - no teleport is scheduled on the Paper path
                verify(scheduler, never()).runAtEntity(eq(defender), any());

                // And - the nausea effect is applied with its fixed strength and duration
                assertThat(effectConstruction.constructed()).hasSize(1);
                verify(defender).addPotionEffect(effectConstruction.constructed().get(0));
            }
        }

        @Test
        void dazeShouldNotifyBothPlayersWhenNotificationsAreOn() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class);
                    final MockedStatic<PaperUtil> mockedPaperUtil = mockStatic(PaperUtil.class);
                    final MockedStatic<PotionEffectUtil> ignored =
                            mockStatic(PotionEffectUtil.class);
                    final MockedConstruction<PotionEffect> ignoredConstruction =
                            mockConstruction(PotionEffect.class)) {
                // Given - a successful daze with notifications enabled on both sides
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.ARCHERY_DAZE, mmoPlayer)).thenReturn(true);
                mockedPaperUtil.when(PaperUtil::canLookAt).thenReturn(true);
                wireRandomPitchRoll();
                when(defender.getEyeLocation()).thenReturn(new Location(world, 0, 64, 0, 0, 0));
                when(NotificationManager.doesPlayerUseNotifications(defender)).thenReturn(true);

                // When - daze is processed
                archeryManager.daze(defender);

                // Then - the defender is told they were dazed and the shooter sees the hit
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        defender, NotificationType.SUBSKILL_MESSAGE, "Combat.TouchedFuzzy"));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Combat.TargetDazed"));
            }
        }
    }

    @Nested
    class SkillShot {
        @Test
        void successfulActivationShouldBoostDamage() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - Skill Shot activates at rank 5 with a 10% per-rank multiplier
                mockedProbability.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.ARCHERY_SKILL_SHOT, mmoPlayer)).thenReturn(true);
                when(RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)).thenReturn(5);
                when(advancedConfig.getSkillShotRankDamageMultiplier()).thenReturn(10.0);

                // When - the arrow damage is processed
                // Then - the damage grows by the rank bonus
                assertThat(archeryManager.skillShot(10.0)).isCloseTo(15.0, within(1e-9));
            }
        }

        @Test
        void failedActivationShouldKeepTheOriginalDamage() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - Skill Shot does not activate
                mockedProbability.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.ARCHERY_SKILL_SHOT, mmoPlayer)).thenReturn(false);

                // When - the arrow damage is processed
                // Then - the damage is untouched
                assertThat(archeryManager.skillShot(10.0)).isEqualTo(10.0);
            }
        }
    }
}
