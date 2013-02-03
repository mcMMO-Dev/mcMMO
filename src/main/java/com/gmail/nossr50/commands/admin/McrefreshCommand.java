package com.gmail.nossr50.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class McrefreshCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer player;
        PlayerProfile profile;
        String usage = LocaleLoader.getString("Commands.Usage.1", "mcrefresh", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]");

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcrefresh")) {
            return true;
        }

        switch (args.length) {
        case 0:
            if (sender instanceof Player) {
                player = (Player) sender;
                profile = Users.getProfile(player);
            }
            else {
                sender.sendMessage(usage);
                return true;
            }
            break;

        case 1:
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcrefresh.others")) {
                return true;
            }

            player = mcMMO.p.getServer().getOfflinePlayer(args[0]);
            profile = Users.getProfile(player);
            String playerName = player.getName();

            if (profile == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            sender.sendMessage(LocaleLoader.getString("Commands.mcrefresh.Success", playerName));

            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        profile.setRecentlyHurt(0);
        profile.resetCooldowns();
        profile.resetToolPrepMode();
        profile.resetAbilityMode();

        if (player.isOnline()) {
            ((Player) player).sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
        }

        return true;
    }
}
