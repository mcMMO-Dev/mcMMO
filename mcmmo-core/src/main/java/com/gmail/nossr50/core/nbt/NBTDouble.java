package com.gmail.nossr50.core.nbt;

import java.util.Objects;

public class NBTDouble implements NBTBase {

    private double value;

    public NBTDouble(double value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.DOUBLE;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NBTDouble{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTDouble nbtDouble = (NBTDouble) o;
        return Double.compare(nbtDouble.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
