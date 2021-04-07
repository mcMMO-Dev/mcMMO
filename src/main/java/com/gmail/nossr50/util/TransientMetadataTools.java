package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class TransientMetadataTools {
    public static final String OLD_NAME_METAKEY = TransientMetadataTools.OLD_NAME_METAKEY;
    private final mcMMO pluginRef;

    public TransientMetadataTools(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void cleanAllLivingEntityMetadata(@NotNull LivingEntity livingEntity) {
        //Since its not written anywhere, apparently the GC won't touch objects with metadata still present on them
        if (livingEntity.hasMetadata(mcMMO.customNameKey)) {
            livingEntity.setCustomName(livingEntity.getMetadata(mcMMO.customNameKey).get(0).asString());
            livingEntity.removeMetadata(mcMMO.customNameKey, pluginRef);
        }

        if(livingEntity.hasMetadata(OLD_NAME_METAKEY)) {
            livingEntity.removeMetadata(OLD_NAME_METAKEY, pluginRef);
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

        if(livingEntity.hasMetadata(mcMMO.RUPTURE_META_KEY)) {
            livingEntity.removeMetadata(mcMMO.RUPTURE_META_KEY, pluginRef);
        }

        if(livingEntity.hasMetadata(mcMMO.EXPLOSION_FROM_RUPTURE)) {
            livingEntity.removeMetadata(mcMMO.EXPLOSION_FROM_RUPTURE, pluginRef);
        }

        //Cleanup mob metadata
        mcMMO.getCompatibilityManager().getPersistentDataLayer().removeMobFlags(livingEntity);
    }
}
