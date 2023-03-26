package com.gmail.nossr50.util.random;

import com.gmail.nossr50.api.exceptions.ValueOutOfBoundsException;
import com.google.common.base.Objects;

public class ProbabilityImpl implements Probability {

    private final double probabilityValue;

    /**
     * Create a probability with a static value
     *
     * @param percentage the percentage value of the probability
     */
    ProbabilityImpl(double percentage) throws ValueOutOfBoundsException {
        if (percentage < 0) {
            throw new ValueOutOfBoundsException("Value should never be negative for Probability! This suggests a coding mistake, contact the devs!");
        }

        // Convert to a 0-1 floating point representation
        probabilityValue = percentage / 100.0D;
    }

    ProbabilityImpl(double xPos, double xCeiling, double probabilityCeiling) throws ValueOutOfBoundsException {
        if(probabilityCeiling > 100) {
            throw new ValueOutOfBoundsException("Probability Ceiling should never be above 100!");
        } else if (probabilityCeiling < 0) {
            throw new ValueOutOfBoundsException("Probability Ceiling should never be below 0!");
        }

        //Get the percent success, this will be from 0-100
        double probabilityPercent = (probabilityCeiling * (xPos / xCeiling));

        //Convert to a 0-1 floating point representation
        this.probabilityValue = probabilityPercent / 100.0D;
    }

    @Override
    public double getValue() {
        return probabilityValue;
    }

    @Override
    public String toString() {
        return "ProbabilityImpl{" +
                "probabilityValue=" + probabilityValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProbabilityImpl that = (ProbabilityImpl) o;
        return Double.compare(that.probabilityValue, probabilityValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(probabilityValue);
    }
}
