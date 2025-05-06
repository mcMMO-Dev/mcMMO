package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.gmail.nossr50.util.MetadataConstants.ARROW_METADATA_KEYS;
import static com.gmail.nossr50.util.MetadataConstants.MCMMO_METADATA_VALUE;

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
    public static void cleanupProjectileMetadata(@NotNull AbstractArrow arrow) {
        ARROW_METADATA_KEYS.stream()
                .filter(arrow::hasMetadata)
                .forEach(key -> arrow.removeMetadata(key, mcMMO.p));
    }

    /**
     * Copies metadata from one arrow to another.
     *
     * @param pluginRef mcMMO plugin reference.
     * @param sourceArrow The arrow from which metadata is copied.
     * @param targetArrow The arrow to which metadata is copied.
     */
    public static void copyArrowMetadata(@NotNull Plugin pluginRef, @NotNull Arrow sourceArrow,
                                         @NotNull Arrow targetArrow) {
        ARROW_METADATA_KEYS.stream()
                .filter(sourceArrow::hasMetadata)
                .forEach(key -> {
                    final MetadataValue metadataValue = sourceArrow.getMetadata(key).get(0);
                    if (key.equals(MetadataConstants.METADATA_KEY_BOW_FORCE)) {
                        targetArrow.setMetadata(key, new FixedMetadataValue(pluginRef, metadataValue.asDouble()));
                    } else if (key.equals(MetadataConstants.METADATA_KEY_CROSSBOW_PROJECTILE)) {
                        targetArrow.setMetadata(key, MCMMO_METADATA_VALUE);
                    } else {
                        targetArrow.setMetadata(key, metadataValue);
                    }
                });
    }

    public static boolean isCrossbowProjectile(@NotNull AbstractArrow arrow) {
        return arrow.isShotFromCrossbow()
                || arrow.hasMetadata(MetadataConstants.METADATA_KEY_CROSSBOW_PROJECTILE);
    }
}
