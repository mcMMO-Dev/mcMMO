package com.bukkit.nossr50.vPlayersOnline;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.ChatColor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle events for all Player related events
 * @author nossr50
 */
public class vPlayerListener extends PlayerListener {
    private final vPlayersOnline plugin;
    protected static final Logger log = Logger.getLogger("Minecraft");

    public vPlayerListener(vPlayersOnline instance) {
        plugin = instance;
    }
    //Function to count the players
    public int playerCount(){
    	Player players[] = plugin.getServer().getOnlinePlayers();
        int x = 0;
        for(Player hurrdurr: players){
         x++;
        }
        return x;
    }

    //Message to be sent when a player joins
    public void onPlayerJoin(PlayerEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.GREEN + "There are " + playerCount() + " players online");
        }
    //Message to be sent when a player uses /list
    public void onPlayerCommand(PlayerChatEvent event) {
    	log.log(Level.INFO, event.getMessage() + "|" + event.getPlayer().getName());
    	String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();
        if(split[0].equalsIgnoreCase("/list") || split[0].equalsIgnoreCase("/who")){
        	event.setCancelled(true);
        	String tempList = "";
            for(Player p : plugin.getServer().getOnlinePlayers())
            {
            	if(p != null){
            		tempList+= p.getName();
            	}
            }
            //Output the player list
            player.sendMessage(ChatColor.RED + "Player List (" + tempList +")");
        }
    }
}
