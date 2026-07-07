package com.gmail.nossr50.util.random;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.ACROBATICS;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.HERBALISM;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.MINING;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.UNARMED;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.ACROBATICS_DODGE;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.AXES_ARMOR_IMPACT;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.AXES_GREATER_IMPACT;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.HERBALISM_GREEN_THUMB;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.MINING_DOUBLE_DROPS;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.TAMING_FAST_FOOD_SERVICE;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.UNARMED_ARROW_DEFLECT;
import static com.gmail.nossr50.util.random.ProbabilityTestUtils.assertProbabilityExpectations;
import static com.gmail.nossr50.util.random.ProbabilityUtil.calculateCurrentSkillProbability;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProbabilityUtilTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(ProbabilityUtilTest.class.getName());

    final static double impactChance = 11D;
    final static double greaterImpactChance = 0.007D;
    final static double fastFoodChance = 45.5D;

    @BeforeEach
    public void setupMocks() {
        mockBaseEnvironment(logger);
        when(advancedConfig.getImpactChance()).thenReturn(impactChance);
        when(advancedConfig.getGreaterImpactChance()).thenReturn(greaterImpactChance);
        when(advancedConfig.getFastFoodChance()).thenReturn(fastFoodChance);
    }

    @AfterEach
    public void tearDown() {
        cleanUpStaticMocks();
    }

    private static Stream<Arguments> staticChanceSkills() {
        return Stream.of(
                // static probability, % of time for success
                Arguments.of(AXES_ARMOR_IMPACT, impactChance),
                Arguments.of(AXES_GREATER_IMPACT, greaterImpactChance),
                Arguments.of(TAMING_FAST_FOOD_SERVICE, fastFoodChance)
        );
    }

    @ParameterizedTest
    @MethodSource("staticChanceSkills")
    void staticChanceSkillsShouldSucceedAsExpected(SubSkillType subSkillType,
            double expectedWinPercent)
            throws InvalidStaticChance {
        Probability staticRandomChance = ProbabilityUtil.getStaticRandomChance(subSkillType);
        assertProbabilityExpectations(expectedWinPercent, staticRandomChance);
    }

    @Test
    public void isSkillRNGSuccessfulShouldBehaveAsExpected() {
        // Given
        when(advancedConfig.getMaximumProbability(UNARMED_ARROW_DEFLECT)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(UNARMED_ARROW_DEFLECT)).thenReturn(0);

        @SuppressWarnings("all") final Probability probability = ProbabilityUtil.getSkillProbability(
                UNARMED_ARROW_DEFLECT, mmoPlayer);
        assertEquals(0.2D, probability.getValue());
        assertProbabilityExpectations(20, probability);
    }

    private static Stream<Arguments> provideSkillProbabilityTestData() {
        return Stream.of(
                // skillLevel, floor, ceiling, maxBonusLevel, expectedValue

                // 20% chance at skill level 1000
                Arguments.of(1000, 0, 20, 1000, 0.2),
                // 10% chance at skill level 500
                Arguments.of(500, 0, 20, 1000, 0.1),
                // 5% chance at skill level 250
                Arguments.of(250, 0, 20, 1000, 0.05),
                // 0% chance at skill level 0
                Arguments.of(0, 0, 20, 1000, 0.0),
                // 0% chance at skill level 1000
                Arguments.of(1000, 0, 0, 1000, 0.0),
                // 1% chance at skill level 1000
                Arguments.of(1000, 0, 1, 1000, 0.01)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSkillProbabilityTestData")
    void testCalculateCurrentSkillProbability(double skillLevel, double floor, double ceiling,
            double maxBonusLevel,
            double expectedValue) {
        // When
        final Probability probability = calculateCurrentSkillProbability(skillLevel, floor, ceiling,
                maxBonusLevel);

        // Then
        assertEquals(expectedValue, probability.getValue());
    }

    @Test
    public void getRNGDisplayValuesShouldReturn10PercentForDodge() {
        // Given
        when(advancedConfig.getMaximumProbability(ACROBATICS_DODGE)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(ACROBATICS_DODGE)).thenReturn(1000);
        mmoPlayer.modifySkill(ACROBATICS, 500);

        // When & Then
        final String[] rngDisplayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                ACROBATICS_DODGE);
        assertEquals("10.00%", rngDisplayValues[0]);
    }

    @Test
    public void getRNGDisplayValuesShouldReturn20PercentForDodge() {
        // Given
        when(advancedConfig.getMaximumProbability(ACROBATICS_DODGE)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(ACROBATICS_DODGE)).thenReturn(1000);
        mmoPlayer.modifySkill(ACROBATICS, 1000);

        // When & then
        final String[] rngDisplayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                ACROBATICS_DODGE);
        assertEquals("20.00%", rngDisplayValues[0]);
    }

    @Test
    public void getRNGDisplayValuesShouldReturn0PercentForDodge() {
        // Given
        when(advancedConfig.getMaximumProbability(ACROBATICS_DODGE)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(ACROBATICS_DODGE)).thenReturn(1000);
        mmoPlayer.modifySkill(ACROBATICS, 0);

        // When & then
        final String[] rngDisplayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                ACROBATICS_DODGE);
        assertEquals("0.00%", rngDisplayValues[0]);
    }

    /**
     * Skill levels reported in issue #5210, where the Dodge chance on one server displayed
     * values climbing past 100% between levels 201 and 850. Boundary levels around the
     * reported "fixes itself" point and the max bonus level are included as well.
     */
    private static Stream<Arguments> reportedDodgeSkillLevels() {
        return Stream.of(
                Arguments.of(201),
                Arguments.of(202),
                Arguments.of(203),
                Arguments.of(204),
                Arguments.of(251),
                Arguments.of(252),
                Arguments.of(253),
                Arguments.of(254),
                Arguments.of(301),
                Arguments.of(302),
                Arguments.of(303),
                Arguments.of(304),
                Arguments.of(351),
                Arguments.of(352),
                Arguments.of(353),
                Arguments.of(354),
                Arguments.of(401),
                Arguments.of(402),
                Arguments.of(403),
                Arguments.of(404),
                Arguments.of(451),
                Arguments.of(452),
                Arguments.of(453),
                Arguments.of(454),
                Arguments.of(501),
                Arguments.of(502),
                Arguments.of(503),
                Arguments.of(504),
                Arguments.of(551),
                Arguments.of(552),
                Arguments.of(553),
                Arguments.of(554),
                Arguments.of(601),
                Arguments.of(602),
                Arguments.of(603),
                Arguments.of(604),
                Arguments.of(651),
                Arguments.of(652),
                Arguments.of(653),
                Arguments.of(654),
                Arguments.of(701),
                Arguments.of(702),
                Arguments.of(703),
                Arguments.of(704),
                Arguments.of(751),
                Arguments.of(752),
                Arguments.of(753),
                Arguments.of(754),
                Arguments.of(801),
                Arguments.of(802),
                Arguments.of(803),
                Arguments.of(804),
                Arguments.of(850),
                Arguments.of(851),
                Arguments.of(1000),
                Arguments.of(1001),
                Arguments.of(1500)
        );
    }

    /**
     * Regression coverage for issue #5210: with standard-mode defaults every reported level is
     * above the max bonus level, so both the real proc chance and the displayed chance must sit
     * exactly at the cap instead of scaling past 100%.
     */
    @ParameterizedTest
    @MethodSource("reportedDodgeSkillLevels")
    void dodgeChanceShouldStayAtCapWhenLevelExceedsStandardMaxBonusLevel(int skillLevel) {
        // Given - Dodge uses the standard-mode defaults (ChanceMax 20, MaxBonusLevel 100)
        when(advancedConfig.getMaximumProbability(ACROBATICS_DODGE)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(ACROBATICS_DODGE)).thenReturn(100);

        // And - the player has one of the Acrobatics levels reported in the issue
        mmoPlayer.modifySkill(ACROBATICS, skillLevel);

        // When - the proc probability and the displayed chance are computed
        final Probability probability = ProbabilityUtil.getSubSkillProbability(ACROBATICS_DODGE,
                mmoPlayer);
        final String[] displayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                ACROBATICS_DODGE);

        // Then - the real proc chance is pinned to the 20% cap rather than scaling unbounded
        assertThat(probability.getValue()).isEqualTo(0.2D);

        // And - the value shown to players matches the capped chance
        assertThat(displayValues[0]).isEqualTo("20.00%");
    }

    /**
     * Regression coverage for issue #5210 under retro-mode defaults: the chance scales linearly
     * up to the max bonus level and never exceeds the configured cap for any reported level.
     */
    @ParameterizedTest
    @MethodSource("reportedDodgeSkillLevels")
    void dodgeChanceShouldScaleLinearlyAndStayCappedWhenRetroDefaults(int skillLevel) {
        // Given - Dodge uses the retro-mode defaults (ChanceMax 20, MaxBonusLevel 1000)
        when(advancedConfig.getMaximumProbability(ACROBATICS_DODGE)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(ACROBATICS_DODGE)).thenReturn(1000);

        // And - the player has one of the Acrobatics levels reported in the issue
        mmoPlayer.modifySkill(ACROBATICS, skillLevel);

        // When - the proc probability is computed
        final Probability probability = ProbabilityUtil.getSubSkillProbability(ACROBATICS_DODGE,
                mmoPlayer);

        // Then - the chance scales linearly below the max bonus level and is capped above it
        final double expectedValue = Math.min(skillLevel, 1000) / 5000.0D;
        assertThat(probability.getValue()).isCloseTo(expectedValue, within(1.0E-12));

        // And - the chance never exceeds the 20% ceiling, let alone 100%
        assertThat(probability.getValue()).isLessThanOrEqualTo(0.2D);
    }

    /**
     * Arrow Deflect shares the same dynamic probability code path as Dodge and was named in the
     * same historical unbounded-chance bug, so it gets the same over-cap regression coverage.
     */
    @ParameterizedTest
    @MethodSource("reportedDodgeSkillLevels")
    void arrowDeflectChanceShouldStayAtCapWhenLevelExceedsMaxBonusLevel(int skillLevel) {
        // Given - Arrow Deflect uses the standard-mode defaults (ChanceMax 20, MaxBonusLevel 100)
        when(advancedConfig.getMaximumProbability(UNARMED_ARROW_DEFLECT)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(UNARMED_ARROW_DEFLECT)).thenReturn(100);

        // And - the player has one of the levels reported in the issue
        mmoPlayer.modifySkill(UNARMED, skillLevel);

        // When - the proc probability is computed
        final Probability probability = ProbabilityUtil.getSubSkillProbability(
                UNARMED_ARROW_DEFLECT, mmoPlayer);

        // Then - the real proc chance is pinned to the 20% cap rather than scaling unbounded
        assertThat(probability.getValue()).isEqualTo(0.2D);
    }

    @Test
    public void getRNGDisplayValuesShouldReturn10PercentForDoubleDrops() {
        // Given
        when(advancedConfig.getMaximumProbability(MINING_DOUBLE_DROPS)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(MINING_DOUBLE_DROPS)).thenReturn(1000);
        mmoPlayer.modifySkill(MINING, 100);

        // When & Then
        final String[] rngDisplayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                MINING_DOUBLE_DROPS);
        assertEquals("10.00%", rngDisplayValues[0]);
    }

    @Test
    public void getRNGDisplayValuesShouldReturn50PercentForDoubleDrops() {
        // Given
        when(advancedConfig.getMaximumProbability(MINING_DOUBLE_DROPS)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(MINING_DOUBLE_DROPS)).thenReturn(1000);
        mmoPlayer.modifySkill(MINING, 500);

        // When & Then
        final String[] rngDisplayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                MINING_DOUBLE_DROPS);
        assertEquals("50.00%", rngDisplayValues[0]);
    }

    /**
     * Regression coverage for GitHub issue #4365. Green Thumb at max bonus level must have a
     * probability value of exactly 1.0, leaving no room for float truncation to shave the
     * chance below 100%.
     */
    @Test
    void greenThumbProbabilityShouldBeExactlyOneWhenAtMaxBonusLevel() {
        // Given - Green Thumb is configured with a 100% ceiling reached at level 100
        when(advancedConfig.getMaximumProbability(HERBALISM_GREEN_THUMB)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(HERBALISM_GREEN_THUMB)).thenReturn(100);
        // And - the player has reached the max bonus level
        mmoPlayer.modifySkill(HERBALISM, 100);

        // When - the Green Thumb probability is computed
        final Probability probability = ProbabilityUtil.getSubSkillProbability(
                HERBALISM_GREEN_THUMB, mmoPlayer);

        // Then - the probability is exactly 1.0, not 0.99x
        assertThat(probability.getValue()).isEqualTo(1.0D);
    }

    private static Stream<Arguments> greenThumbGuaranteedRollCases() {
        return Stream.of(
                // isLucky, herbalismLevel (ceiling 100% at level 100)
                // Max bonus level without the lucky perk is a plain 100% chance
                Arguments.of(false, 100),
                // Max bonus level with the lucky perk is 100% * 1.333
                Arguments.of(true, 100),
                // 76% base chance with the lucky perk is 76% * 1.333 = 101.308%
                Arguments.of(true, 76)
        );
    }

    /**
     * Regression coverage for GitHub issue #4365. When the effective Green Thumb chance is at
     * or above 100% (with or without the lucky perk), the skill RNG must never fail.
     */
    @ParameterizedTest
    @MethodSource("greenThumbGuaranteedRollCases")
    void greenThumbRollShouldNeverFailWhenChanceIsHundredPercent(boolean isLucky,
            int herbalismLevel) {
        // Given - Green Thumb is configured with a 100% ceiling reached at level 100
        when(advancedConfig.getMaximumProbability(HERBALISM_GREEN_THUMB)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(HERBALISM_GREEN_THUMB)).thenReturn(100);
        // And - the player has the given skill level and lucky perk state
        mmoPlayer.modifySkill(HERBALISM, herbalismLevel);
        when(Permissions.lucky(player, HERBALISM)).thenReturn(isLucky);

        // When - the Green Thumb RNG is rolled many times
        int successCount = 0;
        final int trials = 10_000;
        for (int i = 0; i < trials; i++) {
            if (ProbabilityUtil.isSkillRNGSuccessful(HERBALISM_GREEN_THUMB, mmoPlayer)) {
                successCount++;
            }
        }

        // Then - every single roll succeeds
        assertThat(successCount).isEqualTo(trials);
    }

    /**
     * Documents the lucky perk boundary: the modifier is 1.333 (not 4/3), so a 75% base chance
     * becomes 99.975%, which is displayed as 99.98% and is genuinely allowed to fail. Only base
     * chances of roughly 75.02% and above are guaranteed with the lucky perk.
     */
    @Test
    void luckyRollShouldStillBeAbleToFailWhenBaseChanceIsSeventyFivePercent() {
        // Given - a 75% base probability, just below the lucky guarantee threshold
        final Probability probability = Probability.ofPercent(75);

        // When - the probability is evaluated many times with the lucky modifier applied
        boolean sawFailure = false;
        final int trials = 2_000_000;
        for (int i = 0; i < trials; i++) {
            if (!probability.evaluate(ProbabilityUtil.LUCKY_MODIFIER)) {
                sawFailure = true;
                break;
            }
        }

        // Then - at least one roll fails because 0.75 * 1.333 = 0.99975 < 1.0
        assertThat(sawFailure).isTrue();
    }

    @Test
    public void getRNGDisplayValuesShouldReturn100PercentForDoubleDrops() {
        // Given
        when(advancedConfig.getMaximumProbability(MINING_DOUBLE_DROPS)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(MINING_DOUBLE_DROPS)).thenReturn(1000);
        mmoPlayer.modifySkill(MINING, 1000);

        // When & Then
        final String[] rngDisplayValues = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                MINING_DOUBLE_DROPS);
        assertEquals("100.00%", rngDisplayValues[0]);
    }

}
