package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class PurgeCommand implements TabExecutor {

    private mcMMO pluginRef;

    public PurgeCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                pluginRef.getDatabaseManager().purgePowerlessUsers();

                if (pluginRef.getDatabaseCleaningSettings().getOldUserCutoffMonths() != -1) {
                    pluginRef.getDatabaseManager().purgeOldUsers();
                }

                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcpurge.Success"));
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
