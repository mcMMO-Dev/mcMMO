package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableFloat that = (MutableFloat) o;
        return Float.compare(that.floatValue, floatValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(floatValue);
    }
}