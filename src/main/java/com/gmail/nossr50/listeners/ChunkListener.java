package com.gmail.nossr50.listeners;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Chunk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Arrays;

public class ChunkListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = org.bukkit.event.EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        final Chunk unloadingChunk = event.getChunk();

        Arrays.stream(unloadingChunk.getEntities())
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .forEach(livingEntity -> mcMMO.getTransientEntityTracker().removeTrackedEntity(livingEntity));
    }
}
