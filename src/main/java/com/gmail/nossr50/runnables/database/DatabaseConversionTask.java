package com.gmail.nossr50.runnables.database;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

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
        try {
            sourceDatabase.setLoadingDisabled(true);
            sourceDatabase.convertUsers(mcMMO.getDatabaseManager());
        }
        finally {
            sourceDatabase.setLoadingDisabled(false);
        }

        mcMMO.p.getServer().getScheduler().runTask(mcMMO.p, new Runnable() {
            @Override
            public void run() {
                sender.sendMessage(message);

                // Reload all users from the new database
                UserManager.clearAll();
                for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                    UserManager.addUser(player);
                }
            }
        });
   }
}
