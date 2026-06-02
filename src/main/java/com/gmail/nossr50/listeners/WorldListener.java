package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.blockmeta.McMMORegionBackupStore;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;
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
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld())) {
            return;
        }

        // Using 50 ms later as I do not know of a way to run one tick later (safely)
        plugin.getFoliaLib().getScheduler().runLater(() -> {
            for (BlockState blockState : event.getBlocks()) {
                mcMMO.getUserBlockTracker().setEligible(blockState);
            }
        }, 1);
    }

    /**
     * Restores mcMMO block-tracker data from the backup store for any world that loads after
     * plugin enable (Multiverse worlds, lazy-loaded dimensions). Only runs when Paper 26.1+ has
     * reshaped the world and the in-world {@code mcmmo_regions/} folder is empty; a no-op in
     * all other cases.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        if (!PersistentDataConfig.getInstance().useBlockTracker()
                || !plugin.getGeneralConfig().getRegionDataMigrationBackupsEnabled()) {
            return;
        }
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld())) {
            return;
        }
        McMMORegionBackupStore.restoreWorld(event.getWorld(), plugin.getLogger(),
                plugin.getDataFolder().toPath());
    }

    /**
     * Flushes chunk-store data for the unloading world and, on Spigot / pre-26.1 Paper layouts
     * (the "legacy shape"), writes a backup snapshot into the mcMMO plugin data directory so
     * the block-tracker data survives if a future Paper upgrade deletes the world's old folder
     * layout. Skipped for worlds already on the Paper 26.1+ layout and for blacklisted worlds.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld())) {
            return;
        }

        mcMMO.getChunkManager().unloadWorld(event.getWorld());

        if (PersistentDataConfig.getInstance().useBlockTracker()
            && plugin.getGeneralConfig().getRegionDataMigrationBackupsEnabled()) {
            McMMORegionBackupStore.backupWorld(event.getWorld(), plugin.getLogger(),
                    plugin.getDataFolder().toPath());
        }
    }

    /**
     * Monitor ChunkUnload events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getWorld())) {
            return;
        }

        Chunk chunk = event.getChunk();

        mcMMO.getChunkManager().chunkUnloaded(chunk.getX(), chunk.getZ(), event.getWorld());
    }
}
