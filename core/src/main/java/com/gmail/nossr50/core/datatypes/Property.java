package com.gmail.nossr50.core.datatypes;

import java.util.Collection;

/**
 * Properties are Comparable key value pairs for a blocks state
 * In MC this exists in three forms, Integer, Booleans, and Enums
 *
 * This class partially mirrors MC Internals
 *
 */
public interface Property<T extends Comparable<T>> {
    /**
     * The name of the Property
     * @return name of this property
     */
    String getName();

    /**
     * A collection of allowed values for this property
     * @return the allowed values for this property
     */
    Collection<T> getAllowedValues();

    /**
     * The class of the value for this particular property
     * @return the value's class
     */
    Class<T> getValueClass();

    /**
     * The name for a specific value
     * @param value the value to match
     * @return the name of this value
     */
    String getName(T value);
}
