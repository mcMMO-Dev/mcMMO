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
import com.gmail.nossr50.runnables.blockstoreconversion.BlockStoreConversionMain;
import com.gmail.nossr50.util.blockmeta.chunkmeta.HashChunkManager;

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
        mcMMO.p.placeStore.unloadWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        mcMMO.p.placeStore.saveWorld(event.getWorld());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        mcMMO.p.placeStore.chunkUnloaded(event.getChunk().getX(), event.getChunk().getZ(), event.getWorld());
    }
}
