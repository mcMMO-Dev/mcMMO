package com.gmail.nossr50.datatypes.mutableprimitives;

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

}