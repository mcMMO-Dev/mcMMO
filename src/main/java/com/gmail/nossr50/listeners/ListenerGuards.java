package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shared guard preamble for event handlers that act on a player's mcMMO data.
 */
final class ListenerGuards {

    private ListenerGuards() {
    }

    /**
     * Runs the standard handler gates in cheapest-first order — world blacklist, WorldGuard
     * main region flag, player data key, loaded profile — and resolves the player's mcMMO
     * data. New handlers should keep that ordering: set lookups and metadata checks come
     * before WorldGuard region resolution and profile lookups.
     *
     * @param player the player the event concerns
     * @return the player's loaded McMMOPlayer, or null when the handler should ignore the
     * event
     */
    static @Nullable McMMOPlayer resolveEligiblePlayer(@NotNull Player player) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(player.getWorld())) {
            return null;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()
                && !WorldGuardManager.getInstance().hasMainFlag(player)) {
            return null;
        }

        if (!UserManager.hasPlayerDataKey(player)) {
            return null;
        }

        //Profile not loaded
        return UserManager.getPlayer(player);
    }
}
