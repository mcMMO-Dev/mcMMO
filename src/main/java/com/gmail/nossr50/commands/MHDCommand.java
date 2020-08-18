package com.gmail.nossr50.commands;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.FlatFileDatabaseManager;
import com.gmail.nossr50.database.SQLDatabaseManager;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MHDCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (mcMMO.getDatabaseManager() instanceof SQLDatabaseManager) {
            SQLDatabaseManager m = (SQLDatabaseManager) mcMMO.getDatabaseManager();
            m.resetMobHealthSettings();
            for (mmoPlayer player : mcMMO.getUserManager().getPlayers()) {
                player.getProfile().setMobHealthbarType(Config.getInstance().getMobHealthbarDefault());
            }
            sender.sendMessage("Mob health reset");
            return true;
        }
        if (mcMMO.getDatabaseManager() instanceof FlatFileDatabaseManager) {
            FlatFileDatabaseManager m = (FlatFileDatabaseManager) mcMMO.getDatabaseManager();
            m.resetMobHealthSettings();
            for (mmoPlayer player : mcMMO.getUserManager().getPlayers()) {
                player.getProfile().setMobHealthbarType(Config.getInstance().getMobHealthbarDefault());
            }
            sender.sendMessage("Mob health reset");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return ImmutableList.of();
    }
}
