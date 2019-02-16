package com.gmail.nossr50.core.skills.primary.taming;

import com.gmail.nossr50.core.config.MainConfig;
import com.gmail.nossr50.core.util.Misc;
import com.gmail.nossr50.core.util.skills.CombatUtils;
import com.gmail.nossr50.core.util.skills.ParticleEffectUtils;

import java.util.UUID;

public class TrackedTamingEntity extends BukkitRunnable {
    private LivingEntity livingEntity;
    private UUID id;
    private int length;

    protected TrackedTamingEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.id = livingEntity.getUniqueId();

        int tamingCOTWLength = MainConfig.getInstance().getTamingCOTWLength(livingEntity.getType());

        if (tamingCOTWLength > 0) {
            this.length = tamingCOTWLength * Misc.TICK_CONVERSION_FACTOR;
            this.runTaskLater(mcMMO.p, length);
        }
    }

    @Override
    public void run() {
        if (livingEntity.isValid()) {
            Location location = livingEntity.getLocation();
            location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 0.8F, 0.8F);
            ParticleEffectUtils.playCallOfTheWildEffect(livingEntity);
            CombatUtils.dealDamage(livingEntity, livingEntity.getMaxHealth(), DamageCause.SUICIDE, livingEntity);
        }

        TamingManager.removeFromTracker(this);
        this.cancel();
    }

    protected LivingEntity getLivingEntity() {
        return livingEntity;
    }

    protected UUID getID() {
        return id;
    }
}
