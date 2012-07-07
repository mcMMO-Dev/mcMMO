package com.gmail.nossr50.listeners;

import java.io.File;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.gmail.nossr50.mcMMO;

public class WorldListener implements Listener {
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        File dataDir = new File(event.getWorld().getWorldFolder(), "mcmmo_data");
        if(!dataDir.exists()) {
            dataDir.mkdir();
        }
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
}
