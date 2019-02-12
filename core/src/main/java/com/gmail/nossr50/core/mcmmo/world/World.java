package com.gmail.nossr50.core.mcmmo.world;

import com.gmail.nossr50.core.mcmmo.Unique;

/**
 * Represents a world in MC
 */
public interface World extends Unique {
    /**
     * Gets the name of this World
     *
     * @return the name of this world
     */
    String getName();
}
