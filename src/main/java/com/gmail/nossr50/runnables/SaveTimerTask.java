package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.google.common.collect.Lists;
import org.bukkit.scheduler.BukkitRunnable;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;
import java.util.LinkedList;

public class SaveTimerTask extends BukkitRunnable {
    LinkedList<PlayerProfile> profilelist = Lists.newLinkedList();
    int tasknamecount = 0;
    /**
     * Example Calculations
     * Save took 100ms + pause of 50ms = 150ms
     * Default Save Period = 600Seconds
     * 600.000/150ms = 4000 Players saved
     * 1 Minute = 400 Players Saved (Less than 30 seconds could cause ConcurrentModificationExceptions sometimes.)
     * Advantages: Prevent High CPU Usage Spikes,
     * Prevent Stopping the ServerThread due to synchronization on the .save() method,
     * Smooth Synchronized Saving.
     * AVG Save Time on my server(100players): (15000ms)(new Threaded Queue - +50ms/per delay) vs (10100ms)(mcMMO default - +1ms/per delay)
     * My point of view: Creating too many runnables on a synchronized method when the others arent finished isnt wise, so i replaced it with one thread.*/
    @Override
    public void run() {
        tasknamecount++;
        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
                profilelist.add(mcMMOPlayer.getProfile());
        }
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                /*The thread stops itself when profilelist is empty, and after saving the parties.
                you could re-use the thread, but for that you need to make more changes to other classes, there isnt
                much difference about it.*/
                while (!profilelist.isEmpty()) {
                    PlayerProfile pp = profilelist.poll();
                    //Check for a nullpointer?, i simply dont trust the UserManager.
                    if(pp!=null){pp.save();}
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //Save parties after finishing saving the players.
                PartyManager.saveParties();
            }
        });
        tr.setName("mcMMO Saving Task #"+tasknamecount);
        tr.setDaemon(false);
        tr.start();
    }

}
