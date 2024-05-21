package com.gmail.nossr50.util.random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTest {

    private static Stream<Arguments> provideProbabilitiesForWithinExpectations() {
        return Stream.of(
                // static probability, % of time for success
                Arguments.of(new ProbabilityImpl(.05), 5),
                Arguments.of(new ProbabilityImpl(.10), 10),
                Arguments.of(new ProbabilityImpl(.15), 15),
                Arguments.of(new ProbabilityImpl(.20), 20),
                Arguments.of(new ProbabilityImpl(.25), 25),
                Arguments.of(new ProbabilityImpl(.50), 50),
                Arguments.of(new ProbabilityImpl(.75), 75),
                Arguments.of(new ProbabilityImpl(.90), 90),
                Arguments.of(new ProbabilityImpl(.999), 99.9),
                Arguments.of(new ProbabilityImpl(0.0005), 0.05),
                Arguments.of(new ProbabilityImpl(0.001), 0.1),
                Arguments.of(new ProbabilityImpl(50.0), 100),
                Arguments.of(new ProbabilityImpl(100.0), 100)
        );
    }

    private static Stream<Arguments> provideOfPercentageProbabilitiesForWithinExpectations() {
        return Stream.of(
                // static probability, % of time for success
                Arguments.of(Probability.ofPercent(5), 5),
                Arguments.of(Probability.ofPercent(10), 10),
                Arguments.of(Probability.ofPercent(15), 15),
                Arguments.of(Probability.ofPercent(20), 20),
                Arguments.of(Probability.ofPercent(25), 25),
                Arguments.of(Probability.ofPercent(50), 50),
                Arguments.of(Probability.ofPercent(75), 75),
                Arguments.of(Probability.ofPercent(90), 90),
                Arguments.of(Probability.ofPercent(99.9), 99.9),
                Arguments.of(Probability.ofPercent(0.05), 0.05),
                Arguments.of(Probability.ofPercent(0.1), 0.1),
                Arguments.of(Probability.ofPercent(500), 100),
                Arguments.of(Probability.ofPercent(1000), 100)
        );
    }
    @Test
    void testAlwaysWinConstructor() {
        for (int i = 0; i < 100000; i++) {
            assertTrue(new ProbabilityImpl(100).evaluate());
        }
    }

    @Test
    void testAlwaysLoseConstructor() {
        for (int i = 0; i < 100000; i++) {
            assertFalse(new ProbabilityImpl(0).evaluate());
        }
    }

    @Test
    void testAlwaysWinOfPercent() {
        for (int i = 0; i < 100000; i++) {
            assertTrue(Probability.ofPercent(100).evaluate());
        }
    }

    @Test
    void testAlwaysLoseOfPercent() {
        for (int i = 0; i < 100000; i++) {
            assertFalse(Probability.ofPercent(0).evaluate());
        }
    }

    @ParameterizedTest
    @MethodSource("provideProbabilitiesForWithinExpectations")
    void testOddsExpectationsConstructor(Probability probability, double expectedWinPercent) {
        assertExpectations(probability, expectedWinPercent);
    }

    @ParameterizedTest
    @MethodSource("provideOfPercentageProbabilitiesForWithinExpectations")
    void testOddsExpectationsOfPercent(Probability probability, double expectedWinPercent) {
        assertExpectations(probability, expectedWinPercent);
    }

    private static void assertExpectations(Probability probability, double expectedWinPercent) {
        double iterations = 2.0e7;
        double winCount = 0;

        for (int i = 0; i < iterations; i++) {
            if (probability.evaluate()) {
                winCount++;
            }
        }

        double successPercent = (winCount / iterations) * 100;
        System.out.println(successPercent + ", " + expectedWinPercent);
        assertEquals(expectedWinPercent, successPercent, 0.05D);
    }
}
