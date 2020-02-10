package com.gmail.nossr50.core.nbt;

import java.util.Objects;

public class NBTByte implements NBTBase {

    private byte value;

    public NBTByte(byte value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.BYTE;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTByte nbtByte = (NBTByte) o;
        return value == nbtByte.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "NBTByte{" +
                "value=" + value +
                '}';
    }
}
