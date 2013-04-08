package com.gmail.nossr50.runnables;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;

public class MobHealthDisplayUpdaterTask extends BukkitRunnable {
    private LivingEntity target;
    private String oldName;
    private boolean oldNameVisible;

    public MobHealthDisplayUpdaterTask(LivingEntity target) {
        if (target.isValid()) {
            this.target = target;
            this.oldName = target.getMetadata(mcMMO.customNameKey).get(0).asString();
            this.oldNameVisible = target.getMetadata(mcMMO.customVisibleKey).get(0).asBoolean();
        }
    }

    @Override
    public void run() {
        if (target.isValid()) {
            target.setCustomNameVisible(oldNameVisible);
            target.setCustomName(oldName);
            target.removeMetadata(mcMMO.customNameKey, mcMMO.p);
            target.removeMetadata(mcMMO.customVisibleKey, mcMMO.p);
        }
    }
}
