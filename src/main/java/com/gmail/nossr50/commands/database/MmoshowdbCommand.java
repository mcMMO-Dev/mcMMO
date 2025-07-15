package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

public class MmoshowdbCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (args.length == 0) {
            Class<?> clazz = DatabaseManagerFactory.getCustomDatabaseManagerClass();

            if (clazz != null) {
                sender.sendMessage(LocaleLoader.getString("Commands.mmoshowdb", clazz.getName()));
                return true;
            }

            sender.sendMessage(LocaleLoader.getString("Commands.mmoshowdb",
                    (mcMMO.p.getGeneralConfig().getUseMySQL() ? "sql" : "flatfile")));
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
