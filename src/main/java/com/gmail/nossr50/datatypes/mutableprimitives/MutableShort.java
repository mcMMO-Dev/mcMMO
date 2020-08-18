package com.gmail.nossr50.datatypes.mutableprimitives;

public class MutableShort {

    private short shortValue;

    public MutableShort(short shortValue) {
        this.shortValue = shortValue;
    }

    public short getImmutableCopy() {
        return shortValue;
    }

    public void setShort(short shortValue) {
        this.shortValue = shortValue;
    }

}