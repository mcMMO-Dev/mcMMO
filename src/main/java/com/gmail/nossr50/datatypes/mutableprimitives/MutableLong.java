package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

public class MutableLong {

    private long longValue;

    public MutableLong(long longValue) {
        this.longValue = longValue;
    }

    public long getImmutableCopy() {
        return longValue;
    }

    public void setLong(long longValue) {
        this.longValue = longValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableLong that = (MutableLong) o;
        return longValue == that.longValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(longValue);
    }
}
