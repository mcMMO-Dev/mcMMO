package com.gmail.nossr50.core.nbt;

import java.util.Objects;

public class NBTInt implements NBTBase {

    private int value;

    public NBTInt(int value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.INT;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NBTInt{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTInt nbtInt = (NBTInt) o;
        return value == nbtInt.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
