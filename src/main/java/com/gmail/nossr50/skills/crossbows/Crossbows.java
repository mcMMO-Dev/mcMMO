package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

import static com.gmail.nossr50.util.skills.ProjectileUtils.getNormal;

/**
 * Util class for crossbows.
 */
public class Crossbows {
    public static void processCrossbows(ProjectileHitEvent event, Plugin pluginRef) {
        if(event.getEntity() instanceof Arrow originalArrow && event.getHitBlock() != null && event.getHitBlockFace() != null) {
            if (originalArrow.getShooter() instanceof Player) {
                // Avoid infinite spawning of arrows
                if (originalArrow.hasMetadata(MetadataConstants.METADATA_KEY_SPAWNED_ARROW)) {
                    return;
                }

                McMMOPlayer mmoPlayer = UserManager.getPlayer((Player) originalArrow.getShooter());
                if (mmoPlayer != null) {
                    mmoPlayer.getCrossbowsManager().handleRicochet(
                            pluginRef,
                            originalArrow,
                            getNormal(event.getHitBlockFace()));
                }
            }
        }
    }
}
