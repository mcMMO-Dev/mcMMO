package com.gmail.nossr50.commands;

import com.gmail.nossr50.database.FlatFileDatabaseManager;
import com.gmail.nossr50.database.SQLDatabaseManager;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class ResetUserHealthBarSettingsCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public ResetUserHealthBarSettingsCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getDatabaseManager() instanceof SQLDatabaseManager) {
            SQLDatabaseManager sqlDatabaseManager = (SQLDatabaseManager) pluginRef.getDatabaseManager();
            sqlDatabaseManager.resetMobHealthSettings();
            for (McMMOPlayer player : pluginRef.getUserManager().getPlayers()) {
                player.getProfile().setMobHealthbarType(pluginRef.getConfigManager().getConfigMobs().getCombat().getHealthBars().getDisplayBarType());
            }
            sender.sendMessage("Mob health reset");
            return true;
        }
        if (pluginRef.getDatabaseManager() instanceof FlatFileDatabaseManager) {
            FlatFileDatabaseManager flatFileDatabaseManager = (FlatFileDatabaseManager) pluginRef.getDatabaseManager();
            flatFileDatabaseManager.resetMobHealthSettings();
            for (McMMOPlayer player : pluginRef.getUserManager().getPlayers()) {
                player.getProfile().setMobHealthbarType(pluginRef.getConfigManager().getConfigMobs().getCombat().getHealthBars().getDisplayBarType());
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
