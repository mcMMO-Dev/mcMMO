package com.gmail.nossr50.util.blockmeta;

import java.io.Serializable;

/**
 * A ChunkletStore should be responsible for a 16x16x64 area of data
 */
public interface ChunkletStore extends Serializable {
    /**
     * Checks the value at the given coordinates
     *
     * @param x x coordinate in current chunklet
     * @param y y coordinate in current chunklet
     * @param z z coordinate in current chunklet
     * @return true if the value is true at the given coordinates, false if otherwise
     */
    public boolean isTrue(int x, int y, int z);

    /**
     * Set the value to true at the given coordinates
     *
     * @param x x coordinate in current chunklet
     * @param y y coordinate in current chunklet
     * @param z z coordinate in current chunklet
     */
    public void setTrue(int x, int y, int z);

    /**
     * Set the value to false at the given coordinates
     *
     * @param x x coordinate in current chunklet
     * @param y y coordinate in current chunklet
     * @param z z coordinate in current chunklet
     */
    public void setFalse(int x, int y, int z);

    /**
     * @return true if all values in the chunklet are false, false if otherwise
     */
    public boolean isEmpty();

    /**
     * Set all values in this ChunkletStore to the values from another provided ChunkletStore
     *
     * @param otherStore Another ChunkletStore that this one should copy all data from
     */
    public void copyFrom(ChunkletStore otherStore);
}
