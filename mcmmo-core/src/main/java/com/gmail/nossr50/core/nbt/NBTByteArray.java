package com.gmail.nossr50.core.nbt;

import java.util.Arrays;

public class NBTByteArray implements NBTBase {

    private byte[] values;

    public NBTByteArray(byte[] values) {
        this.values = values;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.BYTE_ARRAY;
    }

    public int getLength() {
        return values.length;
    }

    public byte[] getValues() {
        return values;
    }

    public void setValues(byte[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "NBTByteArray{" +
                "values=" + Arrays.toString(values) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTByteArray that = (NBTByteArray) o;
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
