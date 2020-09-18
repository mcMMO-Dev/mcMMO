package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

public class MutableChar {

    private char charValue;

    public MutableChar(char charValue) {
        this.charValue = charValue;
    }

    public char getImmutableCopy() {
        return charValue;
    }

    public void setChar(char charValue) {
        this.charValue = charValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableChar that = (MutableChar) o;
        return charValue == that.charValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(charValue);
    }
}