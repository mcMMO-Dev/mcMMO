package com.gmail.nossr50.skills.taming;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;

public class TrackedTamingEntity extends BukkitRunnable {
    private LivingEntity livingEntity;
    private UUID id;
    private int length;
    private Player owner;

    protected TrackedTamingEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.id = livingEntity.getUniqueId();

        AnimalTamer tamer = ((Tameable)livingEntity).getOwner();

        if (tamer != null && tamer instanceof Player) {
            this.owner = (Player) tamer;
        }

        int tamingCOTWLength = Config.getInstance().getTamingCOTWLength(livingEntity.getType());

        if (tamingCOTWLength > 0) {
            this.length = tamingCOTWLength * Misc.TICK_CONVERSION_FACTOR;
            this.runTaskLater(mcMMO.p, length);
        }
    }

    @Override
    public void run() {
        if (livingEntity.isValid()) {
            Location location = livingEntity.getLocation();
            location.getWorld().playSound(location, Sound.FIZZ, 0.8F, 0.8F);
            ParticleEffectUtils.playCallOfTheWildEffect(livingEntity);
            CombatUtils.dealDamage(livingEntity, livingEntity.getMaxHealth(), DamageCause.SUICIDE, livingEntity);
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(owner);
        TamingManager tamingManager = mcMMOPlayer.getTamingManager();

        tamingManager.removeFromTracker(this);
        this.cancel();
    }

    protected LivingEntity getLivingEntity() {
        return livingEntity;
    }

    protected UUID getID() {
        return id;
    }
}
