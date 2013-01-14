package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Database;

public class McpurgeCommand implements CommandExecutor{
    private Database database = mcMMO.getPlayerDatabase();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mcremove")) {
            return true;
        }

        if (Config.getInstance().getUseMySQL()) {
            database.purgePowerlessSQL();

            if (Config.getInstance().getOldUsersCutoff() != -1) {
                database.purgeOldSQL();
            }
        }
        else {
            //TODO: Make this work for Flatfile data.
        }

        sender.sendMessage(ChatColor.GREEN + "The database was successfully purged!"); //TODO: Locale)
        return true;
    }
}
