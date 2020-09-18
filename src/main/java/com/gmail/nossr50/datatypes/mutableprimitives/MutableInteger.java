package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableInteger that = (MutableInteger) o;
        return integer == that.integer;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(integer);
    }
}
