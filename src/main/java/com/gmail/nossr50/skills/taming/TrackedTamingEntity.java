package com.gmail.nossr50.skills.taming;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;

public class TrackedTamingEntity extends BukkitRunnable {
    private LivingEntity livingEntity;
    private UUID id;
    private long timeStamp;
    private int length;

    protected TrackedTamingEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.id = livingEntity.getUniqueId();
        this.timeStamp = System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR;

        this.length = Config.getInstance().getTamingCOTWLength(livingEntity.getType()) * Misc.TICK_CONVERSION_FACTOR;

        this.runTaskLater(mcMMO.p, length);
    }

    @Override
    public void run() {
        if (livingEntity.isValid()) {
            Location location = livingEntity.getLocation();
            location.getWorld().playSound(location, Sound.FIZZ, 0.8F, 0.8F);
            ParticleEffectUtils.playCallOfTheWildEffect(livingEntity);
            CombatUtils.dealDamage(livingEntity, livingEntity.getMaxHealth(), DamageCause.SUICIDE, livingEntity);
        }

        Taming.removeFromTracker(this);
        this.cancel();
    }

    protected LivingEntity getLivingEntity() {
        return livingEntity;
    }

    protected UUID getID() {
        return id;
    }

    protected long getTimeStamp() {
        return timeStamp;
    }
}
