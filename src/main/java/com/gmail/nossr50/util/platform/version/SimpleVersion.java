package com.gmail.nossr50.util.platform.version;

import org.jetbrains.annotations.NotNull;

public class SimpleVersion {
    @NotNull
    private final String versionString;

    public SimpleVersion(@NotNull String versionString) {
        this.versionString = versionString;
    }

    @NotNull
    public String getVersionString() {
        return versionString;
    }
}
