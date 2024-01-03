package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.gmail.nossr50.util.random.ProbabilityUtil.isStaticSkillRNGSuccessful;

public class CrossbowsManager extends SkillManager {
    public CrossbowsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.CROSSBOWS);
    }

    public void handleRicochet(@NotNull Plugin pluginRef, @NotNull Arrow originalArrow, @NotNull Vector hitBlockNormal) {
        // Check player permission
        if (!Permissions.trickShot(mmoPlayer.getPlayer())) {
            return;
        }

        // TODO: Add an event for this for plugins to hook into
        spawnReflectedArrow(pluginRef, originalArrow, originalArrow.getLocation(), hitBlockNormal);
    }

    public void spawnReflectedArrow(@NotNull Plugin pluginRef, @NotNull Arrow originalArrow,
                                    @NotNull Location origin, @NotNull Vector normal) {
        int bounceCount = 0;

        if (originalArrow.hasMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT)) {
            bounceCount = originalArrow.getMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT).get(0).asInt();
            if (bounceCount >= getTrickShotMaxBounceCount()) {
                return;
            }
        }

        final ProjectileSource originalArrowShooter = originalArrow.getShooter();
        final Vector arrowInBlockVector = originalArrow.getVelocity();
        final Vector reflectedDirection = arrowInBlockVector.subtract(normal.multiply(2 * arrowInBlockVector.dot(normal)));
        final Vector inverseNormal = normal.multiply(-1);


        // check the angle of the arrow against the inverse normal to see if the angle was too shallow
        if (arrowInBlockVector.angle(inverseNormal) < Math.PI / 4) {
            return;
        }

        // Spawn new arrow with the reflected direction
        Arrow arrow = originalArrow.getWorld().spawnArrow(origin,
                reflectedDirection, 1, 1);
        arrow.setShooter(originalArrowShooter);
        arrow.setMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT,
                new FixedMetadataValue(pluginRef, bounceCount + 1));
        arrow.setMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW,
                new FixedMetadataValue(pluginRef, originalArrowShooter));
        arrow.setMetadata(MetadataConstants.METADATA_KEY_BOW_TYPE,
                new FixedMetadataValue(pluginRef, originalArrow.getMetadata(
                        MetadataConstants.METADATA_KEY_BOW_TYPE).get(0)));

        originalArrow.remove();
    }

    public int getTrickShotMaxBounceCount() {
        return RankUtils.getRank(mmoPlayer, SubSkillType.CROSSBOWS_TRICK_SHOT);
    }
}
