package com.gmail.nossr50.runnables.items;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportationWarmup extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private McMMOPlayer mcMMOTarget;

    public TeleportationWarmup(McMMOPlayer mcMMOPlayer, McMMOPlayer mcMMOTarget) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.mcMMOTarget = mcMMOTarget;
    }

    @Override
    public void run() {
        Player teleportingPlayer = mcMMOPlayer.getPlayer();
        Player targetPlayer = mcMMOTarget.getPlayer();
        Location previousLocation = mcMMOPlayer.getTeleportCommenceLocation();
        Location newLocation = mcMMOPlayer.getPlayer().getLocation();
        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();

        mcMMOPlayer.setTeleportCommenceLocation(null);

        if (!pluginRef.getPartyManager().inSameParty(teleportingPlayer, targetPlayer)) {
            teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Party.NotInYourParty", targetPlayer.getName()));
            return;
        }

        if (newLocation.distanceSquared(previousLocation) > 1.0) {
            teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Teleport.Cancelled"));
            return;
        }

        int hurtCooldown = pluginRef.getConfigManager().getConfigParty().getPTP().getPtpRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, teleportingPlayer);

            if (timeRemaining > 0) {
                teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Item.Injured.Wait", timeRemaining));
                return;
            }
        }

        EventUtils.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
    }
}
