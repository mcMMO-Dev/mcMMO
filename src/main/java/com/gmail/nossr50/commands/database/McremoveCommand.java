package com.gmail.nossr50.commands.database;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.LeaderboardManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class McremoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.mcremove(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
            case 1:
                /* MySQL */
                if (Config.getInstance().getUseMySQL()) {
                    String tablePrefix = Config.getInstance().getMySQLTablePrefix();

                    if (DatabaseManager.update("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.user = '" + args[0] + "'") != 0) {
                        Misc.profileCleanup(args[0]);
                        sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", args[0]));
                    }
                    else {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    }
                }
                else {
                    if (LeaderboardManager.removeFlatFileUser(args[0])) {
                        Misc.profileCleanup(args[0]);
                        sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", args[0]));
                    }
                    else {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    }
                }

                return true;

            default:
                return false;
        }
    }
}
