package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class MobDodgeMetaCleanup extends CancellableRunnable {
    private final @NotNull Mob mob;
    private final @NotNull mcMMO pluginRef;

    public MobDodgeMetaCleanup(@NotNull Mob mob, @NotNull mcMMO pluginRef) {
        this.mob = mob;
        this.pluginRef = pluginRef;
    }

    @Override
    public void run() {
        if (!mob.isValid() || mob.getTarget() == null) {
            mob.removeMetadata(MetadataConstants.METADATA_KEY_DODGE_TRACKER, pluginRef);
            this.cancel();
        } else if (!mob.hasMetadata(MetadataConstants.METADATA_KEY_DODGE_TRACKER)) {
            this.cancel();
        }
    }
}