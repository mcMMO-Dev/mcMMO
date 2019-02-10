package com.gmail.nossr50.datatypes;

/**
 * This class represents a Location in MC
 * Locations have a world and x, y, and z axis values
 */
public interface Location {

    /**
     * Returns the position of this location on the x-axis
     * @return x-axis position
     */
    double getX();

    /**
     * Returns the position of this location on the y-axis
     * @return y-axis position
     */
    double getY();

    /**
     * Returns the position of this location on the z-axis
     * @return z-axis position
     */
    double getZ();

    /**
     * The world for this Location
     * @return the world of this location
     */
    World getWorld();
}
