package com.gmail.nossr50.skills.maces;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import java.lang.reflect.Field;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class MacesTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            MacesTest.class.getName());

    private MacesManager macesManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        macesManager = Mockito.spy(new MacesManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() throws ReflectiveOperationException {
        setResolvedSlowType(null);
        cleanUpStaticMocks();
    }

    private void setResolvedSlowType(PotionEffectType effectType)
            throws ReflectiveOperationException {
        final Field field = MacesManager.class.getDeclaredField("slowEffectType");
        field.setAccessible(true);
        field.set(null, effectType);
    }

    @Test
    void crushDamageShouldBeZeroWhenSubSkillNotUsable() {
        Mockito.when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRUSH)).thenReturn(false);

        assertEquals(0.0D, macesManager.getCrushDamage());
    }

    @Test
    void crushDamageShouldBeZeroAtRankZero() {
        Mockito.when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRUSH)).thenReturn(true);
        Mockito.when(advancedConfig.getCrushBaseDamage()).thenReturn(0.5D);
        Mockito.when(advancedConfig.getCrushRankDamageMultiplier()).thenReturn(1.0D);
        Mockito.when(RankUtils.getRank(player, SubSkillType.MACES_CRUSH)).thenReturn(0);

        assertEquals(0.0D, macesManager.getCrushDamage());
    }

    @Test
    void crushDamageShouldMatchConfiguredFormula() {
        Mockito.when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRUSH)).thenReturn(true);
        Mockito.when(advancedConfig.getCrushBaseDamage()).thenReturn(0.5D);
        Mockito.when(advancedConfig.getCrushRankDamageMultiplier()).thenReturn(1.0D);
        Mockito.when(RankUtils.getRank(player, SubSkillType.MACES_CRUSH)).thenReturn(4);

        assertEquals(4.5D, macesManager.getCrushDamage());
    }

    /**
     * Cripple slows the target briefly: players get a shorter but still punishing slow, mobs
     * a longer and stronger one.
     */
    @Test
    void crippleDurationAndStrengthShouldDependOnTargetType() {
        assertThat(MacesManager.getCrippleTickDuration(true)).isEqualTo(20);
        assertThat(MacesManager.getCrippleTickDuration(false)).isEqualTo(30);
        assertThat(MacesManager.getCrippleStrength(true)).isEqualTo(1);
        assertThat(MacesManager.getCrippleStrength(false)).isEqualTo(2);
    }

    @Nested
    class Cripple {
        private static final int CRIPPLE_RANK = 2;
        private static final double CRIPPLE_CHANCE_AT_FULL_STRENGTH = 25.0;

        private PotionEffectType slowness;
        private LivingEntity target;

        @BeforeEach
        void setUpCripple() throws ReflectiveOperationException {
            TestRegistryBootstrap.bootstrap(mockedBukkit);
            slowness = mock(PotionEffectType.class);
            setResolvedSlowType(slowness);

            target = mock(LivingEntity.class);
            when(RankUtils.getRank(player, SubSkillType.MACES_CRIPPLE))
                    .thenReturn(CRIPPLE_RANK);
            when(advancedConfig.getCrippleChanceToApplyOnHit(CRIPPLE_RANK))
                    .thenReturn(CRIPPLE_CHANCE_AT_FULL_STRENGTH);
            doReturn(true).when(mmoPlayer).useChatNotifications();
        }

        @Test
        void missingSlownessEffectTypeShouldFailFast() throws ReflectiveOperationException {
            // Given - a registry with no slowness effect type at all
            setResolvedSlowType(null);
            when(TestRegistryBootstrap.registryFor(PotionEffectType.class)
                    .get(NamespacedKey.minecraft("slowness"))).thenReturn(null);

            // When - cripple processing runs
            // Then - it fails loudly instead of silently disabling the skill
            assertThatThrownBy(() -> macesManager.processCripple(target, 1.0))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Slowness PotionEffectType");
        }

        @Test
        void alreadySlowedTargetsShouldNotBeReCrippled() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a target already slowed by something else
                when(target.getPotionEffect(slowness)).thenReturn(mock(PotionEffect.class));

                // When - cripple processing runs
                macesManager.processCripple(target, 1.0);

                // Then - cripple never rolls
                probabilityUtil.verifyNoInteractions();
            }
        }

        @Test
        void attackStrengthShouldScaleTheCrippleOdds() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // When - a half-strength hit processes
                macesManager.processCripple(target, 0.5);

                // Then - the odds are proportionally reduced
                probabilityUtil.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.MACES, mmoPlayer,
                        CRIPPLE_CHANCE_AT_FULL_STRENGTH * 0.5));
            }
        }

        @Test
        void successfulCrippleShouldSlowTheTarget() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<ParticleEffectUtils> particles =
                            mockStatic(ParticleEffectUtils.class)) {
                // Given - a winning cripple roll against a mob
                probabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.MACES, mmoPlayer, CRIPPLE_CHANCE_AT_FULL_STRENGTH))
                        .thenReturn(true);
                final PotionEffect slowEffect = mock(PotionEffect.class);
                when(slowness.createEffect(MacesManager.getCrippleTickDuration(false),
                        MacesManager.getCrippleStrength(false))).thenReturn(slowEffect);

                // When - the cripple lands at full strength
                macesManager.processCripple(target, 1.0);

                // Then - the mob is slowed with the mob-tier effect and the attacker informed
                verify(target).addPotionEffect(slowEffect);
                particles.verify(() -> ParticleEffectUtils.playCrippleEffect(target));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE,
                        "Maces.SubSkill.Cripple.Activated"));
            }
        }

        @Test
        void playerTargetsShouldGetThePlayerTierSlow() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<ParticleEffectUtils> ignored =
                            mockStatic(ParticleEffectUtils.class)) {
                // Given - a winning cripple roll against a player
                final Player defender = mock(Player.class);
                probabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.MACES, mmoPlayer, CRIPPLE_CHANCE_AT_FULL_STRENGTH))
                        .thenReturn(true);
                final PotionEffect slowEffect = mock(PotionEffect.class);
                when(slowness.createEffect(MacesManager.getCrippleTickDuration(true),
                        MacesManager.getCrippleStrength(true))).thenReturn(slowEffect);

                // When - the cripple lands
                macesManager.processCripple(defender, 1.0);

                // Then - the player gets the shorter, weaker slow
                verify(defender).addPotionEffect(slowEffect);
            }
        }

        @Test
        void missingPermissionShouldBlockCripple() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - no cripple permission
                when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRIPPLE))
                        .thenReturn(false);

                // When - cripple processing runs
                macesManager.processCripple(target, 1.0);

                // Then - cripple never rolls
                probabilityUtil.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()),
                        never());
            }
        }

        @Test
        void failedRollShouldSlowNothing() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // Given - a failing cripple roll (mock default)
                // When - cripple processing runs
                macesManager.processCripple(target, 1.0);

                // Then - the target is untouched
                verify(target, never()).addPotionEffect(any());
            }
        }
    }
}
