package com.gmail.nossr50.core.nbt;

import java.util.Arrays;

public class NBTLongArray implements NBTBase {

    private long[] values;

    public NBTLongArray(long[] values) {
        this.values = values;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.LONG_ARRAY;
    }

    public int getLength() {
        return values.length;
    }

    public long[] getValues() {
        return values;
    }

    public void setValues(long[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "NBTLongArray{" +
                "values=" + Arrays.toString(values) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTLongArray that = (NBTLongArray) o;
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
