package com.gmail.nossr50.database.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.database.Leaderboard;
import com.gmail.nossr50.locale.LocaleLoader;

public class McremoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
        case 1:
            /* MySQL */
            if (Config.getInstance().getUseMySQL()) {
                String tablePrefix = Config.getInstance().getMySQLTablePrefix();

                if (Database.update("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.user = '" + args[0] + "'") != 0) {
                    Database.profileCleanup(args[0]);
                    sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", args[0]));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                }
            }
            else {
                if (Leaderboard.removeFlatFileUser(args[0])) {
                    Database.profileCleanup(args[0]);
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
