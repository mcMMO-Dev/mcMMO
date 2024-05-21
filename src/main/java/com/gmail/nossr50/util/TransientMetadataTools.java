package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import static com.gmail.nossr50.util.MobMetadataUtils.removeMobFlags;

public class TransientMetadataTools {
    private final mcMMO pluginRef;

    public TransientMetadataTools(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void cleanLivingEntityMetadata(@NotNull LivingEntity entity) {
        //Since it's not written anywhere, apparently the GC won't touch objects with metadata still present on them
        if (entity.hasMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME)) {
            entity.setCustomName(entity.getMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME).get(0).asString());
            entity.removeMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME, pluginRef);
        }

        //Involved in changing mob names to hearts
        if (entity.hasMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY)) {
            entity.setCustomNameVisible(entity.getMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY).get(0).asBoolean());
            entity.removeMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY, pluginRef);
        }

        //Gets assigned to endermen, potentially doesn't get cleared before this point
        if (entity.hasMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK)) {
            entity.removeMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK, pluginRef);
        }

        //Cleanup mob metadata
        removeMobFlags(entity);

        //TODO: This loop has some redundancy, this whole method needs to be rewritten
        for(String key : MetadataConstants.MOB_METADATA_KEYS) {
            if (entity.hasMetadata(key)) {
                entity.removeMetadata(key, pluginRef);
            }
        }
    }
}
