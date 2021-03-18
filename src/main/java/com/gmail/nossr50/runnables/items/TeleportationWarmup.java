package com.gmail.nossr50.runnables.items;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportationWarmup extends BukkitRunnable {
    private final McMMOPlayer mmoPlayer;
    private final McMMOPlayer mcMMOTarget;

    public TeleportationWarmup(McMMOPlayer mmoPlayer, McMMOPlayer mcMMOTarget) {
        this.mmoPlayer = mmoPlayer;
        this.mcMMOTarget = mcMMOTarget;
    }

    @Override
    public void run() {
        Player teleportingPlayer = Misc.adaptPlayer(mmoPlayer);
        Player targetPlayer = mcMMOTarget.getPlayer();
        Location previousLocation = mmoPlayer.getTeleportCommenceLocation();
        Location newLocation = Misc.adaptPlayer(mmoPlayer).getLocation();
        long recentlyHurt = mmoPlayer.getRecentlyHurtTimestamp();

        mmoPlayer.setTeleportCommenceLocation(null);

        if (!mcMMO.getPartyManager().inSameParty(teleportingPlayer, targetPlayer)) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetPlayer.getName()));
            return;
        }

        if (newLocation.distanceSquared(previousLocation) > 1.0) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Teleport.Cancelled"));
            return;
        }

        int hurtCooldown = Config.getInstance().getPTPCommandRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, teleportingPlayer);

            if (timeRemaining > 0) {
                teleportingPlayer.sendMessage(LocaleLoader.getString("Item.Injured.Wait", timeRemaining));
                return;
            }
        }

        if (Config.getInstance().getPTPCommandWorldPermissions()) {
            World targetWorld = targetPlayer.getWorld();
            World playerWorld = teleportingPlayer.getWorld();

            if (!Permissions.partyTeleportAllWorlds(teleportingPlayer)) {
                if (!Permissions.partyTeleportWorld(targetPlayer, targetWorld)) {
                    teleportingPlayer.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
                else if (targetWorld != playerWorld && !Permissions.partyTeleportWorld(teleportingPlayer, targetWorld)) {
                    teleportingPlayer.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return;
                }
            }
        }


        EventUtils.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
    }
}
