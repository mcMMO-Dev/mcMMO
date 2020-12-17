package com.gmail.nossr50.listeners;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.layers.persistentdata.MobMetaFlagType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for(Entity entity : event.getChunk().getEntities()) {
            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if(mcMMO.getCompatibilityManager().getPersistentDataLayer().hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, livingEntity)) {

                    //Remove from existence
                    if(livingEntity.isValid()) {
                        mcMMO.getCompatibilityManager().getPersistentDataLayer().removeMobFlags(livingEntity);
                        livingEntity.setHealth(0);
                        livingEntity.remove();
                    }
                }
            }
        }
    }
}
