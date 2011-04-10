package com.gmail.nossr50;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;


public class mcPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Player player = event.getPlayer();
    	if(player != null){
			Location mySpawn = mcUsers.getProfile(player).getMySpawn(player);
			if(mySpawn != null && plugin.getServer().getWorld(mcUsers.getProfile(player).getMySpawnWorld(plugin)) != null)
				mySpawn.setWorld(plugin.getServer().getWorld(mcUsers.getProfile(player).getMySpawnWorld(plugin)));
			if(mcPermissions.getInstance().mySpawn(player) && mySpawn != null){
		    	event.setRespawnLocation(mySpawn);
			}
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
    public void onPlayerLogin(PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	mcUsers.addUser(player);
    }
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	if(mcPermissions.getInstance().motd(player)){
    		player.sendMessage(ChatColor.BLUE + "This server is running mcMMO "+plugin.getDescription().getVersion()+" type /"+ChatColor.YELLOW+mcLoadProperties.mcmmo+ChatColor.BLUE+ " for help.");
    	}
    }
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	Action action = event.getAction();
    	Block block = event.getClickedBlock();
    	//Archery Nerf
    	if(player.getItemInHand().getTypeId() == 261 && mcLoadProperties.archeryFireRateLimit){
    		if(System.currentTimeMillis() < mcUsers.getProfile(player).getArcheryShotATS() + 1000){
    			/*
    			if(mcm.getInstance().hasArrows(player))
    				mcm.getInstance().addArrows(player);
    			*/
    			player.updateInventory();
    			event.setCancelled(true);
    		} else {
    			mcUsers.getProfile(player).setArcheryShotATS(System.currentTimeMillis());
    		}
    	}
    	/*
    	 * Ability checks
    	 */
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		ItemStack is = player.getItemInHand();
        	if(block != null && player != null && mcPermissions.getInstance().repair(player) && event.getClickedBlock().getTypeId() == 42){
            	mcRepair.getInstance().repairCheck(player, is, event.getClickedBlock());
            }
        	if(mcm.getInstance().abilityBlockCheck(block))
	    	{
	    		mcSkills.getInstance().abilityActivationCheck(player);
	    	}
    	}
    	if(action == Action.RIGHT_CLICK_AIR){
		    mcSkills.getInstance().abilityActivationCheck(player);
		    
		    /*
        	 * HERBALISM MODIFIERS
        	 */
        	if(mcPermissions.getInstance().herbalism(player)){
        		mcHerbalism.getInstance().breadCheck(player, player.getItemInHand());
        		mcHerbalism.getInstance().stewCheck(player, player.getItemInHand());
        	}
    	}
    	/*
    	 * ITEM CHECKS
    	 */
    	if(action == Action.RIGHT_CLICK_AIR)
        	mcItem.getInstance().itemChecks(player);
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		if(mcm.getInstance().abilityBlockCheck(event.getClickedBlock()))
    			mcItem.getInstance().itemChecks(player);
    	}
    }
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
    	Player player = event.getPlayer();
    	Location loc = player.getLocation();
    	if(mcPermissions.getInstance().setMySpawn(player)){
    		mcUsers.getProfile(player).setMySpawn(loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
    	}
    	player.sendMessage(ChatColor.DARK_AQUA + "Myspawn has been set to your current location.");
    }
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	//Check if the command is an mcMMO related help command
    	mcm.getInstance().mcmmoHelpCheck(split, player, event);
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+mcLoadProperties.mcability)){
    		event.setCancelled(true);
    		if(mcUsers.getProfile(player).getAbilityUse()){
    			player.sendMessage("Ability use toggled off");
    			mcUsers.getProfile(player).toggleAbilityUse();
    		} else {
    			player.sendMessage("Ability use toggled on");
    			mcUsers.getProfile(player).toggleAbilityUse();
    		}
    	}
		if(mcPermissions.getInstance().mcAbility(player) && split[0].equalsIgnoreCase("/"+mcLoadProperties.mcrefresh)){
			event.setCancelled(true);
    		if(!mcPermissions.getInstance().mcrefresh(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(split.length >= 2 && isPlayer(split[1])){
    			player.sendMessage("You have refreshed "+split[1]+"'s cooldowns!");
    			player = getPlayer(split[1]);
    		}
			/*
			 * PREP MODES
			 */
    		mcUsers.getProfile(player).setAxePreparationMode(false);
    		mcUsers.getProfile(player).setFistsPreparationMode(false);
    		mcUsers.getProfile(player).setSwordsPreparationMode(false);
    		mcUsers.getProfile(player).setPickaxePreparationMode(false);
    		/*
    		 * GIGA DRILL BREAKER
    		 */
    		mcUsers.getProfile(player).setGigaDrillBreakerMode(false);
    		mcUsers.getProfile(player).setGigaDrillBreakerDeactivatedTimeStamp((long) 0);
    		/*
    		 * SERRATED STRIKE
    		 */
    		mcUsers.getProfile(player).setSerratedStrikesMode(false);
    		mcUsers.getProfile(player).setSerratedStrikesDeactivatedTimeStamp((long) 0);
    		/*
    		 * SUPER BREAKER
    		 */
    		mcUsers.getProfile(player).setSuperBreakerMode(false);
    		mcUsers.getProfile(player).setSuperBreakerDeactivatedTimeStamp((long) 0);
    		/*
    		 * TREE FELLER
    		 */
    		mcUsers.getProfile(player).setTreeFellerMode(false);
    		mcUsers.getProfile(player).setTreeFellerDeactivatedTimeStamp((long) 0);
    		/*
    		 * BERSERK
    		 */
    		mcUsers.getProfile(player).setBerserkMode(false);
    		mcUsers.getProfile(player).setBerserkDeactivatedTimeStamp((long)0);
    		
    		player.sendMessage(ChatColor.GREEN+"**ABILITIES REFRESHED!**");
    	}
    	if(split[0].equalsIgnoreCase("/"+mcLoadProperties.mcitem)){
    		
    	}
    	/*
    	 * GODMODE COMMAND
    	 */
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+mcLoadProperties.mcgod)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mcgod(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(mcConfig.getInstance().isGodModeToggled(playerName)){
    			player.sendMessage(ChatColor.YELLOW+"mcMMO Godmode Disabled");
    			mcConfig.getInstance().toggleGodMode(playerName);
    		} else {
    			player.sendMessage(ChatColor.YELLOW+"mcMMO Godmode Enabled");
    			mcConfig.getInstance().toggleGodMode(playerName);
    		}
    	}
    	if(mcPermissions.getInstance().mySpawn(player) && split[0].equalsIgnoreCase("/"+mcLoadProperties.clearmyspawn)){
    		event.setCancelled(true);
    		double x = plugin.getServer().getWorlds().get(0).getSpawnLocation().getX();
    		double y = plugin.getServer().getWorlds().get(0).getSpawnLocation().getY();
    		double z = plugin.getServer().getWorlds().get(0).getSpawnLocation().getZ();
    		String worldname = plugin.getServer().getWorlds().get(0).getName();
    		mcUsers.getProfile(player).setMySpawn(x, y, z, worldname);
    		player.sendMessage(ChatColor.DARK_AQUA+"Myspawn is now cleared.");
    	}
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+mcLoadProperties.mmoedit)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mmoedit(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.mmoedit+" playername skillname newvalue");
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && mcm.getInstance().isInt(split[3]) && mcSkills.getInstance().isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				mcUsers.getProfile(getPlayer(split[1])).modifyskill(newvalue, split[2]);
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3){
    			if(mcm.getInstance().isInt(split[2]) && mcSkills.getInstance().isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				mcUsers.getProfile(player).modifyskill(newvalue, split[1]);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified.");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.mmoedit+" playername skillname newvalue");
    		}
    	}
    	/*
    	 * ADD EXPERIENCE COMMAND
    	 */
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+mcLoadProperties.addxp)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mmoedit(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.addxp+" playername skillname xp");
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && mcm.getInstance().isInt(split[3]) && mcSkills.getInstance().isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				mcUsers.getProfile(getPlayer(split[1])).addXpToSkill(newvalue, split[2]);
    				getPlayer(split[1]).sendMessage(ChatColor.GREEN+"Experience granted!");
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3){
    			if(mcm.getInstance().isInt(split[2]) && mcSkills.getInstance().isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				mcUsers.getProfile(player).addXpToSkill(newvalue, split[1]);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified.");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.addxp+" playername skillname xp");
    		}
    	}
    	
    	if(mcUsers.getProfile(player).inParty() && split[0].equalsIgnoreCase("/"+mcLoadProperties.ptp)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().partyTeleport(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.ptp+" <playername>");
    			return;
    		}
    		if(!isPlayer(split[1])){
    			player.sendMessage("That is not a valid player");
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
    	/*
    	 * WHOIS COMMAND
    	 */
    	if((player.isOp() || mcPermissions.getInstance().whois(player)) && split[0].equalsIgnoreCase("/"+mcLoadProperties.whois)){
    		event.setCancelled(true);
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED + "Proper usage is /"+mcLoadProperties.whois+" <playername>");
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
    		player.sendMessage(ChatColor.GREEN+"mcMMO Stats for "+ChatColor.YELLOW+target.getName());
    		if(mcPermissions.getInstance().mining(target))
    		player.sendMessage(ChatColor.YELLOW + "Mining Skill: " + ChatColor.GREEN + mcUsers.getProfile(target).getMining()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getMiningGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("mining")+")");
    		if(mcPermissions.getInstance().repair(target))
    		player.sendMessage(ChatColor.YELLOW + "Repair Skill: "+ ChatColor.GREEN + mcUsers.getProfile(target).getRepair()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getRepairGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("repair")+")");
    		if(mcPermissions.getInstance().woodcutting(target))
    		player.sendMessage(ChatColor.YELLOW + "Woodcutting Skill: "+ ChatColor.GREEN + mcUsers.getProfile(target).getWoodCutting()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getWoodCuttingGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("woodcutting")+")");
    		if(mcPermissions.getInstance().unarmed(target))
    		player.sendMessage(ChatColor.YELLOW + "Unarmed Skill: " + ChatColor.GREEN + mcUsers.getProfile(target).getUnarmed()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getUnarmedGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("unarmed")+")");
    		if(mcPermissions.getInstance().herbalism(target))
    		player.sendMessage(ChatColor.YELLOW + "Herbalism Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(target).getHerbalism()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getHerbalismGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("herbalism")+")");
    		if(mcPermissions.getInstance().excavation(target))
    		player.sendMessage(ChatColor.YELLOW + "Excavation Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(target).getExcavation()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getExcavationGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("excavation")+")");
    		if(mcPermissions.getInstance().archery(target))
    		player.sendMessage(ChatColor.YELLOW + "Archery Skill: " + ChatColor.GREEN + mcUsers.getProfile(target).getArchery()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getArcheryGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("archery")+")");
    		if(mcPermissions.getInstance().swords(target))
    		player.sendMessage(ChatColor.YELLOW + "Swords Skill: " + ChatColor.GREEN + mcUsers.getProfile(target).getSwords()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getSwordsGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("swords")+")");
    		if(mcPermissions.getInstance().axes(target))
    		player.sendMessage(ChatColor.YELLOW + "Axes Skill: " + ChatColor.GREEN + mcUsers.getProfile(target).getAxes()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getAxesGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("axes")+")");
    		if(mcPermissions.getInstance().acrobatics(target))
    		player.sendMessage(ChatColor.YELLOW + "Acrobatics Skill: " + ChatColor.GREEN + mcUsers.getProfile(target).getAcrobatics()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(target).getAcrobaticsGather()
    				+"/"+mcUsers.getProfile(target).getXpToLevel("acrobatics")+")");
    		player.sendMessage(ChatColor.DARK_RED+"POWER LEVEL: "+ChatColor.GREEN+(mcUsers.getProfile(target).getPowerLevel()));
    		player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~");
    		player.sendMessage("X: "+x);
    		player.sendMessage("Y: "+y);
    		player.sendMessage("Z: "+z);
    		}
    	}
    	/*
    	 * STATS COMMAND
    	 */
    	if(split[0].equalsIgnoreCase("/"+mcLoadProperties.stats)){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.GREEN + "Your mcMMO Stats");
    		if(mcPermissions.getInstance().permissionsEnabled)
    			player.sendMessage(ChatColor.DARK_GRAY+"If you don't have access to a skill it will not be shown here.");
    		if(mcPermissions.getInstance().mining(player))
    		player.sendMessage(ChatColor.YELLOW + "Mining Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getMining()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getMiningGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("mining")+")");
    		if(mcPermissions.getInstance().repair(player))
    		player.sendMessage(ChatColor.YELLOW + "Repair Skill: "+ ChatColor.GREEN + mcUsers.getProfile(player).getRepair()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getRepairGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("repair")+")");
    		if(mcPermissions.getInstance().woodcutting(player))
    		player.sendMessage(ChatColor.YELLOW + "Woodcutting Skill: "+ ChatColor.GREEN + mcUsers.getProfile(player).getWoodCutting()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getWoodCuttingGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("woodcutting")+")");
    		if(mcPermissions.getInstance().unarmed(player))
    		player.sendMessage(ChatColor.YELLOW + "Unarmed Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getUnarmed()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getUnarmedGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("unarmed")+")");
    		if(mcPermissions.getInstance().herbalism(player))
    		player.sendMessage(ChatColor.YELLOW + "Herbalism Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(player).getHerbalism()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getHerbalismGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("herbalism")+")");
    		if(mcPermissions.getInstance().excavation(player))
    		player.sendMessage(ChatColor.YELLOW + "Excavation Skill: "+ ChatColor.GREEN +  mcUsers.getProfile(player).getExcavation()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getExcavationGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("excavation")+")");
    		if(mcPermissions.getInstance().archery(player))
    		player.sendMessage(ChatColor.YELLOW + "Archery Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getArchery()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getArcheryGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("archery")+")");
    		if(mcPermissions.getInstance().swords(player))
    		player.sendMessage(ChatColor.YELLOW + "Swords Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getSwords()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getSwordsGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("swords")+")");
    		if(mcPermissions.getInstance().axes(player))
    		player.sendMessage(ChatColor.YELLOW + "Axes Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getAxes()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getAxesGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("axes")+")");
    		if(mcPermissions.getInstance().acrobatics(player))
    		player.sendMessage(ChatColor.YELLOW + "Acrobatics Skill: " + ChatColor.GREEN + mcUsers.getProfile(player).getAcrobatics()+ChatColor.DARK_AQUA 
    				+ " XP("+mcUsers.getProfile(player).getAcrobaticsGather()
    				+"/"+mcUsers.getProfile(player).getXpToLevel("acrobatics")+")");
    		player.sendMessage(ChatColor.DARK_RED+"POWER LEVEL: "+ChatColor.GREEN+(mcUsers.getProfile(player).getPowerLevel()));
    	}
    	//Invite Command
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+mcLoadProperties.invite)){
    		event.setCancelled(true);
    		if(!mcUsers.getProfile(player).inParty()){
    			player.sendMessage(ChatColor.RED+"You are not in a party.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.invite+" <playername>");
    			return;
    		}
    		if(mcUsers.getProfile(player).inParty() && split.length >= 2 && isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			mcUsers.getProfile(target).modifyInvite(mcUsers.getProfile(player).getParty());
    			player.sendMessage(ChatColor.GREEN+"Invite sent successfully");
    			target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+mcUsers.getProfile(target).getInvite()+" from "+player.getName());
    			target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+"/"+mcLoadProperties.accept+ChatColor.YELLOW+" to accept the invite");
    		}
    	}
    	//Accept invite
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+mcLoadProperties.accept)){
    		event.setCancelled(true);
    		if(mcUsers.getProfile(player).hasPartyInvite()){
    			if(mcUsers.getProfile(player).inParty()){
    				mcParty.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			}
    			mcUsers.getProfile(player).acceptInvite();
    			mcParty.getInstance().informPartyMembers(player, getPlayersOnline());
    			player.sendMessage(ChatColor.GREEN+"Invite accepted. You have joined party ("+mcUsers.getProfile(player).getParty()+")");
    		} else {
    			player.sendMessage(ChatColor.RED+"You have no invites at this time");
    		}
    	}
    	//Party command
    	if(split[0].equalsIgnoreCase("/"+mcLoadProperties.party)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(split.length == 1 && !mcUsers.getProfile(player).inParty()){
    			player.sendMessage("Proper usage is "+"/"+mcLoadProperties.party+" <name> or 'q' to quit");
    			return;
    		}
    		if(split.length == 1 && mcUsers.getProfile(player).inParty()){
            	String tempList = "";
            	int x = 0;
                for(Player p : plugin.getServer().getOnlinePlayers())
                {
                	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(p).getParty())){
	                	if(p != null && x+1 >= mcParty.getInstance().partyCount(player, getPlayersOnline())){
	                		tempList+= p.getName();
	                		x++;
	                	}
	                	if(p != null && x < mcParty.getInstance().partyCount(player, getPlayersOnline())){
	                		tempList+= p.getName() +", ";
	                		x++;
	                	}
                	}
                }
                player.sendMessage(ChatColor.GREEN+"You are in party \""+mcUsers.getProfile(player).getParty()+"\"");
                player.sendMessage(ChatColor.GREEN + "Party Members ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");
    		}
    		if(split.length > 1 && split[1].equals("q") && mcUsers.getProfile(player).inParty()){
    			mcParty.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			mcUsers.getProfile(player).removeParty();
    			player.sendMessage(ChatColor.RED + "You have left that party");
    			return;
    		}
    		if(split.length >= 2){
	    		if(mcUsers.getProfile(player).inParty())
	    			mcParty.getInstance().informPartyMembersQuit(player, getPlayersOnline());
		    	mcUsers.getProfile(player).setParty(split[1]);
		    	player.sendMessage("Joined Party: " + split[1]);
		    	mcParty.getInstance().informPartyMembers(player, getPlayersOnline());
	    		}
    	}
    	if(split[0].equalsIgnoreCase("/p")){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
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
    	/*
    	 * MYSPAWN
    	 */
    	if(split[0].equalsIgnoreCase("/"+mcLoadProperties.myspawn)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mySpawn(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +" Insufficient permissions.");
    			return;
    		}
    		if(mcUsers.getProfile(player).getMySpawn(player) != null){
	    		if(mcLoadProperties.myspawnclearsinventory)
	    			player.getInventory().clear();
	    		player.setHealth(20);
	    		Location mySpawn = mcUsers.getProfile(player).getMySpawn(player);
	    		//player.sendMessage("mcMMO DEBUG CODE 1");
	    		if(mcUsers.getProfile(player).getMySpawnWorld(plugin) != null && !mcUsers.getProfile(player).getMySpawnWorld(plugin).equals("")){
	    			mySpawn.setWorld(plugin.getServer().getWorld(mcUsers.getProfile(player).getMySpawnWorld(plugin)));
	    			//player.sendMessage("mcMMO DEBUG CODE 2");
	    			} else {
	    				//player.sendMessage("mcMMO DEBUG CODE 5");
	    				mySpawn.setWorld(plugin.getServer().getWorlds().get(0));
	    		}
	    		//player.sendMessage("mcMMO DEBUG CODE 3");
	    		player.teleportTo(mySpawn); //It's done twice because teleporting from one world to another is weird
	    		player.teleportTo(mySpawn);
	    		//Two lines of teleporting to prevent a bug when players try teleporting from one world to another bringing them to that worlds spawn at first.
	    		//player.sendMessage("mcMMO DEBUG CODE 4");
	    		if(mcLoadProperties.myspawnclearsinventory)
	    			player.sendMessage("Traveled to your MySpawn, Inventory cleared & health restored");
	    		else
	    			player.sendMessage("Traveled to your MySpawn, Health has been restored.");
    		} else {
    			player.sendMessage(ChatColor.RED+"Configure your myspawn first with /setmyspawn");
    		}
    	}
    }
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";
    	if(mcConfig.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		log.log(Level.INFO, "[P]("+mcUsers.getProfile(player).getParty()+")"+"<"+player.getName()+"> "+event.getMessage());
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(mcUsers.getProfile(herp).inParty()){
    			if(mcParty.getInstance().inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if((player.isOp() || mcPermissions.getInstance().adminChat(player)) && mcConfig.getInstance().isAdminToggled(player.getName())){
    		log.log(Level.INFO, "[A]"+"<"+player.getName()+"> "+event.getMessage());
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if((herp.isOp() || mcPermissions.getInstance().adminChat(herp))){
    				herp.sendMessage(y+event.getMessage());
    			}
    		}
    		return;
    	}
    	}
}