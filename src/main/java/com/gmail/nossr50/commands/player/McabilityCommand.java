package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class McabilityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;

        switch (args.length) {
        case 0:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.mcability")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            profile = Users.getPlayer((Player) sender).getProfile();

            if (profile.getAbilityUse()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Ability.Off"));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Ability.On"));
            }

            profile.toggleAbilityUse();
            return true;

        case 1:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.mcability.others")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            McMMOPlayer mcMMOPlayer = Users.getPlayer(args[0]);

            if (mcMMOPlayer == null) {
                profile = new PlayerProfile(args[0], false);

                if (!profile.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                return true;
            }

            Player player = mcMMOPlayer.getPlayer();
            profile = mcMMOPlayer.getProfile();

            if (!player.isOnline()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                return true;
            }

            if (profile.getAbilityUse()) {
                player.sendMessage(LocaleLoader.getString("Commands.Ability.Off"));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Commands.Ability.On"));
            }

            profile.toggleAbilityUse();
            return true;

        default:
            return false;
        }
    }
}
