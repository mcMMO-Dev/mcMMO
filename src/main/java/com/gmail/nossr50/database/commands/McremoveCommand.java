package com.gmail.nossr50.database.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.database.Leaderboard;
import com.gmail.nossr50.locale.LocaleLoader;

public class McremoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName;
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        //String databaseName = Config.getInstance().getMySQLDatabaseName();
        String usage = LocaleLoader.getString("Commands.Usage.1", "mcremove", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">");
        String success;

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mcremove")) {
            return true;
        }

        switch (args.length) {
        case 1:
            playerName = args[0];
            success = LocaleLoader.getString("Commands.mcremove.Success", playerName);
            break;

        default:
            sender.sendMessage(usage);
            return true;
        }

        /* MySQL */
        if (Config.getInstance().getUseMySQL()) {
            int affected = 0;
            affected = Database.update("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.user = '" + playerName + "'");

            if (affected > 0) {
                sender.sendMessage(success);
            } else {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
            }
        }
        else {
            if (Leaderboard.removeFlatFileUser(playerName)) {
                sender.sendMessage(success);
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
            }
        }

        Database.profileCleanup(playerName);

        return true;
    }
}
