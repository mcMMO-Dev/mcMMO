package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TrackedTamingEntity extends BukkitRunnable {
    private LivingEntity livingEntity;
    private final CallOfTheWildType callOfTheWildType;
    private UUID id;
    private int length;
    private final TamingManager tamingManagerRef;

    protected TrackedTamingEntity(LivingEntity livingEntity, CallOfTheWildType callOfTheWildType, TamingManager tamingManagerRef) {
        this.tamingManagerRef = tamingManagerRef;
        this.callOfTheWildType = callOfTheWildType;
        this.livingEntity = livingEntity;
        this.id = livingEntity.getUniqueId();

        int tamingCOTWLength = Config.getInstance().getTamingCOTWLength(callOfTheWildType.getConfigEntityTypeEntry());

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

            if(tamingManagerRef != null)
                tamingManagerRef.removeFromTracker(this);

            livingEntity.setHealth(0);
            livingEntity.remove();
        }

        this.cancel();
    }

    public CallOfTheWildType getCallOfTheWildType() {
        return callOfTheWildType;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public UUID getID() {
        return id;
    }
}
