package com.gmail.nossr50.database.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.runnables.SQLConversionTask;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class MmoupdateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noCommandPermissions(sender, "mcmmo.admin")) {
            return true;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Start"));
        Users.clearAll();
        convertToMySQL();

        for (Player x : mcMMO.p.getServer().getOnlinePlayers()) {
            Users.addUser(x);
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Finish"));

        return true;
    }

    /**
     * Convert FlatFile data to MySQL data.
     */
    private void convertToMySQL() {
        if (!Config.getInstance().getUseMySQL()) {
            return;
        }

        mcMMO.p.getServer().getScheduler().runTaskLaterAsynchronously(mcMMO.p, new SQLConversionTask(), 1);
    }
}
