package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
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
    }
}
