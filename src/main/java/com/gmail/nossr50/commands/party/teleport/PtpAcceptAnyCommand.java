package com.gmail.nossr50.commands.party.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class PtpAcceptAnyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.partyTeleportAcceptAll(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
        Player player = mcMMOPlayer.getPlayer();

        if (mcMMOPlayer.getPtpConfirmRequired()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.AcceptAny.Enabled"));
        }

        mcMMOPlayer.togglePtpConfirmRequired();
        return true;
    }
}
