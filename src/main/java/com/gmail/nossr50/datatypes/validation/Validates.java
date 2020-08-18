package com.gmail.nossr50.datatypes.validation;

public interface Validates<T> {
    /**
     * Perform a validation check on this object
     * Returns the value if validation passes
     * @return the object
     * @throws Exception if the object fails validation
     * @param object
     */
    T validate(T object) throws Exception;
}
