package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableShort that = (MutableShort) o;
        return shortValue == that.shortValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shortValue);
    }
}