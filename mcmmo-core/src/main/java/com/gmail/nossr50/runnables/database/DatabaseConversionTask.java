package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public class DatabaseConversionTask implements Consumer<Task> {
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
    public void accept(Task task) {
        sourceDatabase.convertUsers(pluginRef.getDatabaseManager());
        sender.sendMessage(message);
    }
}
