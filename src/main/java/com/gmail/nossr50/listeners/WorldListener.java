package com.gmail.nossr50.listeners;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.World;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.BlockStoreConversionMain;
import com.gmail.nossr50.util.blockmeta.ChunkletStore;
import com.gmail.nossr50.util.blockmeta.PrimitiveChunkletStore;
import com.gmail.nossr50.util.blockmeta.PrimitiveExChunkletStore;
import com.gmail.nossr50.util.blockmeta.PrimitiveChunkStore;
import com.gmail.nossr50.util.blockmeta.HashChunkletManager;
import com.gmail.nossr50.util.blockmeta.HashChunkManager;

public class WorldListener implements Listener {
    ArrayList<BlockStoreConversionMain> converters = new ArrayList<BlockStoreConversionMain>();

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        File dataDir = new File(event.getWorld().getWorldFolder(), "mcmmo_data");
        if(!dataDir.exists()) {
            return;
        }

        if(mcMMO.p == null)
            return;

        mcMMO.p.getLogger().info("Converting block storage for " + event.getWorld().getName() + " to a new format.");
        BlockStoreConversionMain converter = new BlockStoreConversionMain(event.getWorld());
        converter.run();
        converters.add(converter);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        mcMMO.placeStore.unloadWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        mcMMO.placeStore.saveWorld(event.getWorld());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        mcMMO.placeStore.chunkUnloaded(event.getChunk().getX(), event.getChunk().getZ(), event.getWorld());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
	File dataDir = new File(event.getChunk().getWorld().getWorldFolder(), "mcmmo_data");

        if(!dataDir.exists() || !dataDir.isDirectory()) {
            return;
        }

        World world = event.getChunk().getWorld();
        int cx = event.getChunk().getX();
        int cz = event.getChunk().getZ();
        HashChunkletManager manager = new HashChunkletManager();
        HashChunkManager newManager = (HashChunkManager) mcMMO.p.placeStore;

        manager.loadChunk(cx, cz, world);

        for(int y = 0; y < (world.getMaxHeight() / 64); y++) {
            String chunkletName = world.getName() + "," + cx + "," + cz + "," + y;
	    ChunkletStore tempChunklet = manager.store.get(chunkletName);
            PrimitiveChunkletStore primitiveChunklet = null;
            PrimitiveExChunkletStore primitiveExChunklet = null;
            if(tempChunklet instanceof PrimitiveChunkletStore)
                primitiveChunklet = (PrimitiveChunkletStore) tempChunklet;
            else if(tempChunklet instanceof PrimitiveExChunkletStore)
                primitiveExChunklet = (PrimitiveExChunkletStore) tempChunklet;
            if(tempChunklet == null) {
                continue;
            } else {
                String chunkName = world.getName() + "," + cx + "," + cz;
                PrimitiveChunkStore cChunk = (PrimitiveChunkStore) newManager.store.get(chunkName);

                if(cChunk != null) {
                    int xPos = cx * 16;
                    int zPos = cz * 16;

                    for(int x = 0; x < 16; x++) {
                        for(int z = 0; z < 16; z++) {
                            int cxPos = xPos + x;
                            int czPos = zPos + z;

                            for(int y2 = (64 * y); y2 < (64 * y + 64); y2++) {
                                if(!manager.isTrue(cxPos, y2, czPos, world))
                                    continue;

                                newManager.setTrue(cxPos, y2, czPos, world);
                            }
                        }
                    }
                    continue;
                }

                newManager.setTrue(cx * 16, 0, cz * 16, world);
		newManager.setFalse(cx * 16, 0, cz * 16, world);
                cChunk = (PrimitiveChunkStore) newManager.store.get(chunkName);

                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        boolean[] oldArray;
                        if(primitiveChunklet != null)
                            oldArray = primitiveChunklet.store[x][z];
                        if(primitiveExChunklet != null)
                            oldArray = primitiveExChunklet.store[x][z];
                        else
                            return;
                        boolean[] newArray = cChunk.store[x][z];
                        if(oldArray.length < 64)
                            return;
                        else if(newArray.length < ((y * 64) + 64))
                            return;
                        System.arraycopy(oldArray, 0, newArray, (y * 64), 64);
                    }
                }
            }
        }

        manager.unloadChunk(cx, cz, world);
        newManager.unloadChunk(cx, cz, world);

        File cxDir = new File(dataDir, "" + cx);
        if(!cxDir.exists()) return;
        File czDir = new File(cxDir, "" + cz);
        if(!czDir.exists()) return;

        for(File yFile : czDir.listFiles()) {
            if(!yFile.exists())
                continue;

            yFile.delete();
        }

        if(czDir.listFiles().length <= 0)
            czDir.delete();
        if(cxDir.listFiles().length <= 0)
            cxDir.delete();
        if(dataDir.listFiles().length <= 0)
            dataDir.delete();
    }
}
