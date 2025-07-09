package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class TravelingBlockMetaCleanup extends CancellableRunnable {
    private final @NotNull Entity entity;
    private final @NotNull mcMMO pluginRef;

    public TravelingBlockMetaCleanup(@NotNull Entity entity, @NotNull mcMMO pluginRef) {
        this.entity = entity;
        this.pluginRef = pluginRef;
    }

    @Override
    public void run() {
        if (!entity.isValid()) {
            entity.removeMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK, pluginRef);
            this.cancel();
        } else if (!entity.hasMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK)) {
            this.cancel();
        }
    }
}
