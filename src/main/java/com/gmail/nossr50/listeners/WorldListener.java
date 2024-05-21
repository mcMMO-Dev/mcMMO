package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.mcMMO;
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
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld()))
            return;

        // Using 50 ms later as I do not know of a way to run one tick later (safely)
        plugin.getFoliaLib().getImpl().runLater(() -> {
            for (BlockState blockState : event.getBlocks()) {
                mcMMO.getUserBlockTracker().setEligible(blockState);
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
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld()))
            return;

        mcMMO.getChunkManager().unloadWorld(event.getWorld());
    }

    /**
     * Monitor ChunkUnload events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld()))
            return;

        Chunk chunk = event.getChunk();

        mcMMO.getChunkManager().chunkUnloaded(chunk.getX(), chunk.getZ(), event.getWorld());
    }
}
