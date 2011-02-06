package com.bukkit.nossr50.BackOff;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author nossr50
 */


public class bPlayerListener extends PlayerListener {
    private final BackOff plugin;
    
    static ArrayList<String> backOffList = new ArrayList<String>();
	public boolean isBackOff(String playerName) {return backOffList.contains(playerName);}
    public void removeBackOff(String playerName) {backOffList.remove(backOffList.indexOf(playerName));}
    public void addBackOff(String playerName) {backOffList.add(playerName);}
    static ArrayList<String> ibackOffList = new ArrayList<String>();
	public boolean isBackOffi(String playerName) {return ibackOffList.contains(playerName);}
    public void removeBackOffi(String playerName) {ibackOffList.remove(backOffList.indexOf(playerName));}
    public void addBackOffi(String playerName) {ibackOffList.add(playerName);}
    
    public static double getDistance(Player player1, Player player2)
    {
    return Math.sqrt(Math.pow(player1.getLocation().getX() - player2.getLocation().getX(), 2) + Math.pow(player1.getLocation().getY() - player2.getLocation().getY(), 2)
    + Math.pow(player1.getLocation().getZ() - player2.getLocation().getZ(), 2));
    }
    public static double getDistance(Location loc, Player player2)
    {
    return Math.sqrt(Math.pow(loc.getX() - player2.getLocation().getX(), 2) + Math.pow(loc.getY() - player2.getLocation().getY(), 2)
    + Math.pow(loc.getZ() - player2.getLocation().getZ(), 2));
    }

    public bPlayerListener(BackOff instance) {
        plugin = instance;
    }
    public boolean isPlayer(String playerName){
    	for(Player herp : plugin.getServer().getOnlinePlayers()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return true;
    		}
    	}
    		return false;
    }
    public Player getPlayer(String playerName){
    	for(Player herp : plugin.getServer().getOnlinePlayers()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return herp;
    		}
    	}
    	return null;
    }
    public void onPlayerMove(PlayerMoveEvent event) {
    	Player player = event.getPlayer();
    	Location to = event.getTo();
    	Location from = event.getFrom();
    	for (Player derp : plugin.getServer().getOnlinePlayers()){
    		if(isBackOff(derp.getName()) && !isBackOffi(player.getName())){
    			if(player != derp && (getDistance(player, derp) < 7)){
    			if(getDistance(to, derp) > getDistance(from, derp)){
    				player.teleportTo(event.getFrom());
    				}
    			}
    		}
    	}    
    }
    public void onPlayerCommand(PlayerChatEvent event){
    	String[] split = event.getMessage().split(" ");
    	Player player = event.getPlayer();
    	if(player.isOp() && split[0].equalsIgnoreCase("/backoff")){
    		if(split.length == 1){
    			if(isBackOff(player.getName())){
    				removeBackOff(player.getName());
    				player.sendMessage("Back off mode disabled");
    				return;
    			} else{
    				addBackOff(player.getName());
    				player.sendMessage("Back off mode enabled");
    				return;
    			}
    		}
    		if(isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			if(isBackOffi(target.getName())){
    				removeBackOffi(target.getName());
    				target.sendMessage("Removed from back off mode immunity");
    				if(!target.getName().equals(player.getName()))
    				player.sendMessage("Removed " + target.getName() + " from back off mode");
    			} else {
    				addBackOffi(target.getName());
    				target.sendMessage("Added to back off mode immunity");
    				if(!target.getName().equals(player.getName()))
    				player.sendMessage("Added " + target.getName() + " from back off mode");
    			}
    		}
    	}
    }
}

