package com.gmail.nossr50.util.random;

import com.google.common.base.Objects;

public class ProbabilityImpl implements Probability {

    private final double probabilityValue;

    /**
     * Create a probability from a static value. A value of 0 represents a 0% chance of success, A
     * value of 1 represents a 100% chance of success. A value of 0.5 represents a 50% chance of
     * success. A value of 0.01 represents a 1% chance of success. And so on.
     *
     * @param value the value of the probability between 0 and 100
     */
    public ProbabilityImpl(double value) throws IllegalArgumentException {
        if (value < 0) {
            throw new IndexOutOfBoundsException("Value should never be negative for Probability!" +
                    " This suggests a coding mistake, contact the devs!");
        }

        probabilityValue = value;
    }

    @Override
    public double getValue() {
        return probabilityValue;
    }

    @Override
    public String toString() {
        return "ProbabilityImpl{" + "probabilityValue=" + probabilityValue + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProbabilityImpl that = (ProbabilityImpl) o;
        return Double.compare(that.probabilityValue, probabilityValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(probabilityValue);
    }
}
