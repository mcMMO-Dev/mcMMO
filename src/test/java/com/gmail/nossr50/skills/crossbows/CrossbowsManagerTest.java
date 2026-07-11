package com.gmail.nossr50.skills.crossbows;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ProjectileUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

/**
 * Covers the Crossbows manager: the Trick Shot ricochet (bounce budget, head-on rejection,
 * reflected arrow spawning with its copied state) and the Powered Shot damage math.
 */
class CrossbowsManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(CrossbowsManagerTest.class.getName());

    private static final int TRICK_SHOT_MAX_BOUNCES = 3;

    private CrossbowsManager crossbowsManager;
    private Arrow originalArrow;
    private Arrow spawnedArrow;
    private ProjectileSource shooter;
    private Location arrowLocation;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        when(Permissions.trickShot(player)).thenReturn(true);
        when(RankUtils.getRank(mmoPlayer, SubSkillType.CROSSBOWS_TRICK_SHOT))
                .thenReturn(TRICK_SHOT_MAX_BOUNCES);

        shooter = mock(ProjectileSource.class);
        arrowLocation = new Location(world, 0, 64, 0);

        originalArrow = mock(Arrow.class);
        when(originalArrow.isShotFromCrossbow()).thenReturn(true);
        when(originalArrow.getShooter()).thenReturn(shooter);
        when(originalArrow.getLocation()).thenReturn(arrowLocation);
        when(originalArrow.getWorld()).thenReturn(world);
        when(originalArrow.isCritical()).thenReturn(true);
        when(originalArrow.getPierceLevel()).thenReturn(2);
        when(originalArrow.getKnockbackStrength()).thenReturn(1);
        when(originalArrow.getPickupStatus())
                .thenReturn(AbstractArrow.PickupStatus.ALLOWED);

        spawnedArrow = mock(Arrow.class);
        when(world.spawnArrow(any(Location.class), any(Vector.class), anyFloat(), anyFloat()))
                .thenReturn(spawnedArrow);

        crossbowsManager = new CrossbowsManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * A glancing shot against an upward-facing surface: flying mostly sideways with a slight
     * dip, well past the 45-degree head-on rejection window.
     */
    private void wireGlancingShot() {
        when(originalArrow.getVelocity()).thenReturn(new Vector(4, -1, 0));
    }

    /**
     * A straight-down shot onto an upward-facing surface: dead head-on, inside the rejection
     * window.
     */
    private void wireHeadOnShot() {
        when(originalArrow.getVelocity()).thenReturn(new Vector(0, -2, 0));
    }

    private void wireBounceCount(int bounceCount) {
        when(originalArrow.hasMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT))
                .thenReturn(true);
        final MetadataValue bounceValue = mock(MetadataValue.class);
        when(bounceValue.asInt()).thenReturn(bounceCount);
        when(originalArrow.getMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT))
                .thenReturn(List.of(bounceValue));
    }

    private void ricochetOffUpwardFace() {
        crossbowsManager.handleRicochet(mcMMO.p, originalArrow, new Vector(0, 1, 0));
    }

    @Nested
    class TrickShotRicochet {
        @Test
        void arrowsNotShotFromACrossbowShouldNotRicochet() {
            // Given - a bow arrow hitting a block
            when(originalArrow.isShotFromCrossbow()).thenReturn(false);
            wireGlancingShot();

            // When - the ricochet is handled
            ricochetOffUpwardFace();

            // Then - no reflected arrow spawns
            verify(world, never()).spawnArrow(any(Location.class), any(Vector.class), anyFloat(),
                    anyFloat());
        }

        @Test
        void missingPermissionShouldNotRicochet() {
            // Given - a player without the Trick Shot permission
            when(Permissions.trickShot(player)).thenReturn(false);
            wireGlancingShot();

            // When - the ricochet is handled
            ricochetOffUpwardFace();

            // Then - no reflected arrow spawns
            verify(world, never()).spawnArrow(any(Location.class), any(Vector.class), anyFloat(),
                    anyFloat());
        }

        @Test
        void headOnHitsShouldNotRicochetOnTheFirstBounce() {
            // Given - an arrow hitting the surface almost perpendicularly
            wireHeadOnShot();

            try (final MockedStatic<ProjectileUtils> ignored = mockStatic(ProjectileUtils.class);
                    final MockedStatic<CombatUtils> ignoredCombat =
                            mockStatic(CombatUtils.class)) {
                // When - the ricochet is handled
                ricochetOffUpwardFace();

                // Then - the head-on hit does not bounce
                verify(world, never()).spawnArrow(any(Location.class), any(Vector.class),
                        anyFloat(), anyFloat());
            }
        }

        @Test
        void glancingHitsShouldSpawnTheReflectedArrow() {
            // Given - a glancing shot off an upward-facing surface
            wireGlancingShot();

            try (final MockedStatic<ProjectileUtils> mockedProjectileUtils =
                    mockStatic(ProjectileUtils.class);
                    final MockedStatic<CombatUtils> mockedCombatUtils =
                            mockStatic(CombatUtils.class)) {
                // When - the ricochet is handled
                ricochetOffUpwardFace();

                // Then - the reflected arrow spawns with the vertical component flipped
                verify(world).spawnArrow(eq(arrowLocation), eq(new Vector(4, 1, 0)), eq(1.0f),
                        eq(1.0f));

                // And - the reflected arrow inherits the original arrow's combat state
                verify(spawnedArrow).setShooter(shooter);
                verify(spawnedArrow).setCritical(true);
                verify(spawnedArrow).setPierceLevel(2);
                verify(spawnedArrow).setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
                verify(spawnedArrow).setKnockbackStrength(1);
                verify(spawnedArrow).setShotFromCrossbow(true);

                // And - the original arrow is consumed and its metadata carried over
                mockedProjectileUtils.verify(() -> ProjectileUtils.copyArrowMetadata(mcMMO.p,
                        originalArrow, spawnedArrow));
                verify(originalArrow).remove();
                mockedCombatUtils.verify(
                        () -> CombatUtils.delayArrowMetaCleanup(spawnedArrow));

                // And - the bounce budget is stamped on the new arrow
                final ArgumentCaptor<MetadataValue> bounceStamp =
                        ArgumentCaptor.forClass(MetadataValue.class);
                verify(spawnedArrow).setMetadata(
                        eq(MetadataConstants.METADATA_KEY_BOUNCE_COUNT), bounceStamp.capture());
                assertThat(bounceStamp.getValue().asInt()).isEqualTo(1);
            }
        }

        @Test
        void laterBouncesShouldSkipTheHeadOnRejection() {
            // Given - an arrow on its second bounce hitting head-on
            wireHeadOnShot();
            wireBounceCount(1);

            try (final MockedStatic<ProjectileUtils> ignored = mockStatic(ProjectileUtils.class);
                    final MockedStatic<CombatUtils> ignoredCombat =
                            mockStatic(CombatUtils.class)) {
                // When - the ricochet is handled
                ricochetOffUpwardFace();

                // Then - it still bounces and the bounce count grows
                final ArgumentCaptor<MetadataValue> bounceStamp =
                        ArgumentCaptor.forClass(MetadataValue.class);
                verify(spawnedArrow).setMetadata(
                        eq(MetadataConstants.METADATA_KEY_BOUNCE_COUNT), bounceStamp.capture());
                assertThat(bounceStamp.getValue().asInt()).isEqualTo(2);
            }
        }

        @Test
        void exhaustedBounceBudgetShouldStopTheRicochet() {
            // Given - an arrow that already bounced as many times as the Trick Shot rank allows
            wireGlancingShot();
            wireBounceCount(TRICK_SHOT_MAX_BOUNCES);

            try (final MockedStatic<ProjectileUtils> ignored = mockStatic(ProjectileUtils.class);
                    final MockedStatic<CombatUtils> ignoredCombat =
                            mockStatic(CombatUtils.class)) {
                // When - the ricochet is handled
                ricochetOffUpwardFace();

                // Then - the arrow stops bouncing
                verify(world, never()).spawnArrow(any(Location.class), any(Vector.class),
                        anyFloat(), anyFloat());
            }
        }

        @Test
        void tippedArrowsShouldKeepTheirPotionState() {
            // Given - a glancing tipped arrow
            wireGlancingShot();
            when(originalArrow.getBasePotionType()).thenReturn(PotionType.POISON);
            final PotionEffect customEffect = mock(PotionEffect.class);
            when(originalArrow.hasCustomEffects()).thenReturn(true);
            when(originalArrow.getCustomEffects()).thenReturn(List.of(customEffect));

            try (final MockedStatic<ProjectileUtils> ignored = mockStatic(ProjectileUtils.class);
                    final MockedStatic<CombatUtils> ignoredCombat =
                            mockStatic(CombatUtils.class)) {
                // When - the ricochet is handled
                ricochetOffUpwardFace();

                // Then - the reflected arrow becomes a tipped arrow with the same potion state
                final ArgumentCaptor<ItemStack> pickupItem =
                        ArgumentCaptor.forClass(ItemStack.class);
                verify(spawnedArrow).setItem(pickupItem.capture());
                assertThat(pickupItem.getValue().getType()).isEqualTo(Material.TIPPED_ARROW);
                verify(spawnedArrow).setBasePotionType(PotionType.POISON);
                verify(spawnedArrow).addCustomEffect(customEffect, true);
            }
        }

        @Test
        void infiniteArrowsShouldNotBePickableAfterTheBounce() {
            // Given - a glancing shot from an Infinity crossbow arrow
            wireGlancingShot();
            when(spawnedArrow.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW))
                    .thenReturn(true);

            try (final MockedStatic<ProjectileUtils> ignored = mockStatic(ProjectileUtils.class);
                    final MockedStatic<CombatUtils> ignoredCombat =
                            mockStatic(CombatUtils.class)) {
                // When - the ricochet is handled
                ricochetOffUpwardFace();

                // Then - the reflected arrow cannot be farmed from the ground
                verify(spawnedArrow)
                        .setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }
    }

    @Test
    void trickShotBounceBudgetShouldMirrorTheRank() {
        // Given - Trick Shot at rank 3
        // When - the bounce budget is read
        // Then - it is the rank
        assertThat(crossbowsManager.getTrickShotMaxBounceCount())
                .isEqualTo(TRICK_SHOT_MAX_BOUNCES);
    }

    @Nested
    class PoweredShot {
        @BeforeEach
        void wireDamageConfig() {
            when(RankUtils.getRank(player, SubSkillType.CROSSBOWS_POWERED_SHOT)).thenReturn(5);
            when(advancedConfig.getPoweredShotRankDamageMultiplier()).thenReturn(10.0);
            when(advancedConfig.getPoweredShotDamageMax()).thenReturn(9.0);
        }

        @Test
        void damageBonusPercentShouldScaleWithRank() {
            // Given - Powered Shot rank 5 with a 10% per-rank multiplier
            // When - the damage bonus percent is computed
            // Then - five ranks of 10% make +50%
            assertThat(crossbowsManager.getDamageBonusPercent(player))
                    .isCloseTo(0.5, within(1e-9));
        }

        @Test
        void bonusDamageShouldCapAtTheConfiguredMaximum() {
            // Given - a +200% bonus that would blow past the cap
            when(RankUtils.getRank(player, SubSkillType.CROSSBOWS_POWERED_SHOT)).thenReturn(20);

            // When - the bonus damage is computed for a 10 damage bolt
            // Then - the result caps at the raw damage plus the configured max bonus
            assertThat(crossbowsManager.getPoweredShotBonusDamage(player, 10.0))
                    .isCloseTo(19.0, within(1e-9));
        }

        @Test
        void successfulActivationShouldBoostDamage() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - Powered Shot activates
                mockedProbability.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.CROSSBOWS_POWERED_SHOT, mmoPlayer)).thenReturn(true);

                // When - the bolt damage is processed
                // Then - the damage grows by the rank bonus
                assertThat(crossbowsManager.poweredShot(10.0)).isCloseTo(15.0, within(1e-9));
            }
        }

        @Test
        void failedActivationShouldKeepTheOriginalDamage() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - Powered Shot does not activate
                mockedProbability.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.CROSSBOWS_POWERED_SHOT, mmoPlayer)).thenReturn(false);

                // When - the bolt damage is processed
                // Then - the damage is untouched
                assertThat(crossbowsManager.poweredShot(10.0)).isEqualTo(10.0);
            }
        }
    }
}
