package com.gmail.nossr50.listeners;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.blockmeta.conversion.BlockStoreConversionMain;

public class WorldListener implements Listener {
    private ArrayList<BlockStoreConversionMain> converters = new ArrayList<BlockStoreConversionMain>();

    private final mcMMO plugin;

    public WorldListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor StructureGrow events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        Location location = event.getLocation();

        if (mcMMO.getPlaceStore().isTrue(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld())) {
            for (BlockState blockState : event.getBlocks()) {
                mcMMO.getPlaceStore().setFalse(blockState);
            }
        }
    }

    /**
     * Monitor WorldInit events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        File dataDir = new File(world.getWorldFolder(), "mcmmo_data");

        if (!dataDir.exists() || plugin == null) {
            return;
        }

        plugin.getLogger().info("Converting block storage for " + world.getName() + " to a new format.");

        BlockStoreConversionMain converter = new BlockStoreConversionMain(world);
        converter.run();
        converters.add(converter);
    }

    /**
     * Monitor WorldUnload events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        mcMMO.getPlaceStore().unloadWorld(event.getWorld());
    }

    /**
     * Monitor ChunkUnload events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        mcMMO.getPlaceStore().chunkUnloaded(chunk.getX(), chunk.getZ(), event.getWorld());
    }
}
