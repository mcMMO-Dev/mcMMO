package com.gmail.nossr50.util.random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ProbabilityTestUtils {
    /**
     * Allowed drift between the observed and expected win percentage. At 20 million samples
     * the worst-case binomial standard deviation is about 0.011 percentage points, putting
     * this bound near 4.5 sigma: loose enough that a fair RNG practically never trips it,
     * tight enough to still catch a real probability regression.
     */
    private static final double TOLERANCE_PERCENTAGE_POINTS = 0.05D;

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
        assertThat(successPercent)
                .isCloseTo(expectedWinPercent, within(TOLERANCE_PERCENTAGE_POINTS));
        System.out.println("Variance is within tolerance levels!");
    }
}
