package com.gmail.nossr50.database.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.database.Leaderboard;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class McpurgeCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.mcpurge(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 0:
            if (Config.getInstance().getUseMySQL()) {
                Database.purgePowerlessSQL();

                if (Config.getInstance().getOldUsersCutoff() != -1) {
                    Database.purgeOldSQL();
                }
            }
            else {
                Leaderboard.purgePowerlessFlatfile();

                if (Config.getInstance().getOldUsersCutoff() != -1) {
                    Leaderboard.purgeOldFlatfile();
                }
            }

            sender.sendMessage(LocaleLoader.getString("Commands.mcpurge.Success"));
            return true;

        default:
            return false;
        }
    }
}
