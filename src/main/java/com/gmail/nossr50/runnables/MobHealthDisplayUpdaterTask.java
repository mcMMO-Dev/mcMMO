package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHealthDisplayUpdaterTask extends BukkitRunnable {
    private LivingEntity target;
    private String oldName;
    private boolean oldNameVisible;

    public MobHealthDisplayUpdaterTask(LivingEntity target) {
        if (target.isValid()) {
            this.target = target;
            this.oldName = target.getMetadata(mcMMO.CUSTOM_NAME_METAKEY).get(0).asString();
            this.oldNameVisible = target.getMetadata(mcMMO.NAME_VISIBILITY_METAKEY).get(0).asBoolean();
        }
    }

    @Override
    public void run() {
        if (target != null && target.isValid()) {
            target.setCustomNameVisible(oldNameVisible);
            target.setCustomName(oldName);
            target.removeMetadata(mcMMO.CUSTOM_NAME_METAKEY, mcMMO.p);
            target.removeMetadata(mcMMO.NAME_VISIBILITY_METAKEY, mcMMO.p);
        }
    }
}
