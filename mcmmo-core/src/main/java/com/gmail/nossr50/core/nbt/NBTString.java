package com.gmail.nossr50.core.nbt;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NBTString implements NBTBase {

    @NonNull
    private String value;

    public NBTString(@NonNull String value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.STRING;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NBTString{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTString nbtString = (NBTString) o;
        return value.equals(nbtString.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
