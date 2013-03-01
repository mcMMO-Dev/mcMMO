package com.gmail.nossr50.commands.database;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.LeaderboardManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class McpurgeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.mcpurge(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
            case 0:
                if (Config.getInstance().getUseMySQL()) {
                    DatabaseManager.purgePowerlessSQL();

                    if (Config.getInstance().getOldUsersCutoff() != -1) {
                        DatabaseManager.purgeOldSQL();
                    }
                }
                else {
                    LeaderboardManager.purgePowerlessFlatfile();

                    if (Config.getInstance().getOldUsersCutoff() != -1) {
                        LeaderboardManager.purgeOldFlatfile();
                    }
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mcpurge.Success"));
                return true;

            default:
                return false;
        }
    }
}
