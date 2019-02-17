package com.gmail.nossr50.core.mcmmo.world;

import com.gmail.nossr50.core.mcmmo.Unique;

import java.io.File;

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

    /**
     * Gets the folder on disk for this world
     *
     * @return the folder on disk for this world
     */
    File getWorldFolder();

    /**
     * Gets the max height for this world
     * @return the max height
     */
    int getMaxHeight();
}
