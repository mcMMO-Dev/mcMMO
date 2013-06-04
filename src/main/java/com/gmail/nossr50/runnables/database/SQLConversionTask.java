package com.gmail.nossr50.runnables.database;

import java.io.BufferedReader;
import java.io.FileReader;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;

public class SQLConversionTask extends BukkitRunnable {

    @Override
    public void run() {
        String location = mcMMO.getUsersFilePath();

        try {
            BufferedReader in = new BufferedReader(new FileReader(location));
            String line = "";
            int converted = 0;

            while ((line = in.readLine()) != null) {

                // Find if the line contains the player we want.
                String[] playerData = line.split(":");
                if (mcMMO.getDatabaseManager().convert(playerData)) {
                    converted++;
                }
            }

            mcMMO.p.getLogger().info("MySQL Updated from users file, " + converted + " items added/updated to MySQL DB");
            in.close();
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
    }
}
