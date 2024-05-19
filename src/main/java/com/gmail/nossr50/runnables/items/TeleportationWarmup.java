package com.gmail.nossr50.runnables.items;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportationWarmup extends CancellableRunnable {
    private final McMMOPlayer mcMMOPlayer;
    private final McMMOPlayer mcMMOTarget;

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

        if (!mcMMO.p.getPartyManager().inSameParty(teleportingPlayer, targetPlayer)) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetPlayer.getName()));
            return;
        }

        if (newLocation.distanceSquared(previousLocation) > 1.0) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Teleport.Cancelled"));
            return;
        }

        int hurtCooldown = mcMMO.p.getGeneralConfig().getPTPCommandRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, teleportingPlayer);

            if (timeRemaining > 0) {
                teleportingPlayer.sendMessage(LocaleLoader.getString("Item.Injured.Wait", timeRemaining));
                return;
            }
        }

        if (mcMMO.p.getGeneralConfig().getPTPCommandWorldPermissions()) {
            World targetWorld = targetPlayer.getWorld();
            World playerWorld = teleportingPlayer.getWorld();

            if (!Permissions.partyTeleportAllWorlds(teleportingPlayer)) {
                if (!Permissions.partyTeleportWorld(targetPlayer, targetWorld)) {
                    teleportingPlayer.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                } else if (targetWorld != playerWorld && !Permissions.partyTeleportWorld(teleportingPlayer, targetWorld)) {
                    teleportingPlayer.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
            }
        }


        EventUtils.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
    }
}
