package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class ShowDatabaseCommand implements TabExecutor {

    private mcMMO pluginRef;

    public ShowDatabaseCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Class<?> clazz = pluginRef.getDatabaseManagerFactory().getCustomDatabaseManagerClass();

            if (clazz != null) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mmoshowdb", clazz.getName()));
                return true;
            }

            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mmoshowdb", (pluginRef.getMySQLConfigSettings().isMySQLEnabled() ? "sql" : "flatfile")));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
