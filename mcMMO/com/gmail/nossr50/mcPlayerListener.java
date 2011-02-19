package com.gmail.nossr50;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.nijikokun.bukkit.Permissions.Permissions;

public class mcPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Player player = event.getPlayer();
    	if(mcPermissions.getInstance().mySpawn(player)){
    	if(mcUsers.getProfile(player).getMySpawn(player) != null)
    	event.setRespawnLocation(mcUsers.getProfile(player).getMySpawn(player));
    	}
    	//HELLO CODE PEAKERS!
    }
    public Player[] getPlayersOnline() {
    		return plugin.getServer().getOnlinePlayers();
    }
	public boolean isPlayer(String playerName){
    	for(Player herp :  getPlayersOnline()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return true;
    		}
    	}
    		return false;
    }
	public Player getPlayer(String playerName){
    	for(Player herp : getPlayersOnline()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return herp;
    		}
    	}
    	return null;
    }
    public int partyCount(Player player){
        int x = 0;
        for(Player hurrdurr: getPlayersOnline()){
        	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(hurrdurr).getParty()))
        	x++;
        }
        return x;
    }
    public void informPartyMembers(Player player){
        int x = 0;
        for(Player p :  getPlayersOnline()){
                if(mcm.getInstance().inSameParty(player, p) && !p.getName().equals(player.getName())){
                p.sendMessage(player.getName() + ChatColor.GREEN + " has joined your party");
                x++;
                }
            }
    }
    public void informPartyMembersQuit(Player player){
        int x = 0;
        for(Player p : getPlayersOnline()){
                if(mcm.getInstance().inSameParty(player, p) && !p.getName().equals(player.getName())){
                p.sendMessage(player.getName() + ChatColor.GREEN + " has left your party");
                x++;
                }
            }
    }
    public void onPlayerJoin(PlayerEvent event) {
    	Player player = event.getPlayer();
    	mcUsers.addUser(player);
    	if(mcPermissions.getInstance().motd(player)){
    	player.sendMessage(ChatColor.BLUE + "This server is running mcMMO "+plugin.getDescription().getVersion()+" type "+ChatColor.YELLOW+"/mcmmo "+ChatColor.BLUE+ "for help.");
    	}
    }
    //Check if string is a player
    
    public void onPlayerItem(PlayerItemEvent event) {
    	Block block = event.getBlockClicked();
    	Player player = event.getPlayer();
    	ItemStack is = player.getItemInHand();
    	if(mcPermissions.getInstance().herbalism(player)){
    	//BREADCHECK, CHECKS HERBALISM SKILL FOR BREAD HP MODIFIERS
    	mcm.getInstance().breadCheck(player, is);
    	//STEW, CHECKS HERBALISM SKILL FOR BREAD HP MODIFIERS
    	mcm.getInstance().stewCheck(player, is);
    	}
    	if(mcPermissions.getInstance().repair(player)){
    	//REPAIRCHECK, CHECKS TO MAKE SURE PLAYER IS RIGHT CLICKING AN ANVIL, PLAYER HAS ENOUGH RESOURCES, AND THE ITEM IS NOT AT FULL DURABILITY.
    	mcm.getInstance().repairCheck(player, is, block);
    	}
    }
  
    public void onPlayerCommand(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	//Check if the command is an mcMMO related help command
    	mcm.getInstance().mcmmoHelpCheck(split, player, event);
    	if(mcPermissions.getInstance().mmoedit(player) && split[0].equalsIgnoreCase("/mmoedit")){
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /mmoedit playername skillname newvalue");
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && mcm.getInstance().isInt(split[3]) && mcm.getInstance().isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				mcUsers.getProfile(getPlayer(split[1])).modifyskill(newvalue, split[2]);
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3){
    			if(mcm.getInstance().isInt(split[2]) && mcm.getInstance().isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				mcUsers.getProfile(player).modifyskill(newvalue, split[1]);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified.");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /mmoedit playername skillname newvalue");
    		}
    	}
    	if(mcUsers.getProfile(player).inParty() && split[0].equalsIgnoreCase("/ptp")){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().partyTeleport(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /ptp <playername>");
    			return;
    		}
    		if(isPlayer(split[1])){
        	Player target = getPlayer(split[1]);
        	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(target).getParty())){
        	player.teleportTo(target);
        	player.sendMessage(ChatColor.GREEN+"You have teleported to "+target.getName());
        	target.sendMessage(ChatColor.GREEN+player.getName() + " has teleported to you.");
        	}
    	}
    	}
    	if((player.isOp() || mcPermissions.getInstance().whois(player)) && split[0].equalsIgnoreCase("/whois")){
    		event.setCancelled(true);
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
    		if(mcUsers.getProfile(target).inParty())
    		player.sendMessage("Party: "+mcUsers.getProfile(target).getParty());
    		player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)");
    		player.sendMessage("OP: " + target.isOp());
    		player.sendMessage(ChatColor.GREEN+"~~mcMMO stats~~");
    		player.sendMessage("Mining Skill: "+mcUsers.getProfile(target).getMining());
    		player.sendMessage("Repair Skill: "+mcUsers.getProfile(target).getRepair());
    		player.sendMessage("Woodcutting Skill: "+mcUsers.getProfile(target).getWoodCutting());
    		player.sendMessage("Unarmed Skill: "+mcUsers.getProfile(target).getUnarmed());
    		player.sendMessage("Herbalism Skill: "+mcUsers.getProfile(target).getHerbalism());
    		player.sendMessage("Excavation Skill: "+mcUsers.getProfile(target).getExcavation());
    		player.sendMessage("Archery Skill: "+mcUsers.getProfile(target).getArchery());
    		player.sendMessage("Swords Skill: "+mcUsers.getProfile(target).getSwords());
    		player.sendMessage("Axes Skill: "+mcUsers.getProfile(target).getAxes());
    		player.sendMessage("Acrobatics Skill: "+mcUsers.getProfile(target).getAcrobatics());
    		player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~");
    		player.sendMessage("X: "+x);
    		player.sendMessage("Y: "+y);
    		player.sendMessage("Z: "+z);
    		}
    	}
    	if(split[0].equalsIgnoreCase("/setmyspawn")){
    		if(!mcPermissions.getInstance().setMySpawn(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		event.setCancelled(true);
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		mcUsers.getProfile(player).setMySpawn(x, y, z);
    		player.sendMessage(ChatColor.DARK_AQUA + "Myspawn has been set to your current location.");
    	}
    	if(split[0].equalsIgnoreCase("/stats")){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.DARK_RED + "mcMMO stats");
    		player.sendMessage(ChatColor.YELLOW + "Mining Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getMining());
    		player.sendMessage(ChatColor.YELLOW + "Repair Skill: "+ ChatColor.GREEN + mcUsers.getProfile(player).getRepair());
    		player.sendMessage(ChatColor.YELLOW + "Woodcutting Skill: "+ ChatColor.GREEN + mcUsers.getProfile(player).getWoodCutting());
    		player.sendMessage(ChatColor.YELLOW + "Unarmed Skill: "+ ChatColor.GREEN + mcUsers.getProfile(player).getUnarmed());
    		player.sendMessage(ChatColor.YELLOW + "Herbalism Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(player).getHerbalism());
    		player.sendMessage(ChatColor.YELLOW + "Excavation Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(player).getExcavation());
    		player.sendMessage(ChatColor.YELLOW + "Archery Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(player).getArchery());
    		player.sendMessage(ChatColor.YELLOW + "Swords Skill: " + ChatColor.GREEN +  mcUsers.getProfile(player).getSwords());
    		player.sendMessage(ChatColor.YELLOW+ "Axes Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getAxes());
    		player.sendMessage(ChatColor.YELLOW + "Acrobatics Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getAcrobatics());
    		player.sendMessage(ChatColor.DARK_RED+"TOTAL SKILL: "+ChatColor.GREEN+
    				(mcUsers.getProfile(player).getAcrobaticsInt()+
    				mcUsers.getProfile(player).getArcheryInt()+
    				mcUsers.getProfile(player).getAxesInt()+
    				mcUsers.getProfile(player).getExcavationInt()+
    				mcUsers.getProfile(player).getHerbalismInt()+
    				mcUsers.getProfile(player).getMiningInt()+
    				mcUsers.getProfile(player).getRepairInt()+
    				mcUsers.getProfile(player).getSwordsInt()+
    				mcUsers.getProfile(player).getUnarmedInt()+
    				mcUsers.getProfile(player).getWoodCuttingint())
    				);
    	}
    	//Invite Command
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/invite")){
    		if(!mcUsers.getProfile(player).inParty()){
    			player.sendMessage(ChatColor.RED+"You are not in a party.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /invite <playername");
    			return;
    		}
    		if(mcUsers.getProfile(player).inParty() && split.length >= 2 && isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			mcUsers.getProfile(target).modifyInvite(mcUsers.getProfile(player).getParty());
    			player.sendMessage(ChatColor.GREEN+"Invite sent successfully");
    			target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+mcUsers.getProfile(target).getInvite());
    			target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+"/accept"+ChatColor.YELLOW+" to accept the invite");
    		}
    	}
    	//Accept invite
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/accept")){
    		if(mcUsers.getProfile(player).hasPartyInvite()){
    			if(mcUsers.getProfile(player).inParty()){
    				informPartyMembersQuit(player);
    			}
    			mcUsers.getProfile(player).acceptInvite();
    			informPartyMembers(player);
    			player.sendMessage(ChatColor.GREEN+"Invite accepted. You have joined party ("+mcUsers.getProfile(player).getParty()+")");
    		} else {
    			player.sendMessage(ChatColor.RED+"You have no invites at this time");
    		}
    	}
    	//Party command
    	if(split[0].equalsIgnoreCase("/party")){
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
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
                player.sendMessage(ChatColor.GREEN+"You are in party \""+mcUsers.getProfile(player).getParty()+"\"");
                player.sendMessage(ChatColor.GREEN + "Party Members ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");
    		}
    		if(split.length > 1 && split[1].equals("q") && mcUsers.getProfile(player).inParty()){
    			informPartyMembersQuit(player);
    			mcUsers.getProfile(player).removeParty();
    			player.sendMessage(ChatColor.RED + "You have left that party");
    			return;
    		}
    		if(split.length >= 2){
    		mcUsers.getProfile(player).setParty(split[1]);
    		player.sendMessage("Joined Party: " + split[1]);
    		informPartyMembers(player);
    		}
    	}
    	if(split[0].equalsIgnoreCase("/p")){
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		event.setCancelled(true);
    		if(mcConfig.getInstance().isAdminToggled(player.getName()))
    		mcConfig.getInstance().toggleAdminChat(playerName);
    		mcConfig.getInstance().togglePartyChat(playerName);
    		if(mcConfig.getInstance().isPartyToggled(playerName)){
    			player.sendMessage(ChatColor.GREEN + "Party Chat Toggled On");
    		} else {
    			player.sendMessage(ChatColor.GREEN + "Party Chat Toggled " + ChatColor.RED + "Off");
    		}
    	}
    	if(split[0].equalsIgnoreCase("/a") && (player.isOp() || mcPermissions.getInstance().adminChat(player))){
    		if(!mcPermissions.getInstance().adminChat(player) && !player.isOp()){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		event.setCancelled(true);
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
    		if(!mcPermissions.getInstance().mySpawn(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		event.setCancelled(true);
    		if(mcUsers.getProfile(player).getMySpawn(player) != null){
    		player.getInventory().clear();
    		player.setHealth(20);
    		player.teleportTo(mcUsers.getProfile(player).getMySpawn(player));
    		player.sendMessage("Inventory cleared & health restored");
    		}else{
    			player.sendMessage(ChatColor.RED+"Configure your myspawn first with /setmyspawn");
    		}
    	}
    }
	public void onPlayerChat(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";
    	if(mcConfig.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		log.log(Level.INFO, "[P]("+mcUsers.getProfile(player).getParty()+")"+"<"+player.getName()+"> "+event.getMessage());
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(mcUsers.getProfile(herp).inParty()){
    			if(mcm.getInstance().inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if((player.isOp() || mcPermissions.getInstance().adminChat(player)) 
    			&& mcConfig.getInstance().isAdminToggled(player.getName())){
    		log.log(Level.INFO, "[A]"+"<"+player.getName()+"> "+event.getMessage());
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(herp.isOp()){
    				herp.sendMessage(y+event.getMessage());
    			}
    		}
    		return;
    	}
    	}
}