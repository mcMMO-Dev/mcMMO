package com.gmail.nossr50.runnables;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
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
        if (target.hasMetadata(MetadataConstants.CUSTOM_NAME_METAKEY.getKey())) {
            target.setCustomName(target.getMetadata(MetadataConstants.CUSTOM_NAME_METAKEY.getKey()).get(0).asString());
            target.removeMetadata(MetadataConstants.CUSTOM_NAME_METAKEY.getKey(), (Plugin) pluginRef.getPlatformProvider());
        }

        if (target.hasMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY.getKey())) {
            target.setCustomNameVisible(target.getMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY.getKey()).get(0).asBoolean());
            target.removeMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY.getKey(), (Plugin) pluginRef.getPlatformProvider());
        }
    }
}
