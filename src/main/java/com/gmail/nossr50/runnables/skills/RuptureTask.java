package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.google.common.base.Objects;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class RuptureTask extends BukkitRunnable {

    public static final int FIVE_SECOND_DURATION = 20 * 5;
    public static final int DAMAGE_TICK_INTERVAL = 10;

    private final @NotNull McMMOPlayer ruptureSource;
    private final @NotNull LivingEntity targetEntity;
    private final int ruptureRank;
    private final int expireTick;

    private int ruptureTick;
    private int damageTickTracker;
    private final double damageValue; //TODO: Make configurable

    public RuptureTask(@NotNull McMMOPlayer ruptureSource, @NotNull LivingEntity targetEntity, int ruptureRank, double damageValue) {
        this.ruptureSource = ruptureSource;
        this.targetEntity = targetEntity;
        this.ruptureRank = ruptureRank;
        this.expireTick = FIVE_SECOND_DURATION;
        this.damageValue = damageValue;

        this.ruptureTick = 0;
        this.damageTickTracker = 0;
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
                        double damagedHealth = healthBeforeRuptureIsApplied - getTickDamage();

                        if(damagedHealth <= 0) {
                            mcMMO.p.getLogger().severe("DEBUG: Miscalculating Rupture tick damage");
                        } else {
                            targetEntity.setHealth(damagedHealth); //Hurt entity without the unwanted side effects of damage()

                            //TODO: Do we need to set last damage? Double check
                            double finalDamage = healthBeforeRuptureIsApplied - targetEntity.getHealth();

                            //Update health bars
                            MobHealthbarUtils.handleMobHealthbars(targetEntity, finalDamage, mcMMO.p);

                            if(finalDamage <= 0) {
                                mcMMO.p.getLogger().severe("DEBUG: Miscalculating final damage for Rupture");
                            } else {
                                //Actually should this even be done?
                                targetEntity.setLastDamage(finalDamage);
                            }
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

    public void explode() {
        ParticleEffectUtils.playBleedEffect(targetEntity); //Animate

        if(ruptureSource.getPlayer() != null && ruptureSource.getPlayer().isValid()) {
            targetEntity.damage(getExplosionDamage(), ruptureSource.getPlayer());
        } else {
            targetEntity.damage(getExplosionDamage(), null);
        }

        targetEntity.removeMetadata(mcMMO.RUPTURE_META_KEY, mcMMO.p);
        this.cancel(); //Task no longer needed
    }

    private double getTickDamage() {
        double tickDamage = damageValue;

        if(targetEntity.getHealth() <= tickDamage) {
            tickDamage = targetEntity.getHealth() - 0.01;

            if(tickDamage <= 0) {
                tickDamage = 0;
            }
        }

        return tickDamage;
    }

    private int getExplosionDamage() {
        //TODO: Balance pass
        return ruptureRank * 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuptureTask that = (RuptureTask) o;
        return ruptureRank == that.ruptureRank && expireTick == that.expireTick && ruptureTick == that.ruptureTick && damageTickTracker == that.damageTickTracker && Double.compare(that.damageValue, damageValue) == 0 && Objects.equal(ruptureSource, that.ruptureSource) && Objects.equal(targetEntity, that.targetEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ruptureSource, targetEntity, ruptureRank, expireTick, ruptureTick, damageTickTracker, damageValue);
    }

    @Override
    public String toString() {
        return "RuptureTimerTask{" +
                "ruptureSource=" + ruptureSource +
                ", targetEntity=" + targetEntity +
                ", ruptureRank=" + ruptureRank +
                ", expireTick=" + expireTick +
                ", ruptureTick=" + ruptureTick +
                ", damageTickTracker=" + damageTickTracker +
                ", damageValue=" + damageValue +
                '}';
    }
}
