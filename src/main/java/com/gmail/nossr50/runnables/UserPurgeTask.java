package com.gmail.nossr50.runnables;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Database;

public class UserPurgeTask implements Runnable {
    private Plugin plugin;
    private Database database = mcMMO.getPlayerDatabase();
    private String tablePrefix = Config.getInstance().getMySQLTablePrefix();
    private String databaseName = Config.getInstance().getMySQLDatabaseName();

    public UserPurgeTask(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (Config.getInstance().getUseMySQL()) {
            purgePowerlessSQL();
            purgeOldSQL();
        }
        else {
            //TODO: Make this work for Flatfile data.
        }
    }

    private void purgePowerlessSQL() {
        String query = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";
        HashMap<Integer, ArrayList<String>> userslist = database.read("SELECT " + query + ", user_id FROM " + tablePrefix + "skills WHERE " + query + " > 0 ORDER BY " + query + " DESC ");

        int purgedUsers = 0;

        for (int i = 1; i <= userslist.size(); i++) {
            int userId = Integer.valueOf(userslist.get(i).get(1));
            HashMap<Integer, ArrayList<String>> username = database.read("SELECT user FROM " + tablePrefix + "users WHERE id = '" + userId + "'");

            if (username != null && Bukkit.getOfflinePlayer(username.get(1).get(0)).isOnline()) {
                continue;
            }

            deleteFromSQL(userId);
            purgedUsers++;
        }

        plugin.getLogger().info("Purged " + purgedUsers + "users from the database.");
    }

    private void purgeOldSQL() {
        String query = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";
        HashMap<Integer, ArrayList<String>> userslist = database.read("SELECT " + query + ", user_id FROM " + tablePrefix + "skills WHERE " + query + " > 0 ORDER BY " + query + " DESC ");

        int purgedUsers = 0;

        for (int i = 1; i <= userslist.size(); i++) {
            int userId = Integer.valueOf(userslist.get(i).get(1));
            long lastLoginTime = database.getInt("SELECT lastlogin FROM " + tablePrefix + "users WHERE id = '" + userId + "'") * 1000L;
            long loginDifference = System.currentTimeMillis() - lastLoginTime;

            if (loginDifference > 2630000000L) {
                deleteFromSQL(userId);
                purgedUsers++;
            }
        }

        plugin.getLogger().info("Purged " + purgedUsers + "users from the database.");
    }

    private void deleteFromSQL(int userId) {
        database.write("DELETE FROM "
                + databaseName + "."
                + tablePrefix + "users WHERE "
                + tablePrefix + "users.id=" + userId);
    
        database.write("DELETE FROM "
                + databaseName + "."
                + tablePrefix + "cooldowns WHERE "
                + tablePrefix + "cooldowns.user_id=" + userId);

        database.write("DELETE FROM "
                + databaseName + "."
                + tablePrefix + "huds WHERE "
                + tablePrefix + "huds.user_id=" + userId);

        database.write("DELETE FROM "
                + databaseName + "."
                + tablePrefix + "skills WHERE "
                + tablePrefix + "skills.user_id=" + userId);

        database.write("DELETE FROM "
                + databaseName + "."
                + tablePrefix + "experience WHERE "
                + tablePrefix + "experience.user_id=" + userId);
    }
}
