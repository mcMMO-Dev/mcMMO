package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class DatabaseConversionTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private final DatabaseManager sourceDatabase;
    private final CommandSender sender;
    private final String message;

    public DatabaseConversionTask(mcMMO pluginRef, DatabaseManager sourceDatabase, CommandSender sender, String oldType, String newType) {
        this.pluginRef = pluginRef;
        this.sourceDatabase = sourceDatabase;
        this.sender = sender;
        this.message = pluginRef.getLocaleManager().getString("Commands.mcconvert.Database.Finish", oldType, newType);
    }

    @Override
    public void run() {
        sourceDatabase.convertUsers(pluginRef.getDatabaseManager());

        pluginRef.getServer().getScheduler().runTask(pluginRef, () -> sender.sendMessage(message));
    }
}
