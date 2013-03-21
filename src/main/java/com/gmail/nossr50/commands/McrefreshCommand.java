package com.gmail.nossr50.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class McrefreshCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.mcrefresh(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(sender.getName());
                player = mcMMOPlayer.getPlayer();

                refreshPlayer();
                return true;

            case 1:
                if (!Permissions.mcrefreshOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(args[0]);

                if (CommandUtils.checkPlayerExistence(sender, args[0], mcMMOPlayer)) {
                    return true;
                }

                Player player = mcMMOPlayer.getPlayer();

                if (CommandUtils.isOffline(sender, player)) {
                    return true;
                }

                refreshPlayer();
                sender.sendMessage(LocaleLoader.getString("Commands.mcrefresh.Success", args[0]));
                return true;

            default:
                return false;
        }
    }

    private void refreshPlayer() {
        mcMMOPlayer.setRecentlyHurt(0);
        mcMMOPlayer.getProfile().resetCooldowns();
        mcMMOPlayer.resetToolPrepMode();
        mcMMOPlayer.resetAbilityMode();

        player.sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
    }
}
