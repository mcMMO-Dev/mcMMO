package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

public class ScoreboardListeners implements Listener {
    //private final mcMMO plugin;

    public ScoreboardListeners(/*final mcMMO plugin*/) {
        //this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ScoreboardManager.setupPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ScoreboardManager.teardownPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent e) {

    }
}
