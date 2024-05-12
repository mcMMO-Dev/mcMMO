package com.gmail.nossr50.util.random;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.*;
import static com.gmail.nossr50.util.random.ProbabilityTestUtils.assertProbabilityExpectations;
import static com.gmail.nossr50.util.random.ProbabilityUtil.calculateCurrentSkillProbability;
import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
        cleanupBaseEnvironment();
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
    void staticChanceSkillsShouldSucceedAsExpected(SubSkillType subSkillType, double expectedWinPercent)
            throws InvalidStaticChance {
        Probability staticRandomChance = ProbabilityUtil.getStaticRandomChance(subSkillType);
        assertProbabilityExpectations(expectedWinPercent, staticRandomChance);
    }

    @Test
    public void isSkillRNGSuccessfulShouldBehaveAsExpected() {
        // Given
        when(advancedConfig.getMaximumProbability(UNARMED_ARROW_DEFLECT)).thenReturn(20D);
        when(advancedConfig.getMaxBonusLevel(UNARMED_ARROW_DEFLECT)).thenReturn(0);

        @SuppressWarnings("all")
        final Probability probability = ProbabilityUtil.getSkillProbability(UNARMED_ARROW_DEFLECT, mmoPlayer);
        assertEquals(0.2D, probability.getValue());
        assertProbabilityExpectations(20, probability);
    }

    @Test
    public void calculateCurrentSkillProbabilityShouldBeTwenty() {
        final Probability probability = calculateCurrentSkillProbability(1000, 0, 20, 1000);
        assertEquals(0.2D, probability.getValue());
    }
}
