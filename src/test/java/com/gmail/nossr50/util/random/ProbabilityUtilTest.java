package com.gmail.nossr50.util.random;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProbabilityUtilTest {

    mcMMO mmoInstance;
    AdvancedConfig advancedConfig;

    final static double impactChance = 11D;
    final static double greaterImpactChance = 0.007D;
    final static double fastFoodChance = 45.5D;

    @BeforeEach
    public void setupMocks() throws NoSuchFieldException, IllegalAccessException {
        this.mmoInstance = Mockito.mock(mcMMO.class);
        mcMMO.class.getField("p").set(null, mmoInstance);
        this.advancedConfig = Mockito.mock(AdvancedConfig.class);
        Mockito.when(mmoInstance.getAdvancedConfig()).thenReturn(advancedConfig);
        Mockito.when(advancedConfig.getImpactChance()).thenReturn(impactChance);
        Mockito.when(advancedConfig.getGreaterImpactChance()).thenReturn(greaterImpactChance);
        Mockito.when(advancedConfig.getFastFoodChance()).thenReturn(fastFoodChance);
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
        // Probabilities are tested 2.0 x 10^9 with a margin of error of 0.01%
        double iterations = 2.0e9;
        double winCount = 0;

        Probability staticRandomChance = ProbabilityUtil.getStaticRandomChance(subSkillType);
        for (int i = 0; i < iterations; i++) {
            if(staticRandomChance.evaluate()) {
                winCount++;
            }
        }

        double successPercent = (winCount / iterations) * 100;
        System.out.println(successPercent + ", " + expectedWinPercent);
        assertEquals(expectedWinPercent, successPercent, 0.01D);
    }
}