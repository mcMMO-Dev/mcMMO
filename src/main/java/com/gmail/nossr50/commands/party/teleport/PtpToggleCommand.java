package com.gmail.nossr50.commands.party.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class PtpToggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.partyTeleportToggle(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
        Player player = mcMMOPlayer.getPlayer();

        if (mcMMOPlayer.getPtpEnabled()) {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Disabled"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.ptp.Enabled"));
        }

        mcMMOPlayer.togglePtpUse();
        return true;
    }
}
