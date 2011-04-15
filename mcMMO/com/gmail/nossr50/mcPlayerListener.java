package com.gmail.nossr50;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.PlayerList.PlayerProfile;


public class mcPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(player != null){
			Location mySpawn = PP.getMySpawn(player);
			if(mySpawn != null && plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)) != null)
				mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
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
    		player.sendMessage(ChatColor.BLUE +"This server is running mcMMO "+plugin.getDescription().getVersion()+" type /"+ChatColor.YELLOW+mcLoadProperties.mcmmo+ChatColor.BLUE+ " for help.");
    		player.sendMessage(ChatColor.GREEN+"http://mcmmo.wikia.com"+ChatColor.BLUE+" - mcMMO Wiki");
    	}
    }
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	Action action = event.getAction();
    	Block block = event.getClickedBlock();
    	//Archery Nerf
    	if(player.getItemInHand().getTypeId() == 261 && mcLoadProperties.archeryFireRateLimit){
    		if(System.currentTimeMillis() < PP.getArcheryShotATS() + 1000){
    			/*
    			if(mcm.getInstance().hasArrows(player))
    				mcm.getInstance().addArrows(player);
    			*/
    			player.updateInventory();
    			event.setCancelled(true);
    		} else {
    			PP.setArcheryShotATS(System.currentTimeMillis());
    		}
    	}
    	/*
    	 * Ability checks
    	 */
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		ItemStack is = player.getItemInHand();
    		if(block != null && player != null){
    			if(block.getTypeId() == 26 && mcPermissions.getInstance().setMySpawn(player)){
    		    	Location loc = player.getLocation();
    		    	if(mcPermissions.getInstance().setMySpawn(player)){
    		    		PP.setMySpawn(loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
    		    	}
    		    	player.sendMessage(ChatColor.DARK_AQUA + "Myspawn has been set to your current location.");
    			}
    		}
        	if(block != null && player != null && mcPermissions.getInstance().repair(player) && event.getClickedBlock().getTypeId() == 42){
            	mcRepair.getInstance().repairCheck(player, is, event.getClickedBlock());
            }
        	
        	if(mcm.getInstance().abilityBlockCheck(block))
	    	{
        		if(block != null && mcm.getInstance().isHoe(player.getItemInHand()) && block.getTypeId() != 3 && block.getTypeId() != 2 && block.getTypeId() != 60){
        			mcSkills.getInstance().hoeReadinessCheck(player);
        		}
	    		mcSkills.getInstance().abilityActivationCheck(player);
	    	}
        	
        	//GREEN THUMB
        	if(block != null && (block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT) && player.getItemInHand().getType() == Material.SEEDS){
        		boolean pass = false;
        		if(mcHerbalism.getInstance().hasSeeds(player)){
        			mcHerbalism.getInstance().removeSeeds(player);
	        		if(block.getType() == Material.COBBLESTONE && Math.random() * 1500 <= PP.getHerbalismInt()){
	        			player.sendMessage(ChatColor.GREEN+"**GREEN THUMB**");
	        			block.setType(Material.MOSSY_COBBLESTONE);
	        			pass = true;
	        		}
	        		if(block.getType() == Material.DIRT && Math.random() * 1500 <= PP.getHerbalismInt()){
	        			player.sendMessage(ChatColor.GREEN+"**GREEN THUMB**");
	        			block.setType(Material.GRASS);
	        			pass = true;
	        		}
	        		if(pass == false)
	        			player.sendMessage(ChatColor.RED+"**GREEN THUMB FAIL**");
	        		}
        		return;
        	}
    	}
    	if(action == Action.RIGHT_CLICK_AIR){
    		mcSkills.getInstance().hoeReadinessCheck(player);
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
        	mcItem.getInstance().itemChecks(player, plugin);
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		if(mcm.getInstance().abilityBlockCheck(event.getClickedBlock()))
    			mcItem.getInstance().itemChecks(player, plugin);
    	}
    }
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	//Check if the command is an mcMMO related help command
    	mcm.getInstance().mcmmoHelpCheck(split, player, event);
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+mcLoadProperties.mcability)){
    		event.setCancelled(true);
    		if(PP.getAbilityUse()){
    			player.sendMessage("Ability use toggled off");
    			PP.toggleAbilityUse();
    		} else {
    			player.sendMessage("Ability use toggled on");
    			PP.toggleAbilityUse();
    		}
    	}
    	/*
    	if(split[0].equalsIgnoreCase("/mutechat")){
    		event.setCancelled(true);
    		if(PP.getPartyChatOnlyToggle() == true)
    			player.sendMessage("Party Chat Only "+ChatColor.RED+"Off");
    		if(PP.getPartyChatOnlyToggle() == false)
    			player.sendMessage("Party Chat Only "+ChatColor.RED+"On");
    		PP.togglePartyChatOnly();
    	}
    	*/
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
    		PP.setRecentlyHurt((long) 0);
    		PP.setHoePreparationMode(false);
    		PP.setAxePreparationMode(false);
    		PP.setFistsPreparationMode(false);
    		PP.setSwordsPreparationMode(false);
    		PP.setPickaxePreparationMode(false);
    		/*
    		 * GREEN TERRA
    		 */
    		PP.setGreenTerraMode(false);
    		PP.setGreenTerraDeactivatedTimeStamp((long) 0);
    		
    		/*
    		 * GIGA DRILL BREAKER
    		 */
    		PP.setGigaDrillBreakerMode(false);
    		PP.setGigaDrillBreakerDeactivatedTimeStamp((long) 0);
    		/*
    		 * SERRATED STRIKE
    		 */
    		PP.setSerratedStrikesMode(false);
    		PP.setSerratedStrikesDeactivatedTimeStamp((long) 0);
    		/*
    		 * SUPER BREAKER
    		 */
    		PP.setSuperBreakerMode(false);
    		PP.setSuperBreakerDeactivatedTimeStamp((long) 0);
    		/*
    		 * TREE FELLER
    		 */
    		PP.setTreeFellerMode(false);
    		PP.setTreeFellerDeactivatedTimeStamp((long) 0);
    		/*
    		 * BERSERK
    		 */
    		PP.setBerserkMode(false);
    		PP.setBerserkDeactivatedTimeStamp((long)0);
    		
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
    		PP.setMySpawn(x, y, z, worldname);
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
    				mcUsers.getProfile(getPlayer(split[1]).getName()).modifyskill(newvalue, split[2]);
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3){
    			if(mcm.getInstance().isInt(split[2]) && mcSkills.getInstance().isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				PP.modifyskill(newvalue, split[1]);
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
    				mcUsers.getProfile(getPlayer(split[1]).getName()).addXpToSkill(newvalue, split[2]);
    				getPlayer(split[1]).sendMessage(ChatColor.GREEN+"Experience granted!");
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3){
    			if(mcm.getInstance().isInt(split[2]) && mcSkills.getInstance().isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				PP.addXpToSkill(newvalue, split[1]);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified.");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.addxp+" playername skillname xp");
    		}
    	}
    	
    	if(PP.inParty() && split[0].equalsIgnoreCase("/"+mcLoadProperties.ptp)){
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
        	PlayerProfile PPt = mcUsers.getProfile(target.getName());
        	if(PP.getParty().equals(PPt.getParty())){
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
    		PlayerProfile PPt = mcUsers.getProfile(target.getName());
    		double x,y,z;
    		x = target.getLocation().getX();
    		y = target.getLocation().getY();
    		z = target.getLocation().getZ();
    		player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~");
    		player.sendMessage(target.getName());
    		if(PPt.inParty())
    		player.sendMessage("Party: "+PPt.getParty());
    		player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)");
    		player.sendMessage("OP: " + target.isOp());
    		player.sendMessage(ChatColor.GREEN+"mcMMO Stats for "+ChatColor.YELLOW+target.getName());
    		if(mcPermissions.getInstance().taming(target))
        		player.sendMessage(ChatColor.YELLOW + "Taming Skill: " + ChatColor.GREEN + PPt.getTaming()+ChatColor.DARK_AQUA 
        				+ " XP("+PPt.getTamingXP()
        				+"/"+PPt.getXpToLevel("taming")+")");
    		if(mcPermissions.getInstance().mining(target))
    		player.sendMessage(ChatColor.YELLOW + "Mining Skill: " + ChatColor.GREEN + PPt.getMining()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getMiningXP()
    				+"/"+PPt.getXpToLevel("mining")+")");
    		if(mcPermissions.getInstance().repair(target))
    		player.sendMessage(ChatColor.YELLOW + "Repair Skill: "+ ChatColor.GREEN + PPt.getRepair()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getRepairXP()
    				+"/"+PPt.getXpToLevel("repair")+")");
    		if(mcPermissions.getInstance().woodcutting(target))
    		player.sendMessage(ChatColor.YELLOW + "Woodcutting Skill: "+ ChatColor.GREEN + PPt.getWoodCutting()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getWoodCuttingXP()
    				+"/"+PPt.getXpToLevel("woodcutting")+")");
    		if(mcPermissions.getInstance().unarmed(target))
    		player.sendMessage(ChatColor.YELLOW + "Unarmed Skill: " + ChatColor.GREEN + PPt.getUnarmed()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getUnarmedXP()
    				+"/"+PPt.getXpToLevel("unarmed")+")");
    		if(mcPermissions.getInstance().herbalism(target))
    		player.sendMessage(ChatColor.YELLOW + "Herbalism Skill: "+ ChatColor.GREEN +  PPt.getHerbalism()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getHerbalismXP()
    				+"/"+PPt.getXpToLevel("herbalism")+")");
    		if(mcPermissions.getInstance().excavation(target))
    		player.sendMessage(ChatColor.YELLOW + "Excavation Skill: "+ ChatColor.GREEN +  PPt.getExcavation()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getExcavationXP()
    				+"/"+PPt.getXpToLevel("excavation")+")");
    		if(mcPermissions.getInstance().archery(target))
    		player.sendMessage(ChatColor.YELLOW + "Archery Skill: " + ChatColor.GREEN + PPt.getArchery()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getArcheryXP()
    				+"/"+PPt.getXpToLevel("archery")+")");
    		if(mcPermissions.getInstance().swords(target))
    		player.sendMessage(ChatColor.YELLOW + "Swords Skill: " + ChatColor.GREEN + PPt.getSwords()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getSwordsXP()
    				+"/"+PPt.getXpToLevel("swords")+")");
    		if(mcPermissions.getInstance().axes(target))
    		player.sendMessage(ChatColor.YELLOW + "Axes Skill: " + ChatColor.GREEN + PPt.getAxes()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getAxesXP()
    				+"/"+PPt.getXpToLevel("axes")+")");
    		if(mcPermissions.getInstance().acrobatics(target))
    		player.sendMessage(ChatColor.YELLOW + "Acrobatics Skill: " + ChatColor.GREEN + PPt.getAcrobatics()+ChatColor.DARK_AQUA 
    				+ " XP("+PPt.getAcrobaticsXP()
    				+"/"+PPt.getXpToLevel("acrobatics")+")");
    		player.sendMessage(ChatColor.DARK_RED+"POWER LEVEL: "+ChatColor.GREEN+(mcm.getInstance().getPowerLevel(target)));
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
    		
    		if(mcPermissions.getInstance().taming(player))
        		player.sendMessage(ChatColor.YELLOW + "Taming Skill: " + ChatColor.GREEN + PP.getTaming()+ChatColor.DARK_AQUA 
        				+ " XP("+PP.getTamingXP()
        				+"/"+PP.getXpToLevel("taming")+")");
    		if(mcPermissions.getInstance().mining(player))
    		player.sendMessage(ChatColor.YELLOW + "Mining Skill: " + ChatColor.GREEN + PP.getMining()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getMiningXP()
    				+"/"+PP.getXpToLevel("mining")+")");
    		if(mcPermissions.getInstance().repair(player))
    		player.sendMessage(ChatColor.YELLOW + "Repair Skill: "+ ChatColor.GREEN + PP.getRepair()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getRepairXP()
    				+"/"+PP.getXpToLevel("repair")+")");
    		if(mcPermissions.getInstance().woodcutting(player))
    		player.sendMessage(ChatColor.YELLOW + "Woodcutting Skill: "+ ChatColor.GREEN + PP.getWoodCutting()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getWoodCuttingXP()
    				+"/"+PP.getXpToLevel("woodcutting")+")");
    		if(mcPermissions.getInstance().unarmed(player))
    		player.sendMessage(ChatColor.YELLOW + "Unarmed Skill: " + ChatColor.GREEN + PP.getUnarmed()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getUnarmedXP()
    				+"/"+PP.getXpToLevel("unarmed")+")");
    		if(mcPermissions.getInstance().herbalism(player))
    		player.sendMessage(ChatColor.YELLOW + "Herbalism Skill: "+ ChatColor.GREEN +  PP.getHerbalism()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getHerbalismXP()
    				+"/"+PP.getXpToLevel("herbalism")+")");
    		if(mcPermissions.getInstance().excavation(player))
    		player.sendMessage(ChatColor.YELLOW + "Excavation Skill: "+ ChatColor.GREEN +  PP.getExcavation()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getExcavationXP()
    				+"/"+PP.getXpToLevel("excavation")+")");
    		if(mcPermissions.getInstance().archery(player))
    		player.sendMessage(ChatColor.YELLOW + "Archery Skill: " + ChatColor.GREEN + PP.getArchery()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getArcheryXP()
    				+"/"+PP.getXpToLevel("archery")+")");
    		if(mcPermissions.getInstance().swords(player))
    		player.sendMessage(ChatColor.YELLOW + "Swords Skill: " + ChatColor.GREEN + PP.getSwords()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getSwordsXP()
    				+"/"+PP.getXpToLevel("swords")+")");
    		if(mcPermissions.getInstance().axes(player))
    		player.sendMessage(ChatColor.YELLOW + "Axes Skill: " + ChatColor.GREEN + PP.getAxes()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getAxesXP()
    				+"/"+PP.getXpToLevel("axes")+")");
    		if(mcPermissions.getInstance().acrobatics(player))
    		player.sendMessage(ChatColor.YELLOW + "Acrobatics Skill: " + ChatColor.GREEN + PP.getAcrobatics()+ChatColor.DARK_AQUA 
    				+ " XP("+PP.getAcrobaticsXP()
    				+"/"+PP.getXpToLevel("acrobatics")+")");
    		player.sendMessage(ChatColor.DARK_RED+"POWER LEVEL: "+ChatColor.GREEN+(mcm.getInstance().getPowerLevel(player)));
    	}
    	//Invite Command
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+mcLoadProperties.invite)){
    		event.setCancelled(true);
    		if(!PP.inParty()){
    			player.sendMessage(ChatColor.RED+"You are not in a party.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+mcLoadProperties.invite+" <playername>");
    			return;
    		}
    		if(PP.inParty() && split.length >= 2 && isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			PlayerProfile PPt = mcUsers.getProfile(target.getName());
    			PPt.modifyInvite(PP.getParty());
    			player.sendMessage(ChatColor.GREEN+"Invite sent successfully");
    			target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+PPt.getInvite()+" from "+player.getName());
    			target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+"/"+mcLoadProperties.accept+ChatColor.YELLOW+" to accept the invite");
    		}
    	}
    	//Accept invite
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+mcLoadProperties.accept)){
    		event.setCancelled(true);
    		if(PP.hasPartyInvite()){
    			if(PP.inParty()){
    				mcParty.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			}
    			PP.acceptInvite();
    			mcParty.getInstance().informPartyMembers(player, getPlayersOnline());
    			player.sendMessage(ChatColor.GREEN+"Invite accepted. You have joined party ("+PP.getParty()+")");
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
    		if(split.length == 1 && !PP.inParty()){
    			player.sendMessage("Proper usage is "+"/"+mcLoadProperties.party+" <name> or 'q' to quit");
    			return;
    		}
    		if(split.length == 1 && PP.inParty()){
            	String tempList = "";
            	int x = 0;
                for(Player p : plugin.getServer().getOnlinePlayers())
                {
                	if(PP.getParty().equals(mcUsers.getProfile(p.getName()).getParty())){
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
                player.sendMessage(ChatColor.GREEN+"You are in party \""+PP.getParty()+"\"");
                player.sendMessage(ChatColor.GREEN + "Party Members ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");
    		}
    		if(split.length > 1 && split[1].equals("q") && PP.inParty()){
    			mcParty.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			PP.removeParty();
    			player.sendMessage(ChatColor.RED + "You have left that party");
    			return;
    		}
    		if(split.length >= 2){
	    		if(PP.inParty())
	    			mcParty.getInstance().informPartyMembersQuit(player, getPlayersOnline());
		    	PP.setParty(split[1]);
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
    		if(System.currentTimeMillis() < PP.getMySpawnATS() + 3600000){
    			long x = System.currentTimeMillis();
    			int seconds = 0;
    			int minutes = 0;
    			while(x < PP.getMySpawnATS() + 3600000){
    				x+=1000;
    				seconds++;
    			}
    			while(seconds >= 60){
    				seconds-=60;
    				minutes++;
    			}
    			player.sendMessage("You must wait "+minutes+"m"+seconds+"s"+" to use myspawn");
    			return;
    		}
    		PP.setMySpawnATS(System.currentTimeMillis());
    		if(PP.getMySpawn(player) != null){
	    		player.setHealth(20);
	    		Location mySpawn = PP.getMySpawn(player);
	    		//player.sendMessage("mcMMO DEBUG CODE 1");
	    		if(PP.getMySpawnWorld(plugin) != null && !PP.getMySpawnWorld(plugin).equals("")){
	    			mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
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
	    			player.sendMessage("Traveled to your MySpawn");
    		} else {
    			player.sendMessage(ChatColor.RED+"Configure your myspawn first with a bed.");
    		}
    	}
    }
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerProfile PP = mcUsers.getProfile(player.getName());
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";
    	if(mcConfig.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		log.log(Level.INFO, "[P]("+PP.getParty()+")"+"<"+player.getName()+"> "+event.getMessage());
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(mcUsers.getProfile(herp.getName()).inParty()){
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
    	/*
    	 * Remove from normal chat if toggled 
    	for(Player z : event.getRecipients()){
    		if(mcUsers.getProfile(z.getName()).getPartyChatOnlyToggle() == true)
    			event.getRecipients().remove(z);
    	}
    	*/
    	}
}