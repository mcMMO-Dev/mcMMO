package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHealthDisplayUpdaterTask extends BukkitRunnable {
    private LivingEntity target;
    //private String oldName;
    //private boolean oldNameVisible;

    public MobHealthDisplayUpdaterTask(LivingEntity target) {
        if (target.isValid()) {
            this.target = target;
            // Instead of caching, we will get values later from the Metadata.
            //this.oldName = target.getMetadata(mcMMO.customNameKey).get(0).asString();
            //this.oldNameVisible = target.getMetadata(mcMMO.customVisibleKey).get(0).asBoolean();
        }
    }

    @Override
    public void run() {
        if (target != null && target.isValid()) {
            // Not caching values anymore.
            //target.setCustomNameVisible(oldNameVisible);
            //target.setCustomName(oldName);
            target.setCustomNameVisible(target.getMetadata(mcMMO.customVisibleKey).get(0).asBoolean());
            target.setCustomName(target.getMetadata(mcMMO.customNameKey).get(0).asString());
            target.removeMetadata(mcMMO.customNameKey, mcMMO.p);
            target.removeMetadata(mcMMO.customVisibleKey, mcMMO.p);
        }
    }
}
