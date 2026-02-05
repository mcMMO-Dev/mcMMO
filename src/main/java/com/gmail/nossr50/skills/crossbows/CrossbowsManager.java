package com.gmail.nossr50.skills.crossbows;

import static com.gmail.nossr50.util.MetadataConstants.MCMMO_METADATA_VALUE;
import static com.gmail.nossr50.util.MetadataConstants.METADATA_KEY_CROSSBOW_PROJECTILE;
import static com.gmail.nossr50.util.skills.CombatUtils.delayArrowMetaCleanup;
import static com.gmail.nossr50.util.skills.ProjectileUtils.isCrossbowProjectile;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.ProjectileUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CrossbowsManager extends SkillManager {
    public CrossbowsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.CROSSBOWS);
    }

    public void handleRicochet(@NotNull Plugin pluginRef, @NotNull Arrow arrow,
            @NotNull Vector hitBlockNormal) {
        if (!isCrossbowProjectile(arrow)) {
            return;
        }

        // Check player permission
        if (!Permissions.trickShot(mmoPlayer.getPlayer())) {
            return;
        }

        // TODO: Add an event for this for plugins to hook into
        spawnReflectedArrow(pluginRef, arrow, arrow.getLocation(), hitBlockNormal);
    }

    private void spawnReflectedArrow(@NotNull Plugin pluginRef, @NotNull Arrow originalArrow,
            @NotNull Location origin, @NotNull Vector normal) {
        int bounceCount = 0;

        if (originalArrow.hasMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT)) {
            bounceCount = originalArrow.getMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT)
                    .get(0).asInt();
            if (bounceCount >= getTrickShotMaxBounceCount()) {
                return;
            }
        }

        final ProjectileSource originalArrowShooter = originalArrow.getShooter();
        final Vector arrowInBlockVector = originalArrow.getVelocity();
        final Vector reflectedDirection = arrowInBlockVector.subtract(
                normal.multiply(2 * arrowInBlockVector.dot(normal)));
        final Vector inverseNormal = normal.multiply(-1);

        // check the angle of the arrow against the inverse normal to see if the angle was too shallow
        // only checks angle on the first bounce
        if (bounceCount == 0 && arrowInBlockVector.angle(inverseNormal) < Math.PI / 4) {
            return;
        }

        // Spawn new arrow with the reflected direction
        final Arrow spawnedArrow = originalArrow.getWorld()
                .spawnArrow(origin, reflectedDirection, 1, 1);
        // copy some properties from the old arrow
        spawnedArrow.setShooter(originalArrowShooter);
        spawnedArrow.setCritical(originalArrow.isCritical());
        spawnedArrow.setPierceLevel(originalArrow.getPierceLevel());
        spawnedArrow.setPickupStatus(originalArrow.getPickupStatus());
        spawnedArrow.setKnockbackStrength(originalArrow.getKnockbackStrength());

        if (originalArrow.getBasePotionType() != null) {
            spawnedArrow.setBasePotionType(originalArrow.getBasePotionType());
        }

        if (originalArrow.hasCustomEffects()) {
            for (var effect : originalArrow.getCustomEffects()) {
                spawnedArrow.addCustomEffect(effect, true);
            }
        }

        // copy metadata from old arrow
        ProjectileUtils.copyArrowMetadata(pluginRef, originalArrow, spawnedArrow);
        originalArrow.remove();
        // add important metadata to new arrow
        spawnedArrow.setMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT,
                new FixedMetadataValue(pluginRef, bounceCount + 1));
        spawnedArrow.setMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW,
                new FixedMetadataValue(pluginRef, originalArrowShooter));
        // Easy fix to recognize the arrow as a crossbow projectile
        // TODO: Replace the hack with the new API for setting weapon on projectiles
        if (!spawnedArrow.hasMetadata(METADATA_KEY_CROSSBOW_PROJECTILE)) {
            spawnedArrow.setMetadata(MetadataConstants.METADATA_KEY_CROSSBOW_PROJECTILE,
                    MCMMO_METADATA_VALUE);
        }
        // There are reasons to keep this despite using the metadata values above
        spawnedArrow.setShotFromCrossbow(true);

        // Don't allow multi-shot or infinite arrows to be picked up
        if (spawnedArrow.hasMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW)
                || spawnedArrow.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW)) {
            spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }

        // Schedule cleanup of metadata in case metadata cleanup fails
        delayArrowMetaCleanup(spawnedArrow);
    }

    public int getTrickShotMaxBounceCount() {
        return RankUtils.getRank(mmoPlayer, SubSkillType.CROSSBOWS_TRICK_SHOT);
    }

    public double getPoweredShotBonusDamage(Player player, double oldDamage) {
        double damageBonusPercent = getDamageBonusPercent(player);
        double newDamage = oldDamage + (oldDamage * damageBonusPercent);
        return Math.min(newDamage,
                (oldDamage + mcMMO.p.getAdvancedConfig().getPoweredShotDamageMax()));
    }

    public double getDamageBonusPercent(Player player) {
        return ((RankUtils.getRank(player, SubSkillType.CROSSBOWS_POWERED_SHOT))
                * (mcMMO.p.getAdvancedConfig().getPoweredShotRankDamageMultiplier()) / 100.0D);
    }

    public double poweredShot(double oldDamage) {
        if (ProbabilityUtil.isNonRNGSkillActivationSuccessful(SubSkillType.CROSSBOWS_POWERED_SHOT,
                mmoPlayer)) {
            return getPoweredShotBonusDamage(getPlayer(), oldDamage);
        } else {
            return oldDamage;
        }
    }
}
