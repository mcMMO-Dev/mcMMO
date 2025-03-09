package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import org.bukkit.command.CommandSender;

public class DatabaseConversionTask extends CancellableRunnable {
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

        mcMMO.p.getFoliaLib().getScheduler().runNextTick(t -> sender.sendMessage(message));
    }
}
