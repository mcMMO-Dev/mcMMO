package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.google.common.base.Objects;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class RuptureTask extends BukkitRunnable {

    public static final int DAMAGE_TICK_INTERVAL = 10;

    private final @NotNull McMMOPlayer ruptureSource;
    private final @NotNull LivingEntity targetEntity;
    private final int expireTick;

    private int ruptureTick;
    private int damageTickTracker;
    private final double pureTickDamage; //TODO: Make configurable
    private final double explosionDamage; //TODO: Make configurable

    public RuptureTask(@NotNull McMMOPlayer ruptureSource, @NotNull LivingEntity targetEntity, double pureTickDamage, double explosionDamage) {
        this.ruptureSource = ruptureSource;
        this.targetEntity = targetEntity;
        this.expireTick = mcMMO.p.getAdvancedConfig().getRuptureDurationSeconds(targetEntity instanceof Player) * 20;

        this.ruptureTick = 0;
        this.damageTickTracker = 0;
        this.pureTickDamage = pureTickDamage;
        this.explosionDamage = explosionDamage;
    }

    @Override
    public void run() {
        //Check validity
        if(targetEntity.isValid()) {
            ruptureTick += 1; //Advance rupture tick by 1.
            damageTickTracker += 1; //Increment damage tick tracker

            //Rupture hasn't ended yet
            if(ruptureTick < expireTick) {
                //Is it time to damage?
                if(damageTickTracker >= DAMAGE_TICK_INTERVAL) {
                    damageTickTracker = 0; //Reset
                    ParticleEffectUtils.playBleedEffect(targetEntity); //Animate

                    if(targetEntity.getHealth() > 0.01) {
                        double healthBeforeRuptureIsApplied = targetEntity.getHealth();
                        double damagedHealth = healthBeforeRuptureIsApplied - calculateAdjustedTickDamage();

                        if(damagedHealth <= 0) {
                            mcMMO.p.getLogger().severe("DEBUG: Miscalculating Rupture tick damage");
                        } else {
                            targetEntity.setHealth(damagedHealth); //Hurt entity without the unwanted side effects of damage()
                        }
                    }
                }
            } else {
                explode();
            }
        } else {
            targetEntity.removeMetadata(mcMMO.RUPTURE_META_KEY, mcMMO.p);
            this.cancel(); //Task no longer needed
        }
    }

    public void refreshRupture() {
        damageTickTracker = DAMAGE_TICK_INTERVAL;
        ruptureTick = 0;
    }

    public void explode() {
        targetEntity.setMetadata(mcMMO.EXPLOSION_FROM_RUPTURE, new FixedMetadataValue(mcMMO.p, "null"));

        ParticleEffectUtils.playGreaterImpactEffect(targetEntity); //Animate

        if(ruptureSource.getPlayer() != null && ruptureSource.getPlayer().isValid()) {
            targetEntity.damage(getExplosionDamage(), ruptureSource.getPlayer());
        } else {
            targetEntity.damage(getExplosionDamage(), null);
        }

        targetEntity.removeMetadata(mcMMO.RUPTURE_META_KEY, mcMMO.p);

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
