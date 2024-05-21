package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
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
    /**
     * Process events that may happen from a crossbow hitting an entity.
     *
     * @param event      the projectile hit event
     * @param pluginRef  the plugin ref
     * @param arrow      the arrow
     */
    public static void processCrossbows(ProjectileHitEvent event, Plugin pluginRef, Arrow arrow) {
        if (arrow.getShooter() instanceof Player) {
            McMMOPlayer mmoPlayer = UserManager.getPlayer((Player) arrow.getShooter());
            if (mmoPlayer == null)
                return;

            processTrickShot(event, pluginRef, arrow, mmoPlayer);
        }
    }

    private static void processTrickShot(ProjectileHitEvent event, Plugin pluginRef, Arrow arrow, McMMOPlayer mmoPlayer) {
        if (event.getHitBlock() != null && event.getHitBlockFace() != null) {
            mmoPlayer.getCrossbowsManager().handleRicochet(
                    pluginRef,
                    arrow,
                    getNormal(event.getHitBlockFace()));
        }
    }
}
