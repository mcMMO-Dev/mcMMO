package com.gmail.nossr50.skills.unarmed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Unarmed combat effects that depend on the committed attack strength of a hit.
 *
 * <p>Regression background: Paper 26.1.2+ resets the attack cooldown ticker before
 * {@code EntityDamageByEntityEvent} fires, so {@code Player#getAttackCooldown()} reads ~0.1
 * mid-event even for a fully charged punch. Berserk and Disarm must use the attack strength
 * scale that CombatUtils back-derives from the event instead of the live cooldown, otherwise
 * Berserk turns into a damage penalty (a 9.0 damage punch collapsed to ~1.35).
 */
class UnarmedManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            UnarmedManagerTest.class.getName());

    private UnarmedManager unarmedManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        unarmedManager = new UnarmedManager(mmoPlayer);

        // Simulate Paper 26.1.2+ during a damage event: the ticker was already reset, so the
        // live cooldown reads ~0.1 for a fist even though the hit was fully charged.
        when(advancedConfig.useAttackCooldown()).thenReturn(true);
        when(player.getAttackCooldown()).thenReturn(0.1f);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void berserkDamageShouldUseCommittedAttackStrengthWhenLiveCooldownIsStale() {
        // Given - a fully charged 9.0 damage punch (1.0 fist + 8.0 Steel Arm Style) while the
        // live cooldown misreports the charge as 0.1

        // When - Berserk computes its bonus from the committed attack strength of the hit
        final double bonus = unarmedManager.berserkDamage(9.0, 1.0);

        // Then - the +50% bonus applies (9.0 -> 13.5) instead of collapsing the hit to 1.35
        assertThat(bonus).isEqualTo(4.5);

        // And - the stale live cooldown is never consulted
        verify(player, never()).getAttackCooldown();
    }

    /**
     * Bonus formula: {@code (damage * 1.5 * attackStrengthScale) - damage}. Weak (uncharged)
     * hits keep their historical penalty: the bonus goes negative below ~2/3 charge.
     */
    @ParameterizedTest(name = "damage={0}, scale={1} -> bonus={2}")
    @CsvSource({
            "9.0,  1.0,  4.5",    // fully charged hit gains the full +50%
            "1.0,  1.0,  0.5",    // bare fist without Steel Arm Style
            "9.0,  0.5,  -2.25",  // half charge keeps its historical spam-click penalty
            "9.0,  0.0,  -9.0",   // zero charge cancels the hit entirely
    })
    void berserkDamageShouldScaleWithCommittedAttackStrength(final double damage,
            final double attackStrengthScale, final double expectedBonus) {
        // Given - a hit committed at the given attack strength

        // When
        final double bonus = unarmedManager.berserkDamage(damage, attackStrengthScale);

        // Then
        assertThat(bonus).isCloseTo(expectedBonus, offset(1e-9));
    }

    @Test
    void disarmCheckShouldRollOddsWithCommittedAttackStrength() {
        // Given - a defender and a hit committed at 3/4 attack strength
        final Player defender = Mockito.mock(Player.class);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - the RNG roll fails (mock default) so no disarm side effects run
            unarmedManager.disarmCheck(defender, 0.75);

            // Then - the odds were scaled by the committed attack strength, not the stale cooldown
            probabilityUtil.verify(() -> ProbabilityUtil.isSkillRNGSuccessful(
                    eq(SubSkillType.UNARMED_DISARM), eq(mmoPlayer), eq(0.75)));
            verify(player, never()).getAttackCooldown();
        }
    }
}
