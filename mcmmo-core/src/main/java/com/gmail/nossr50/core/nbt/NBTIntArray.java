package com.gmail.nossr50.core.nbt;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class NBTIntArray implements NBTBase {

    private int[] values;

    public NBTIntArray(int[] values) {
        this.values = values;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.INT_ARRAY;
    }

    public int getLength() {
        return values.length;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "NBTIntArray{" +
                "values=" + Arrays.toString(values) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTIntArray that = (NBTIntArray) o;
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
