package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.skills.rupture.McMMOEntityDamageByRuptureEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.google.common.base.Objects;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RuptureTask extends CancellableRunnable {

    public static final int DAMAGE_TICK_INTERVAL = 10;
    public static final int ANIMATION_TICK_INTERVAL = 1;

    private final @NotNull McMMOPlayer ruptureSource;
    private final @NotNull LivingEntity targetEntity;
    private final int expireTick;

    private int ruptureTick;
    private int damageTickTracker;
    private int animationTick;
    private final double pureTickDamage;
    private final double explosionDamage;

    public RuptureTask(@NotNull McMMOPlayer ruptureSource, @NotNull LivingEntity targetEntity, double pureTickDamage, double explosionDamage) {
        this.ruptureSource = ruptureSource;
        this.targetEntity = targetEntity;
        this.expireTick = mcMMO.p.getAdvancedConfig().getRuptureDurationSeconds(targetEntity instanceof Player) * 20;

        this.ruptureTick = 0;
        this.damageTickTracker = 0;
        this.animationTick = ANIMATION_TICK_INTERVAL; //Play an animation right away
        this.pureTickDamage = pureTickDamage;
        this.explosionDamage = explosionDamage;
    }

    @Override
    public void run() {
        //Check validity
        if(targetEntity.isValid()) {
            ruptureTick += 1; //Advance rupture tick by 1.
            damageTickTracker += 1; //Increment damage tick tracker

            //TODO: Clean this code up, applyRupture() is a confusing name for something that returns boolean
            //Rupture hasn't ended yet
            if(ruptureTick < expireTick) {
                //Is it time to damage?
                if(damageTickTracker >= DAMAGE_TICK_INTERVAL) {

                    damageTickTracker = 0; //Reset timer
                    if (applyRupture()) return;

                    playAnimation();
                }
            } else {
                if(!applyRupture()) {
                    playAnimation();
                }

                endRupture();
            }
        } else {
            targetEntity.removeMetadata(MetadataConstants.METADATA_KEY_RUPTURE, mcMMO.p);
            this.cancel(); //Task no longer needed
        }
    }

    private void playAnimation() {
        if(animationTick >= ANIMATION_TICK_INTERVAL) {
            ParticleEffectUtils.playBleedEffect(targetEntity); //Animate
            animationTick = 0;
        } else {
            animationTick++;
        }
    }

    private boolean applyRupture() {
        double healthBeforeRuptureIsApplied = targetEntity.getHealth();

        //Ensure victim has health
        if (healthBeforeRuptureIsApplied > 0.01) {
            //Send a fake damage event
            McMMOEntityDamageByRuptureEvent event =
                    new McMMOEntityDamageByRuptureEvent(ruptureSource, targetEntity, calculateAdjustedTickDamage());
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            //Ensure the event wasn't cancelled and damage is still greater than 0
            double damage = event.getDamage(); //Use raw damage for Rupture

            if (event.isCancelled() || damage <= 0 || healthBeforeRuptureIsApplied - damage <= 0)
                return true;

            double damagedHealth = healthBeforeRuptureIsApplied - damage;

            targetEntity.setHealth(damagedHealth); //Hurt entity without the unwanted side effects of damage()}
            MobHealthbarUtils.handleMobHealthbars(targetEntity, damage, mcMMO.p);
        }

        return false;
    }

    public void refreshRupture() {
        damageTickTracker = DAMAGE_TICK_INTERVAL;
        ruptureTick = 0;
    }

    public void endRupture() {
//        targetEntity.setMetadata(mcMMO.EXPLOSION_FROM_RUPTURE, new FixedMetadataValue(mcMMO.p, "null"));
//
//        ParticleEffectUtils.playGreaterImpactEffect(targetEntity); //Animate
//
//        if(ruptureSource.getPlayer() != null && ruptureSource.getPlayer().isValid()) {
//            targetEntity.damage(getExplosionDamage(), ruptureSource.getPlayer());
//        } else {
//            targetEntity.damage(getExplosionDamage(), null);
//        }
//
//        targetEntity.removeMetadata(mcMMO.RUPTURE_META_KEY, mcMMO.p);

        targetEntity.removeMetadata(MetadataConstants.METADATA_KEY_RUPTURE, mcMMO.p);
        this.cancel(); //Task no longer needed
    }

    private double calculateAdjustedTickDamage() {
        double tickDamage = pureTickDamage;

        if(targetEntity.getHealth() <= tickDamage) {
            tickDamage = targetEntity.getHealth() - 0.01;

            if(tickDamage <= 0) {
                tickDamage = 0;
            }
        }

        return tickDamage;
    }

    private double getExplosionDamage() {
        return explosionDamage;
    }

    @Override
    public String toString() {
        return "RuptureTask{" +
                "ruptureSource=" + ruptureSource +
                ", targetEntity=" + targetEntity +
                ", expireTick=" + expireTick +
                ", ruptureTick=" + ruptureTick +
                ", damageTickTracker=" + damageTickTracker +
                ", pureTickDamage=" + pureTickDamage +
                ", explosionDamage=" + explosionDamage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuptureTask that = (RuptureTask) o;
        return expireTick == that.expireTick && ruptureTick == that.ruptureTick && damageTickTracker == that.damageTickTracker && Double.compare(that.pureTickDamage, pureTickDamage) == 0 && Double.compare(that.explosionDamage, explosionDamage) == 0 && Objects.equal(ruptureSource, that.ruptureSource) && Objects.equal(targetEntity, that.targetEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ruptureSource, targetEntity, expireTick, ruptureTick, damageTickTracker, pureTickDamage, explosionDamage);
    }
}
