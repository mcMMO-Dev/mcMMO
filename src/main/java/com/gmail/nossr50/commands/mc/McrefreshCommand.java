package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class McrefreshCommand implements CommandExecutor {
    private final McMMO plugin;

    public McrefreshCommand(McMMO instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer player;
        PlayerProfile PP;
        String usage = ChatColor.RED + "Proper usage is /mcrefresh [player]"; //TODO: Needs more locale

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mcrefresh")) {
            return true;
        }

        switch (args.length) {
        case 0:
            if (sender instanceof Player) {
                player = (Player) sender;
                PP = Users.getProfile(player);
            }
            else {
                sender.sendMessage(usage);
                return true;
            }
            break;

        case 1:
            player = plugin.getServer().getOfflinePlayer(args[0]);
            PP = Users.getProfile(player);
            String playerName = player.getName();

            if (!PP.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            sender.sendMessage("You have refreshed " + playerName + "'s cooldowns!"); //TODO: Use locale

            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        PP.setRecentlyHurt(0);
        PP.resetCooldowns();
        PP.resetToolPrepMode();
        PP.resetAbilityMode();

        if (player.isOnline()) {
            ((Player) player).sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
        }

        return true;
    }
}
