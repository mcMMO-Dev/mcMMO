package com.gmail.nossr50.runnables.database;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleLoader;

public class ConversionTask extends BukkitRunnable {
    private final DatabaseManager sourceDb;
    private final CommandSender sender;
    private final String message;

    public ConversionTask(DatabaseManager from, CommandSender sendback, String oldType, String newType) {
        sourceDb = from;
        sender = sendback;
        message = LocaleLoader.getString("Commands.mmoupdate.Finish", oldType, newType);
    }

    @Override
    public void run() {
        sourceDb.convertUsers(mcMMO.getDatabaseManager());

        // Announce completeness
        mcMMO.p.getServer().getScheduler().runTask(mcMMO.p, new CompleteAnnouncement());
    }

    public class CompleteAnnouncement implements Runnable {
        @Override
        public void run() {
            try {
                sender.sendMessage(message);
            } catch (Exception e) {
                mcMMO.p.getLogger().log(Level.WARNING, "Exception sending database conversion completion message to " + sender.getName(), e);
            }
        }
    }
}
