package com.gmail.nossr50.skills.acrobatics;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.acrobatics.Roll;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class AcrobaticsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(AcrobaticsTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.ACROBATICS_ROLL, 1)).thenReturn(1);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.ACROBATICS_DODGE, 1)).thenReturn(1);

        // wire advanced config
        when(advancedConfig.getMaximumProbability(SubSkillType.ACROBATICS_ROLL)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL)).thenReturn(1000);
        when(advancedConfig.getRollDamageThreshold()).thenReturn(7D);

        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.ACROBATICS_ROLL, 1))
                .thenReturn(1); // needed?
        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.ACROBATICS_DODGE, 1))
                .thenReturn(1000); // needed?

        when(RankUtils.getRankUnlockLevel(SubSkillType.ACROBATICS_ROLL, 1)).thenReturn(
                1); // needed?
        when(RankUtils.hasReachedRank(eq(1), any(Player.class),
                eq(SubSkillType.ACROBATICS_ROLL))).thenReturn(true);
        when(RankUtils.hasReachedRank(eq(1), any(Player.class),
                any(AbstractSubSkill.class))).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void rollShouldLowerDamage() {
        // Given
        final Roll roll = new Roll();
        final double damage = 2D;
        final EntityDamageEvent mockEvent = mockEntityDamageEvent(damage);
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, 1000);
        when(roll.canRoll(mmoPlayer)).thenReturn(true);
        assertThat(roll.canRoll(mmoPlayer)).isTrue();

        // When
        roll.doInteraction(mockEvent, mcMMO.p);

        // Then
        verify(mockEvent, atLeastOnce()).setDamage(0);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void rollShouldNotLowerDamage() {
        // Given
        final Roll roll = new Roll();
        final double damage = 100D;
        final EntityDamageEvent mockEvent = mockEntityDamageEvent(damage);
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, 0);
        when(roll.canRoll(mmoPlayer)).thenReturn(true);
        assertThat(roll.canRoll(mmoPlayer)).isTrue();

        // When
        roll.doInteraction(mockEvent, mcMMO.p);

        // Then
        assertThat(roll.canRoll(mmoPlayer)).isTrue();
        verify(mockEvent, Mockito.never()).setDamage(any(Double.class));
    }

    /**
     * calculateModifiedRollDamage should subtract the threshold from the base damage and clamp to 0.
     */
    @ParameterizedTest(name = "baseDamage={0}, threshold={1}, expected={2}")
    @MethodSource("calculateModifiedRollDamageArgs")
    void calculateModifiedRollDamage_subtractsThresholdAndClampsToZero(
            final double baseDamage,
            final double threshold,
            final double expected) {
        assertThat(Roll.calculateModifiedRollDamage(baseDamage, threshold)).isEqualTo(expected);
    }

    static Stream<Arguments> calculateModifiedRollDamageArgs() {
        return Stream.of(
                Arguments.of(10.0, 7.0, 3.0),   // normal case: 10 - 7 = 3 remaining
                Arguments.of(5.0, 7.0, 0.0),    // damage < threshold: clamped to 0
                Arguments.of(14.0, 14.0, 0.0),  // exactly at threshold: clamped to 0
                Arguments.of(20.0, 3.0, 17.0),  // large damage, small threshold
                Arguments.of(0.0, 7.0, 0.0)     // zero base damage: stays 0
        );
    }

    /**
     * The configured RollDamageThreshold must flow through rollCheck and appear in the MAGIC
     * damage modifier applied to the event.  At max skill level the roll always succeeds, so the
     * only variable is how much damage remains after applying the threshold.
     */
    @ParameterizedTest(name = "threshold={0}, damage={1}, expectedMagicDamage={2}")
    @MethodSource("rollDamageThresholdArgs")
    @SuppressWarnings("deprecation")
    void rollCheck_appliesDamageThresholdFromConfig(
            final double configuredThreshold,
            final double incomingDamage,
            final double expectedMagicDamage) throws InvalidSkillException {
        // Arrange
        when(advancedConfig.getRollDamageThreshold()).thenReturn(configuredThreshold);
        final Roll roll = new Roll();
        final EntityDamageEvent mockEvent = mockEntityDamageEvent(incomingDamage);
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, 1000); // max level → roll always succeeds
        when(roll.canRoll(mmoPlayer)).thenReturn(true);
        when(player.isSneaking()).thenReturn(false);

        // Act
        roll.doInteraction(mockEvent, mcMMO.p);

        // Assert
        verify(mockEvent).setDamage(eq(EntityDamageEvent.DamageModifier.MAGIC),
                eq(expectedMagicDamage));
    }

    static Stream<Arguments> rollDamageThresholdArgs() {
        // rollCheck uses getRollDamageThreshold() * 2 as the effective cap
        return Stream.of(
                // threshold=3 → effective cap=6: 10 - 6 = 4 magic damage remains
                Arguments.of(3.0, 10.0, 4.0),
                // threshold=7 → effective cap=14: 10 - 14 → 0 magic damage remains
                Arguments.of(7.0, 10.0, 0.0),
                // threshold=4 → effective cap=8: 15 - 8 = 7 magic damage remains
                Arguments.of(4.0, 15.0, 7.0)
        );
    }

    /**
     * Graceful roll probability must be exactly double the non-graceful probability at any skill
     * level. This is a regression test for the bug fixed in 2.2.007 where ofPercent() was used
     * instead of ofValue(), causing the value to be divided by 100 a second time (resulting in ~2%
     * at max level instead of 100%).
     */
    @ParameterizedTest(name = "acrobaticsLevel={0}")
    @MethodSource("acrobaticsLevels")
    void gracefulRollProbability_isExactlyDoubleNonGraceful(final int acrobaticsLevel) {
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, acrobaticsLevel);

        final double normalOdds = Roll.getNonGracefulProbability(mmoPlayer).getValue();
        final double gracefulOdds = Roll.getGracefulProbability(mmoPlayer).getValue();

        assertThat(gracefulOdds).isCloseTo(normalOdds * 2, within(1e-9));
    }

    /**
     * At max level, the graceful probability must be >= 1.0 so that evaluate() always returns true
     * (guaranteed success).
     */
    @Test
    void gracefulRollProbability_atMaxLevel_guaranteesSuccess() {
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, 1000); // MaxBonusLevel = 1000

        final double gracefulOdds = Roll.getGracefulProbability(mmoPlayer).getValue();

        assertThat(gracefulOdds).isGreaterThanOrEqualTo(1.0);
    }

    static Stream<Arguments> acrobaticsLevels() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(100),
                Arguments.of(500),
                Arguments.of(999),
                Arguments.of(1000)
        );
    }

    private @NotNull EntityDamageEvent mockEntityDamageEvent(double damage) {
        final EntityDamageEvent mockEvent = mock(EntityDamageEvent.class);
        when(mockEvent.isApplicable(any(EntityDamageEvent.DamageModifier.class))).thenReturn(true);
        when(mockEvent.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);
        when(mockEvent.getFinalDamage()).thenReturn(damage);
        when(mockEvent.getDamage(any(EntityDamageEvent.DamageModifier.class))).thenReturn(damage);
        when(mockEvent.getDamage()).thenReturn(damage);
        when(mockEvent.isCancelled()).thenReturn(false);
        when(mockEvent.getEntity()).thenReturn(player);
        return mockEvent;
    }
}