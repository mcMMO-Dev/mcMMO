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
        plugin.getLogger().info("Purging powerless users...");
        HashMap<Integer, ArrayList<String>> usernames = database.read("SELECT u.user FROM " + tablePrefix + "skills AS s, " + tablePrefix + "users AS u WHERE s.user_id = u.id AND (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0");
        database.write("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.id IN (SELECT * FROM (SELECT u.id FROM " + tablePrefix + "skills AS s, " + tablePrefix + "users AS u WHERE s.user_id = u.id AND (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0) AS p)");

        for (int i = 1; i <= usernames.size(); i++) {
            String playerName = usernames.get(i).get(0);

            if (playerName == null || Bukkit.getOfflinePlayer(playerName).isOnline()) {
                continue;
            }

            profileCleanup(playerName);
            purgedUsers++;
        }

        plugin.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    private void purgeOldSQL() {
        plugin.getLogger().info("Purging old users...");
        long currentTime = System.currentTimeMillis();
        String query = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";
        HashMap<Integer, ArrayList<String>> userslist = database.read("SELECT " + query + ", user_id FROM " + tablePrefix + "skills WHERE " + query + " > 0 ORDER BY " + query + " DESC ");

        int purgedUsers = 0;

        for (int i = 1; i <= userslist.size(); i++) {
            int userId = Integer.valueOf(userslist.get(i).get(1));
            HashMap<Integer, ArrayList<String>> username = database.read("SELECT user FROM " + tablePrefix + "users WHERE id = '" + userId + "'");
            String playerName = username.get(1).get(0);

            long lastLoginTime = database.getInt("SELECT lastlogin FROM " + tablePrefix + "users WHERE id = '" + userId + "'") * 1000L;
            long loginDifference = currentTime - lastLoginTime;

            if (loginDifference > 2630000000L) {
                database.write("DELETE FROM " + databaseName + "." + tablePrefix + "users WHERE " + tablePrefix + "users.id IN " + userId);
                profileCleanup(playerName);

                purgedUsers++;
            }
        }

        plugin.getLogger().info("Purged " + purgedUsers + " users from the database.");
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
