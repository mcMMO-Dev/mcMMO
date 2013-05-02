package com.gmail.nossr50.runnables.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

        if (!PartyManager.inSameParty(teleportingPlayer, targetPlayer)) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetPlayer.getName()));
            return;
        }

        if (newLocation.distanceSquared(previousLocation) > 1.0) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Teleport.Cancelled"));
            return;
        }

        int recentlyhurt_cooldown = Config.getInstance().getPTPCommandRecentlyHurtCooldown();

        if (!SkillUtils.cooldownOver(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, teleportingPlayer)) {
            teleportingPlayer.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, teleportingPlayer)));
            return;
        }

        PtpCommand.handlePartyTeleportEvent(teleportingPlayer, targetPlayer);
    }
}
