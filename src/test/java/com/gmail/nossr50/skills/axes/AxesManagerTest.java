package com.gmail.nossr50.skills.axes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Axes combat effects that depend on the committed attack strength of a hit.
 *
 * <p>Regression background: Paper 26.1.2+ resets the attack cooldown ticker before
 * {@code EntityDamageByEntityEvent} fires, so {@code Player#getAttackCooldown()} reads ~0.1
 * mid-event. Critical Strikes, Armor Impact, Greater Impact, and Skull Splitter must use the
 * attack strength scale passed in from CombatUtils, otherwise their proc odds and AoE damage
 * silently run at ~10% of the intended values.
 */
class AxesManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            AxesManagerTest.class.getName());

    private AxesManager axesManager;
    private LivingEntity target;

    private double originalAxeMasteryMultiplier;
    private double originalCritPvpModifier;
    private double originalCritPveModifier;
    private double originalGreaterImpactBonus;
    private double originalGreaterImpactKnockback;
    private double originalSkullSplitterModifier;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        axesManager = new AxesManager(mmoPlayer);
        target = Mockito.mock(LivingEntity.class);

        originalAxeMasteryMultiplier = Axes.axeMasteryRankDamageMultiplier;
        originalCritPvpModifier = Axes.criticalHitPVPModifier;
        originalCritPveModifier = Axes.criticalHitPVEModifier;
        originalGreaterImpactBonus = Axes.greaterImpactBonusDamage;
        originalGreaterImpactKnockback = Axes.greaterImpactKnockbackMultiplier;
        originalSkullSplitterModifier = Axes.skullSplitterModifier;

        // Simulate Paper 26.1.2+ during a damage event: the ticker was already reset, so the
        // live cooldown misreports the charge of the committed hit.
        when(advancedConfig.useAttackCooldown()).thenReturn(true);
        when(player.getAttackCooldown()).thenReturn(0.1f);
    }

    @AfterEach
    void tearDown() {
        Axes.axeMasteryRankDamageMultiplier = originalAxeMasteryMultiplier;
        Axes.criticalHitPVPModifier = originalCritPvpModifier;
        Axes.criticalHitPVEModifier = originalCritPveModifier;
        Axes.greaterImpactBonusDamage = originalGreaterImpactBonus;
        Axes.greaterImpactKnockbackMultiplier = originalGreaterImpactKnockback;
        Axes.skullSplitterModifier = originalSkullSplitterModifier;
        cleanUpStaticMocks();
    }

    private ItemStack mockArmorPiece() {
        final ItemStack chestplate = Mockito.mock(ItemStack.class);
        when(chestplate.getType()).thenReturn(Material.IRON_CHESTPLATE);
        return chestplate;
    }

    private void wireTargetArmor(ItemStack... armorContents) {
        final EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        when(target.getEquipment()).thenReturn(equipment);
        when(equipment.getArmorContents()).thenReturn(armorContents);
    }

    @Test
    void criticalHitShouldRollOddsWithCommittedAttackStrength() {
        // Given - Critical Strikes doubles damage against non-players and the roll succeeds
        // only when queried with the committed attack strength
        Axes.criticalHitPVEModifier = 2.0;
        doReturn(false).when(mmoPlayer).useChatNotifications();

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                            eq(SubSkillType.AXES_CRITICAL_STRIKES), eq(mmoPlayer), eq(0.6)))
                    .thenReturn(true);

            // When - an 8.0 damage hit committed at 0.6 attack strength lands
            final double bonus = axesManager.criticalHit(target, 8.0, 0.6);

            // Then - the crit fires with the PVE modifier (8.0 * 2.0 - 8.0)
            assertThat(bonus).isEqualTo(8.0);
            verify(player, never()).getAttackCooldown();
        }
    }

    @Test
    void greaterImpactShouldRollOddsWithCommittedAttackStrength() {
        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - the roll fails (mock default) so no knockback side effects run
            final double bonus = axesManager.greaterImpact(target, 0.25);

            // Then - the odds were scaled by the committed attack strength
            assertThat(bonus).isZero();
            probabilityUtil.verify(() -> ProbabilityUtil.isSkillRNGSuccessful(
                    eq(SubSkillType.AXES_GREATER_IMPACT), eq(mmoPlayer), eq(0.25)));
            verify(player, never()).getAttackCooldown();
        }
    }

    @Test
    void impactCheckShouldRollOddsPerArmorPieceWithCommittedAttackStrength() {
        // Given - the target wears a single piece of armor
        final EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        final ItemStack chestplate = Mockito.mock(ItemStack.class);
        when(chestplate.getType()).thenReturn(Material.IRON_CHESTPLATE);
        when(target.getEquipment()).thenReturn(equipment);
        when(equipment.getArmorContents()).thenReturn(new ItemStack[]{chestplate});
        when(advancedConfig.getImpactDurabilityDamageMultiplier()).thenReturn(1.0);
        Mockito.when(RankUtils.getRank(player, SubSkillType.AXES_ARMOR_IMPACT)).thenReturn(4);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - the roll fails (mock default) so no durability change happens
            axesManager.impactCheck(target, 0.4);

            // Then - the odds were scaled by the committed attack strength
            probabilityUtil.verify(() -> ProbabilityUtil.isSkillRNGSuccessful(
                    eq(SubSkillType.AXES_ARMOR_IMPACT), eq(mmoPlayer), eq(0.4)));
            verify(player, never()).getAttackCooldown();
        }
    }

    @Test
    void skullSplitterCheckShouldScaleAoEDamageByCommittedAttackStrength() {
        // Given - Skull Splitter divides the hit damage by its modifier before the AoE
        Axes.skullSplitterModifier = 4.0;

        try (MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            // When - a 12.0 damage hit committed at half attack strength triggers the AoE
            axesManager.skullSplitterCheck(target, 12.0, 0.5);

            // Then - the AoE damage is (12.0 / 4.0) * 0.5 and the scale is forwarded so
            // downstream effects (e.g. Rupture from Serrated Strikes) can use it too
            combatUtils.verify(() -> CombatUtils.applyAbilityAoE(eq(player), eq(target), eq(1.5),
                    eq(0.5), eq(PrimarySkillType.AXES)));
            verify(player, never()).getAttackCooldown();
        }
    }

    @Nested
    class AbilityGates {
        @BeforeEach
        void wireValidTarget() {
            when(target.isValid()).thenReturn(true);
        }

        @Test
        void axeMasteryShouldRequireItsUnlock() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_AXE_MASTERY))
                    .thenReturn(false);
            assertThat(axesManager.canUseAxeMastery()).isFalse();

            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_AXE_MASTERY))
                    .thenReturn(true);
            assertThat(axesManager.canUseAxeMastery()).isTrue();
        }

        @Test
        void criticalHitsShouldRequireAValidTarget() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_CRITICAL_STRIKES))
                    .thenReturn(true);

            assertThat(axesManager.canCriticalHit(target)).isTrue();
            when(target.isValid()).thenReturn(false);
            assertThat(axesManager.canCriticalHit(target)).isFalse();
        }

        @Test
        void armorImpactShouldRequireAnArmoredTarget() {
            // Given - Armor Impact is unlocked and the target wears iron armor
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_ARMOR_IMPACT))
                    .thenReturn(true);
            wireTargetArmor(mockArmorPiece());

            // When / Then - the armored target can be impacted
            assertThat(axesManager.canImpact(target)).isTrue();

            // And - a naked target cannot
            wireTargetArmor(new ItemStack[]{null, null, null, null});
            assertThat(axesManager.canImpact(target)).isFalse();
        }

        @Test
        void greaterImpactShouldRequireANakedTarget() {
            // Given - Greater Impact is unlocked and the target wears nothing
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_GREATER_IMPACT))
                    .thenReturn(true);
            wireTargetArmor(new ItemStack[]{null, null, null, null});

            // When / Then - the naked target can be knocked flying
            assertThat(axesManager.canGreaterImpact(target)).isTrue();

            // And - an armored target cannot
            wireTargetArmor(mockArmorPiece());
            assertThat(axesManager.canGreaterImpact(target)).isFalse();
        }

        @Test
        void skullSplitterUseShouldRequireTheActiveSuperAbility() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_SKULL_SPLITTER))
                    .thenReturn(true);
            when(Permissions.skullSplitter(player)).thenReturn(true);

            doReturn(false).when(mmoPlayer).getAbilityMode(SuperAbilityType.SKULL_SPLITTER);
            assertThat(axesManager.canUseSkullSplitter(target)).isFalse();

            doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.SKULL_SPLITTER);
            assertThat(axesManager.canUseSkullSplitter(target)).isTrue();
        }

        @Test
        void abilityActivationShouldRequireAReadiedAxe() {
            when(Permissions.skullSplitter(player)).thenReturn(true);

            doReturn(false).when(mmoPlayer).getToolPreparationMode(ToolType.AXE);
            assertThat(axesManager.canActivateAbility()).isFalse();

            doReturn(true).when(mmoPlayer).getToolPreparationMode(ToolType.AXE);
            assertThat(axesManager.canActivateAbility()).isTrue();
        }
    }

    @Nested
    class AxeMastery {
        @Test
        void axeMasteryBonusShouldScaleWithRank() {
            // Given - rank 4 Axe Mastery with a 1.0 per-rank multiplier
            Axes.axeMasteryRankDamageMultiplier = 1.0;
            Mockito.when(RankUtils.getRank(player, SubSkillType.AXES_AXE_MASTERY)).thenReturn(4);

            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // And - the mastery activation succeeds
                probabilityUtil.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.AXES_AXE_MASTERY, mmoPlayer)).thenReturn(true);

                // When / Then - the bonus is rank times multiplier
                assertThat(axesManager.axeMastery()).isEqualTo(4.0);
            }
        }

        @Test
        void failedActivationShouldAddNothing() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                assertThat(axesManager.axeMastery()).isZero();
            }
        }
    }

    @Nested
    class CriticalHit {
        @Test
        void failedRollShouldDealNoBonus() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                assertThat(axesManager.criticalHit(target, 8.0, 1.0)).isZero();
            }
        }

        @Test
        void playerVictimsShouldUseThePvpModifierAndBeTold() {
            // Given - a 1.5x PVP crit modifier against a player who uses notifications
            Axes.criticalHitPVPModifier = 1.5;
            doReturn(true).when(mmoPlayer).useChatNotifications();
            final org.bukkit.entity.Player defender = Mockito.mock(org.bukkit.entity.Player.class);
            when(NotificationManager.doesPlayerUseNotifications(defender)).thenReturn(true);

            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        eq(SubSkillType.AXES_CRITICAL_STRIKES), eq(mmoPlayer), eq(1.0)))
                        .thenReturn(true);

                // When - an 8.0 damage hit crits
                final double bonus = axesManager.criticalHit(defender, 8.0, 1.0);

                // Then - the bonus uses the PVP modifier (8.0 * 1.5 - 8.0)
                assertThat(bonus).isEqualTo(4.0);

                // And - both sides are informed
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CriticalHit"));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        defender, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CritStruck"));
            }
        }
    }

    @Nested
    class ImpactAndGreaterImpact {
        @Test
        void impactShouldDamageEachArmorPieceThatFailsItsRoll() {
            // Given - a target wearing one armor piece and a winning impact roll
            final ItemStack chestplate = mockArmorPiece();
            wireTargetArmor(chestplate);
            when(advancedConfig.getImpactDurabilityDamageMultiplier()).thenReturn(1.5);
            Mockito.when(RankUtils.getRank(player, SubSkillType.AXES_ARMOR_IMPACT)).thenReturn(4);

            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<SkillUtils> skillUtils = mockStatic(SkillUtils.class)) {
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        eq(SubSkillType.AXES_ARMOR_IMPACT), eq(mmoPlayer), eq(1.0)))
                        .thenReturn(true);

                // When - the impact check runs at full strength
                axesManager.impactCheck(target, 1.0);

                // Then - the armor takes rank-scaled durability damage
                skillUtils.verify(() -> SkillUtils.handleArmorDurabilityChange(chestplate, 6.0,
                        1));
            }
        }

        @Test
        void targetsWithoutEquipmentShouldBeSkipped() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a target with no equipment at all
                when(target.getEquipment()).thenReturn(null);

                // When - the impact check runs
                axesManager.impactCheck(target, 1.0);

                // Then - no rolls happen
                probabilityUtil.verifyNoInteractions();
            }
        }

        @Test
        void greaterImpactShouldKnockTheTargetFlyingAndPayBonusDamage() {
            // Given - a successful greater impact with a 1.5x knockback and 2.0 bonus damage
            Axes.greaterImpactKnockbackMultiplier = 1.5;
            Axes.greaterImpactBonusDamage = 2.0;
            doReturn(true).when(mmoPlayer).useChatNotifications();
            when(player.getLocation()).thenReturn(new org.bukkit.Location(world, 0, 64, 0, 0, 0));

            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<ParticleEffectUtils> particles =
                            mockStatic(ParticleEffectUtils.class)) {
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        eq(SubSkillType.AXES_GREATER_IMPACT), eq(mmoPlayer), eq(1.0)))
                        .thenReturn(true);

                // When - the greater impact procs
                final double bonus = axesManager.greaterImpact(target, 1.0);

                // Then - the target is knocked along the attacker's facing and the bonus lands
                assertThat(bonus).isEqualTo(2.0);
                particles.verify(() -> ParticleEffectUtils.playGreaterImpactEffect(target));
                verify(target).setVelocity(new org.bukkit.util.Vector(0, 0, 1.5));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Proc"));
            }
        }
    }

    @Nested
    class ArmorDetection {
        @Test
        void invalidTargetsShouldCountAsUnarmored() {
            when(target.isValid()).thenReturn(false);
            assertThat(Axes.hasArmor(target)).isFalse();
        }

        @Test
        void armoredTargetsShouldBeDetected() {
            when(target.isValid()).thenReturn(true);
            wireTargetArmor(mockArmorPiece());
            assertThat(Axes.hasArmor(target)).isTrue();
        }

        @Test
        void emptyArmorSlotsShouldCountAsUnarmored() {
            when(target.isValid()).thenReturn(true);
            wireTargetArmor(new ItemStack[]{null, null, null, null});
            assertThat(Axes.hasArmor(target)).isFalse();
        }
    }
}
