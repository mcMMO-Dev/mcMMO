package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHealthDisplayUpdaterTask extends BukkitRunnable {
    private final LivingEntity target;

    public MobHealthDisplayUpdaterTask(LivingEntity target) {
        this.target = target;
    }

    @Override
    public void run() {
        if (target.hasMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME)) {
            target.setCustomName(target.getMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME).get(0).asString());
            target.removeMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME, mcMMO.p);
        }

        if (target.hasMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY)) {
            target.setCustomNameVisible(target.getMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY).get(0).asBoolean());
            target.removeMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY, mcMMO.p);
        }
    }
}
