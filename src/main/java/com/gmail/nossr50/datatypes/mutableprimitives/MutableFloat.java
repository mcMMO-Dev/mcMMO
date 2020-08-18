package com.gmail.nossr50.datatypes.mutableprimitives;

public class MutableFloat {

    private float floatValue;

    public MutableFloat(float floatValue) {
        this.floatValue = floatValue;
    }

    public float getImmutableCopy() {
        return floatValue;
    }

    public void setFloat(float floatValue) {
        this.floatValue = floatValue;
    }

}