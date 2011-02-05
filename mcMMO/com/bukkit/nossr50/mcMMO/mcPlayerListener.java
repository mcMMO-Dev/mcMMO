package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class mcPlayerListener extends PlayerListener {
	public Location spawn = null;
    private static mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }
    public void onPlayerJoin(PlayerEvent event) {
    	Player player = event.getPlayer();
    	mcUsers.addUser(player);
    	player.sendMessage(ChatColor.DARK_RED+"Welcome to /v/ - Minecraft");
    	player.sendMessage(ChatColor.DARK_RED+"Steam Group: vminecraft");
    	player.sendMessage(ChatColor.AQUA + "This server is running mcMMO type /stats for your information");
    	player.sendMessage(ChatColor.GREEN + "Use "+ChatColor.YELLOW+"/party "+ChatColor.GREEN+"to create/join parties and");
    	player.sendMessage(ChatColor.GREEN+"to check who is in your current party.");
    	player.sendMessage(ChatColor.GREEN + "Use "+ChatColor.YELLOW+"/p"+ChatColor.GREEN+" to toggle party chat");
    	player.sendMessage(ChatColor.GREEN + "Use "+ChatColor.YELLOW+"/ptp "+ChatColor.GREEN+"to teleport to party members");
    	player.sendMessage("Set your spawn with "+ChatColor.YELLOW+"/setmyspawn"+ChatColor.WHITE+", Travel to it with /myspawn");
    	player.sendMessage(ChatColor.RED+"WARNING: "+ChatColor.DARK_GRAY+ "Using /myspawn will clear your inventory!"); 
    }
    //Check if string is a player
    public boolean isPlayer(String playerName){
    	for(Player herp : plugin.getServer().getOnlinePlayers()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return true;
    		}
    	}
    		return false;
    }
    public int partyCount(Player player){
    	Player players[] = plugin.getServer().getOnlinePlayers();
        int x = 0;
        for(Player hurrdurr: players){
        	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(hurrdurr).getParty()))
        	x++;
        }
        return x;
    }
    public Player getPlayer(String playerName){
    	for(Player herp : plugin.getServer().getOnlinePlayers()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return herp;
    		}
    	}
    	return null;
    }
    public void onPlayerCommand(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	//mcMMO command
    	if(mcUsers.getProfile(player).inParty() && split[0].equalsIgnoreCase("/ptp")){
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /ptp <playername>");
    			return;
    		}
    		if(isPlayer(split[1])){
        	Player target = getPlayer(split[1]);
        	player.teleportTo(target);
        	player.sendMessage(ChatColor.GREEN+"You have teleport to "+target.getName());
        	target.sendMessage(ChatColor.GREEN+player.getName() + " has teleported to you.");
    	}
    	}
    	if(player.isOp() && split[0].equalsIgnoreCase("/whois")){
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED + "Proper usage is /whois <playername>");
    			return;
    		}
    		//if split[1] is a player
    		if(isPlayer(split[1])){
    		Player target = getPlayer(split[1]);
    		double x,y,z;
    		x = target.getLocation().getX();
    		y = target.getLocation().getY();
    		z = target.getLocation().getZ();
    		player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~");
    		player.sendMessage(target.getName());
    		player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)");
    		player.sendMessage("OP: " + target.isOp());
    		player.sendMessage(ChatColor.GREEN+"~~mcMMO stats~~");
    		player.sendMessage("Gathering Skill: "+mcUsers.getProfile(target).getgather());
    		player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~");
    		player.sendMessage("X: "+x);
    		player.sendMessage("Y: "+y);
    		player.sendMessage("Z: "+z);
    		}
    	}
    	if(split[0].equalsIgnoreCase("/setmyspawn")){
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		mcUsers.getProfile(player).setMySpawn(x, y, z);
    		player.sendMessage(ChatColor.DARK_AQUA + "Myspawn has been set to your current location.");
    	}
    	if(player.isOp() && split[0].equalsIgnoreCase("/setspawn")){
    		spawn = event.getPlayer().getLocation();
    		player.sendMessage("Spawn set to current location");
    	}
    	if(split[0].equalsIgnoreCase("/stats")){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.DARK_GREEN + "mcMMO stats");
    		player.sendMessage(ChatColor.DARK_GREEN + "Gathering Skill: " + mcUsers.getProfile(player).getgather());
    		player.sendMessage(ChatColor.GRAY + "Increases as you gather materials from the world");
    		player.sendMessage(ChatColor.GRAY + "Effect: Increases chance to gather more than one of a rare material");
    	}
    	//Party command
    	if(split[0].equalsIgnoreCase("/party")){
    		event.setCancelled(true);
    		if(split.length == 1 && !mcUsers.getProfile(player).inParty()){
    			player.sendMessage("Proper usage is /party <name> or 'q' to quit");
    			return;
    		}
    		if(split.length == 1 && mcUsers.getProfile(player).inParty()){
            	String tempList = "";
            	int x = 0;
                for(Player p : plugin.getServer().getOnlinePlayers())
                {
                	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(p).getParty())){
	                	if(p != null && x+1 >= partyCount(player)){
	                		tempList+= p.getName();
	                		x++;
	                	}
	                	if(p != null && x < partyCount(player)){
	                		tempList+= p.getName() +", ";
	                		x++;
	                	}
                	}
                }
                player.sendMessage(ChatColor.GREEN + "Party Members ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");
    		}
    		if(split[1].equals("q") && mcUsers.getProfile(player).inParty()){
    			informPartyMembersQuit(player);
    			mcUsers.getProfile(player).removeParty();
    			player.sendMessage(ChatColor.RED + "You have left that party");
    			return;
    		}
    		mcUsers.getProfile(player).setParty(split[1]);
    		player.sendMessage("Joined Party: " + split[1]);
    		informPartyMembers(player);
    	}
    	if(split[0].equalsIgnoreCase("/p")){
    		if(mcConfig.getInstance().isAdminToggled(player.getName()))
    		mcConfig.getInstance().toggleAdminChat(playerName);
    		mcConfig.getInstance().togglePartyChat(playerName);
    		if(mcConfig.getInstance().isPartyToggled(playerName)){
    			player.sendMessage(ChatColor.GREEN + "Party Chat Toggled On");
    		} else {
    			player.sendMessage(ChatColor.GREEN + "Party Chat Toggled " + ChatColor.RED + "Off");
    		}
    	}
    	if(split[0].equalsIgnoreCase("/a") && player.isOp()){
    		if(mcConfig.getInstance().isPartyToggled(player.getName()))
    		mcConfig.getInstance().togglePartyChat(playerName);
    		mcConfig.getInstance().toggleAdminChat(playerName);
    		if(mcConfig.getInstance().isAdminToggled(playerName)){
    			player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.GREEN + "On");
    		} else {
    			player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.RED + "Off");
    		}
    	}
    	if(split[0].equalsIgnoreCase("/myspawn")){
    		player.getInventory().clear();
    		player.setHealth(20);
    		player.teleportTo(mcUsers.getProfile(player).getMySpawn(player));
    		player.sendMessage("Inventory cleared & health restored");
    	}
    	if(split[0].equalsIgnoreCase("/spawn")){
    		if(spawn != null){
    			player.teleportTo(spawn);
    			player.sendMessage("Welcome to spawn, home of the dumbfucks");
    			return;
    		}
    		player.sendMessage("Spawn isn't configured. Have an OP set it with /setspawn");
    	}
    }
    private String String(double x) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onPlayerChat(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";
    	if(mcConfig.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(mcUsers.getProfile(herp).inParty()){
    			if(inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if(player.isOp() && mcConfig.getInstance().isAdminToggled(player.getName())){
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(herp.isOp()){
    				herp.sendMessage(y+event.getMessage());
    			}
    		}
    		return;
    	}
    	if(player.isOp()){
    		event.setCancelled(true);
    		for(Player derp : plugin.getServer().getOnlinePlayers()){
    			String z = ChatColor.RED + "<" + ChatColor.WHITE + player.getName() + ChatColor.RED + "> "+ChatColor.WHITE;
    			derp.sendMessage(z+event.getMessage());
    		}
    	}
    	}
    public static void informPartyMembers(Player player){
        int x = 0;
        for(Player p : plugin.getServer().getOnlinePlayers()){
                if(inSameParty(player, p) && !p.getName().equals(player.getName())){
                p.sendMessage(player.getName() + ChatColor.GREEN + " has joined your party");
                x++;
                }
            }
    }
    public static void informPartyMembersQuit(Player player){
        int x = 0;
        for(Player p : plugin.getServer().getOnlinePlayers()){
                if(inSameParty(player, p) && !p.getName().equals(player.getName())){
                p.sendMessage(player.getName() + ChatColor.GREEN + " has left your party");
                x++;
                }
            }
    }
    public static boolean inSameParty(Player playera, Player playerb){
        if(mcUsers.getProfile(playera).getParty().equals(mcUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
}