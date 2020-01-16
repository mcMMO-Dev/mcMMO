package com.gmail.nossr50.runnables.items;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportationWarmup extends BukkitRunnable {
    private final mcMMO pluginRef;
    private BukkitMMOPlayer mcMMOPlayer;
    private BukkitMMOPlayer mcMMOTarget;

    public TeleportationWarmup(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer, BukkitMMOPlayer mcMMOTarget) {
        this.pluginRef = pluginRef;
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
            int timeRemaining = pluginRef.getSkillTools().calculateTimeLeft(recentlyHurt * pluginRef.getMiscTools().TIME_CONVERSION_FACTOR, hurtCooldown, teleportingPlayer);

            if (timeRemaining > 0) {
                teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Item.Injured.Wait", timeRemaining));
                return;
            }
        }

        if (pluginRef.getConfigManager().getConfigParty().getPTP().isPtpWorldBasedPermissions()) {
            World targetWorld = targetPlayer.getWorld();
            World playerWorld = teleportingPlayer.getWorld();

            if (!pluginRef.getPermissionTools().partyTeleportAllWorlds(teleportingPlayer)) {
                if (!pluginRef.getPermissionTools().partyTeleportWorld(targetPlayer, targetWorld)) {
                    teleportingPlayer.sendMessage(pluginRef.getLocaleManager().formatString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
                else if (targetWorld != playerWorld && !pluginRef.getPermissionTools().partyTeleportWorld(teleportingPlayer, targetWorld)) {
                    teleportingPlayer.sendMessage(pluginRef.getLocaleManager().formatString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
            }
        }


        pluginRef.getEventManager().handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
    }
}
