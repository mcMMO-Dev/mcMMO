package com.gmail.nossr50.util.blockmeta.chunkmeta;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.blockmeta.conversion.BlockStoreConversionZDirectory;
import com.google.common.collect.Sets;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HashChunkManager implements ChunkManager {
    private final Map<UUID, Map<Long, McMMOSimpleRegionFile>> regionFiles = new ConcurrentHashMap<>();
    public Map<String, ChunkStore> store = new ConcurrentHashMap<>();
    public Set<BlockStoreConversionZDirectory> converters = Sets.newConcurrentHashSet();
    private final Map<UUID, Boolean> oldData = new ConcurrentHashMap<>();
    private final LockManager lockManager = new LockManager();

    @Override
    public void closeAll() {
        for (UUID uid : regionFiles.keySet()) {
            Map<Long, McMMOSimpleRegionFile> worldRegions = regionFiles.get(uid);
            for (Iterator<McMMOSimpleRegionFile> worldRegionIterator = worldRegions.values().iterator(); worldRegionIterator.hasNext(); ) {
                McMMOSimpleRegionFile rf = worldRegionIterator.next();
                if (rf != null) {
                    rf.close();
                    worldRegionIterator.remove();
                }
            }
        }
        regionFiles.clear();
    }

    @Override
    public synchronized ChunkStore readChunkStore(World world, int x, int z) throws IOException {
        McMMOSimpleRegionFile rf = getSimpleRegionFile(world, x, z);
        InputStream in = rf.getInputStream(x, z);
        if (in == null) {
            return null;
        }
        try (ObjectInputStream objectStream = new ObjectInputStream(in)) {
            Object o = objectStream.readObject();
            if (o instanceof ChunkStore) {
                return (ChunkStore) o;
            }

            throw new RuntimeException("Wrong class type read for chunk meta data for " + x + ", " + z);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Assume the format changed
            return null;
            //throw new RuntimeException("Unable to process chunk meta data for " + x + ", " + z, e);
        }
    }

    @Override
    public CompletableFuture<ChunkStore> readChunkStoreAsync(World world, int x, int z) {
        return lockManager.supplyAsyncWithLock(getChunkKey(world, x, z), () -> {
            try {
                return readChunkStore(world, x, z);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to read chunk meta data for " + x + ", " + z, ex);
            }
        });
    }

    @Override
    public void writeChunkStore(World world, int x, int z, ChunkStore data) {
        if (!data.isDirty()) {
            return;
        }
        try {
            McMMOSimpleRegionFile rf = getSimpleRegionFile(world, x, z);
            ObjectOutputStream objectStream = new ObjectOutputStream(rf.getOutputStream(x, z));
            objectStream.writeObject(data);
            objectStream.flush();
            objectStream.close();
            data.setDirty(false);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to write chunk meta data for " + x + ", " + z, e);
        }
    }

    @Override
    public CompletableFuture<Void> writeChunkStoreAsync(World world, int x, int z, ChunkStore data) {
        return lockManager.runAsyncWithLock(getChunkKey(world, x, z), () -> writeChunkStore(world, x, z, data));
    }

    @Override
    public void closeChunkStore(World world, int x, int z) {
        McMMOSimpleRegionFile rf = getSimpleRegionFile(world, x, z);
        if (rf != null) {
            rf.close();
        }
    }

    @Override
    public CompletableFuture<Void> closeChunkStoreAsync(World world, int x, int z) {
        return lockManager.runAsyncWithLock(getChunkKey(world, x, z), () -> closeChunkStore(world, x, z));
    }

    private McMMOSimpleRegionFile getSimpleRegionFile(World world, int x, int z) {
        File directory = new File(world.getWorldFolder(), "mcmmo_regions");

        directory.mkdirs();

        UUID key = world.getUID();

        Map<Long, McMMOSimpleRegionFile> worldRegions = regionFiles.computeIfAbsent(key, k -> new HashMap<>());

        int rx = x >> 5;
        int rz = z >> 5;

        long key2 = (((long) rx) << 32) | ((rz) & 0xFFFFFFFFL);

        McMMOSimpleRegionFile regionFile = worldRegions.get(key2);

        if (regionFile == null) {
            File file = new File(directory, "mcmmo_" + rx + "_" + rz + "_.mcm");
            regionFile = new McMMOSimpleRegionFile(file, rx, rz);
            worldRegions.put(key2, regionFile);
        }

        return regionFile;
    }

    @Override
    public void loadChunklet(int cx, int cy, int cz, World world) {
        loadChunk(cx, cz, world, null);
    }

    @Override
    public CompletableFuture<Void> loadChunkletAsync(int cx, int cy, int cz, World world) {
        return lockManager.runAsyncWithLock(getChunkKey(world, cx, cz), () -> loadChunklet(cx, cy, cz, world));
    }

    @Override
    public void unloadChunklet(int cx, int cy, int cz, World world) {
        unloadChunk(cx, cz, world);
    }

    @Override
    public CompletableFuture<Void> unloadChunkletAsync(int cx, int cy, int cz, World world) {
        return lockManager.runAsyncWithLock(getChunkKey(world, cx, cz), () -> unloadChunklet(cx, cy, cz, world));
    }

    @Override
    public void loadChunk(int cx, int cz, World world, Entity[] entities) {
        if (world == null || store.containsKey(world.getName() + "," + cx + "," + cz)) {
            return;
        }

        UUID key = world.getUID();

        if (!oldData.containsKey(key)) {
            oldData.put(key, (new File(world.getWorldFolder(), "mcmmo_data")).exists());
        }
        else if (oldData.get(key)) {
            if (convertChunk(new File(world.getWorldFolder(), "mcmmo_data"), cx, cz, world, true)) {
                return;
            }
        }

        ChunkStore chunkStore = null;

        try {
            chunkStore = readChunkStore(world, cx, cz);
        }
        catch (Exception e) { e.printStackTrace(); }

        if (chunkStore == null) {
            return;
        }

        store.put(world.getName() + "," + cx + "," + cz, chunkStore);
    }

    @Override
    public CompletableFuture<Void> loadChunkAsync(int cx, int cz, World world, Entity[] entities) {
        String chunkKey = getChunkKey(world, cx, cz);
        return lockManager.runAsyncWithLock(chunkKey, () -> loadChunk(cx, cz, world, entities));
    }

    @Override
    public void unloadChunk(int cx, int cz, World world) {
        saveChunk(cx, cz, world);

        if (store.containsKey(world.getName() + "," + cx + "," + cz)) {
            store.remove(world.getName() + "," + cx + "," + cz);

            //closeChunkStore(world, cx, cz);
        }
    }

    @Override
    public CompletableFuture<Void> unloadChunkAsync(int cx, int cz, World world) {
        return lockManager.runAsyncWithLock(getChunkKey(world, cx, cz), () -> unloadChunk(cx, cz, world));
    }

    @Override
    public synchronized void saveChunk(int cx, int cz, World world) {
        if (world == null) {
            return;
        }

        String key = world.getName() + "," + cx + "," + cz;

        if (store.containsKey(key)) {
            ChunkStore out = store.get(world.getName() + "," + cx + "," + cz);

            if (!out.isDirty()) {
                return;
            }

            writeChunkStore(world, cx, cz, out);
        }
    }

    @Override
    public CompletableFuture<Void> saveChunkAsync(int cx, int cz, World world) {
        return lockManager.runAsyncWithLock(getChunkKey(world, cx, cz), () -> saveChunk(cx, cz, world));
    }

    @Override
    public boolean isChunkLoaded(int cx, int cz, World world) {
        if (world == null) {
            return false;
        }

        return store.containsKey(world.getName() + "," + cx + "," + cz);
    }

    @Override
    public void chunkLoaded(int cx, int cz, World world) {}

    @Override
    public CompletableFuture<Void> chunkLoadedAsync(int cx, int cz, World world) {
        return lockManager.runAsyncWithLock(getChunkKey(world, cx, cz), () -> chunkLoaded(cx, cz, world));
    }

    @Override
    public void chunkUnloaded(int cx, int cz, World world) {
        if (world == null) {
            return;
        }

        unloadChunk(cx, cz, world);
    }

    @Override
    public CompletableFuture<Void> chunkUnloadedAsync(int cx, int cz, World world) {
        return lockManager.runAsyncWithLock(getChunkKey(world, cx, cz), () -> chunkUnloaded(cx, cz, world));
    }

    @Override
    public void saveWorld(World world) {
        if (world == null) {
            return;
        }

        closeAll();
        String worldName = world.getName();

        List<String> keys = new ArrayList<>(store.keySet());
        for (String key : keys) {
            String[] info = key.split(",");
            if (worldName.equals(info[0])) {
                try {
                    saveChunk(Integer.parseInt(info[1]), Integer.parseInt(info[2]), world);
                }
                catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    @Override
    public synchronized void unloadWorld(World world) {
        if (world == null) {
            return;
        }

        String worldName = world.getName();

        List<String> keys = new ArrayList<>(store.keySet());
        for (String key : keys) {
            String[] info = key.split(",");
            if (worldName.equals(info[0])) {
                try {
                    unloadChunk(Integer.parseInt(info[1]), Integer.parseInt(info[2]), world);
                }
                catch (Exception e) {
                    // Ignore
                }
            }
        }
        closeAll();
    }

    @Override
    public synchronized void loadWorld(World world) {}

    @Override
    public synchronized void saveAll() {
        closeAll();

        for (World world : mcMMO.p.getServer().getWorlds()) {
            saveWorld(world);
        }
    }

    @Override
    public synchronized void unloadAll() {
        closeAll();

        for (World world : mcMMO.p.getServer().getWorlds()) {
            unloadWorld(world);
        }
    }

    @Override
    public boolean isTrue(int x, int y, int z, World world) {
        if (world == null) {
            return false;
        }

        int cx = x >> 4;
        int cz = z >> 4;

        String key = world.getName() + "," + cx + "," + cz;

        if (!store.containsKey(key)) {
            loadChunk(cx, cz, world, null);
        }

        if (!store.containsKey(key)) {
            return false;
        }

        ChunkStore check = store.get(key);
        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;

        return check.isTrue(ix, y, iz);
    }

    @Override
    public CompletableFuture<Boolean> isTrueAsync(int x, int y, int z, World world) {
        return lockManager.supplyAsyncWithLock(getBlockKey(world, x, y, z), () -> isTrue(x, y, z, world));
    }

    @Override
    public boolean isTrue(Block block) {
        if (block == null) {
            return false;
        }

        return isTrue(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public CompletableFuture<Boolean> isTrueAsync(Block block) {
        return lockManager.supplyAsyncWithLock(getBlockKey(block), () -> isTrue(block));
    }

    @Override
    public boolean isTrue(BlockState blockState) {
        if (blockState == null) {
            return false;
        }

        return isTrue(blockState.getX(), blockState.getY(), blockState.getZ(), blockState.getWorld());
    }

    @Override
    public CompletableFuture<Boolean> isTrueAsync(BlockState blockState) {
        return lockManager.supplyAsyncWithLock(getBlockKey(blockState), () -> isTrue(blockState));
    }

    @Override
    public void setTrue(int x, int y, int z, World world) {
        if (world == null) {
            return;
        }

        int cx = x >> 4;
        int cz = z >> 4;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;

        String key = world.getName() + "," + cx + "," + cz;

        if (!store.containsKey(key)) {
            loadChunk(cx, cz, world, null);
        }

        ChunkStore cStore = store.get(key);

        if (cStore == null) {
            cStore = ChunkStoreFactory.getChunkStore(world, cx, cz);
            store.put(key, cStore);
        }

        cStore.setTrue(ix, y, iz);
    }

    @Override
    public CompletableFuture<Void> setTrueAsync(int x, int y, int z, World world) {
        return lockManager.runAsyncWithLock(getBlockKey(world, x, y, z), () -> setTrue(x, y, z, world));
    }

    @Override
    public void setTrue(Block block) {
        if (block == null) {
            return;
        }

        setTrue(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public CompletableFuture<Void> setTrueAsync(Block block) {
        return lockManager.runAsyncWithLock(getBlockKey(block), () -> setTrue(block));
    }

    @Override
    public void setTrue(BlockState blockState) {
        if (blockState == null) {
            return;
        }

        setTrue(blockState.getX(), blockState.getY(), blockState.getZ(), blockState.getWorld());
    }

    @Override
    public CompletableFuture<Void> setTrueAsync(BlockState blockState) {
        return lockManager.runAsyncWithLock(getBlockKey(blockState), () -> setTrue(blockState));
    }

    @Override
    public void setFalse(int x, int y, int z, World world) {
        if (world == null) {
            return;
        }

        int cx = x >> 4;
        int cz = z >> 4;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;

        String key = world.getName() + "," + cx + "," + cz;

        if (!store.containsKey(key)) {
            loadChunk(cx, cz, world, null);
        }

        ChunkStore cStore = store.get(key);

        if (cStore == null) {
            return; // No need to make a store for something we will be setting to false
        }

        cStore.setFalse(ix, y, iz);
    }

    @Override
    public CompletableFuture<Void> setFalseAsync(int x, int y, int z, World world) {
        return lockManager.runAsyncWithLock(getBlockKey(world, x, y, z), () -> setFalse(x, y, z, world));
    }

    @Override
    public void setFalse(Block block) {
        if (block == null) {
            return;
        }

        setFalse(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public CompletableFuture<Void> setFalseAsync(Block block) {
        return lockManager.runAsyncWithLock(getBlockKey(block), () -> setFalse(block));
    }

    @Override
    public void setFalse(BlockState blockState) {
        if (blockState == null) {
            return;
        }

        setFalse(blockState.getX(), blockState.getY(), blockState.getZ(), blockState.getWorld());
    }

    @Override
    public CompletableFuture<Void> setFalseAsync(BlockState blockState) {
        return lockManager.runAsyncWithLock(getBlockKey(blockState), () -> setFalse(blockState));
    }

    @Override
    public synchronized void cleanUp() {}

    public synchronized void convertChunk(File dataDir, int cx, int cz, World world) {
        convertChunk(dataDir, cx, cz, world, false);
    }

    public synchronized boolean convertChunk(File dataDir, int cx, int cz, World world, boolean actually) {
        if (!actually || !dataDir.exists()) {
            return false;
        }

        File cxDir = new File(dataDir, "" + cx);
        if (!cxDir.exists()) {
            return false;
        }

        File czDir = new File(cxDir, "" + cz);
        if (!czDir.exists()) {
            return false;
        }

        boolean conversionSet = false;

        for (BlockStoreConversionZDirectory converter : this.converters) {
            if (converter == null) {
                continue;
            }

            if (converter.taskID >= 0) {
                continue;
            }

            converter.start(world, cxDir, czDir);
            conversionSet = true;
            break;
        }

        if (!conversionSet) {
            BlockStoreConversionZDirectory converter = new BlockStoreConversionZDirectory();
            converter.start(world, cxDir, czDir);
            converters.add(converter);
        }

        return true;
    }

    public static String getChunkKey(World world, int cx, int cz) {
        if (world == null) return UUID.randomUUID().toString();
        return world.getName() + "," + cx + "," + cz;
    }

    public static String getBlockKey(World world, int x, int y, int z) {
        if (world == null) return UUID.randomUUID().toString();
        return world.getName() + "," + x + "," + y + "," + z;
    }

    public static String getBlockKey(Block block) {
        return getBlockKey(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static String getBlockKey(BlockState state) {
        return getBlockKey(state.getWorld(), state.getX(), state.getY(), state.getZ());
    }
}
