package com.gmail.nossr50.core.mcmmo.meta;

/**
 * A metadataHolder is something that can hold metadata
 * Both Bukkit and Sponge provide metadata APIs
 */
public interface MetadataHolder {
    /**
     * Gets the metadata for the appropriate key
     * @param key the key for the metadata
     * @return the metadata for this key
     */
    Metadata getMetadata(String key);

    /**
     * Sets the metadata, will replace metadata with an existing key or add metadata if there was none
     * @param metadata metadata to add
     * @return a copy of the metadata
     */
    Metadata setMetadata(Metadata metadata);

    /**
     * Sets the metadata, will replace metadata with a matching key or add metadata if there was none
     * @param key metadata key
     * @param value metadata value
     * @return a copy of the metadata
     */
    Metadata setMetadata(String key, Object value);
}
