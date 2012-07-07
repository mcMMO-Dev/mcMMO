package com.gmail.nossr50.util.blockmeta;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UTFDataFormatException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.gmail.nossr50.mcMMO;

public class HashChunkletManager implements ChunkletManager {
    private HashMap<String, ChunkletStore> store = new HashMap<String, ChunkletStore>();

    @Override
    public void loadChunklet(int cx, int cy, int cz, World world) {
        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");
        File cxDir = new File(dataDir, "" + cx);
        if(!cxDir.exists()) return;
        File czDir = new File(cxDir, "" + cz);
        if(!czDir.exists()) return;
        File yFile = new File(czDir, "" + cy);
        if(!yFile.exists()) return;

        ChunkletStore in = deserializeChunkletStore(yFile);
        if(in != null) {
            store.put(world.getName() + "," + cx + "," + cz + "," + cy, in);
        }
    }

    @Override
    public void chunkLoaded(int cx, int cz, World world) {
        //File dataDir = new File(world.getWorldFolder(), "mcmmo_data");
        //File cxDir = new File(dataDir, "" + cx);
        //if(!cxDir.exists()) return;
        //File czDir = new File(cxDir, "" + cz);
        //if(!czDir.exists()) return;

        //for(int y = 0; y < 4; y++) {
        //    File yFile = new File(czDir, "" + y);
        //    if(!yFile.exists()) {
        //        continue;
        //    } else {
        //        ChunkletStore in = deserializeChunkletStore(yFile);
        //        if(in != null) {
        //            store.put(world.getName() + "," + cx + "," + cz + "," + y, in);
        //        }
        //    }
        //}
    }

    @Override
    public void chunkUnloaded(int cx, int cz, World world) {
        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");

        for(int y = 0; y < 4; y++) {
            if(store.containsKey(world.getName() + "," + cx + "," + cz + "," + y)) {
                File cxDir = new File(dataDir, "" + cx);
                if(!cxDir.exists()) cxDir.mkdir();
                File czDir = new File(cxDir, "" + cz);
                if(!czDir.exists()) czDir.mkdir();
                File yFile = new File(czDir, "" + y);

                ChunkletStore out = store.get(world.getName() + "," + cx + "," + cz + "," + y);
                serializeChunkletStore(out, yFile);
                store.remove(world.getName() + "," + cx + "," + cz + "," + y);
            }
        }
    }

    @Override
    public void saveWorld(World world) {
        String worldName = world.getName();
        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");

        for(String key : store.keySet()) {
            String[] info = key.split(",");
            if(worldName.equals(info[0])) {
                File cxDir = new File(dataDir, "" + info[1]);
                if(!cxDir.exists()) cxDir.mkdir();
                File czDir = new File(cxDir, "" + info[2]);
                if(!czDir.exists()) czDir.mkdir();

                File yFile = new File(czDir, "" + info[3]);
                serializeChunkletStore(store.get(key), yFile);
            }
        }
    }

    @Override
    public void unloadWorld(World world) {
        saveWorld(world);

        String worldName = world.getName();

        for(String key : store.keySet()) {
            String tempWorldName = key.split(",")[0];
            if(tempWorldName.equals(worldName)) {
                store.remove(key);
                return;
            }
        }
    }

    @Override
    public void loadWorld(World world) {
        //for(Chunk chunk : world.getLoadedChunks()) {
        //    this.chunkLoaded(chunk.getX(), chunk.getZ(), world);
        //}
    }

    @Override
    public void saveAll() {
        for(World world : Bukkit.getWorlds()) {
            saveWorld(world);
        }
    }

    @Override
    public void unloadAll() {
        saveAll();
        for(World world : Bukkit.getWorlds()) {
            unloadWorld(world);
        }
    }

    @Override
    public boolean isTrue(int x, int y, int z, World world) {
        int cx = x / 16;
        int cz = z / 16;
        int cy = y / 64;
        String key = world.getName() + "," + cx + "," + cz + "," + cy;

        if (!store.containsKey(key)) {
            loadChunklet(cx, cy, cz, world);
        }

        if (!store.containsKey(key)) {
            return false;
        }

        ChunkletStore check = store.get(world.getName() + "," + cx + "," + cz + "," + cy);
        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;
        int iy = Math.abs(y) % 64;

        return check.isTrue(ix, iy, iz);
    }

