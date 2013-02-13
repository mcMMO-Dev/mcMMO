package com.gmail.nossr50.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Hardcore;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class HardcoreListener implements Listener {
    /**
     * Monitor PlayerDeath events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!Config.getInstance().getHardcoreEnabled()) {
            return;
        }

        Player player = event.getEntity();

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        if (!Permissions.hardcoremodeBypass(player)) {
            Player killer = player.getKiller();

            if (killer != null && Config.getInstance().getHardcoreVampirismEnabled()) {
                Hardcore.invokeVampirism(killer, player);
            }

            Hardcore.invokeStatPenalty(player);
        }
    }
}
