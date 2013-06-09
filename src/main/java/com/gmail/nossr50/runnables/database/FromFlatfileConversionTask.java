package com.gmail.nossr50.runnables.database;

import java.io.BufferedReader;
import java.io.FileReader;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

public class FromFlatfileConversionTask extends BukkitRunnable {

    @Override
    public void run() {
        String location = mcMMO.getUsersFilePath();

        try {
            DatabaseManager from = DatabaseManagerFactory.createFlatfileDatabaseManager();
            DatabaseManager to = mcMMO.getDatabaseManager();

            BufferedReader in = new BufferedReader(new FileReader(location));
            String line = "";
            int converted = 0;

            while ((line = in.readLine()) != null) {

                // Find if the line contains the player we want.
                String[] playerData = line.split(":");
                String playerName = playerData[0];
                PlayerProfile profile = from.loadPlayerProfile(playerName, false);
                if (profile.isLoaded()) {
                    to.saveUser(profile);
                    converted++;
                }
            }

            mcMMO.p.getLogger().info("Database updated from users file, " + converted + " items added/updated to DB");
            in.close();
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?) " + e.toString());
        }
    }
}
