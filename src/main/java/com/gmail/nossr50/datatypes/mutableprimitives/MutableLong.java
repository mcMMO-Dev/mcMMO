package com.gmail.nossr50.datatypes.mutableprimitives;

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

}
