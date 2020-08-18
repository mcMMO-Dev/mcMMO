package com.gmail.nossr50.datatypes.mutableprimitives;

public class MutableInteger {
    private int integer;

    public MutableInteger(int integer) {
        this.integer = integer;
    }

    public int getImmutableCopy() {
        return integer;
    }

    public void setInt(int integer) {
        this.integer = integer;
    }
}
