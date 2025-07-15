package com.gmail.nossr50.runnables.skills;

import static com.gmail.nossr50.util.AttributeMapper.MAPPED_MAX_HEALTH;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.skills.rupture.McMMOEntityDamageByRuptureEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import org.bukkit.attribute.AttributeInstance;
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
    // failsafe to ensure Rupture always exits and does not run forever
    private int totalTicks = 0;
    private final int totalTickCeiling;

    /**
     * Constructor for the RuptureTask class.
     *
     * @param ruptureSource The McMMOPlayer who is the source of the rupture.
     * @param targetEntity The LivingEntity that is the target of the rupture.
     * @param pureTickDamage The amount of damage to be applied per tick.
     */
    public RuptureTask(@NotNull McMMOPlayer ruptureSource, @NotNull LivingEntity targetEntity,
            double pureTickDamage) {
        this.ruptureSource = ruptureSource;
        this.targetEntity = targetEntity;
        this.expireTick = mcMMO.p.getAdvancedConfig()
                .getRuptureDurationSeconds(targetEntity instanceof Player) * 20;
        this.totalTickCeiling = Math.min(this.expireTick, 200);
        this.ruptureTick = 0;
        this.damageTickTracker = 0;
        this.animationTick = ANIMATION_TICK_INTERVAL; //Play an animation right away
        this.pureTickDamage = pureTickDamage;
    }

    /**
     * Deprecated constructor for the RuptureTask class.
     *
     * @param ruptureSource The McMMOPlayer who is the source of the rupture.
     * @param targetEntity The LivingEntity that is the target of the rupture.
     * @param pureTickDamage The amount of damage to be applied per tick.
     * @param ignored This parameter is ignored and should not be used.
     * @since 2.2.023
     * @deprecated This constructor is deprecated and will be removed in future versions. Use
     * {@link #RuptureTask(McMMOPlayer, LivingEntity, double)} instead.
     */
    @Deprecated(forRemoval = true, since = "2.2.023")
    public RuptureTask(@NotNull McMMOPlayer ruptureSource, @NotNull LivingEntity targetEntity,
            double pureTickDamage, double ignored) {
        this(ruptureSource, targetEntity, pureTickDamage);
    }

    @Override
    public void run() {
        // always increment the fail-safe
        totalTicks++;

        if (totalTicks >= totalTickCeiling) {
            this.cancel();
            return;
        }

        //Check validity
        if (targetEntity.isValid()) {
            ruptureTick += 1; //Advance rupture tick by 1.
            damageTickTracker += 1; //Increment damage tick tracker

            //TODO: Clean this code up, applyRupture() is a confusing name for something that returns boolean
            //Rupture hasn't ended yet
            if (ruptureTick < expireTick) {
                //Is it time to damage?
                if (damageTickTracker >= DAMAGE_TICK_INTERVAL) {
                    damageTickTracker = 0; //Reset timer
                    if (applyRupture()) {
                        return;
                    }

                    playAnimation();
                }
            } else {
                if (!applyRupture()) {
                    playAnimation();
                }

                endRupture();
            }
        } else {
            this.cancel(); //Task no longer needed
            targetEntity.removeMetadata(MetadataConstants.METADATA_KEY_RUPTURE, mcMMO.p);
        }
    }

    private void playAnimation() {
        if (animationTick >= ANIMATION_TICK_INTERVAL) {
            ParticleEffectUtils.playBleedEffect(targetEntity); //Animate
            animationTick = 0;
        } else {
            animationTick++;
        }
    }

    private boolean applyRupture() {
        double healthBeforeRuptureIsApplied = targetEntity.getHealth();

        // Ensure victim has health
        if (healthBeforeRuptureIsApplied > 0.01) {
            //Send a fake damage event
            McMMOEntityDamageByRuptureEvent event =
                    new McMMOEntityDamageByRuptureEvent(ruptureSource, targetEntity,
                            calculateAdjustedTickDamage());
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            //Ensure the event wasn't canceled and damage is still greater than 0
            double damage = event.getDamage(); //Use raw damage for Rupture

            if (event.isCancelled() || damage <= 0 || healthBeforeRuptureIsApplied - damage <= 0) {
                return true;
            }

            final double damagedHealth = healthBeforeRuptureIsApplied - damage;

            final AttributeInstance maxHealthAttribute = targetEntity.getAttribute(
                    MAPPED_MAX_HEALTH);
            if (maxHealthAttribute == null) {
                // Can't remove health if max health is null
                mcMMO.p.getLogger()
                        .info("RuptureTask: Target entity has an illegal state for its health." +
                                " Cancelling Rupture. Target has null " + MAPPED_MAX_HEALTH
                                + " attribute.");
                return true;
            }

            if (damagedHealth > maxHealthAttribute.getValue()) {
                // Something went very wrong here, target has an illegal state for its health
                mcMMO.p.getLogger()
                        .info("RuptureTask: Target entity has an illegal state for its health." +
                                " Cancelling Rupture. Target has " + targetEntity.getHealth()
                                + " health," +
                                " but max health is " + maxHealthAttribute.getValue());
                return true;
            }

            targetEntity.setHealth(
                    damagedHealth); //Hurt entity without the unwanted side effects of damage()}
            MobHealthbarUtils.handleMobHealthbars(targetEntity, damage, mcMMO.p);
        }

        return false;
    }

    public void refreshRupture() {
        damageTickTracker = DAMAGE_TICK_INTERVAL;
        ruptureTick = 0;
    }

    private void endRupture() {
        targetEntity.removeMetadata(MetadataConstants.METADATA_KEY_RUPTURE, mcMMO.p);
        this.cancel(); //Task no longer needed
    }

    private double calculateAdjustedTickDamage() {
        double tickDamage = pureTickDamage;

        if (targetEntity.getHealth() <= tickDamage) {
            tickDamage = targetEntity.getHealth() - 0.01;

            if (tickDamage <= 0) {
                tickDamage = 0;
            }
        }

        return tickDamage;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RuptureTask that)) {
            return false;
        }

        return ruptureSource.equals(that.ruptureSource) && targetEntity.equals(that.targetEntity);
    }

    @Override
    public int hashCode() {
        int result = ruptureSource.hashCode();
        result = 31 * result + targetEntity.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RuptureTask{" +
                "ruptureSource=" + ruptureSource +
                ", targetEntity=" + targetEntity +
                ", expireTick=" + expireTick +
                ", ruptureTick=" + ruptureTick +
                ", damageTickTracker=" + damageTickTracker +
                ", animationTick=" + animationTick +
                ", pureTickDamage=" + pureTickDamage +
                ", totalTicks=" + totalTicks +
                ", totalTickCeiling=" + totalTickCeiling +
                '}';
    }
}
