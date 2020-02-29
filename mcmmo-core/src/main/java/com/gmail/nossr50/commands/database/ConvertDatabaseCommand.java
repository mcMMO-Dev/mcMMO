package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.DatabaseConversionTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConvertDatabaseCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public ConvertDatabaseCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                DatabaseType previousType = getDatabaseType(args[1]);
                DatabaseType newType = pluginRef.getDatabaseManager().getDatabaseType();

                if (previousType == newType || (newType == DatabaseType.CUSTOM && pluginRef.getDatabaseManagerFactory().getCustomDatabaseManagerClass().getSimpleName().equalsIgnoreCase(args[1]))) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Database.Same", newType.toString()));
                    return true;
                }

                DatabaseManager oldDatabase = pluginRef.getDatabaseManagerFactory().createDatabaseManager(previousType);

                if (previousType == DatabaseType.CUSTOM) {
                    Class<?> clazz;

                    try {
                        clazz = Class.forName(args[1]);

                        if (!DatabaseManager.class.isAssignableFrom(clazz)) {
                            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Database.InvalidType", args[1]));
                            return true;
                        }

                        oldDatabase = pluginRef.getDatabaseManagerFactory().createCustomDatabaseManager((Class<? extends DatabaseManager>) clazz);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Database.InvalidType", args[1]));
                        return true;
                    }
                }

                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Database.Start", previousType.toString(), newType.toString()));

                pluginRef.getUserManager().saveAll();
                pluginRef.getUserManager().clearAll();

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    PlayerProfile profile = oldDatabase.loadPlayerProfile(player.getUniqueId());

                    if (profile.isLoaded()) {
                        pluginRef.getDatabaseManager().saveUser(profile);
                    }

                    pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                            .setAsync(true)
                            .setDelay(1L) // 1 Tick delay to ensure the player is marked as online before we begin loading
                            .setTask(new PlayerProfileLoadingTask(pluginRef, player))
                            .schedule();
                }

                pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                        .setAsync(true)
                        .setTask(new DatabaseConversionTask(pluginRef, oldDatabase, sender, previousType.toString(), newType.toString()))
                        .schedule();
                return true;

            default:
                return false;
        }
    }

    public DatabaseType getDatabaseType(String typeName) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }

        if (typeName.equalsIgnoreCase("file")) {
            return DatabaseType.FLATFILE;
        } else if (typeName.equalsIgnoreCase("mysql")) {
            return DatabaseType.SQL;
        }

        return DatabaseType.CUSTOM;
    }
}
