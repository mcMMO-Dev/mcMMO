package com.gmail.nossr50.commands.database;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.locale.LocaleLoader;
import com.google.common.collect.ImmutableList;

public class MmoshowdbCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            return false;
        }

        Class<?> clazz = DatabaseManagerFactory.getCustomDatabaseManagerClass();

        if (clazz != null) {
            sender.sendMessage(LocaleLoader.getString("Commands.mmoshowdb", clazz.getName()));
            return true;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mmoshowdb", (Config.getInstance().getUseMySQL() ? "sql" : "flatfile")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
