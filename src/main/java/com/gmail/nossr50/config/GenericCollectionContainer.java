package com.gmail.nossr50.config;

import java.util.Collection;

/**
 * Represents a class that contains a generic collection
 * @param <T>
 */
public interface GenericCollectionContainer<T> {
    /**
     * Grab the collection held by this class
     * @return the collection held by this class
     */
    Collection<T> getLoadedCollection();
}
