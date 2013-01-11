package com.gmail.nossr50.runnables;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.SpoutHud;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Users;

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
        System.out.println("Purging powerless users...");
        String query = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";
        HashMap<Integer, ArrayList<String>> userslist = database.read("SELECT " + query + ", user_id FROM " + tablePrefix + "skills WHERE " + query + " = 0 ORDER BY " + query + " DESC ");

        int purgedUsers = 0;

        for (int i = 1; i <= userslist.size(); i++) {
            System.out.println("Checking user " + i + "/" + userslist.size());
            int userId = Integer.valueOf(userslist.get(i).get(1));
            HashMap<Integer, ArrayList<String>> username = database.read("SELECT user FROM " + tablePrefix + "users WHERE id = '" + userId + "'");
            String playerName = username.get(1).get(0);

            if (Bukkit.getOfflinePlayer(playerName).isOnline()) {
                continue;
            }

            deleteFromSQL(userId, playerName);
            purgedUsers++;
        }

        plugin.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    private void purgeOldSQL() {
        System.out.println("Purging old users...");
        long currentTime = System.currentTimeMillis();
        String query = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";
        HashMap<Integer, ArrayList<String>> userslist = database.read("SELECT " + query + ", user_id FROM " + tablePrefix + "skills WHERE " + query + " > 0 ORDER BY " + query + " DESC ");

        int purgedUsers = 0;

        for (int i = 1; i <= userslist.size(); i++) {
            System.out.println("Checking user " + i + "/" + userslist.size());
            int userId = Integer.valueOf(userslist.get(i).get(1));
            HashMap<Integer, ArrayList<String>> username = database.read("SELECT user FROM " + tablePrefix + "users WHERE id = '" + userId + "'");
            String playerName = username.get(1).get(0);

            long lastLoginTime = database.getInt("SELECT lastlogin FROM " + tablePrefix + "users WHERE id = '" + userId + "'") * 1000L;
            long loginDifference = currentTime - lastLoginTime;

            if (loginDifference > 2630000000L) {
                deleteFromSQL(userId, playerName);
                purgedUsers++;
            }
        }

        plugin.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    private void deleteFromSQL(int userId, String playerName) {
        System.out.println("Deleting user " + userId);
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

        profileCleanup(playerName);

        System.out.println("User " + userId + " was successfully removed!");
    }

    private void profileCleanup(String playerName) {
        McMMOPlayer mcmmoPlayer = Users.getPlayer(playerName);

        if (mcmmoPlayer != null) {
            Player player = mcmmoPlayer.getPlayer();
            SpoutHud spoutHud = mcmmoPlayer.getProfile().getSpoutHud();

            if (spoutHud != null) {
                spoutHud.removeWidgets();
            }

            Users.remove(playerName);

            if (player.isOnline()) {
                Users.addUser(player);

                if (mcMMO.spoutEnabled) {
                    SpoutStuff.reloadSpoutPlayer(player);
                }
            }
        }
    }
}
