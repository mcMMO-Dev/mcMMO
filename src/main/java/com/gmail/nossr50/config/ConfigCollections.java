package com.gmail.nossr50.config;

import java.io.File;

/**
 * Represents a config file that registers keys after its initialized
 */
public abstract class ConfigCollections extends Config implements RegistersKeys, ConfigCollection {

    public ConfigCollections(String pathToParentFolder, String relativePath, boolean mergeNewKeys) {
        super(pathToParentFolder, relativePath, mergeNewKeys);
        loadKeys();
    }

    public ConfigCollections(File pathToParentFolder, String relativePath, boolean mergeNewKeys) {
        super(pathToParentFolder, relativePath, mergeNewKeys);
        loadKeys();
    }
}
