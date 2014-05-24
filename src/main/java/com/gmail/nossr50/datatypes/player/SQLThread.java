/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer.Callback;
import com.gmail.nossr50.mcMMO;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author devan_000
 */
public class SQLThread extends BukkitRunnable {

    public volatile static ConcurrentHashMap<String, Callback> pending = new ConcurrentHashMap<String, Callback>();

    @Override
    public void run() {
        while (true) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }

            for (String playerName : pending.keySet()) {
                pending.get(playerName).done(playerName, mcMMO.getDatabaseManager().loadPlayerProfile(playerName, true));
                pending.remove(playerName);
            }
        }
    }

}
