package com.bukkit.nossr50.mcMMO;

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
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Player player = event.getPlayer();
    	if(mcPermissions.getInstance().mySpawn(player)){
    	if(mcUsers.getProfile(player).getMySpawn(player) != null);
    	event.setRespawnLocation(mcUsers.getProfile(player).getMySpawn(player));
    	}
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
    	//BREAD
    	if(is.getTypeId() == 297){
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 2);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() <= 250){
    			player.setHealth(player.getHealth() + 4);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() <= 400){
    			player.setHealth(player.getHealth() + 5);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 6);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    	//STEW
    	if(is.getTypeId() == 282){
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() <= 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() <= 400){
    			player.setHealth(player.getHealth() + 3);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 4);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 6);
    		}
    	}
    	if(block != null && block.getTypeId() == 42){
    	short durability = is.getDurability();
    	if(player.getItemInHand().getDurability() > 0){
    		/*
    		 * ARMOR
    		 */
    		if(mcm.getInstance().isArmor(is) && block.getTypeId() == 42){
    			if(mcm.getInstance().isDiamondArmor(is) && mcm.getInstance().hasDiamond(player)){
    			mcm.getInstance().removeDiamond(player);
    			player.getItemInHand().setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
    			mcUsers.getProfile(player).skillUpRepair(1);
    			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
    			} else if (mcm.getInstance().isIronArmor(is) && mcm.getInstance().hasIron(player)){
    			mcm.getInstance().removeIron(player);
        		player.getItemInHand().setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
        		mcUsers.getProfile(player).skillUpRepair(1);
        		player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");	
    			}
    		}
    		/*
    		 * TOOLS
    		 */
    		if(mcm.getInstance().isTools(is) && block.getTypeId() == 42){
        		if(mcm.getInstance().isIronTools(is) && mcm.getInstance().hasIron(player)){
        			is.setDurability(mcm.getInstance().getToolRepairAmount(is, durability, player));
        			mcm.getInstance().removeIron(player);
        			mcUsers.getProfile(player).skillUpRepair(1);
        			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
        		} else if (mcm.getInstance().isDiamondTools(is) && mcm.getInstance().hasDiamond(player) && mcUsers.getProfile(player).getRepairInt() >= 50){ //Check if its diamond and the player has diamonds
        			is.setDurability(mcm.getInstance().getToolRepairAmount(is, durability, player));
        			mcm.getInstance().removeDiamond(player);
        			mcUsers.getProfile(player).skillUpRepair(1);
        			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
        		} else if (mcm.getInstance().isDiamondTools(is) && mcUsers.getProfile(player).getRepairInt() < 50){
        			player.sendMessage(ChatColor.DARK_RED +"You're not adept enough to repair Diamond");
        		} else if (mcm.getInstance().isDiamondTools(is) && !mcm.getInstance().hasDiamond(player) || mcm.getInstance().isIronTools(is) && !mcm.getInstance().hasIron(player)){
        			if(mcm.getInstance().isDiamondTools(is) && !mcm.getInstance().hasDiamond(player))
        				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
        			if(mcm.getInstance().isIronTools(is) && !mcm.getInstance().hasIron(player))
        				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
        		} else if (mcm.getInstance().isDiamondArmor(is) && !mcm.getInstance().hasDiamond(player)){
        			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
        		} else if (mcm.getInstance().isIronArmor(is) && !mcm.getInstance().hasIron(player))
        			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
        	}
    	} else {
    		player.sendMessage("That is at full durability.");
    	}
    	}
    }
    public void onPlayerCommand(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	if(split[0].equalsIgnoreCase("/woodcutting")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~WOODCUTTING INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Chop down trees.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Double Drops start to happen at 10 woodcutting skill");
			player.sendMessage(ChatColor.GRAY+"and it gets more frequent from there.");
    	}
    	if(split[0].equalsIgnoreCase("/mining")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~MINING INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Mining ore and stone,");
			player.sendMessage(ChatColor.DARK_GRAY+"the xp rate depends entirely upon the rarity of what you're harvesting.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Double Drops start to happen at 25 Mining skill,");
			player.sendMessage(ChatColor.GRAY+"and the chance for it increases with skill.");
    	}
    	if(split[0].equalsIgnoreCase("/repair")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~REPAIR INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Repairing tools and armor.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"High skill levels make a proc to fully repair items happen more often.");
			player.sendMessage(ChatColor.GREEN+"~~USE~~");
			player.sendMessage(ChatColor.GRAY+"Approach an Anvil (Iron Block) with the item you wish ");
			player.sendMessage(ChatColor.GRAY+"to repair in hand, right click to consume resources of the");
			player.sendMessage(ChatColor.GRAY+"same type to repair it. This does not work for stone/wood/gold");
    	}
    	if(split[0].equalsIgnoreCase("/unarmed")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~UNARMED INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Punching monsters and players.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Damage scales with unarmed skill. The first damage increase");
			player.sendMessage(ChatColor.DARK_GRAY+"happens at 50 skill. At very high skill levels, you will");
			player.sendMessage(ChatColor.DARK_GRAY+"gain a proc to disarm player opponents on hit");
    	}
    	if(split[0].equalsIgnoreCase("/herbalism")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~HERBALISM INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Farming and picking herbs.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"Increases healing effects of bread and stew.");
			player.sendMessage(ChatColor.GRAY+"Allows for chance to receive double drops based on skill");
    	}
    	if(split[0].equalsIgnoreCase("/excavation")){
			event.setCancelled(true);
			player.sendMessage(ChatColor.GREEN+"~~EXCAVATION INFO~~");
			player.sendMessage(ChatColor.GREEN+"Gaining Skill: "+ChatColor.DARK_GRAY+"Digging.");
			player.sendMessage(ChatColor.GREEN+"~~EFFECTS~~");
			player.sendMessage(ChatColor.GRAY+"You will find treasures while digging based on your excavation,");
			player.sendMessage(ChatColor.GRAY+"and at high levels the rewards are quite nice. The items you get");
			player.sendMessage(ChatColor.GRAY+"depend on the block you're digging.");
			player.sendMessage(ChatColor.GRAY+"Different blocks give diffrent stuff.");
    	}
		if(split[0].equalsIgnoreCase("/mcmmo")){
			event.setCancelled(true);
    		player.sendMessage(ChatColor.GRAY+"mcMMO is an RPG inspired plugin");
    		player.sendMessage(ChatColor.GRAY+"You can gain skills in several professions by");
    		player.sendMessage(ChatColor.GRAY+"doing things related to that profession.");
    		player.sendMessage(ChatColor.GRAY+"Mining for example will increase your mining XP.");
    		player.sendMessage(ChatColor.GRAY+"Wood Cutting will increase Wood Cutting, etc...");
    		player.sendMessage(ChatColor.GRAY+"Repairing is simple in mcMMO");
    		player.sendMessage(ChatColor.GRAY+"Say you want to repair an iron shovel");
    		player.sendMessage(ChatColor.GRAY+"start by making an anvil by combining 9 iron ingots");
    		player.sendMessage(ChatColor.GRAY+"on a workbench. Place the anvil and while holding the shovel");
    		player.sendMessage(ChatColor.GRAY+"right click the anvil to interact with it, If you have spare");
    		player.sendMessage(ChatColor.GRAY+"iron ingots in your inventory the item will be repaired.");
    		player.sendMessage(ChatColor.GRAY+"You cannot hurt other party members");
    		player.sendMessage(ChatColor.BLUE+"Set your own spawn with "+ChatColor.RED+"/myspawn");
    		player.sendMessage(ChatColor.GREEN+"Based on your skills you will get "+ChatColor.DARK_RED+"random procs "+ChatColor.GREEN+ "when");
    		player.sendMessage(ChatColor.GREEN+"using your profession, like "+ChatColor.DARK_RED+"double drops "+ChatColor.GREEN+"or "+ChatColor.DARK_RED+"better repairs");
    		player.sendMessage(ChatColor.GREEN+"Find out mcMMO commands with /mcc");
    	}
    	if(split[0].equalsIgnoreCase("/mcc")){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.GRAY+"mcMMO has a party system included");
    		player.sendMessage(ChatColor.GREEN+"~~Commands~~");
    		player.sendMessage(ChatColor.GRAY+"/party <name> - to join a party");
    		player.sendMessage(ChatColor.GRAY+"/party q - to quit a party");
    		player.sendMessage(ChatColor.GRAY+"/ptp <name> - party teleport");
    		player.sendMessage(ChatColor.GRAY+"/p - toggles party chat");
    		player.sendMessage(ChatColor.GRAY+"/setmyspawn - set your own spawn location");
    		player.sendMessage(ChatColor.GRAY+"/myspawn - travel to myspawn, clears inventory");
    		player.sendMessage(ChatColor.GRAY+"/whois - view detailed info about a player (req op)");
    		player.sendMessage(ChatColor.GRAY+"/woodcutting - displays info about the skill");
    		player.sendMessage(ChatColor.GRAY+"/mining - displays info about the skill");
    		player.sendMessage(ChatColor.GRAY+"/repair - displays info about the skill");
    		player.sendMessage(ChatColor.GRAY+"/unarmed - displays info about the skill");
    		player.sendMessage(ChatColor.GRAY+"/herbalism - displays info about the skill");
    		player.sendMessage(ChatColor.GRAY+"/excavation - displays info about the skill");
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
    		player.sendMessage("Excavation Skill: "+mcUsers.getProfile(target).getWoodCutting());
    		player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~");
    		player.sendMessage("X: "+x);
    		player.sendMessage("Y: "+y);
    		player.sendMessage("Z: "+z);
    		}
    	}
    	if(split[0].equalsIgnoreCase("/setmyspawn")){
    		if(!mcPermissions.getInstance().mySpawn(player)){
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
    		player.sendMessage(ChatColor.DARK_GREEN + "mcMMO stats");
    		player.sendMessage(ChatColor.DARK_GREEN + "Mining Skill: " + mcUsers.getProfile(player).getMining());
    		player.sendMessage(ChatColor.DARK_GREEN + "Repair Skill: " + mcUsers.getProfile(player).getRepair());
    		player.sendMessage(ChatColor.DARK_GREEN + "Woodcutting Skill: "+mcUsers.getProfile(player).getWoodCutting());
    		player.sendMessage(ChatColor.DARK_GREEN + "Unarmed Skill: " + mcUsers.getProfile(player).getUnarmed());
    		player.sendMessage(ChatColor.DARK_GREEN + "Herbalism Skill: " + mcUsers.getProfile(player).getHerbalism());
    		player.sendMessage(ChatColor.DARK_GREEN + "Excavation Skill: " + mcUsers.getProfile(player).getExcavation());
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
    		if(!mcPermissions.getInstance().adminChat(player)){
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
    public void onItemHeldChange(PlayerItemHeldEvent event) {
    	Player player = event.getPlayer();
    }
	private Block getBlockAt(int x, int y, int z) {
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
    			if(mcm.getInstance().inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if((player.isOp() || mcPermissions.getInstance().adminChat(player)) && mcConfig.getInstance().isAdminToggled(player.getName())){
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