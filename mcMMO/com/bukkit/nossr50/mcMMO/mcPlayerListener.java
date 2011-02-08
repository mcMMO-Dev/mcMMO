package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class mcPlayerListener extends PlayerListener {
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
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
    	player.sendMessage(ChatColor.BLUE + "This server is running mcMMO type "+ChatColor.YELLOW+"/mcmmo "+ChatColor.BLUE+ "for help.");
    	player.sendMessage(ChatColor.RED+"WARNING: "+ChatColor.DARK_GRAY+ "Using /myspawn will clear your inventory!"); 
    }
    //Check if string is a player
    
    public void onPlayerItem(PlayerItemEvent event) {
    	Block block = event.getBlockClicked();
    	Player player = event.getPlayer();
    	ItemStack is = player.getItemInHand();
    	if(block != null && block.getTypeId() == 42){
    	short durability = is.getDurability();
    		if(mcm.getInstance().isArmor(is) && block.getTypeId() == 42){
    			if(mcm.getInstance().isDiamondArmor(is) && mcm.getInstance().hasDiamond(player)){
    			mcm.getInstance().removeDiamond(player);
    			player.getItemInHand().setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
    			mcUsers.getProfile(player).skillUpRepair(1);
    			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
    			} else if (mcm.getInstance().isIronArmor(is) && mcm.getInstance().hasIron(player)){
    			player.sendMessage(ChatColor.DARK_RED+"Changing the durability of iron armor is currently bugged.");
    			player.sendMessage(ChatColor.YELLOW+"I'm looking into this issue. -mcMMO Author");
    			/*
    			mcm.getInstance().removeIron(player);
        		is.setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
        		mcUsers.getProfile(player).skillUpRepair(1);
        		player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");	
        		*/
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
    		} else if (mcm.getInstance().isDiamondTools(is) && mcm.getInstance().hasDiamond(player) && mcUsers.getProfile(player).getRepairInt() > 50){ //Check if its diamond and the player has diamonds
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
    	}
    }
    public void onPlayerCommand(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
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
    		player.sendMessage(ChatColor.GRAY+"/setmyspawn - set your own spawn location");
    		player.sendMessage(ChatColor.GRAY+"/myspawn - travel to myspawn, clears inventory");
    		player.sendMessage(ChatColor.GRAY+"/setspawn - Server ops can designate a 'spawn'");
    		player.sendMessage(ChatColor.GRAY+"/spawn - Travel to the op designated spawn");
    		player.sendMessage(ChatColor.GRAY+"/whois - view detailed info about a player (req op)");
    	}
    	if(mcUsers.getProfile(player).inParty() && split[0].equalsIgnoreCase("/ptp")){
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
}