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

public class McabilityCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.mcability(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(sender.getName());
                player = mcMMOPlayer.getPlayer();

                toggleAbilityUse();
                return true;

            case 1:
                if (!Permissions.mcabilityOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(args[0]);

                if (CommandUtils.checkPlayerExistence(sender, args[0], mcMMOPlayer)) {
                    return true;
                }

                player = mcMMOPlayer.getPlayer();

                if (CommandUtils.isOffline(sender, player)) {
                    return true;
                }

                toggleAbilityUse();
                sender.sendMessage("Ability use has been toggled for" + args[0]); // TODO: Localize
                return true;

            default:
                return false;
        }
    }

    private void toggleAbilityUse() {
        if (mcMMOPlayer.getAbilityUse()) {
            player.sendMessage(LocaleLoader.getString("Commands.Ability.Off"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.Ability.On"));
        }

        mcMMOPlayer.toggleAbilityUse();
    }
}
