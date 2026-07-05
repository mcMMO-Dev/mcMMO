package com.gmail.nossr50.util;

import static com.gmail.nossr50.util.MobMetadataUtils.removeMobFlags;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class TransientMetadataTools {
    private final mcMMO pluginRef;

    public TransientMetadataTools(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void cleanLivingEntityMetadata(@NotNull LivingEntity entity) {
        // Restore mob name from healthbar snapshot if one is present. This ensures the entity
        // leaves the world with its correct name, not a stale healthbar string.
        // Since it's not written anywhere, apparently the GC won't touch objects with metadata
        // still present on them.
        MobHealthbarUtils.restoreNameFromSnapshot(entity);

        //Gets assigned to endermen, potentially doesn't get cleared before this point
        if (entity.hasMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK)) {
            entity.removeMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK, pluginRef);
        }

        //Cleanup mob metadata
        removeMobFlags(entity);

        for (String key : MetadataConstants.MOB_METADATA_KEYS) {
            if (entity.hasMetadata(key)) {
                entity.removeMetadata(key, pluginRef);
            }
        }
    }
}
