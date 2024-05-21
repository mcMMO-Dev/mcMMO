package com.gmail.nossr50.util.blockmeta;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HashChunkManager implements ChunkManager {
    private final HashMap<CoordinateKey, McMMOSimpleRegionFile> regionMap = new HashMap<>(); // Tracks active regions
    private final HashMap<CoordinateKey, HashSet<CoordinateKey>> chunkUsageMap = new HashMap<>(); // Tracks active chunks by region
    private final HashMap<CoordinateKey, ChunkStore> chunkMap = new HashMap<>(); // Tracks active chunks

    @Override
    public synchronized void closeAll() {
        // Save all dirty chunkstores
        for (ChunkStore chunkStore : chunkMap.values()) {
            if (!chunkStore.isDirty())
                continue;
            World world = Bukkit.getWorld(chunkStore.getWorldId());
            if (world == null)
                continue; // Oh well
            writeChunkStore(world, chunkStore);
        }
        // Clear in memory chunks
        chunkMap.clear();
        chunkUsageMap.clear();
        // Close all region files
        for (McMMOSimpleRegionFile rf : regionMap.values())
            rf.close();
        regionMap.clear();
    }

    private synchronized @Nullable ChunkStore readChunkStore(@NotNull World world, int cx, int cz) throws IOException {
        McMMOSimpleRegionFile rf = getReadableSimpleRegionFile(world, cx, cz);
        if (rf == null)
            return null; // If there is no region file, there can't be a chunk
        try (DataInputStream in = rf.getInputStream(cx, cz)) { // Get input stream for chunk
            if (in == null)
                return null; // No chunk
            return BitSetChunkStore.Serialization.readChunkStore(in); // Read in the chunkstore
        }
    }

    private synchronized void writeChunkStore(@NotNull World world, @NotNull ChunkStore data) {
        if (!data.isDirty())
            return; // Don't save unchanged data
        try {
            McMMOSimpleRegionFile rf = getWriteableSimpleRegionFile(world, data.getChunkX(), data.getChunkZ());
            try (DataOutputStream out = rf.getOutputStream(data.getChunkX(), data.getChunkZ())) {
                BitSetChunkStore.Serialization.writeChunkStore(out, data);
            }
            data.setDirty(false);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to write chunk meta data for " + data.getChunkX() + ", " + data.getChunkZ(), e);
        }
    }

    private synchronized @NotNull McMMOSimpleRegionFile getWriteableSimpleRegionFile(@NotNull World world, int cx, int cz) {
        CoordinateKey regionKey = toRegionKey(world.getUID(), cx, cz);

        return regionMap.computeIfAbsent(regionKey, k -> {
            File regionFile = getRegionFile(world, regionKey);
            regionFile.getParentFile().mkdirs();
            return new McMMOSimpleRegionFile(regionFile, regionKey.x, regionKey.z);
        });
    }

    private synchronized @Nullable McMMOSimpleRegionFile getReadableSimpleRegionFile(@NotNull World world, int cx, int cz) {
        CoordinateKey regionKey = toRegionKey(world.getUID(), cx, cz);

        return regionMap.computeIfAbsent(regionKey, k -> {
            File regionFile = getRegionFile(world, regionKey);
            if (!regionFile.exists())
                return null; // Don't create the file on read-only operations
            return new McMMOSimpleRegionFile(regionFile, regionKey.x, regionKey.z);
        });
    }

    private @NotNull File getRegionFile(@NotNull World world, @NotNull CoordinateKey regionKey) {
        if (world.getUID() != regionKey.worldID)
            throw new IllegalArgumentException();
        return new File(new File(world.getWorldFolder(), "mcmmo_regions"), "mcmmo_" + regionKey.x + "_" + regionKey.z + "_.mcm");
    }

    private @Nullable ChunkStore loadChunk(int cx, int cz, @NotNull World world) {
        try {
            return readChunkStore(world, cx, cz);
        }
        catch (Exception ignored) {}

        return null;
    }

    private void unloadChunk(int cx, int cz, @NotNull World world) {
        CoordinateKey chunkKey = toChunkKey(world.getUID(), cx, cz);
        ChunkStore chunkStore = chunkMap.remove(chunkKey); // Remove from chunk map
        if (chunkStore == null)
            return;

        if (chunkStore.isDirty())
            writeChunkStore(world, chunkStore);

        CoordinateKey regionKey = toRegionKey(world.getUID(), cx, cz);
        HashSet<CoordinateKey> chunkKeys = chunkUsageMap.get(regionKey);
        chunkKeys.remove(chunkKey); // remove from region file in-use set
        // If it was last chunk in region, close the region file and remove it from memory
        if (chunkKeys.isEmpty()) {
            chunkUsageMap.remove(regionKey);
            regionMap.remove(regionKey).close();
        }
    }

    @Override
    public synchronized void chunkUnloaded(int cx, int cz, @NotNull World world) {
        unloadChunk(cx, cz, world);
    }

    @Override
    public synchronized void unloadWorld(@NotNull World world) {
        UUID wID = world.getUID();

        // Save and remove all the chunks
        List<CoordinateKey> chunkKeys = new ArrayList<>(chunkMap.keySet());
        for (CoordinateKey chunkKey : chunkKeys) {
            if (!wID.equals(chunkKey.worldID))
                continue;
            ChunkStore chunkStore = chunkMap.remove(chunkKey);
            if (!chunkStore.isDirty())
                continue;
            try {
                writeChunkStore(world, chunkStore);
            }
            catch (Exception ignore) { }
        }
        // Clear all the region files
        List<CoordinateKey> regionKeys = new ArrayList<>(regionMap.keySet());
        for (CoordinateKey regionKey : regionKeys) {
            if (!wID.equals(regionKey.worldID))
                continue;
            regionMap.remove(regionKey).close();
            chunkUsageMap.remove(regionKey);
        }
    }

    private synchronized boolean isIneligible(int x, int y, int z, @NotNull World world) {
        CoordinateKey chunkKey = blockCoordinateToChunkKey(world.getUID(), x, y, z);

        // Get chunk, load from file if necessary
        // Get/Load/Create chunkstore
        ChunkStore check = chunkMap.computeIfAbsent(chunkKey, k -> {
            // Load from file
            ChunkStore loaded = loadChunk(chunkKey.x, chunkKey.z, world);
            if (loaded == null)
                return null;
            // Mark chunk in-use for region tracking
            chunkUsageMap.computeIfAbsent(toRegionKey(chunkKey.worldID, chunkKey.x, chunkKey.z), j -> new HashSet<>()).add(chunkKey);
            return loaded;
        });

        // No chunk, return false
        if (check == null)
            return false;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;

        return check.isTrue(ix, y, iz);
    }

    @Override
    public synchronized boolean isIneligible(@NotNull Block block) {
        return isIneligible(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public synchronized boolean isIneligible(@NotNull BlockState blockState) {
        return isIneligible(blockState.getX(), blockState.getY(), blockState.getZ(), blockState.getWorld());
    }

    @Override
    public synchronized boolean isEligible(@NotNull Block block) {
        return !isIneligible(block);
    }

    @Override
    public synchronized boolean isEligible(@NotNull BlockState blockState) {
        return !isIneligible(blockState);
    }

    @Override
    public synchronized void setIneligible(@NotNull Block block) {
        set(block.getX(), block.getY(), block.getZ(), block.getWorld(), true);
    }

    @Override
    public synchronized void setIneligible(@NotNull BlockState blockState) {
        set(blockState.getX(), blockState.getY(), blockState.getZ(), blockState.getWorld(), true);
    }

    @Override
    public synchronized void setEligible(@NotNull Block block) {
        set(block.getX(), block.getY(), block.getZ(), block.getWorld(), false);
    }

    @Override
    public synchronized void setEligible(@NotNull BlockState blockState) {
        set(blockState.getX(), blockState.getY(), blockState.getZ(), blockState.getWorld(), false);
    }

    private synchronized void set(int x, int y, int z, @NotNull World world, boolean value){
        CoordinateKey chunkKey = blockCoordinateToChunkKey(world.getUID(), x, y, z);

        // Get/Load/Create chunkstore
        ChunkStore cStore = chunkMap.computeIfAbsent(chunkKey, k -> {
            // Load from file
            ChunkStore loaded = loadChunk(chunkKey.x, chunkKey.z, world);
            if (loaded != null) {
                chunkUsageMap.computeIfAbsent(toRegionKey(chunkKey.worldID, chunkKey.x, chunkKey.z), j -> new HashSet<>()).add(chunkKey);
                return loaded;
            }
            // If setting to false, no need to create an empty chunkstore
            if (!value)
                return null;
            // Mark chunk in-use for region tracking
            chunkUsageMap.computeIfAbsent(toRegionKey(chunkKey.worldID, chunkKey.x, chunkKey.z), j -> new HashSet<>()).add(chunkKey);
            // Create a new chunkstore
            return new BitSetChunkStore(world, chunkKey.x, chunkKey.z);
        });

        // Indicates setting false on empty chunkstore
        if (cStore == null)
            return;

        // Get block offset (offset from chunk corner)
        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;

        // Set chunk store value
        cStore.set(ix, y, iz, value);
    }

    private @NotNull CoordinateKey blockCoordinateToChunkKey(@NotNull UUID worldUid, int x, int y, int z) {
        return toChunkKey(worldUid, x >> 4, z >> 4);
    }

    private @NotNull CoordinateKey toChunkKey(@NotNull UUID worldUid, int cx, int cz){
        return new CoordinateKey(worldUid, cx, cz);
    }

    private @NotNull CoordinateKey toRegionKey(@NotNull UUID worldUid, int cx, int cz) {
        // Compute region index (32x32 chunk regions)
        int rx = cx >> 5;
        int rz = cz >> 5;
        return new CoordinateKey(worldUid, rx, rz);
    }

    private static final class CoordinateKey {
        public final @NotNull UUID worldID;
        public final int x;
        public final int z;

        private CoordinateKey(@NotNull UUID worldID, int x, int z) {
            this.worldID = worldID;
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoordinateKey coordinateKey = (CoordinateKey) o;
            return x == coordinateKey.x &&
                    z == coordinateKey.z &&
                    worldID.equals(coordinateKey.worldID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(worldID, x, z);
        }
    }
}
