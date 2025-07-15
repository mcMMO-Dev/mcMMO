package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

public class McpurgeCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (args.length == 0) {
            mcMMO.getDatabaseManager().purgePowerlessUsers();

            if (mcMMO.p.getGeneralConfig().getOldUsersCutoff() != -1) {
                mcMMO.getDatabaseManager().purgeOldUsers();
            }

            sender.sendMessage(LocaleLoader.getString("Commands.mcpurge.Success"));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        return ImmutableList.of();
    }
}
