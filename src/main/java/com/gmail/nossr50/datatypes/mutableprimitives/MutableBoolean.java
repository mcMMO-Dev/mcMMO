package com.gmail.nossr50.datatypes.mutableprimitives;

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
}
