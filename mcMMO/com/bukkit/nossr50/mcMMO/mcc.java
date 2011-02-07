package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class mcc {
	  private static volatile mcc instance;
		public static mcc getInstance() {
	    	if (instance == null) {
	    	instance = new mcc();
	    	}
	    	return instance;
	    	}
	  
	public void CommandCheck(Player player, String split[], Location spawn){
		String playerName = player.getName();
		if(split[0].equalsIgnoreCase("/mcmmo")){
    		player.sendMessage(ChatColor.GRAY+"mcMMO is an RPG inspired plugin");
    		player.sendMessage(ChatColor.GRAY+"You can gain skills in several professions by");
    		player.sendMessage(ChatColor.GRAY+"doing things related to that profession.");
    		player.sendMessage(ChatColor.GRAY+"Mining for example will increase your mining XP.");
    		player.sendMessage(ChatColor.GRAY+"Wood Cutting will increase Wood Cutting, etc...");
    		player.sendMessage(ChatColor.GRAY+"Repairing is simple in mcMMO");
    		player.sendMessage(ChatColor.GRAY+"Say you want to repair an iron shovel");
    		player.sendMessage(ChatColor.GRAY+"start by making an anvil by combining 9 iron ingots");
    		player.sendMessage(ChatColor.GRAY+"on a workbench. Place the anvil and while holding the shovel");
    		player.sendMessage(ChatColor.GRAY+"right click the anvil interact with it, If you have spare");
    		player.sendMessage(ChatColor.GRAY+"iron ingots in your inventory the item will be repaired.");
    		player.sendMessage(ChatColor.GRAY+"You cannot hurt other party members");
    		player.sendMessage(ChatColor.BLUE+"Set your own spawn with "+ChatColor.RED+"/myspawn");
    		player.sendMessage(ChatColor.GREEN+"Based on your skills you will get "+ChatColor.DARK_RED+"random procs "+ChatColor.GREEN+ "when");
    		player.sendMessage(ChatColor.GREEN+"using your profession, like "+ChatColor.DARK_RED+"double drops "+ChatColor.GREEN+"or "+ChatColor.DARK_RED+"better repairs");
    		player.sendMessage(ChatColor.GREEN+"Find out mcMMO commands with /mcc");
    	}
    	if(split[0].equalsIgnoreCase("/mcc")){
    		player.sendMessage(ChatColor.GRAY+"mcMMO has a party system included");
    		player.sendMessage(ChatColor.GREEN+"~~Commands~~");
    		player.sendMessage(ChatColor.GRAY+"/party <name> - to join a party");
    		player.sendMessage(ChatColor.GRAY+"/party q - to quit a party");
    		player.sendMessage(ChatColor.GRAY+"/ptp <name> - party teleport");
    		player.sendMessage(ChatColor.GRAY+"/p - toggles party chat");
    	}
    	if(mcUsers.getProfile(player).inParty() && split[0].equalsIgnoreCase("/ptp")){
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /ptp <playername>");
    			return;
    		}
    		if(mcm.getInstance().isPlayer(split[1])){
        	Player target = mcm.getInstance().getPlayer(split[1]);
        	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(target).getParty())){
        	player.teleportTo(target);
        	player.sendMessage(ChatColor.GREEN+"You have teleported to "+target.getName());
        	target.sendMessage(ChatColor.GREEN+player.getName() + " has teleported to you.");
        	}
    	}
    	}
    	if(player.isOp() && split[0].equalsIgnoreCase("/whois")){
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED + "Proper usage is /whois <playername>");
    			return;
    		}
    		//if split[1] is a player
    		if(mcm.getInstance().isPlayer(split[1])){
    		Player target = mcm.getInstance().getPlayer(split[1]);
    		double x,y,z;
    		x = target.getLocation().getX();
    		y = target.getLocation().getY();
    		z = target.getLocation().getZ();
    		player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~");
    		player.sendMessage(target.getName());
    		if(mcUsers.getProfile(target).inParty())
    		player.sendMessage("Party: "+mcUsers.getProfile(target).getParty());
    		player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)");
    		player.sendMessage("OP: " + target.isOp());
    		player.sendMessage(ChatColor.GREEN+"~~mcMMO stats~~");
    		player.sendMessage("Mining Skill: "+mcUsers.getProfile(target).getMining());
    		player.sendMessage("Repair Skill: "+mcUsers.getProfile(target).getRepair());
    		player.sendMessage("Woodcutting Skill: "+mcUsers.getProfile(target).getWoodCutting());
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
    		spawn = player.getLocation();
    		player.sendMessage("Spawn set to current location");
    	}
    	if(split[0].equalsIgnoreCase("/stats")){
    		player.sendMessage(ChatColor.DARK_GREEN + "mcMMO stats");
    		player.sendMessage(ChatColor.DARK_GREEN + "Mining Skill: " + mcUsers.getProfile(player).getMining());
    		player.sendMessage(ChatColor.DARK_GREEN + "Repair Skill: " + mcUsers.getProfile(player).getRepair());
    		player.sendMessage(ChatColor.DARK_GREEN + "Woodcutting Skill: "+mcUsers.getProfile(player).getWoodCutting());
    		player.sendMessage(ChatColor.GRAY + "Increases depending on the material you mine");
    	}
    	//Party command
    	if(split[0].equalsIgnoreCase("/party")){
    		if(split.length == 1 && !mcUsers.getProfile(player).inParty()){
    			player.sendMessage("Proper usage is /party <name> or 'q' to quit");
    			return;
    		}
    		if(split.length == 1 && mcUsers.getProfile(player).inParty()){
            	String tempList = "";
            	int x = 0;
                for(Player p : mcPlayerListener.getInstance().getPlayersOnline())
                {
                	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(p).getParty())){
	                	if(p != null && x+1 >= mcm.getInstance().partyCount(player)){
	                		tempList+= p.getName();
	                		x++;
	                	}
	                	if(p != null && x < mcm.getInstance().partyCount(player)){
	                		tempList+= p.getName() +", ";
	                		x++;
	                	}
                	}
                }
                player.sendMessage(ChatColor.GREEN+"You are in party \""+mcUsers.getProfile(player).getParty()+"\"");
                player.sendMessage(ChatColor.GREEN + "Party Members ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");
    		}
    		if(split.length > 1 && split[1].equals("q") && mcUsers.getProfile(player).inParty()){
    			mcm.getInstance().informPartyMembersQuit(player);
    			mcUsers.getProfile(player).removeParty();
    			player.sendMessage(ChatColor.RED + "You have left that party");
    			return;
    		}
    		if(split.length >= 2){
    		mcUsers.getProfile(player).setParty(split[1]);
    		player.sendMessage("Joined Party: " + split[1]);
    		mcm.getInstance().informPartyMembers(player);
    		}
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
    		if(mcUsers.getProfile(player).getMySpawn(player) != null){
    		player.getInventory().clear();
    		player.setHealth(20);
    		player.teleportTo(mcUsers.getProfile(player).getMySpawn(player));
    		player.sendMessage("Inventory cleared & health restored");
    		}else{
    			player.sendMessage(ChatColor.RED+"Configure your myspawn first with /setmyspawn");
    		}
    	}
    	if(split[0].equalsIgnoreCase("/spawn")){
    		if(spawn != null){
    			player.teleportTo(spawn);
    			player.sendMessage("Welcome to spawn, home of the feeble.");
    			return;
    		}
    		player.sendMessage("Spawn isn't configured. Have an OP set it with /setspawn");
    	}
	}
}
