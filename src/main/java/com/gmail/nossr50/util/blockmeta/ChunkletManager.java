package com.gmail.nossr50.util.blockmeta;

import org.bukkit.World;
import org.bukkit.block.Block;

public interface ChunkletManager {
    /**
     * Informs the ChunkletManager a chunk is loaded, it should load appropriate data
     *
     * @param cx Chunk X coordiate that is loaded
     * @param cz Chunk Z coordiate that is loaded
     * @param world World that the chunk was loaded in
     */
    public void chunkLoaded(int cx, int cz, World world);

    /**
     * Informs the ChunkletManager a chunk is unloaded, it should unload and save appropriate data
     *
     * @param cx Chunk X coordiate that is unloaded
     * @param cz Chunk Z coordiate that is unloaded
     * @param world World that the chunk was unloaded in
     */
    public void chunkUnloaded(int cx, int cz, World world);

    /**
     * Save all ChunkletStores related to the given world
     *
     * @param world World to save
     */
    public void saveWorld(World world);

    /**
     * Unload all ChunkletStores from memory related to the given world after saving them
     *
     * @param world World to unload
     */
    public void unloadWorld(World world);

    /**
     * Save all ChunkletStores
     */
    public void saveAll();

    /**
     * Unload all ChunkletStores after saving them
     */
    public void unloadAll();

    /**
     * Check to see if a given location is set to true
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @param z Z coordinate to check
     * @param world World to check in
     * @return true if the given location is set to true, false if otherwise
     */
    public boolean isTrue(int x, int y, int z, World world);

    /**
     * Check to see if a given block location is set to true
     *
     * @param block Block location to check
     * @return true if the given block location is set to true, false if otherwise
     */
    public boolean isTrue(Block block);

    /**
     * Set a given location to true, should create stores as necessary if the location does not exist
     *
     * @param x X coordinate to set
     * @param y Y coordinate to set
     * @param z Z coordinate to set
     * @param world World to set in
     */
    public void setTrue(int x, int y, int z, World world);

    /**
     * Set a given block location to true, should create stores as necessary if the location does not exist
     *
     * @param block Block location to set
     */
    public void setTrue(Block block);

    /**
     * Set a given location to false, should not create stores if one does not exist for the given location
     *
     * @param x X coordinate to set
     * @param y Y coordinate to set
     * @param z Z coordinate to set
     * @param world World to set in
     */
    public void setFalse(int x, int y, int z, World world);

    /**
     * Set a given block location to false, should not create stores if one does not exist for the given location
     *
     * @param block Block location to set
     */
    public void setFalse(Block block);

    /**
     * Delete any ChunkletStores that are empty
     */
    public void cleanUp();
}
