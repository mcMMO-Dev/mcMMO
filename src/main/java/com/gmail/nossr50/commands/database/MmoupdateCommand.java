package com.gmail.nossr50.commands.database;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.database.ConversionTask;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class MmoupdateCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                String argType = args[0];
                String oldType = validateName(sender, args[0]);
                if (oldType == null) {
                    return true;
                }

                String newType = getCurrentDb();

                if (newType.equals(oldType)) {
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Same", argType));
                    return true;
                }

                DatabaseManager oldDb;
                if (oldType.equals("sql")) {
                    oldDb = DatabaseManagerFactory.createSQLDatabaseManager();
                }
                else if (oldType.equals("flatfile")) {
                    oldDb = DatabaseManagerFactory.createFlatfileDatabaseManager();
                }
                else try {
                    @SuppressWarnings("unchecked")
                    Class<? extends DatabaseManager> clazz = (Class<? extends DatabaseManager>) Class.forName(oldType);
                    oldDb = DatabaseManagerFactory.createCustomDatabaseManager(clazz);

                    oldType = clazz.getSimpleName(); // For pretty-printing; we have the database now
                }
                catch (Throwable e) {
                    return false;
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.Start", oldType, newType));

                // Convert the online players right away, without waiting
                // first, flush out the current data
                UserManager.saveAll();
                UserManager.clearAll();

                for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                    // Get the profile from the old database and save it in the new
                    PlayerProfile profile = oldDb.loadPlayerProfile(player.getName(), false);
                    if (profile.isLoaded()) {
                        mcMMO.getDatabaseManager().saveUser(profile);
                    }

                    // Reload from the current database via UserManager
                    UserManager.addUser(player);
                }

                // Schedule the task for all users
                new ConversionTask(oldDb, sender, oldType, newType).runTaskAsynchronously(mcMMO.p);

                return true;

            default:
                break;
        }
        return false;
    }

    /**
     * @return null - if type not recognized / class not found
     *         empty string - if type is same as current
     *         normalized string - if type is recognized
     */
    private String validateName(CommandSender sender, String type) {
        if (type.equalsIgnoreCase("sql") || type.equalsIgnoreCase("mysql")) {
            return "sql";
        }

        if (type.equalsIgnoreCase("flatfile") || type.equalsIgnoreCase("file")) {
            return "flatfile";
        }

        try {
            Class<?> clazz = Class.forName(type);

            if (!DatabaseManager.class.isAssignableFrom(clazz)) {
                sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.InvalidType", type));
                return null;
            }

            return type;
        }
        catch (Exception e) {
            sender.sendMessage(LocaleLoader.getString("Commands.mmoupdate.InvalidType", type));
            return null;
        }
    }

    private String getCurrentDb() {
        if (DatabaseManagerFactory.getCustomDatabaseManagerClass() != null) {
            return DatabaseManagerFactory.getCustomDatabaseManagerClass().getSimpleName();
        }

        return Config.getInstance().getUseMySQL() ? "sql" : "flatfile";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Class<?> clazz = DatabaseManagerFactory.getCustomDatabaseManagerClass();
        if (clazz != null) {
            return ImmutableList.of("flatfile", "sql", clazz.getName());
        }
        return ImmutableList.of("flatfile", "sql");
    }
}
