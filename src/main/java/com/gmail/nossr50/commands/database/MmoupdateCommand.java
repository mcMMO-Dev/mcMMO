package com.gmail.nossr50.commands.database;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.database.SQLConversionTask;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class MmoupdateCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Config.getInstance().getUseMySQL()) {
            sender.sendMessage("SQL Mode is not enabled."); // TODO: Localize
            return true;
        }

        switch (args.length) {
            case 0:
                sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Start"));
                UserManager.saveAll();
                UserManager.clearAll();
                new SQLConversionTask().runTaskLaterAsynchronously(mcMMO.p, 1);

                for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                    UserManager.addUser(player);
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Finish"));
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
