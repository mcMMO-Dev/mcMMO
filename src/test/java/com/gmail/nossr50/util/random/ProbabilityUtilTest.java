package com.gmail.nossr50.util.random;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProbabilityUtilTest {
    mcMMO mmoInstance;
    AdvancedConfig advancedConfig;

    final static double impactChance = 11D;
    final static double greaterImpactChance = 0.007D;
    final static double fastFoodChance = 45.5D;

    @BeforeEach
    public void setupMocks() throws NoSuchFieldException, IllegalAccessException {
        this.mmoInstance = mock(mcMMO.class);
        mcMMO.class.getField("p").set(null, mmoInstance);
        this.advancedConfig = mock(AdvancedConfig.class);
        when(mmoInstance.getAdvancedConfig()).thenReturn(advancedConfig);
        when(advancedConfig.getImpactChance()).thenReturn(impactChance);
        when(advancedConfig.getGreaterImpactChance()).thenReturn(greaterImpactChance);
        when(advancedConfig.getFastFoodChance()).thenReturn(fastFoodChance);
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
    void testStaticChanceSkills(SubSkillType subSkillType, double expectedWinPercent) throws InvalidStaticChance {
        Probability staticRandomChance = ProbabilityUtil.getStaticRandomChance(subSkillType);
        assertProbabilityExpectations(expectedWinPercent, staticRandomChance);
    }

    private static void assertProbabilityExpectations(double expectedWinPercent, Probability probability) {
        double iterations = 2.0e7;
        double winCount = 0;
        for (int i = 0; i < iterations; i++) {
            if(probability.evaluate()) {
                winCount++;
            }
        }

        double successPercent = (winCount / iterations) * 100;
        System.out.println(successPercent + ", " + expectedWinPercent);
        assertEquals(expectedWinPercent, successPercent, 0.05D);
    }
}
