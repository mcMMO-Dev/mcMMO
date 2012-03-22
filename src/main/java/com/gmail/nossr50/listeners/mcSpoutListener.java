package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.spout.SpoutStuff;

public class mcSpoutListener implements Listener {
    //Why do we have this here? We never use it...
    mcMMO plugin = null;
    
    public mcSpoutListener(mcMMO pluginx) {
        plugin = pluginx;
    }
    
    /**
     * Monitor SpoutCraftEnable events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        SpoutPlayer sPlayer = event.getPlayer();

        if (sPlayer.isSpoutCraftEnabled()) {
            SpoutStuff.playerHUDs.put(sPlayer, new HUDmmo(sPlayer)); //Setup Party HUD stuff
            Users.getProfile(sPlayer).toggleSpoutEnabled();
            sPlayer.setTitle(String.valueOf(m.getPowerLevel(sPlayer, Users.getProfile(sPlayer))));
        }
    }
}