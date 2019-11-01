package com.gmail.nossr50.core.nbt;

import java.util.Objects;

public class NBTShort implements NBTBase {

    private short value;

    public NBTShort(short value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.SHORT;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NBTShort{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTShort nbtShort = (NBTShort) o;
        return value == nbtShort.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
