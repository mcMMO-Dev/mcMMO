package com.gmail.nossr50.commands.party.teleport;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class PtpAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.partyTeleportAccept(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
        Player player = mcMMOPlayer.getPlayer();

        if (!mcMMOPlayer.hasPtpRequest()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.NoRequests"));
            return true;
        }

        if ((mcMMOPlayer.getPtpTimeout() + Config.getInstance().getPTPCommandTimeout()) * Misc.TIME_CONVERSION_FACTOR < System.currentTimeMillis()) {
            mcMMOPlayer.removePtpRequest();
            player.sendMessage(LocaleLoader.getString("Commands.ptp.RequestExpired"));
            return true;
        }

        Player target = mcMMOPlayer.getPtpRequest();
        mcMMOPlayer.removePtpRequest();

        if (!PtpCommand.canTeleport(sender, player, target.getName())) {
            return true;
        }

        if (Config.getInstance().getPTPCommandWorldPermissions()) {
            World targetWorld = target.getWorld();
            World playerWorld = player.getWorld();

            if (!Permissions.partyTeleportAllWorlds(target)) {
                if (!Permissions.partyTeleportWorld(target, targetWorld)) {
                    target.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", targetWorld.getName()));
                    return true;
                }
                else if (targetWorld != playerWorld && !Permissions.partyTeleportWorld(target, playerWorld)) {
                    target.sendMessage(LocaleLoader.getString("Commands.ptp.NoWorldPermissions", playerWorld.getName()));
                    return true;
                }
            }
        }

        PtpCommand.handlePartyTeleportEvent(target, player);
        return true;
    }
}
