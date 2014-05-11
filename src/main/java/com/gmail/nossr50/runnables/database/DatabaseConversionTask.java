package com.gmail.nossr50.runnables.database;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleLoader;

public class DatabaseConversionTask extends BukkitRunnable {
    private final DatabaseManager sourceDatabase;
    private final CommandSender sender;
    private final String message;

    public DatabaseConversionTask(DatabaseManager sourceDatabase, CommandSender sender, String oldType, String newType) {
        this.sourceDatabase = sourceDatabase;
        this.sender = sender;
        message = LocaleLoader.getString("Commands.mcconvert.Database.Finish", oldType, newType);
    }

    @Override
    public void run() {
        sourceDatabase.convertUsers(mcMMO.getDatabaseManager());

        mcMMO.p.getServer().getScheduler().runTask(mcMMO.p, new Runnable() {
            @Override
            public void run() {
                sender.sendMessage(message);
            }
        });
    }
}
