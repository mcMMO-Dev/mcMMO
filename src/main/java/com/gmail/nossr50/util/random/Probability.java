package com.gmail.nossr50.util.random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.concurrent.ThreadLocalRandom;

public interface Probability {
    /**
     * The value of this Probability
     * Should return a result between 0 and 1 (inclusive)
     * 1 should represent something that will always succeed
     * 0.5 should represent something that succeeds around half the time
     * etc
     *
     * @return the value of probability
     */
    double getValue();

    static @NotNull Probability ofPercent(double percentageValue) {
        return new ProbabilityImpl(percentageValue);
    }

    /**
     * Simulates a "roll of the dice"
     * If the value passed is higher than the "random" value, than it is a successful roll
     *
     * @param probabilityValue probability value
     * @return true for succeeding, false for failing
     */
    static private boolean isSuccessfulRoll(double probabilityValue) {
        return (probabilityValue * 100) >= ThreadLocalRandom.current().nextDouble(100D);
    }

    /**
     * Simulate an outcome on a probability and return true or false for the result of that outcome
     *
     * @return true if the probability succeeded, false if it failed
     */
    default boolean evaluate() {
        return isSuccessfulRoll(getValue());
    }

    /**
     * Modify and then Simulate an outcome on a probability and return true or false for the result of that outcome
     *
     * @param probabilityMultiplier probability will be multiplied by this before success is checked
     * @return true if the probability succeeded, false if it failed
     */
    default boolean evaluate(double probabilityMultiplier) {
        double probabilityValue = getValue() * probabilityMultiplier;
        return isSuccessfulRoll(probabilityValue);
    }
}
