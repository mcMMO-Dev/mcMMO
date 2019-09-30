package com.gmail.nossr50.datatypes.items;

/**
 * The interface Defined match.
 *
 * @param <T> the type parameter
 */
public interface DefinedMatch<T> {

    /**
     * Determines whether or not this object of type T matches certain criteria of another object of type T
     * Behaviours for matching depend solely on the implementation of DefinedMatch
     *
     * @param other target item to compare itself to
     * @return true if this item matches the target item
     */
    boolean isMatch(T other);

}
