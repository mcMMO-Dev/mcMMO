package com.gmail.nossr50.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a config file that registers keys after its initialized
 */
public abstract class ConfigCollection<T> extends Config implements Registers, GenericCollectionContainer {

    //The collection held by this class
    protected Collection<T> genericCollection;

    public ConfigCollection(String pathToParentFolder, String relativePath, boolean mergeNewKeys) {
        super(pathToParentFolder, relativePath, mergeNewKeys);

        //init
        initCollection();

        //load
        register();
    }

    private void initCollection() {
        if (genericCollection == null)
            genericCollection = new ArrayList<>();
    }

    public ConfigCollection(File pathToParentFolder, String relativePath, boolean mergeNewKeys) {
        super(pathToParentFolder, relativePath, mergeNewKeys);

        //init
        initCollection();

        //load
        register();
    }

    @Override
    public Collection<T> getLoadedCollection() {
        return this.genericCollection;
    }

    @Override
    public void unload() {
        genericCollection.clear();
    }
}
