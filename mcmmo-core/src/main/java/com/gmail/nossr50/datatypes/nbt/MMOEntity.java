package com.gmail.nossr50.datatypes.nbt;

/**
 * Platform independent representation of a Entity in Minecraft
 * @param <T> the platform specific type of this Entity
 */
public interface MMOEntity<T> {
    /**
     * Get the platform specific implementation of this entity
     * @return the platform specific implementation of this entity
     */
    T getImplementation();
}
