package com.gmail.nossr50.runnables.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;

public class TeleportationWarmup extends BukkitRunnable {
    private static Player teleportingPlayer;
    private McMMOPlayer mcMMOPlayer;
    private static Player targetPlayer;
    private McMMOPlayer mcMMOTarget;

    public TeleportationWarmup(McMMOPlayer mcMMOPlayer, McMMOPlayer mcMMOTarget) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.mcMMOTarget = mcMMOTarget;
    }

    @Override
    public void run() {
        checkPartyTeleport();
    }

    private void checkPartyTeleport() {
        teleportingPlayer = mcMMOPlayer.getPlayer();
        targetPlayer = mcMMOTarget.getPlayer();
        Location previousLocation = mcMMOPlayer.getTeleportCommenceLocation();
        Location newLocation = mcMMOPlayer.getPlayer().getLocation();
        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();

        mcMMOPlayer.setTeleportCommenceLocation(null);

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

        PtpCommand.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
    }
}
