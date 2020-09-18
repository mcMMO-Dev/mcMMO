package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;

public class MutableBoolean {
    private boolean bool;

    public MutableBoolean(boolean bool) {
        this.bool = bool;
    }

    public boolean getImmutableCopy() {
        return bool;
    }

    public void setBoolean(boolean bool) {
        this.bool = bool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableBoolean that = (MutableBoolean) o;
        return bool == that.bool;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bool);
    }
}
