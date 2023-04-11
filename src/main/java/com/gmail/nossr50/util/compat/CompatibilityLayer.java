package com.gmail.nossr50.util.compat;

/**
 * Compatibility Layers should be named after the functionality they serve
 */
public interface CompatibilityLayer {
    /**
     * Whether this CompatibilityLayer successfully initialized and in theory should be functional
     * @return true if this CompatibilityLayer is functional
     */
    default boolean noErrorsOnInitialize() { return true; };
}
