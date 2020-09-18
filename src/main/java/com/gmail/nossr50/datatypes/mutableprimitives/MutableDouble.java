package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

public class MutableDouble {

    private double doubleValue;

    public MutableDouble(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public double getImmutableCopy() {
        return doubleValue;
    }

    public void setDouble(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableDouble that = (MutableDouble) o;
        return Double.compare(that.doubleValue, doubleValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(doubleValue);
    }
}