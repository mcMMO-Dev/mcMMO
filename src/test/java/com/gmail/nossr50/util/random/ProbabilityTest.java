package com.gmail.nossr50.util.random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.gmail.nossr50.util.random.RandomChanceUtil.processProbability;
import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTest {

    private static Stream<Arguments> provideProbabilitiesForWithinExpectations() {
        return Stream.of(
                // static probability, % of time for success
                Arguments.of(new ProbabilityImpl(5), 5),
                Arguments.of(new ProbabilityImpl(10), 10),
                Arguments.of(new ProbabilityImpl(15), 15),
                Arguments.of(new ProbabilityImpl(20), 20),
                Arguments.of(new ProbabilityImpl(25), 25),
                Arguments.of(new ProbabilityImpl(50), 50),
                Arguments.of(new ProbabilityImpl(75), 75),
                Arguments.of(new ProbabilityImpl(90), 90),
                Arguments.of(new ProbabilityImpl(99.9), 99.9),
                Arguments.of(new ProbabilityImpl(0.05), 0.05),
                Arguments.of(new ProbabilityImpl(0.1), 0.1),
                Arguments.of(new ProbabilityImpl(500), 100),
                Arguments.of(new ProbabilityImpl(1000), 100)
        );
    }
    @Test
    void testIsSuccessfulRollSucceeds() {
        Probability probability = new ProbabilityImpl(100);

        for (int i = 0; i < 100000; i++) {
            assertTrue(processProbability(probability));
        }
    }

    @Test
    void testIsSuccessfulRollFails() {
        Probability probability = new ProbabilityImpl(0);

        for (int i = 0; i < 100000; i++) {
            assertFalse(processProbability(probability));
        }
    }

    @Test
    void testIsSuccessfulRollFailsOfPercentage() {
        Probability probability = Probability.ofPercentageValue(100);

        for (int i = 0; i < 100000; i++) {
            assertFalse(processProbability(probability));
        }
    }

    @ParameterizedTest
    @MethodSource("provideProbabilitiesForWithinExpectations")
    void testProcessProbabilityWithinExpectations(Probability probability, double expectedWinPercent) {
        // Probabilities are tested 200,000,000 times with a margin of error of 0.01%
        int iterations = 200000000;
        double winCount = 0;

        for (int i = 0; i < iterations; i++) {
            if(processProbability(probability)) {
                winCount++;
            }
        }

        double successPercent = (winCount / iterations) * 100;
        System.out.println(successPercent + ", " + expectedWinPercent);
        assertEquals(expectedWinPercent, successPercent, 0.01D);
    }

    @Test
    void ofPercentageValue() {
    }

    @Test
    void ofSubSkill() {
    }
}