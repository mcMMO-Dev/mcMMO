package com.gmail.nossr50.util.random;

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
}
