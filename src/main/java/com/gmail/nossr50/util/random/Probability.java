package com.gmail.nossr50.util.random;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public interface Probability {
    /**
     * The value of this Probability
     * Should return a result between 0 and 1 (inclusive)
     * A value of 1 or greater represents something that will always succeed
     * A value of around 0.5 represents something that succeeds around half the time
     * A value of 0 represents something that will always fail
     *
     * @return the value of probability
     */
    double getValue();

    /**
     * Create a new Probability with the given value
     * A value of 100 would represent 100% chance of success
     * A value of 50 would represent 50% chance of success
     * A value of 0 would represent 0% chance of success
     * A value of 1 would represent 1% chance of success
     * A value of 0.5 would represent 0.5% chance of success
     * A value of 0.01 would represent 0.01% chance of success
     *
     * @param percentage the value of the probability
     * @return a new Probability with the given value
     */
    static @NotNull Probability ofPercent(double percentage) {
        return new ProbabilityImpl(percentage);
    }

    /**
     * Simulates a "roll of the dice"
     * If the value passed is higher than the "random" value, than it is a successful roll
     *
     * @param probabilityValue probability value
     * @return true for succeeding, false for failing
     */
    static private boolean isSuccessfulRoll(double probabilityValue) {
        return (probabilityValue) >= ThreadLocalRandom.current().nextDouble(1D);
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
