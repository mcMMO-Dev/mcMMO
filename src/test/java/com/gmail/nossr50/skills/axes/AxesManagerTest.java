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
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        axesManager = new AxesManager(mmoPlayer);
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
}
