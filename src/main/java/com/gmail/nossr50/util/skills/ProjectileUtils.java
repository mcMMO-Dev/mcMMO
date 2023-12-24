package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class ProjectileUtils {
    public static Vector getNormal(BlockFace blockFace) {
        return switch (blockFace) {
            case UP -> new Vector(0, 1, 0);
            case DOWN -> new Vector(0, -1, 0);
            case NORTH -> new Vector(0, 0, -1);
            case SOUTH -> new Vector(0, 0, 1);
            case EAST -> new Vector(1, 0, 0);
            case WEST -> new Vector(-1, 0, 0);
            default -> new Vector(0, 0, 0);
        };
    }

    public static void spawnReflectedArrow(Plugin pluginRef, Arrow originalArrow, Location origin, Vector normal) {
        // TODO: Add an event for this for plugins to hook into
        ProjectileSource originalArrowShooter = originalArrow.getShooter();
        Vector incomingDirection = originalArrow.getVelocity();
        Vector reflectedDirection = incomingDirection.subtract(normal.multiply(2 * incomingDirection.dot(normal)));

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
