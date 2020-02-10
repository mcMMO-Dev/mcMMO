package com.gmail.nossr50.core.nbt;

import java.util.Objects;

public class NBTFloat implements NBTBase {

    private float value;

    public NBTFloat(float value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.FLOAT;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NBTFloat{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTFloat nbtFloat = (NBTFloat) o;
        return Float.compare(nbtFloat.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
