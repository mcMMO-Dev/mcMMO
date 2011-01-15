package com.bukkit.nossr50.vPlayersOnline;

import org.bukkit.Location;
import org.bukkit.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.ChatColor;

/**
 * Handle events for all Player related events
 * @author nossr50
 */
public class vPlayerListener extends PlayerListener {
    private final vPlayersOnline plugin;

    public vPlayerListener(vPlayersOnline instance) {
        plugin = instance;
    }

    public void onPlayerJoin(PlayerEvent event) {
        Player player = event.getPlayer();
        Player players[] = plugin.getServer().getOnlinePlayers();
        int x = 0;
        for(Player hurrdurr: players){
         x++;
        }
        player.sendMessage(ChatColor.GREEN + "There are " + x + " players online");
        }
}
