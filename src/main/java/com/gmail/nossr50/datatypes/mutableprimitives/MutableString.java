package com.gmail.nossr50.datatypes.mutableprimitives;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

public class MutableString {

    private @NotNull String string;

    public MutableString(@NotNull String string) {
        this.string = string;
    }

    public @NotNull String getImmutableCopy() {
        return string;
    }

    public void setString(@NotNull String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableString that = (MutableString) o;
        return Objects.equal(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(string);
    }
}
