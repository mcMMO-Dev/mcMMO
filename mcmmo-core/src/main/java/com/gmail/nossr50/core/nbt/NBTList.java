package com.gmail.nossr50.core.nbt;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

public class NBTList implements NBTBase {

    @NonNull
    private List<? extends NBTBase> values;

    public NBTList(@NonNull List<? extends NBTBase> values) {
        this.values = values;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.LIST;
    }

    public int getLength() {
        return values.size();
    }

    public List<? extends NBTBase> getValues() {
        return values;
    }

    public void setValues(@NonNull List<? extends NBTBase> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTList nbtList = (NBTList) o;
        return values.equals(nbtList.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "NBTList{" +
                "values=" + values +
                '}';
    }
}
