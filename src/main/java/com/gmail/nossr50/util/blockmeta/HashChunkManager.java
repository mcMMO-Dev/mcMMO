package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.mcMMO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HashChunkManager implements ChunkManager {
    private final HashMap<CoordinateKey, McMMOSimpleRegionFile> regionMap = new HashMap<>(); // Tracks active regions
    private final HashMap<CoordinateKey, HashSet<CoordinateKey>> chunkUsageMap = new HashMap<>(); // Tracks active chunks by region
    private final HashMap<CoordinateKey, ChunkStore> chunkMap = new HashMap<>(); // Tracks active chunks

    @Override
    public synchronized void closeAll() {
        // Save all dirty chunkstores; one failing chunk must not abort the rest of shutdown
        for (ChunkStore chunkStore : chunkMap.values()) {
            if (!chunkStore.isDirty()) {
                continue;
            }
            World world = Bukkit.getWorld(chunkStore.getWorldId());
            if (world == null) {
                continue; // Oh well
            }
            try {
                writeChunkStore(world, chunkStore);
            } catch (Exception e) {
                logChunkSaveFailure(chunkStore.getChunkX(), chunkStore.getChunkZ(),
                        world.getName(), e);
            }
        }
        // Clear in memory chunks
        chunkMap.clear();
        chunkUsageMap.clear();
        // Close all region files
        for (McMMOSimpleRegionFile rf : regionMap.values()) {
            closeQuietly(rf);
        }
        regionMap.clear();
    }

    private static void logChunkSaveFailure(int cx, int cz, @NotNull String worldName,
            @NotNull Exception e) {
        mcMMO.p.getLogger().warning("Failed to save placed-block data for chunk (" + cx + ", "
                + cz + ") in world '" + worldName + "': " + e);
    }

    private static void closeQuietly(@Nullable McMMOSimpleRegionFile regionFile) {
        if (regionFile == null) {
            return;
        }
        try {
            regionFile.close();
        } catch (Exception e) {
            mcMMO.p.getLogger().warning("Failed to close placed-block region file: " + e);
        }
    }

    private synchronized @Nullable ChunkStore readChunkStore(@NotNull World world, int cx, int cz)
            throws IOException {
        final McMMOSimpleRegionFile rf = getWriteableSimpleRegionFile(world, cx, cz);
        try (DataInputStream in = rf.getInputStream(cx, cz)) { // Get input stream for chunk
            if (in == null) {
                return null; // No chunk
            }
            return BitSetChunkStore.Serialization.readChunkStore(in); // Read in the chunkstore
        }
    }

    private synchronized void writeChunkStore(@NotNull World world, @NotNull ChunkStore data) {
        if (!data.isDirty()) {
            return; // Don't save unchanged data
        }
        try {
            McMMOSimpleRegionFile rf = getWriteableSimpleRegionFile(world, data.getChunkX(),
                    data.getChunkZ());
            try (DataOutputStream out = rf.getOutputStream(data.getChunkX(), data.getChunkZ())) {
                BitSetChunkStore.Serialization.writeChunkStore(out, data);
            }
            data.setDirty(false);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to write chunk meta data for " + data.getChunkX() + ", "
                            + data.getChunkZ(), e);
        }
    }

    private synchronized @NotNull McMMOSimpleRegionFile getWriteableSimpleRegionFile(
            @NotNull World world, int cx, int cz) {
        CoordinateKey regionKey = toRegionKey(world.getUID(), cx, cz);

        return regionMap.computeIfAbsent(regionKey, k -> {
            File regionFile = getRegionFile(world, regionKey);
            regionFile.getParentFile().mkdirs();
            return new McMMOSimpleRegionFile(regionFile, regionKey.x(), regionKey.z());
        });
    }

    /**
     * Resolves the on-disk region file for a chunk's region.
     *
     * <p>Region files live inside the world folder at
     * {@code [worldFolder]/mcmmo_regions/mcmmo_[regionX]_[regionZ]_.mcm}, where
     * {@code worldFolder} is whatever {@link World#getWorldFolder()} returns on the running
     * server. On Spigot and pre-26.1 Paper this resolves to
     * {@code [container]/[worldName]/mcmmo_regions/}; on Paper 26.1+ (PaperMC/Paper PR #13736)
     * it resolves to
     * {@code [container]/[worldName]/dimensions/minecraft/<dim>/mcmmo_regions/}.
     *
     * <p>Because Paper's {@code LegacyCraftBukkitWorldMigration} runs before plugins load and
     * deletes the old per-world roots for non-overworld dimensions, mcMMO maintains a restore
     * store inside the mcMMO plugin data directory that is populated by
     * {@link McMMORegionBackupStore#backupWorld} on shutdown and replayed by
     * {@link McMMORegionBackupStore#restoreWorld} on the next startup if the in-world data has
     * been removed.
     */
    private @NotNull File getRegionFile(@NotNull World world, @NotNull CoordinateKey regionKey) {
        if (!world.getUID().equals(regionKey.worldID())) {
            throw new IllegalArgumentException(
                    "Region key world " + regionKey.worldID() + " does not match world "
                            + world.getUID());
        }
        final File worldRegionRoot = new File(world.getWorldFolder(),
                McMMORegionBackupStore.IN_WORLD_FOLDER_NAME);
        return new File(worldRegionRoot,
                "mcmmo_" + regionKey.x() + "_" + regionKey.z() + "_.mcm");
    }

    private @Nullable ChunkStore loadChunk(int cx, int cz, @NotNull World world) {
        try {
            return readChunkStore(world, cx, cz);
        } catch (Exception e) {
            mcMMO.p.getLogger().warning(
                    "Failed to read placed-block data for chunk (" + cx + ", " + cz
                            + ") in world '" + world.getName() + "', treating it as empty: " + e);
        }

        return null;
    }

    private void unloadChunk(int cx, int cz, @NotNull World world) {
        CoordinateKey chunkKey = toChunkKey(world.getUID(), cx, cz);
        ChunkStore chunkStore = chunkMap.remove(chunkKey); // Remove from chunk map
        if (chunkStore == null) {
            return;
        }

        try {
            if (chunkStore.isDirty()) {
                writeChunkStore(world, chunkStore);
            }
        } catch (Exception e) {
            // Log-and-degrade: a failed save must not escape into the chunk unload event or
            // strand the region bookkeeping below
            logChunkSaveFailure(cx, cz, world.getName(), e);
        } finally {
            CoordinateKey regionKey = toRegionKey(world.getUID(), cx, cz);
            HashSet<CoordinateKey> chunkKeys = chunkUsageMap.get(regionKey);
            if (chunkKeys != null) {
                chunkKeys.remove(chunkKey); // remove from region file in-use set
                // If it was the last chunk in the region, close the region file and forget it
                if (chunkKeys.isEmpty()) {
                    chunkUsageMap.remove(regionKey);
                    closeQuietly(regionMap.remove(regionKey));
                }
            }
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
            if (!wID.equals(chunkKey.worldID())) {
                continue;
            }
            ChunkStore chunkStore = chunkMap.remove(chunkKey);
            if (!chunkStore.isDirty()) {
                continue;
            }
            try {
                writeChunkStore(world, chunkStore);
            } catch (Exception e) {
                logChunkSaveFailure(chunkKey.x(), chunkKey.z(), world.getName(), e);
            }
        }
        // Clear all the region files
        List<CoordinateKey> regionKeys = new ArrayList<>(regionMap.keySet());
        for (CoordinateKey regionKey : regionKeys) {
            if (!wID.equals(regionKey.worldID())) {
                continue;
            }
            closeQuietly(regionMap.remove(regionKey));
            chunkUsageMap.remove(regionKey);
        }
    }

    /**
     * Gets the chunk store for the chunk, loading it from disk or creating a fresh one when
     * absent, and marks the chunk in-use for region file tracking.
     */
    private @NotNull ChunkStore getOrLoadChunkStore(@NotNull World world,
            @NotNull CoordinateKey chunkKey) {
        return chunkMap.computeIfAbsent(chunkKey, k -> {
            // Mark chunk in-use for region tracking
            chunkUsageMap.computeIfAbsent(
                    toRegionKey(chunkKey.worldID(), chunkKey.x(), chunkKey.z()),
                    j -> new HashSet<>()).add(chunkKey);
            // Load from file, or create a new chunkstore when the chunk has no stored data
            ChunkStore loaded = loadChunk(chunkKey.x(), chunkKey.z(), world);
            return loaded != null ? loaded
                    : new BitSetChunkStore(world, chunkKey.x(), chunkKey.z());
        });
    }

    /**
     * Maps a world coordinate to this plugin's chunk-local index.
     *
     * <p>The mirrored mapping for negative coordinates (Math.abs instead of a proper floor
     * modulo) is load-bearing for on-disk compatibility: every existing region file was written
     * with it. Changing it to the mathematically correct {@code coordinate & 0xF} would
     * silently corrupt the stored markers of every chunk with negative coordinates.
     */
    private static int toChunkLocal(int worldCoordinate) {
        return Math.abs(worldCoordinate) % 16;
    }

    private synchronized boolean isIneligible(int x, int y, int z, @NotNull World world) {
        CoordinateKey chunkKey = blockCoordinateToChunkKey(world.getUID(), x, y, z);
        ChunkStore check = getOrLoadChunkStore(world, chunkKey);

        return check.isTrue(toChunkLocal(x), y, toChunkLocal(z));
    }

    @Override
    public synchronized boolean isIneligible(@NotNull Block block) {
        return isIneligible(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public synchronized boolean isIneligible(@NotNull BlockState blockState) {
        return isIneligible(blockState.getX(), blockState.getY(), blockState.getZ(),
                blockState.getWorld());
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

    private synchronized void set(int x, int y, int z, @NotNull World world, boolean value) {
        CoordinateKey chunkKey = blockCoordinateToChunkKey(world.getUID(), x, y, z);
        ChunkStore cStore = getOrLoadChunkStore(world, chunkKey);

        cStore.set(toChunkLocal(x), y, toChunkLocal(z), value);
    }

    private @NotNull CoordinateKey blockCoordinateToChunkKey(@NotNull UUID worldUid, int x, int y,
            int z) {
        return toChunkKey(worldUid, x >> 4, z >> 4);
    }

    private @NotNull CoordinateKey toChunkKey(@NotNull UUID worldUid, int cx, int cz) {
        return new CoordinateKey(worldUid, cx, cz);
    }

    private @NotNull CoordinateKey toRegionKey(@NotNull UUID worldUid, int cx, int cz) {
        // Compute region index (32x32 chunk regions)
        int rx = cx >> 5;
        int rz = cz >> 5;
        return new CoordinateKey(worldUid, rx, rz);
    }

    private record CoordinateKey(@NotNull UUID worldID, int x, int z) {
    }
}
