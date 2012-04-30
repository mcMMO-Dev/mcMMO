package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;

/**
 * Listener for listening to our own events, only really useful for catching errors
 */
public class SelfListener implements Listener {

    /**
     * Monitor internal XP gain events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onPlayerXpGain(McMMOPlayerXpGainEvent event) {
        int xp = event.getXpGained();

        if(xp < 0) {
            try {
                throw new Exception("Gained negative XP!");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
