package com.gmail.nossr50.util.blockmeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class HashChunkletManager implements ChunkletManager {
    private HashMap<String, ChunkletStore> store = new HashMap<String, ChunkletStore>();

    public void chunkLoaded(int cx, int cz, World world) {
        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");
        File cxDir = new File(dataDir, "" + cx);
        if(!cxDir.exists()) return;
        File czDir = new File(cxDir, "" + cz);
        if(!czDir.exists()) return;

        for(int y = 1; y <= 4; y++) {
            File yFile = new File(czDir, "" + y);
            if(!yFile.exists()) {
                continue;
            } else {
                ChunkletStore in = deserializeChunkletStore(yFile);
                if(in != null) {
                	store.put(world.getName() + "," + cx + "," + cz + "," + y, in);
                }
            }
        }
    }

    public void chunkUnloaded(int cx, int cz, World world) {
        boolean found = false;

        for(String key : store.keySet()) {
            if(key.startsWith(world.getName() + "," + cx + "," + cz)) found = true;
        }

        if(!found) return;

        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");
        File cxDir = new File(dataDir, "" + cx);
        if(!cxDir.exists()) cxDir.mkdir();
        File czDir = new File(cxDir, "" + cz);
        if(!czDir.exists()) czDir.mkdir();

        for(int y = 1; y <= 4; y++) {
            File yFile = new File(czDir, "" + y);
            if(store.containsKey(world.getName() + "," + cx + "," + cz + "," + y)) {
                ChunkletStore out = store.get(world.getName() + "," + cx + "," + cz + "," + y);
                serializeChunkletStore(out, yFile);
            }
        }
    }

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

    public void unloadWorld(World world) {
    	saveWorld(world);

        String worldName = world.getName();

        for(String key : store.keySet()) {
        	String tempWorldName = key.split(",")[0];
        	if(tempWorldName.equals(worldName)) {
        	    store.remove(key);
        	}
        }
    }

    public void saveAll() {
    	for(World world : Bukkit.getWorlds()) {
    		saveWorld(world);
    	}
    }

    public void unloadAll() {
    	saveAll();
    	for(World world : Bukkit.getWorlds()) {
    		unloadWorld(world);
    	}
    }

    public boolean isTrue(int x, int y, int z, World world) {
        int cx = x / 16;
        int cz = z / 16;
        int cy = y / 64;
        if(!store.containsKey(world.getName() + "," + cx + "," + cz + "," + cy)) return false;

        ChunkletStore check = store.get(world.getName() + "," + cx + "," + cz + "," + cy);
        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;
        int iy = Math.abs(y) % 64;

        return check.isTrue(ix, iy, iz);
    }

    public boolean isTrue(Block block) {
        return isTrue(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    public void setTrue(int x, int y, int z, World world) {
        int cx = x / 16;
        int cz = z / 16;
        int cy = y / 64;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;
        int iy = Math.abs(y) % 64;

        ChunkletStore cStore;
        if(!store.containsKey(world.getName() + "," + cx + "," + cz + "," + cy)) {
            cStore = new PrimitiveChunkletStore();
            store.put(world.getName() + "," + cx + "," + cz + "," + cy, cStore);
        }

        cStore = store.get(world.getName() + "," + cx + "," + cz + "," + cy);
        cStore.setTrue(ix, iy, iz);
    }

    public void setTrue(Block block) {
        setTrue(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    public void setFalse(int x, int y, int z, World world) {
        int cx = x / 16;
        int cz = z / 16;
        int cy = y / 64;

        int ix = Math.abs(x) % 16;
        int iz = Math.abs(z) % 16;
        int iy = Math.abs(y) % 64;

        ChunkletStore cStore;
        if(!store.containsKey(world.getName() + "," + cx + "," + cz + "," + cy)) {
            return;	// No need to make a store for something we will be setting to false
        }

        cStore = store.get(world.getName() + "," + cx + "," + cz + "," + cy);
        cStore.setFalse(ix, iy, iz);
    }

    public void setFalse(Block block) {
        setFalse(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

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
    	try {
            FileOutputStream fileOut = new FileOutputStream(location);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(cStore);
            objOut.close();
            fileOut.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param location Where on the disk to read from
     * @return ChunkletStore from the specified location
     */
    private ChunkletStore deserializeChunkletStore(File location) {
        ChunkletStore storeIn = null;

        try {
            FileInputStream fileIn = new FileInputStream(location);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            storeIn = (ChunkletStore) objIn.readObject();
            objIn.close();
            fileIn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

        return storeIn;
    }
}
