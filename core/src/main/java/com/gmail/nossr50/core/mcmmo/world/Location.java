package com.gmail.nossr50.core.mcmmo.world;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.nossr50.core.mcmmo.world.World;

/**
 * This class represents a Location in MC
 * Locations have a world and x, y, and z axis values
 */
public interface Location {

    /**
     * The Vector3d of this location
     * @return this vector
     */
    Vector3d getVector();

    /**
     * Returns the position of this location on the x-axis
     *
     * @return x-axis position
     */
    //double getX();

    /**
     * Returns the position of this location on the y-axis
     *
     * @return y-axis position
     */
    //double getY();

    /**
     * Returns the position of this location on the z-axis
     *
     * @return z-axis position
     */
    //double getZ();

    /**
     * The world for this Location
     *
     * @return the world of this location
     */
    World getWorld();
}
