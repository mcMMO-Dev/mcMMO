package com.gmail.nossr50.core.config;

import java.io.File;

/**
 * Represents a config file that registers keys after its initialized
 */
public abstract class ConfigKeyRegister extends Config implements RegistersKeys {

    public ConfigKeyRegister(String pathToParentFolder, String relativePath, boolean mergeNewKeys) {
        super(pathToParentFolder, relativePath, mergeNewKeys);
        loadKeys();
    }

    public ConfigKeyRegister(File pathToParentFolder, String relativePath, boolean mergeNewKeys) {
        super(pathToParentFolder, relativePath, mergeNewKeys);
        loadKeys();
    }
}
