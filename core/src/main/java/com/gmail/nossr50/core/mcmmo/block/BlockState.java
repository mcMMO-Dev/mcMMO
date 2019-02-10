package com.gmail.nossr50.core.mcmmo.block;

import com.gmail.nossr50.core.mcmmo.Property;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;

/**
 * Representation of the state for a Block
 * This tries to mirror MC internals, but only the parts mcMMO cares about
 */
public interface BlockState {
    //This is the immutable map of all properties for this block state
    ImmutableMap<Property<?>, Comparable<?>> getImmutablePropertyMap();

    //This will return the keyset for properties on this block state
    Collection<Property<?>> getPropertyKeyset();

    //TODO: I don't know if we need to mirror the cycling of properties

    /**
     * Get the value for the given property key
     *
     * @param property the property key
     * @param <T>      the type of property
     * @return the value, can be null
     */
    <T extends Comparable<T>> T getPropertyValue(Property<T> property);

    /**
     * This will attempt to find a matching property for this block state
     *
     * @param property the property we want to match
     * @param value    the value we are trying to match
     * @param <T>      the type of the property
     * @param <V>      the type of the value
     * @return the matching property on this block state, can be null
     */
    <T extends Comparable<T>, V extends T> BlockState findProperty(Property<T> property, V value);

    /**
     * This returns the block that this state belongs to
     *
     * @return the parent Block
     */
    Block getBlock();
}
