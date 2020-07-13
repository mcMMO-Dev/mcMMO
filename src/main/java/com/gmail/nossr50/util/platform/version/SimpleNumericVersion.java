package com.gmail.nossr50.util.platform.version;

import org.jetbrains.annotations.NotNull;

public class SimpleNumericVersion extends SimpleVersion implements NumericVersioned {
    private final int versionNumber;

    public SimpleNumericVersion(int versionNumber) {
        super(String.valueOf(versionNumber));
        this.versionNumber = versionNumber;
    }

    @Override
    public int asInt() {
        return versionNumber;
    }

    @Override
    public @NotNull String getVersionStr() {
        return super.getVersionString();
    }
}
