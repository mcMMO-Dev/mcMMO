package com.gmail.nossr50.commands.database;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.database.SQLConversionTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class MmoupdateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.mmoupdate(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
            case 0:
                sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Start"));
                UserManager.clearAll();
                convertToMySQL();

                for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                    UserManager.addUser(player);
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Finish"));
                return true;

            default:
                return false;
        }
    }

    /**
     * Convert FlatFile data to MySQL data.
     */
    private void convertToMySQL() {
        if (!Config.getInstance().getUseMySQL()) {
            return;
        }

        new SQLConversionTask().runTaskLaterAsynchronously(mcMMO.p, 1);
    }
}
