package com.gmail.nossr50.runnables;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHealthDisplayUpdaterTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private LivingEntity target;

    public MobHealthDisplayUpdaterTask(mcMMO pluginRef, LivingEntity target) {
        this.pluginRef = pluginRef;
        this.target = target;
    }

    @Override
    public void run() {
        if (target.hasMetadata(MetadataConstants.CUSTOM_NAME_METAKEY)) {
            target.setCustomName(target.getMetadata(MetadataConstants.CUSTOM_NAME_METAKEY).get(0).asString());
            target.removeMetadata(MetadataConstants.CUSTOM_NAME_METAKEY, pluginRef);
        }

        if (target.hasMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY)) {
            target.setCustomNameVisible(target.getMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY).get(0).asBoolean());
            target.removeMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY, pluginRef);
        }
    }
}
