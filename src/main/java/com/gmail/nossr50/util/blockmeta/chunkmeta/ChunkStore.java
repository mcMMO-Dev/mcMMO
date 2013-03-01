package com.gmail.nossr50.util.blockmeta.chunkmeta;

import java.io.Serializable;

import com.gmail.nossr50.util.blockmeta.ChunkletStore;

/**
 * A ChunkStore should be responsible for a 16x16xWorldHeight area of data
 */
public interface ChunkStore extends Serializable {
    /**
     * Checks the chunk's save state
     *
     * @return true if the has been modified since it was last saved
     */
    public boolean isDirty();

    /**
     * Checks the chunk's save state
     *
     * @param dirty the save state of the current chunk
     */
    public void setDirty(boolean dirty);

    /**
     * Checks the chunk's x coordinate
     *
     * @return the chunk's x coordinate.
     */
    public int getChunkX();

    /**
     * Checks the chunk's z coordinate
     *
     * @return the chunk's z coordinate.
     */
    public int getChunkZ();

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
