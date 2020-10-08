package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.layers.persistentdata.SpigotPersistentDataLayer_1_13;
import org.bukkit.entity.LivingEntity;

public class TransientMetadataTools {
    private final mcMMO pluginRef;

    public TransientMetadataTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void cleanAllMobMetadata(LivingEntity livingEntity) {
        //Since its not written anywhere, apparently the GC won't touch objects with metadata still present on them
        if (livingEntity.hasMetadata(mcMMO.customNameKey)) {
            livingEntity.setCustomName(livingEntity.getMetadata(mcMMO.customNameKey).get(0).asString());
            livingEntity.removeMetadata(mcMMO.customNameKey, pluginRef);
        }

        //Involved in changing mob names to hearts
        if (livingEntity.hasMetadata(mcMMO.customVisibleKey)) {
            livingEntity.setCustomNameVisible(livingEntity.getMetadata(mcMMO.customVisibleKey).get(0).asBoolean());
            livingEntity.removeMetadata(mcMMO.customVisibleKey, pluginRef);
        }

        //Gets assigned to endermen, potentially doesn't get cleared before this point
        if(livingEntity.hasMetadata(mcMMO.travelingBlock)) {
            livingEntity.removeMetadata(mcMMO.travelingBlock, pluginRef);
        }

        //1.13.2 uses transient mob flags and needs to be cleaned up
        if(mcMMO.getCompatibilityManager().getPersistentDataLayer() instanceof SpigotPersistentDataLayer_1_13) {
            mcMMO.getCompatibilityManager().getPersistentDataLayer().removeMobFlags(livingEntity);
        }
    }
}
