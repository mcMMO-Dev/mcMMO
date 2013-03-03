package com.gmail.nossr50.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class McrefreshCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        McMMOPlayer mcMMOPlayer;

        switch (args.length) {
            case 0:
                if (!Permissions.mcrefresh(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!(sender instanceof Player)) {
                    return false;
                }

                mcMMOPlayer = UserManager.getPlayer(sender.getName());

                mcMMOPlayer.setRecentlyHurt(0);
                mcMMOPlayer.getProfile().resetCooldowns();
                mcMMOPlayer.resetToolPrepMode();
                mcMMOPlayer.resetAbilityMode();

                sender.sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
                return true;

            case 1:
                if (!Permissions.mcrefreshOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(args[0]);

                if (mcMMOPlayer == null) {
                    PlayerProfile playerProfile = new PlayerProfile(args[0], false);

                    if (!playerProfile.isLoaded()) {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                        return true;
                    }

                    sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                    return true;
                }

                Player player = mcMMOPlayer.getPlayer();

                if (!player.isOnline()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                    return true;
                }

                mcMMOPlayer.setRecentlyHurt(0);
                mcMMOPlayer.getProfile().resetCooldowns();
                mcMMOPlayer.resetToolPrepMode();
                mcMMOPlayer.resetAbilityMode();

                player.sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
                sender.sendMessage(LocaleLoader.getString("Commands.mcrefresh.Success", args[0]));
                return true;

            default:
                return false;
        }
    }
}
