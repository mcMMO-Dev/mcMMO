package com.gmail.nossr50.core.mcmmo.meta;

/**
 * Represents custom state in the API
 * Mostly provided by plugins
 */
public interface Metadata {
    /**
     * The metadata key for this metadata
     * @return the metadata key
     */
    String getKey();

    /**
     * The value for this metadata key
     * @return the value of this metadata
     */
    Object getValue();

    /**
     * Replace the value in this metadata
     * @param newValue the replacement metadata value
     */
    void setValue(Object newValue);
}
