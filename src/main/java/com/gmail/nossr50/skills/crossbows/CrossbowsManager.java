package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CrossbowsManager extends SkillManager {
    public CrossbowsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.CROSSBOWS);
    }

    public void handleRicochet(@NotNull Plugin pluginRef, @NotNull Arrow originalArrow, @NotNull Vector hitBlockNormal) {
        // Reflect arrow in new direction
        // cleanup metadata on original arrow
        // TODO: Add an event for this for plugins to hook into
        spawnReflectedArrow(pluginRef, originalArrow, originalArrow.getLocation(), hitBlockNormal);
    }

    public void spawnReflectedArrow(@NotNull Plugin pluginRef, @NotNull Arrow originalArrow, @NotNull Location origin, @NotNull Vector normal) {
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
        arrow.setMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW,
                new FixedMetadataValue(pluginRef, originalArrowShooter));

        // TODO: This metadata needs to get cleaned up at some point
        arrow.setMetadata(MetadataConstants.METADATA_KEY_BOW_TYPE,
                new FixedMetadataValue(pluginRef, originalArrow.getMetadata(
                        MetadataConstants.METADATA_KEY_BOW_TYPE).get(0)));
    }
}
