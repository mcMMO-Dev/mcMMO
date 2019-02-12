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
     */
    void setMetadata(Metadata metadata);
}
