package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Clean up all possible mcMMO related metadata for a projectile
     *
     * @param arrow projectile
     */
    // TODO: Add test
    public static void cleanupProjectileMetadata(@NotNull Arrow arrow) {
        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW)) {
            arrow.removeMetadata(MetadataConstants.METADATA_KEY_INF_ARROW, mcMMO.p);
        }

        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE)) {
            arrow.removeMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE, mcMMO.p);
        }

        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE)) {
            arrow.removeMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE, mcMMO.p);
        }

        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW)) {
            arrow.removeMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW, mcMMO.p);
        }

        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW)) {
            arrow.removeMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW, mcMMO.p);
        }

        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT)) {
            arrow.removeMetadata(MetadataConstants.METADATA_KEY_BOUNCE_COUNT, mcMMO.p);
        }
    }

    public static void copyArrowMetadata(@NotNull Plugin pluginRef, @NotNull Arrow arrowToCopy, @NotNull Arrow newArrow) {
        if(arrowToCopy.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW)) {
            newArrow.setMetadata(MetadataConstants.METADATA_KEY_INF_ARROW,
                    arrowToCopy.getMetadata(MetadataConstants.METADATA_KEY_INF_ARROW).get(0));
        }

        if(arrowToCopy.hasMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE)) {
            newArrow.setMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE,
                    new FixedMetadataValue(pluginRef,
                            arrowToCopy.getMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE).get(0).asDouble()));
        }

        if(arrowToCopy.hasMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE)) {
            newArrow.setMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE,
                    arrowToCopy.getMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE).get(0));
        }

        if(arrowToCopy.hasMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW)) {
            newArrow.setMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW,
                    arrowToCopy.getMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW).get(0));
        }

        if(arrowToCopy.hasMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW)) {
            newArrow.setMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW,
                    arrowToCopy.getMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW).get(0));
        }
    }
}
