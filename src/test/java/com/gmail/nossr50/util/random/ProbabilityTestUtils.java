package com.gmail.nossr50.util.random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProbabilityTestUtils {
    public static void assertProbabilityExpectations(double expectedWinPercent,
            Probability probability) {
        double iterations = 2.0e7; //20 million
        double winCount = 0;
        for (int i = 0; i < iterations; i++) {
            if (probability.evaluate()) {
                winCount++;
            }
        }

        double successPercent = (winCount / iterations) * 100;
        System.out.println("Wins: " + winCount);
        System.out.println("Fails: " + (iterations - winCount));
        System.out.println(
                "Percentage succeeded: " + successPercent + ", Expected: " + expectedWinPercent);
        assertEquals(expectedWinPercent, successPercent, 0.035D);
        System.out.println("Variance is within tolerance levels!");
    }
}
