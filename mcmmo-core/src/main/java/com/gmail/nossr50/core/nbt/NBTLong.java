package com.gmail.nossr50.core.nbt;

import java.util.Objects;

public class NBTLong implements NBTBase {

    private long value;

    public NBTLong(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.LONG;
    }

    @Override
    public String toString() {
        return "NBTLong{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTLong nbtLong = (NBTLong) o;
        return value == nbtLong.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
