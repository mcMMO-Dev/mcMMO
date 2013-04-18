package com.gmail.nossr50.commands.database;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

import com.google.common.collect.ImmutableList;

public class McpurgeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.mcpurge(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
            case 0:
                mcMMO.databaseManager.purgePowerlessUsers();

                if (Config.getInstance().getOldUsersCutoff() != -1) {
                    mcMMO.databaseManager.purgeOldUsers();
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mcpurge.Success"));
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
