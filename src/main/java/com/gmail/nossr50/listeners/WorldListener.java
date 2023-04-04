package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {
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
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getWorld()))
            return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(mcMMO.p, () -> {
            for (BlockState blockState : event.getBlocks()) {
                mcMMO.getPlaceStore().setFalse(blockState);
            }
        }, 1);
    }

    /**
     * Monitor WorldUnload events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getWorld()))
            return;

        mcMMO.getPlaceStore().unloadWorld(event.getWorld());
    }

    /**
     * Monitor ChunkUnload events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getWorld()))
            return;

        Chunk chunk = event.getChunk();

        mcMMO.getPlaceStore().chunkUnloaded(chunk.getX(), chunk.getZ(), event.getWorld());
    }
}
