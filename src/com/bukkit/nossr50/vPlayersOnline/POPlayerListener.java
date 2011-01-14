package com.bukkit.nossr50.vPlayersOnline;

import org.bukkit.Player;
import org.bukkit.Color;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author nossr50
 */
public class POPlayerListener extends PlayerListener {
    private final vPlayersOnline plugin;
    
    public POPlayerListener(vPlayersOnline instance) {
        this.plugin = instance;
    }

    //Insert Player related code here
    public void onPlayerJoin(PlayerEvent event) {
    Player player = event.getPlayer();
    Player players[] = plugin.getServer().getOnlinePlayers();
    int x = 0;
    for(Player herp: players){
    	x++;
    }
    player.sendMessage("There are " + x + " players online");
    }
    }