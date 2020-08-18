package com.gmail.nossr50.datatypes.mutableprimitives;

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

}
