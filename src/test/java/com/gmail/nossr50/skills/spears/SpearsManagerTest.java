package com.gmail.nossr50.skills.spears;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.TestRegistryBootstrap;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import java.lang.reflect.Field;
import java.util.logging.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

/**
 * Covers the Spears manager: the Momentum swiftness buff (lazy effect type resolution,
 * attack-strength-scaled odds, the do-not-downgrade guard against stronger existing buffs)
 * and the Spear Mastery bonus damage math.
 *
 * <p>Registry-backed effect types come from {@link TestRegistryBootstrap}.</p>
 */
class SpearsManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(SpearsManagerTest.class.getName());

    private static final int MOMENTUM_RANK = 3;
    private static final double MOMENTUM_CHANCE_AT_FULL_STRENGTH = 20.0;

    private SpearsManager spearsManager;
    private PotionEffectType swiftness;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        mockBaseEnvironment(logger);
        TestRegistryBootstrap.bootstrap(mockedBukkit);

        swiftness = mock(PotionEffectType.class);
        setResolvedSwiftnessType(swiftness);

        when(RankUtils.getRank(player, SubSkillType.SPEARS_MOMENTUM)).thenReturn(MOMENTUM_RANK);
        when(advancedConfig.getMomentumChanceToApplyOnHit(MOMENTUM_RANK))
                .thenReturn(MOMENTUM_CHANCE_AT_FULL_STRENGTH);
        doReturn(true).when(mmoPlayer).useChatNotifications();

        spearsManager = new SpearsManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() throws ReflectiveOperationException {
        setResolvedSwiftnessType(null);
        cleanUpStaticMocks();
    }

    private void setResolvedSwiftnessType(PotionEffectType effectType)
            throws ReflectiveOperationException {
        final Field field = SpearsManager.class.getDeclaredField("swiftnessEffectType");
        field.setAccessible(true);
        field.set(null, effectType);
    }

    private Registry<PotionEffectType> effectRegistry() {
        return TestRegistryBootstrap.registryFor(PotionEffectType.class);
    }

    @Nested
    class Momentum {
        @Test
        void missingSpeedEffectTypeShouldFailFast() throws ReflectiveOperationException {
            // Given - a server whose registry has no speed effect type at all
            setResolvedSwiftnessType(null);
            when(effectRegistry().get(NamespacedKey.minecraft("speed"))).thenReturn(null);

            // When - momentum processing runs
            // Then - it fails loudly instead of silently disabling the skill
            assertThatThrownBy(() -> spearsManager.potentiallyApplyMomentum(1.0))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Speed PotionEffectType");
        }

        @Test
        void speedEffectTypeShouldResolveLazilyFromTheRegistry() {
            try (final MockedStatic<ProbabilityUtil> ignored =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the effect type has not been resolved yet
                try {
                    setResolvedSwiftnessType(null);
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
                when(effectRegistry().get(NamespacedKey.minecraft("speed")))
                        .thenReturn(swiftness);

                // When - momentum processing runs (with a failing roll)
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - the buff check consulted the freshly resolved type
                verify(player).getPotionEffect(swiftness);
            }
        }

        @Test
        void successfulMomentumShouldApplyTheSwiftnessBuff() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a full-strength hit that wins the Momentum roll
                mockedProbability.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                                PrimarySkillType.SPEARS, mmoPlayer,
                                MOMENTUM_CHANCE_AT_FULL_STRENGTH))
                        .thenReturn(true);
                final PotionEffect momentumBuff = mock(PotionEffect.class);
                when(swiftness.createEffect(
                        SpearsManager.getMomentumTickDuration(MOMENTUM_RANK),
                        SpearsManager.getMomentumStrength())).thenReturn(momentumBuff);

                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - the swiftness buff lands on the attacker with the rank-scaled duration
                verify(player).addPotionEffect(momentumBuff);

                // And - the player is told about it
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE,
                        "Spears.SubSkill.Momentum.Activated"));
            }
        }

        @Test
        void failedRollShouldApplyNothing() {
            try (final MockedStatic<ProbabilityUtil> ignored =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a Momentum roll that fails (unstubbed RNG rolls false)
                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - no buff and no notification
                verify(player, never()).addPotionEffect(any());
                notificationManager.verifyNoInteractions();
            }
        }

        @ParameterizedTest
        @CsvSource({
                "1.0, 20.0",
                "0.5, 10.0",
                "1.5, 20.0",
        })
        void attackStrengthShouldScaleTheOddsAndClampAtFull(double attackStrength,
                double expectedOdds) {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a hit at the given committed attack strength
                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(attackStrength);

                // Then - weak hits proportionally lower the odds, never exceeding the full
                // configured chance
                mockedProbability.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.SPEARS, mmoPlayer, expectedOdds));
            }
        }

        @Test
        void strongerExistingSwiftnessShouldNotBeDowngraded() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the player already has a stronger swiftness buff (amplifier above
                // Momentum's fixed strength)
                final PotionEffect strongerBuff = mock(PotionEffect.class);
                when(strongerBuff.getAmplifier()).thenReturn(3);
                when(player.getPotionEffect(swiftness)).thenReturn(strongerBuff);

                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - momentum never even rolls
                mockedProbability.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()),
                        never());
                verify(player, never()).addPotionEffect(any());
            }
        }

        @Test
        void longerExistingSwiftnessShouldNotBeCutShort() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - an equal-strength buff that outlasts what Momentum would grant
                final PotionEffect longerBuff = mock(PotionEffect.class);
                when(longerBuff.getAmplifier())
                        .thenReturn(SpearsManager.getMomentumStrength());
                when(longerBuff.getDuration()).thenReturn(
                        SpearsManager.getMomentumTickDuration(MOMENTUM_RANK) + 1);
                when(player.getPotionEffect(swiftness)).thenReturn(longerBuff);

                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - momentum never rolls
                mockedProbability.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()),
                        never());
            }
        }

        @Test
        void weakerExistingSwiftnessShouldBeReplaceable() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a weaker, shorter swiftness buff than Momentum grants
                final PotionEffect weakerBuff = mock(PotionEffect.class);
                when(weakerBuff.getAmplifier()).thenReturn(1);
                when(weakerBuff.getDuration()).thenReturn(50);
                when(player.getPotionEffect(swiftness)).thenReturn(weakerBuff);

                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - momentum still gets its roll
                mockedProbability.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.SPEARS, mmoPlayer, MOMENTUM_CHANCE_AT_FULL_STRENGTH));
            }
        }

        @Test
        void missingPermissionShouldBlockMomentum() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a player without the Momentum permission
                when(Permissions.canUseSubSkill(player, SubSkillType.SPEARS_MOMENTUM))
                        .thenReturn(false);

                // When - momentum processing runs
                spearsManager.potentiallyApplyMomentum(1.0);

                // Then - momentum never rolls
                mockedProbability.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()),
                        never());
            }
        }
    }

    /**
     * Momentum grants two seconds of swiftness per rank (40 ticks), at a fixed amplifier.
     */
    @ParameterizedTest
    @CsvSource({
            "1, 40",
            "3, 120",
            "5, 200",
    })
    void momentumDurationShouldScaleWithRank(int rank, int expectedTicks) {
        assertThat(SpearsManager.getMomentumTickDuration(rank)).isEqualTo(expectedTicks);
    }

    @Test
    void spearMasteryBonusShouldScaleWithRank() {
        // Given - Spear Mastery rank 4 with a 1.5 damage-per-rank multiplier
        when(advancedConfig.getSpearMasteryRankDamageMultiplier()).thenReturn(1.5);
        when(RankUtils.getRank(player, SubSkillType.SPEARS_SPEAR_MASTERY)).thenReturn(4);

        // When - the bonus damage is computed
        // Then - it is the multiplier times the rank
        assertThat(spearsManager.getSpearMasteryBonusDamage()).isCloseTo(6.0, within(1e-9));
    }
}
