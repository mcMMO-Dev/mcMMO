package com.gmail.nossr50.listeners;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class ChunkListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        List<LivingEntity> matchingEntities
                = mcMMO.getTransientEntityTracker().getAllTransientEntitiesInChunk(event.getChunk());
        for(LivingEntity livingEntity : matchingEntities) {
            mcMMO.getTransientEntityTracker().removeSummon(livingEntity, null, false);
        }
    }
}
