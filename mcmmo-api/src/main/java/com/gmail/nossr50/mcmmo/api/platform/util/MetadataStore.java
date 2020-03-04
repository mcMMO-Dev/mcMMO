package com.gmail.nossr50.mcmmo.api.platform.util;

import com.gmail.nossr50.mcmmo.api.data.MMOEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface MetadataStore<E extends MMOEntity<?>> {

    /**
     * @param holder holder of the metadata
     * @param key key for the metdata
     * @param <V> value type
     * @return the metadata value or null
     */
    @Nullable
    <V> V getMetadata(@NotNull E holder, @NotNull MetadataKey<V> key);

    /**
     * @param holder holder of the metdata
     * @param key metadata key
     * @param value metadata value
     * @param <V> value type
     */
    <V> void setMetadata(@NotNull E holder, @NotNull MetadataKey<V> key, @NotNull V value);

    /**
     * @param holder holder of the metadata
     * @param key metadata key
     * @param <V> value type
     * @return the removed metadata key
     */
    @Nullable
    <V> V removeMetadata(@NotNull E holder, @NotNull MetadataKey<V> key);

}
