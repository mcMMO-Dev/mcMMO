package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableByte that = (MutableByte) o;
        return byteValue == that.byteValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(byteValue);
    }
}