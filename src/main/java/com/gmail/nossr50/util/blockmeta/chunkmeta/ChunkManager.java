package com.gmail.nossr50.util.blockmeta.chunkmeta;

import java.io.IOException;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public interface ChunkManager {
    public void closeAll();

    public ChunkStore readChunkStore(World world, int x, int z) throws IOException;

    public void writeChunkStore(World world, int x, int z, ChunkStore data);

    public void closeChunkStore(World world, int x, int z);

    /**
     * Loads a specific chunklet
     *
     * @param cx Chunklet X coordinate that needs to be loaded
     * @param cy Chunklet Y coordinate that needs to be loaded
     * @param cz Chunklet Z coordinate that needs to be loaded
     * @param world World that the chunklet needs to be loaded in
     */
    public void loadChunklet(int cx, int cy, int cz, World world);

    /**
     * Unload a specific chunklet
     *
     * @param cx Chunklet X coordinate that needs to be unloaded
     * @param cy Chunklet Y coordinate that needs to be unloaded
     * @param cz Chunklet Z coordinate that needs to be unloaded
     * @param world World that the chunklet needs to be unloaded from
     */
    public void unloadChunklet(int cx, int cy, int cz, World world);

    /**
     * Load a given Chunk's Chunklet data
     *
     * @param cx Chunk X coordinate that is to be loaded
     * @param cz Chunk Z coordinate that is to be loaded
     * @param world World that the Chunk is in
     */
    public void loadChunk(int cx, int cz, World world, Entity[] entities);

    /**
     * Unload a given Chunk's Chunklet data
     *
     * @param cx Chunk X coordinate that is to be unloaded
     * @param cz Chunk Z coordinate that is to be unloaded
     * @param world World that the Chunk is in
     */
    public void unloadChunk(int cx, int cz, World world);

    /**
     * Saves a given Chunk's Chunklet data
     *
     * @param cx Chunk X coordinate that is to be saved
     * @param cz Chunk Z coordinate that is to be saved
     * @param world World that the Chunk is in
     */
    public void saveChunk(int cx, int cz, World world);

    public boolean isChunkLoaded(int cx, int cz, World world);

    /**
     * Informs the ChunkletManager a chunk is loaded
     *
     * @param cx Chunk X coordinate that is loaded
     * @param cz Chunk Z coordinate that is loaded
     * @param world World that the chunk was loaded in
     */
    public void chunkLoaded(int cx, int cz, World world);

    /**
     * Informs the ChunkletManager a chunk is unloaded
     *
     * @param cx Chunk X coordinate that is unloaded
     * @param cz Chunk Z coordinate that is unloaded
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
     * Load all ChunkletStores from all loaded chunks from this world into memory
     *
     * @param world World to load
     */
    public void loadWorld(World world);

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
     * Check to see if a given BlockState location is set to true
     *
     * @param location BlockState location to check
     * @return true if the given BlockState location is set to true, false if otherwise
     */
    public boolean isTrue(BlockState blockState);

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
     * Set a given BlockState location to true, should create stores as necessary if the location does not exist
     *
     * @param block BlockState location to set
     */
    public void setTrue(BlockState blockState);

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
     * Set a given BlockState location to false, should not create stores if one does not exist for the given location
     *
     * @param block BlockState location to set
     */
    public void setFalse(BlockState blockState);

    /**
     * Delete any ChunkletStores that are empty
     */
    public void cleanUp();
}
