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

    /**
     * @param parentFolderPath Path to the "parent" folder on disk
     * @param relativePath Path to the config relative to the "parent" folder, this should mirror internal structure of resource files
     * @param mergeNewKeys if true, the users config will add keys found in the internal file that are missing from the users file during load
     * @param copyDefaults if true, the users config file when it is first made will be a copy of an internal resource file of the same name and path
     * @param removeOldKeys if true, the users config file will have keys not found in the internal default resource file of the same name and path removed
     */
    public ConfigCollection(String parentFolderPath, String relativePath, boolean mergeNewKeys, boolean copyDefaults, boolean removeOldKeys) {
        super(parentFolderPath, relativePath, mergeNewKeys, copyDefaults, removeOldKeys);

        //init
        initCollection();

        //load
        register();
    }

    /**
     * @param parentFolderPath Path to the "parent" folder on disk
     * @param relativePath Path to the config relative to the "parent" folder, this should mirror internal structure of resource files
     * @param mergeNewKeys if true, the users config will add keys found in the internal file that are missing from the users file during load
     * @param copyDefaults if true, the users config file when it is first made will be a copy of an internal resource file of the same name and path
     * @param removeOldKeys if true, the users config file will have keys not found in the internal default resource file of the same name and path removed
     */
    public ConfigCollection(File parentFolderPath, String relativePath, boolean mergeNewKeys, boolean copyDefaults, boolean removeOldKeys) {
        super(parentFolderPath, relativePath, mergeNewKeys, copyDefaults, removeOldKeys);

        //init
        initCollection();

        //load
        register();
    }

    /**
     * Initializes the generic collection held by this class
     */
    private void initCollection() {
        if (genericCollection == null)
            genericCollection = new ArrayList<>();
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