    @Override
    public boolean isTrue(Block block) {
        return isTrue(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public void setTrue(int x, int y, int z, World world) {
        int cx = x / 16;
        int cz = z / 16;
        int cy = y / 64;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;
        int iy = Math.abs(y) % 64;

        String key = world.getName() + "," + cx + "," + cz + "," + cy;

        if (!store.containsKey(key)) {
            loadChunklet(cx, cy, cz, world);
        }

        ChunkletStore cStore = store.get(key);

        if (cStore == null) {
            cStore = ChunkletStoreFactory.getChunkletStore();

            store.put(world.getName() + "," + cx + "," + cz + "," + cy, cStore);
        }

        cStore.setTrue(ix, iy, iz);
    }

    @Override
    public void setTrue(Block block) {
        setTrue(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public void setFalse(int x, int y, int z, World world) {
        int cx = x / 16;
        int cz = z / 16;
        int cy = y / 64;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;
        int iy = Math.abs(y) % 64;

        String key = world.getName() + "," + cx + "," + cz + "," + cy;

        if (!store.containsKey(key)) {
            loadChunklet(cx, cy, cz, world);
        }

        ChunkletStore cStore = store.get(key);

        if (cStore == null) {
            return; //No need to make a store for something we will be setting to false
        }

        cStore.setFalse(ix, iy, iz);
    }

    @Override
    public void setFalse(Block block) {
        setFalse(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @Override
    public void cleanUp() {
        for(String key : store.keySet()) {
            if(store.get(key).isEmpty()) {
                String[] info = key.split(",");
                File dataDir = new File(Bukkit.getWorld(info[0]).getWorldFolder(), "mcmmo_data");

                File cxDir = new File(dataDir, "" + info[1]);
                if(!cxDir.exists()) continue;
                File czDir = new File(cxDir, "" + info[2]);
                if(!czDir.exists()) continue;

                File yFile = new File(czDir, "" + info[3]);
                yFile.delete();

                //Delete empty directories
                if(czDir.list().length == 0) czDir.delete();
                if(cxDir.list().length == 0) cxDir.delete();
            }
        }
    }

    /**
     * @param cStore ChunkletStore to save
     * @param location Where on the disk to put it
     */
    private void serializeChunkletStore(ChunkletStore cStore, File location) {
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;

        try {
            fileOut = new FileOutputStream(location);
            objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(cStore);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            if (objOut != null) {
                try {
                    objOut.flush();
                    objOut.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (fileOut != null) {
                try {
                    fileOut.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @param location Where on the disk to read from
     * @return ChunkletStore from the specified location
     */
    private ChunkletStore deserializeChunkletStore(File location) {
        ChunkletStore storeIn = null;
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;

        try {
            fileIn = new FileInputStream(location);
            objIn = new ObjectInputStream(fileIn);
            storeIn = (ChunkletStore) objIn.readObject();
        }
        catch (IOException ex) {
            if (ex instanceof EOFException) {
                // EOF should only happen on Chunklets that somehow have been corrupted.
                mcMMO.p.getLogger().severe("Chunklet data at " + location.toString() + " could not be read due to an EOFException, data in this area will be lost.");
                return ChunkletStoreFactory.getChunkletStore();
            }
            else if (ex instanceof StreamCorruptedException) {
                // StreamCorrupted happens when the Chunklet is no good.
                mcMMO.p.getLogger().severe("Chunklet data at " + location.toString() + " is corrupted, data in this area will be lost.");
                return ChunkletStoreFactory.getChunkletStore();
            }
            else if (ex instanceof UTFDataFormatException) {
                // UTF happens when the Chunklet cannot be read or is corrupted
                mcMMO.p.getLogger().severe("Chunklet data at " + location.toString() + " could not be read due to an UTFDataFormatException, data in this area will be lost.");
                return ChunkletStoreFactory.getChunkletStore();
            }

            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        finally {
            if (objIn != null) {
                try {
                    objIn.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (fileIn != null) {
                try {
                    fileIn.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // TODO: Make this less messy, as it is, it's kinda... depressing to do it like this.
        // Might also make a mess when we move to stacks, but at that point I think I will write a new Manager...
        // IMPORTANT! If ChunkletStoreFactory is going to be returning something other than PrimitiveEx we need to remove this, as it will be breaking time for old maps
        if(!(storeIn instanceof PrimitiveExChunkletStore)) {
            ChunkletStore tempStore = ChunkletStoreFactory.getChunkletStore();
            tempStore.copyFrom(storeIn);
            storeIn = tempStore;
        }

        return storeIn;
    }
}
