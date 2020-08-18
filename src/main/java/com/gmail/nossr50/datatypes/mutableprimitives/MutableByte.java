package com.gmail.nossr50.datatypes.mutableprimitives;

public class MutableByte {

    private byte byteValue;

    public MutableByte(byte byteValue) {
        this.byteValue = byteValue;
    }

    public byte getImmutableCopy() {
        return byteValue;
    }

    public void setByte(byte byteValue) {
        this.byteValue = byteValue;
    }

}