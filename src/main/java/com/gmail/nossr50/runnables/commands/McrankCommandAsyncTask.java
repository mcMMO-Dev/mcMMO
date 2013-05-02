package com.gmail.nossr50.runnables.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.FlatfileDatabaseManager;
import com.gmail.nossr50.database.SQLDatabaseManager;

public class McrankCommandAsyncTask extends BukkitRunnable {
    private final String playerName;
    private final CommandSender sender;

    public McrankCommandAsyncTask(String playerName, CommandSender sender) {
        this.playerName = playerName;
        this.sender = sender;
    }

    @Override
    public void run() {
        Map<String, Integer> skills = Config.getInstance().getUseMySQL() ? SQLDatabaseManager.readSQLRank(playerName) : FlatfileDatabaseManager.getPlayerRanks(playerName);

        new McrankCommandDisplayTask(skills, sender, playerName).runTaskLater(mcMMO.p, 1);
    }
}
