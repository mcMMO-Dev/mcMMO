package com.gmail.nossr50.listeners;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = org.bukkit.event.EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        final Chunk unloadingChunk = event.getChunk();

        // Avoid processing if chunk is null or unloaded
        if (unloadingChunk == null || !unloadingChunk.isLoaded()) {
            return;
        }

        // Copies the entity list, so only fetch it once
        final Entity[] entities = unloadingChunk.getEntities();
        if (entities == null) {
            return;
        }

        try {
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    mcMMO.getTransientEntityTracker().removeTrackedEntity(livingEntity);
                }
            }
        } catch (Exception ex) {
            mcMMO.p.getLogger().warning(
                    "Caught exception during chunk unload event processing: " + ex);
        }
    }
}
