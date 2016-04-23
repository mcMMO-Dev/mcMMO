package com.gmail.nossr50.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.FlatfileDatabaseManager;
import com.gmail.nossr50.database.SQLDatabaseManager;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class MHDCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (mcMMO.getDatabaseManager() instanceof SQLDatabaseManager) {
            SQLDatabaseManager m = (SQLDatabaseManager) mcMMO.getDatabaseManager();
            m.resetMobHealthSettings();
            for (McMMOPlayer player : UserManager.getPlayers()) {
                player.getProfile().setMobHealthbarType(Config.getInstance().getMobHealthbarDefault());
            }
            sender.sendMessage("Mob health reset");
            return true;
        }
        if (mcMMO.getDatabaseManager() instanceof FlatfileDatabaseManager) {
            FlatfileDatabaseManager m = (FlatfileDatabaseManager) mcMMO.getDatabaseManager();
            m.resetMobHealthSettings();
            for (McMMOPlayer player : UserManager.getPlayers()) {
                player.getProfile().setMobHealthbarType(Config.getInstance().getMobHealthbarDefault());
            }
            sender.sendMessage("Mob health reset");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
