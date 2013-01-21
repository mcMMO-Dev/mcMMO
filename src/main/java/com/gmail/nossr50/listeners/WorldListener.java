package com.gmail.nossr50.listeners;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.blockstoreconversion.BlockStoreConversionMain;

public class WorldListener implements Listener {
    ArrayList<BlockStoreConversionMain> converters = new ArrayList<BlockStoreConversionMain>();

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        TreeType species = event.getSpecies();

        if (species == TreeType.BROWN_MUSHROOM || species == TreeType.RED_MUSHROOM) {
            return;
        }

        if (mcMMO.placeStore.isTrue(event.getLocation().getBlock())) {
            for (BlockState block : event.getBlocks()) {
                mcMMO.placeStore.setFalse(block.getBlock());
            }   
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();

        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");
        if (!dataDir.exists()) {
            return;
        }

        if (mcMMO.p == null)
            return;

        mcMMO.p.getLogger().info("Converting block storage for " + world.getName() + " to a new format.");
        BlockStoreConversionMain converter = new BlockStoreConversionMain(world);
        converter.run();
        converters.add(converter);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        mcMMO.placeStore.unloadWorld(event.getWorld());
    }

    // This gets called every 45 seconds, by default.
    // The call can and does result in excessive lag, especially on larger servers.
    //@EventHandler
    //public void onWorldSave(WorldSaveEvent event) {
    //    mcMMO.placeStore.saveWorld(event.getWorld());
    //}

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        mcMMO.placeStore.chunkUnloaded(chunk.getX(), chunk.getZ(), event.getWorld());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Entity[] entities = chunk.getEntities();

        for(Entity entity : entities) {
            if(!(entity instanceof LivingEntity) && !(entity instanceof FallingBlock))
                continue;

            mcMMO.placeStore.loadChunk(chunk.getX(), chunk.getZ(), event.getWorld(), entities);
            return;
        }
    }
}
