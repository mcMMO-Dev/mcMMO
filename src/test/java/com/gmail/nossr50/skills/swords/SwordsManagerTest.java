package com.gmail.nossr50.skills.swords;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.RuptureTaskMeta;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.RuptureTask;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Swords combat effects that depend on the committed attack strength of a hit.
 *
 * <p>Regression background: Paper 26.1.2+ resets the attack cooldown ticker before
 * {@code EntityDamageByEntityEvent} fires, so {@code Player#getAttackCooldown()} reads ~0.1
 * mid-event. Rupture must roll its odds with the attack strength scale passed in from
 * CombatUtils, otherwise it silently procs at ~10% of the intended rate.
 */
class SwordsManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            SwordsManagerTest.class.getName());

    private SwordsManager swordsManager;
    private LivingEntity target;
    private PlatformScheduler scheduler;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);

        swordsManager = new SwordsManager(mmoPlayer);
        target = Mockito.mock(LivingEntity.class);

        // Simulate Paper 26.1.2+ during a damage event: the ticker was already reset, so the
        // live cooldown misreports the charge of the committed hit.
        when(advancedConfig.useAttackCooldown()).thenReturn(true);
        when(player.getAttackCooldown()).thenReturn(0.1f);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void processRuptureShouldRollOddsWithCommittedAttackStrength() {
        // Given - Rupture unlocked at rank 3 with a 33% base chance to apply on hit
        Mockito.when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_RUPTURE))
                .thenReturn(true);
        Mockito.when(RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE)).thenReturn(3);
        when(advancedConfig.getRuptureChanceToApplyOnHit(3)).thenReturn(33.0);
        when(target.hasMetadata(MetadataConstants.METADATA_KEY_RUPTURE)).thenReturn(false);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - a hit committed at half attack strength lands and the roll fails
            // (mock default) so no rupture task is scheduled
            swordsManager.processRupture(target, 0.5);

            // Then - the odds are the base chance scaled by the committed attack strength,
            // not by the stale live cooldown
            probabilityUtil.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                    eq(PrimarySkillType.SWORDS), eq(mmoPlayer), eq(16.5)));
            verify(player, never()).getAttackCooldown();
        }
    }

    @Nested
    class AbilityGates {
        @Test
        void serratedStrikesActivationShouldRequireAReadiedSword() {
            // Given - the sword tool is readied and the permission is held
            doReturn(true).when(mmoPlayer).getToolPreparationMode(ToolType.SWORD);
            when(Permissions.serratedStrikes(player)).thenReturn(true);

            // When / Then - the ability can activate
            assertThat(swordsManager.canActivateAbility()).isTrue();

            // And - an unreadied sword cannot
            doReturn(false).when(mmoPlayer).getToolPreparationMode(ToolType.SWORD);
            assertThat(swordsManager.canActivateAbility()).isFalse();
        }

        @Test
        void counterAttackShouldOnlyWorkAgainstLivingAttackers() {
            // Given - Counter Attack is unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_COUNTER_ATTACK))
                    .thenReturn(true);

            // When / Then - arrows and other non-living attackers cannot be countered
            assertThat(swordsManager.canUseCounterAttack(Mockito.mock(Entity.class))).isFalse();
            assertThat(swordsManager.canUseCounterAttack(target)).isTrue();
        }

        @Test
        void serratedStrikeUseShouldRequireTheActiveSuperAbility() {
            // Given - Serrated Strikes is unlocked but not currently active
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_SERRATED_STRIKES))
                    .thenReturn(true);
            doReturn(false).when(mmoPlayer).getAbilityMode(SuperAbilityType.SERRATED_STRIKES);

            // When / Then - the ability effects stay off until the super ability is active
            assertThat(swordsManager.canUseSerratedStrike()).isFalse();
            doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.SERRATED_STRIKES);
            assertThat(swordsManager.canUseSerratedStrike()).isTrue();
        }

        @Test
        void stabAndRuptureShouldRequireTheirUnlocks() {
            // Given - neither subskill is unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_STAB))
                    .thenReturn(false);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_RUPTURE))
                    .thenReturn(false);

            // When / Then - both gates stay closed until unlocked
            assertThat(swordsManager.canUseStab()).isFalse();
            assertThat(swordsManager.canUseRupture()).isFalse();
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_STAB))
                    .thenReturn(true);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_RUPTURE))
                    .thenReturn(true);
            assertThat(swordsManager.canUseStab()).isTrue();
            assertThat(swordsManager.canUseRupture()).isTrue();
        }
    }

    @Nested
    class Rupture {
        @BeforeEach
        void unlockRupture() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_RUPTURE))
                    .thenReturn(true);
            when(RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE)).thenReturn(3);
            when(advancedConfig.getRuptureChanceToApplyOnHit(3)).thenReturn(100.0);
        }

        @Test
        void existingRuptureShouldBeRefreshedInsteadOfReRolled() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the target is already bleeding from a rupture
                final RuptureTask ongoingRupture = Mockito.mock(RuptureTask.class);
                final RuptureTaskMeta ruptureMeta = Mockito.mock(RuptureTaskMeta.class);
                when(ruptureMeta.getRuptureTimerTask()).thenReturn(ongoingRupture);
                when(target.hasMetadata(MetadataConstants.METADATA_KEY_RUPTURE))
                        .thenReturn(true);
                when(target.getMetadata(MetadataConstants.METADATA_KEY_RUPTURE))
                        .thenReturn(List.of(ruptureMeta));

                // When - another rupture-capable hit lands
                swordsManager.processRupture(target, 1.0);

                // Then - the bleed is refreshed and no new roll happens
                verify(ongoingRupture).refreshRupture();
                probabilityUtil.verifyNoInteractions();
            }
        }

        @Test
        void successfulRuptureShouldScheduleTheBleedTask() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the rupture roll succeeds against a mob
                probabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        eq(PrimarySkillType.SWORDS), eq(mmoPlayer), eq(100.0)))
                        .thenReturn(true);
                when(advancedConfig.getRuptureTickDamage(false, 3)).thenReturn(2.0);

                // When - the hit lands at full strength
                swordsManager.processRupture(target, 1.0);

                // Then - a bleed task starts ticking on the target and is remembered on it
                verify(scheduler).runAtEntityTimer(eq(target), any(RuptureTask.class), eq(1L),
                        eq(1L));
                verify(target).setMetadata(eq(MetadataConstants.METADATA_KEY_RUPTURE),
                        any(RuptureTaskMeta.class));
            }
        }

        @Test
        void blockingDefendersShouldNotStartBleeding() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a defending player who is blocking with a shield
                final Player defender = Mockito.mock(Player.class);
                when(defender.isBlocking()).thenReturn(true);
                probabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        eq(PrimarySkillType.SWORDS), eq(mmoPlayer), eq(100.0)))
                        .thenReturn(true);

                // When - the rupture roll succeeds anyway
                swordsManager.processRupture(defender, 1.0);

                // Then - no bleed starts
                verify(defender, never()).setMetadata(eq(MetadataConstants.METADATA_KEY_RUPTURE),
                        any());
            }
        }

        @Test
        void bledPlayersShouldBeNotified() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a non-blocking defending player who uses notifications
                final Player defender = Mockito.mock(Player.class);
                when(NotificationManager.doesPlayerUseNotifications(defender)).thenReturn(true);
                probabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        eq(PrimarySkillType.SWORDS), eq(mmoPlayer), eq(100.0)))
                        .thenReturn(true);
                when(advancedConfig.getRuptureTickDamage(true, 3)).thenReturn(1.5);

                // When - the rupture lands
                swordsManager.processRupture(defender, 1.0);

                // Then - the defender is told they started bleeding
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        defender, NotificationType.SUBSKILL_MESSAGE,
                        "Swords.Combat.Bleeding.Started"));
            }
        }
    }

    @Nested
    class StabDamage {
        @Test
        void stabDamageShouldGrowWithRank() {
            // Given - rank 3 stab with a 1.5 base and 0.5 per rank
            when(RankUtils.getRank(player, SubSkillType.SWORDS_STAB)).thenReturn(3);
            when(advancedConfig.getStabBaseDamage()).thenReturn(1.5);
            when(advancedConfig.getStabPerRankMultiplier()).thenReturn(0.5);

            // When / Then - the bonus is base plus rank scaling
            assertThat(swordsManager.getStabDamage()).isCloseTo(3.0, within(1e-9));
        }

        @Test
        void unrankedStabShouldDealNoBonus() {
            // Given - stab has no rank yet
            when(RankUtils.getRank(player, SubSkillType.SWORDS_STAB)).thenReturn(0);

            // When / Then - no bonus damage
            assertThat(swordsManager.getStabDamage()).isZero();
        }
    }

    @Nested
    class CounterAttack {
        private double originalCounterModifier;

        @BeforeEach
        void setUpModifier() {
            originalCounterModifier = Swords.counterAttackModifier;
            Swords.counterAttackModifier = 2.0;
        }

        @AfterEach
        void restoreModifier() {
            Swords.counterAttackModifier = originalCounterModifier;
        }

        @Test
        void successfulCounterShouldReflectReducedDamage() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
                // Given - the counter roll succeeds against a player attacker
                final Player attacker = Mockito.mock(Player.class);
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.SWORDS_COUNTER_ATTACK, mmoPlayer)).thenReturn(true);

                // When - 10 damage is countered
                swordsManager.counterAttackChecks(attacker, 10.0);

                // Then - half the damage is reflected back at the attacker
                combatUtils.verify(() -> CombatUtils.safeDealDamage(attacker, 5.0, player));

                // And - both sides are informed
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Countered"));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        attacker, NotificationType.SUBSKILL_MESSAGE,
                        "Swords.Combat.Counter.Hit"));
            }
        }

        @Test
        void failedCounterShouldReflectNothing() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class);
                    MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
                // Given - the counter roll fails (mock default)
                // When - damage comes in
                swordsManager.counterAttackChecks(target, 10.0);

                // Then - nothing is reflected
                combatUtils.verifyNoInteractions();
            }
        }
    }

    @Test
    void serratedStrikesShouldApplyReducedAoeDamage() {
        final double originalModifier = Swords.serratedStrikesModifier;
        Swords.serratedStrikesModifier = 4.0;
        try (MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            // Given - a 12 damage serrated strike at full attack strength
            // When - the AoE is processed
            swordsManager.serratedStrikes(target, 12.0, 1.0);

            // Then - the AoE deals the modifier-reduced damage
            combatUtils.verify(() -> CombatUtils.applyAbilityAoE(player, target, 3.0, 1.0,
                    PrimarySkillType.SWORDS));
        } finally {
            Swords.serratedStrikesModifier = originalModifier;
        }
    }
}
