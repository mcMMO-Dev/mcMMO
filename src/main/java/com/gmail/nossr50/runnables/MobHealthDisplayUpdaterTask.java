package com.gmail.nossr50.runnables;

import com.gmail.nossr50.core.MetadataConstants;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHealthDisplayUpdaterTask extends BukkitRunnable {
    private LivingEntity target;
    private String oldName;
    private boolean oldNameVisible;

    public MobHealthDisplayUpdaterTask(LivingEntity target) {
        if (target.isValid()) {
            this.target = target;
            this.oldName = target.getMetadata(MetadataConstants.CUSTOM_NAME_METAKEY).get(0).asString();
            this.oldNameVisible = target.getMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY).get(0).asBoolean();
        }
    }

    @Override
    public void run() {
        if (target != null && target.isValid()) {
            target.setCustomNameVisible(oldNameVisible);
            target.setCustomName(oldName);
            target.removeMetadata(MetadataConstants.CUSTOM_NAME_METAKEY, pluginRef);
            target.removeMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY, pluginRef);
        }
    }
}
