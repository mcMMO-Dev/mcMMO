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

public class McgodCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;

        switch (args.length) {
            case 0:
                if (!Permissions.mcgod(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!(sender instanceof Player)) {
                    return false;
                }

                profile = UserManager.getPlayer((Player) sender).getProfile();

                if (profile == null) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                if (profile.getGodMode()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.GodMode.Disabled"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.GodMode.Enabled"));
                }

                profile.toggleGodMode();
                return true;

            case 1:
                if (!Permissions.mcgodOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);

                if (mcMMOPlayer == null) {
                    profile = new PlayerProfile(args[0], false);

                    if (!profile.isLoaded()) {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                        return true;
                    }

                    sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                    return true;
                }

                profile = mcMMOPlayer.getProfile();
                Player player = mcMMOPlayer.getPlayer();

                if (!player.isOnline()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                    return true;
                }

                if (profile.getGodMode()) {
                    player.sendMessage(LocaleLoader.getString("Commands.GodMode.Disabled"));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Commands.GodMode.Enabled"));
                }

                profile.toggleGodMode();
                return true;

            default:
                return false;
        }
    }
}
