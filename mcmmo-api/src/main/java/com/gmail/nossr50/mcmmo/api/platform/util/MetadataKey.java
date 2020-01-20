package com.gmail.nossr50.mcmmo.api.platform.util;

import org.jetbrains.annotations.NotNull;

public class MetadataKey<V> {

    private final String key;

    public MetadataKey(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }
}
