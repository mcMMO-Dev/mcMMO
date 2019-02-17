package com.gmail.nossr50.config;

/**
 * Represents a config that is version checked
 */
public interface VersionedConfig {
    /**
     * The version of this config
     * @return
     */
    double getConfigVersion();
}
