package com.gmail.nossr50;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;


public class mcPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }

   
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	if(player != null){
    		PP.setRespawnATS(System.currentTimeMillis());
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
    	if(Users.getProfile(event.getPlayer()) != null){
    		Users.getProfile(event.getPlayer()).setOnline(true);
    	}
    	Users.addUser(event.getPlayer());	
    }
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Users.getProfile(event.getPlayer()).setOnline(false);
    }        
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	if(mcPermissions.getInstance().motd(player)){
    		player.sendMessage(ChatColor.BLUE +"This server is running mcMMO "+plugin.getDescription().getVersion()+" type /"+ChatColor.YELLOW+LoadProperties.mcmmo+ChatColor.BLUE+ " for help.");
    		player.sendMessage(ChatColor.GREEN+"http://mcmmo.wikia.com"+ChatColor.BLUE+" - mcMMO Wiki");
    	}
    }
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	Action action = event.getAction();
    	Block block = event.getClickedBlock();
    	//Archery Nerf
    	if(player.getItemInHand().getTypeId() == 261 && LoadProperties.archeryFireRateLimit){
    		if(System.currentTimeMillis() < PP.getArcheryShotATS() + 1000){
    			/*
    			if(m.hasArrows(player))
    				m.addArrows(player);
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
            	Repair.repairCheck(player, is, event.getClickedBlock());
            }
        	
        	if(m.abilityBlockCheck(block))
	    	{
        		if(block != null && m.isHoe(player.getItemInHand()) && block.getTypeId() != 3 && block.getTypeId() != 2 && block.getTypeId() != 60){
        			Skills.hoeReadinessCheck(player);
        		}
	    		Skills.abilityActivationCheck(player);
	    	}
        	
        	//GREEN THUMB
        	if(block != null && (block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT) && player.getItemInHand().getType() == Material.SEEDS){
        		boolean pass = false;
        		if(Herbalism.hasSeeds(player) && mcPermissions.getInstance().herbalism(player)){
        			Herbalism.removeSeeds(player);
	        		if(LoadProperties.enableCobbleToMossy && block.getType() == Material.COBBLESTONE && Math.random() * 1500 <= PP.getHerbalismInt()){
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
    		Skills.hoeReadinessCheck(player);
		    Skills.abilityActivationCheck(player);
		    
		    /*
        	 * HERBALISM MODIFIERS
        	 */
        	if(mcPermissions.getInstance().herbalism(player)){
        		Herbalism.breadCheck(player, player.getItemInHand());
        		Herbalism.stewCheck(player, player.getItemInHand());
        	}
    	}
    	/*
    	 * ITEM CHECKS
    	 */
    	if(action == Action.RIGHT_CLICK_AIR)
        	Item.itehecks(player, plugin);
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		if(m.abilityBlockCheck(event.getClickedBlock()))
    			Item.itehecks(player, plugin);
    	}
    }
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	//Check if the command is an MMO related help command
    	m.mmoHelpCheck(split, player, event);
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.mcability)){
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
    	 * FFS -> MySQL
    	 */
    	if(split[0].equalsIgnoreCase("/mmoupdate"))
    	{
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().admin(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		player.sendMessage(ChatColor.GRAY+"Starting conversion...");
    		Users.clearUsers();
    		m.convertToMySQL(plugin);
    		for(Player x : plugin.getServer().getOnlinePlayers())
    		{
    			Users.addUser(x);
    		}
    		player.sendMessage(ChatColor.GREEN+"Conversion finished!");
    	}
    	
    	/*
    	 * LEADER BOARD COMMAND
    	 */
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.mctop)){
    		event.setCancelled(true);
    		if(LoadProperties.useMySQL == false){
	    		/*
	    		 * POWER LEVEL INFO RETRIEVAL
	    		 */
	    		if(split.length == 1){
	    			int p = 1;
	    			String[] info = Leaderboard.retrieveInfo("powerlevel", p);
	    			player.sendMessage(ChatColor.YELLOW+"--mcMMO"+ChatColor.BLUE+" Power Level "+ChatColor.YELLOW+"Leaderboard--");
	    			int n = 1 * p; //Position
	    			for(String x : info){
	    				if(x != null){
	    					String digit = String.valueOf(n);
	    					if(n < 10)
	    						digit ="0"+String.valueOf(n);
		    				String[] splitx = x.split(":");
		    				//Format: 1. Playername - skill value
		    				player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]);
		    				n++;
	    				}
	    			}
	    		}
	    		if(split.length >= 2 && Leaderboard.isInt(split[1])){
	    			int p = 1;
	    			//Grab page value if specified
	    			if(split.length >= 2){
	    				if(Leaderboard.isInt(split[1])){
	    					p = Integer.valueOf(split[1]);
	    				}
	    			}
	    			int pt = p;
	    			if(p > 1){
	    				pt -= 1;
	    				pt += (pt * 10);
	    				pt = 10;
	    			}
	    			String[] info = Leaderboard.retrieveInfo("powerlevel", p);
	    			player.sendMessage("--mcMMO Power Level Leaderboard--");
	    			int n = 1 * pt; //Position
	    			for(String x : info){
	    				if(x != null){
	    					String digit = String.valueOf(n);
	    					if(n < 10)
	    						digit ="0"+String.valueOf(n);
		    				String[] splitx = x.split(":");
		    				//Format: 1. Playername - skill value
		    				player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]);
		    				n++;
	    				}
	    			}
	    		}
	    		/*
	    		 * SKILL SPECIFIED INFO RETRIEVAL
	    		 */
	    		if(split.length >= 2 && Skills.isSkill(split[1])){
	    			int p = 1;
	    			//Grab page value if specified
	    			if(split.length >= 3){
	    				if(Leaderboard.isInt(split[2])){
	    					p = Integer.valueOf(split[2]);
	    				}
	    			}
	    			int pt = p;
	    			if(p > 1){
	    				pt -= 1;
	    				pt += (pt * 10);
	    				pt = 10;
	    			}
	    			String firstLetter = split[1].substring(0,1);  // Get first letter
	    	        String remainder   = split[1].substring(1);    // Get remainder of word.
	    	        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
	    	        
	    			String[] info = Leaderboard.retrieveInfo(split[1].toLowerCase(), p);
	    			player.sendMessage(ChatColor.YELLOW+"--mcMMO "+ChatColor.BLUE+capitalized+ChatColor.YELLOW+" Leaderboard--");
	    			int n = 1 * pt; //Position
	    			for(String x : info){
	    				if(x != null){
	    					String digit = String.valueOf(n);
	    					if(n < 10)
	    						digit ="0"+String.valueOf(n);
		    				String[] splitx = x.split(":");
		    				//Format: 1. Playername - skill value
		    				player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]);
		    				n++;
	    				}
	    			}
	    		}
    		} else
    		/*
    		* MYSQL LEADERBOARDS
    		*/
    		{
    			String powerlevel = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics";
    			if(split.length >= 2 && Skills.isSkill(split[1]))
    			{
    				/*
    				 * Create a nice consistent capitalized leaderboard name
    				 */
    				String lowercase = split[1].toLowerCase(); //For the query
    				String firstLetter = split[1].substring(0,1); //Get first letter
	    	        String remainder   = split[1].substring(1); //Get remainder of word.
	    	        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
	    	        
	    	        player.sendMessage(ChatColor.YELLOW+"--mcMMO "+ChatColor.BLUE+capitalized+ChatColor.YELLOW+" Leaderboard--");
	    	        if(split.length >= 3 && m.isInt(split[2]))
	    	        {
	    	        	int n = 1; //For the page number
	    	        	int n2 = Integer.valueOf(split[2]);
	    	        	if(n2 > 1)
	    	        	{
	    	        		//Figure out the 'page' here
	    	        		n = 10;
	    	        		n = n * (n2-1);
	    	        	}
	    	        	//If a page number is specified
	    	        	HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+lowercase+", user_id FROM "
    	    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+lowercase+" > 0 ORDER BY `"+LoadProperties.MySQLtablePrefix+"skills`.`"+lowercase+"` DESC ");
	    	        	
	    	        	for(int i=n;i<=n+10;i++)
    	    			{
	    	        		if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
		    					break;
	    	        		HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
    	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));
    	    			}
    	        		return;
	    	        }
	    	        //If no page number is specified
	    	        HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+lowercase+", user_id FROM "
	    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+lowercase+" > 0 ORDER BY `"+LoadProperties.MySQLtablePrefix+"skills`.`"+lowercase+"` DESC ");
	    	        for(int i=1;i<=10;i++) //i<=userslist.size()
	    			{
	    	        	if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
	    					break;
	    				HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));
	    			}
	    	        return;
    			}
    			if(split.length >= 1)
    			{
	    			player.sendMessage(ChatColor.YELLOW+"--mcMMO "+ChatColor.BLUE+"Power Level"+ChatColor.YELLOW+" Leaderboard--");
	    			if(split.length >= 2 && m.isInt(split[1]))
	    	        {
	    	        	int n = 1; //For the page number
	    	        	int n2 = Integer.valueOf(split[1]);
	    	        	if(n2 > 1)
	    	        	{
	    	        		//Figure out the 'page' here
	    	        		n = 10;
	    	        		n = n * (n2-1);
	    	        	}
	    	        	//If a page number is specified
	    	        	HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+powerlevel+", user_id FROM "
		    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+powerlevel+" > 0 ORDER BY taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics DESC ");
	    	        	for(int i=n;i<=n+10;i++)
    	    			{
	    	        		if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
		    					break;
	    	        		HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
    	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));
    	    			}
    	        		return;
	    	        }
	    			HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics, user_id FROM "
	    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+powerlevel+" > 0 ORDER BY taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics DESC ");
	    			for(int i=1;i<=10;i++)
	    			{
	    				if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null)
	    					break;
	    				HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0));
	    				//System.out.println(username.get(1).get(0));
	    				//System.out.println("Mining : " + userslist.get(i).get(0) + ", User id : " + userslist.get(i).get(1));
	    			}
    			}
    		}
    	}
    	
		if(split[0].equalsIgnoreCase("/"+LoadProperties.mcrefresh)){
			event.setCancelled(true);
    		if(!mcPermissions.getInstance().mcrefresh(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(split.length >= 2 && isPlayer(split[1])){
    			player.sendMessage("You have refreshed "+split[1]+"'s cooldowns!");
    			player = getPlayer(split[1]);
    		}
			/*
			 * PREP MODES
			 */
    		PP = Users.getProfile(player);
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
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.mcitem)){
    		
    	}
    	/*
    	 * GODMODE COMMAND
    	 */
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.mcgod)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mcgod(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(Config.getInstance().isGodModeToggled(playerName)){
    			player.sendMessage(ChatColor.YELLOW+"MMO Godmode Disabled");
    			Config.getInstance().toggleGodMode(playerName);
    		} else {
    			player.sendMessage(ChatColor.YELLOW+"MMO Godmode Enabled");
    			Config.getInstance().toggleGodMode(playerName);
    		}
    	}
    	if(mcPermissions.getInstance().mySpawn(player) && split[0].equalsIgnoreCase("/"+LoadProperties.clearmyspawn)){
    		event.setCancelled(true);
    		double x = plugin.getServer().getWorlds().get(0).getSpawnLocation().getX();
    		double y = plugin.getServer().getWorlds().get(0).getSpawnLocation().getY();
    		double z = plugin.getServer().getWorlds().get(0).getSpawnLocation().getZ();
    		String worldname = plugin.getServer().getWorlds().get(0).getName();
    		PP.setMySpawn(x, y, z, worldname);
    		player.sendMessage(ChatColor.DARK_AQUA+"Myspawn is now cleared.");
    	}
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.mmoedit)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mmoedit(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.mmoedit+" playername skillname newvalue");
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				Users.getProfile(getPlayer(split[1])).modifyskill(newvalue, split[2]);
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3){
    			if(m.isInt(split[2]) && Skills.isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				PP.modifyskill(newvalue, split[1]);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified.");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.mmoedit+" playername skillname newvalue");
    		}
    	}
    	/*
    	 * ADD EXPERIENCE COMMAND
    	 */
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.addxp)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mmoedit(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp");
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				Users.getProfile(getPlayer(split[1])).addXpToSkill(newvalue, split[2], getPlayer(split[1]));
    				getPlayer(split[1]).sendMessage(ChatColor.GREEN+"Experience granted!");
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified.");
    			}
    		}
    		else if(split.length == 3 && m.isInt(split[2]) && Skills.isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				PP.addXpToSkill(newvalue, split[1], player);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified.");
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp");
    		}
    	}
    	
    	if(PP.inParty() && split[0].equalsIgnoreCase("/"+LoadProperties.ptp)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().partyTeleport(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.ptp+" <playername>");
    			return;
    		}
    		if(!isPlayer(split[1])){
    			player.sendMessage("That is not a valid player");
    		}
    		if(isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			PlayerProfile PPt = Users.getProfile(target);
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
    	if((player.isOp() || mcPermissions.getInstance().whois(player)) && split[0].equalsIgnoreCase("/"+LoadProperties.whois)){
    		event.setCancelled(true);
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED + "Proper usage is /"+LoadProperties.whois+" <playername>");
    			return;
    		}
    		//if split[1] is a player
    		if(isPlayer(split[1])){
    		Player target = getPlayer(split[1]);
    		PlayerProfile PPt = Users.getProfile(target);
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
    		player.sendMessage(ChatColor.GREEN+"MMO Stats for "+ChatColor.YELLOW+target.getName());
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
    		player.sendMessage(ChatColor.DARK_RED+"POWER LEVEL: "+ChatColor.GREEN+(m.getPowerLevel(target)));
    		player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~");
    		player.sendMessage("X: "+x);
    		player.sendMessage("Y: "+y);
    		player.sendMessage("Z: "+z);
    		}
    	}
    	/*
    	 * STATS COMMAND
    	 */
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.stats)){
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.GREEN + "Your MMO Stats");
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
    		player.sendMessage(ChatColor.DARK_RED+"POWER LEVEL: "+ChatColor.GREEN+(m.getPowerLevel(player)));
    	}
    	//Invite Command
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+LoadProperties.invite)){
    		event.setCancelled(true);
    		if(!PP.inParty()){
    			player.sendMessage(ChatColor.RED+"You are not in a party.");
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.invite+" <playername>");
    			return;
    		}
    		if(PP.inParty() && split.length >= 2 && isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			PlayerProfile PPt = Users.getProfile(target);
    			PPt.modifyInvite(PP.getParty());
    			player.sendMessage(ChatColor.GREEN+"Invite sent successfully");
    			target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+PPt.getInvite()+" from "+player.getName());
    			target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+"/"+LoadProperties.accept+ChatColor.YELLOW+" to accept the invite");
    		}
    	}
    	//Accept invite
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+LoadProperties.accept)){
    		event.setCancelled(true);
    		if(PP.hasPartyInvite()){
    			if(PP.inParty()){
    				Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			}
    			PP.acceptInvite();
    			Party.getInstance().informPartyMembers(player, getPlayersOnline());
    			player.sendMessage(ChatColor.GREEN+"Invite accepted. You have joined party ("+PP.getParty()+")");
    		} else {
    			player.sendMessage(ChatColor.RED+"You have no invites at this time");
    		}
    	}
    	//Party command
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.party)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(split.length == 1 && !PP.inParty()){
    			player.sendMessage("Proper usage is "+"/"+LoadProperties.party+" <name> or 'q' to quit");
    			return;
    		}
    		if(split.length == 1 && PP.inParty()){
            	String tempList = "";
            	int x = 0;
                for(Player p : plugin.getServer().getOnlinePlayers())
                {
                	if(PP.getParty().equals(Users.getProfile(p).getParty())){
	                	if(p != null && x+1 >= Party.getInstance().partyCount(player, getPlayersOnline())){
	                		tempList+= p.getName();
	                		x++;
	                	}
	                	if(p != null && x < Party.getInstance().partyCount(player, getPlayersOnline())){
	                		tempList+= p.getName() +", ";
	                		x++;
	                	}
                	}
                }
                player.sendMessage(ChatColor.GREEN+"You are in party \""+PP.getParty()+"\"");
                player.sendMessage(ChatColor.GREEN + "Party Members ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")");
    		}
    		if(split.length > 1 && split[1].equals("q") && PP.inParty()){
    			Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			PP.removeParty();
    			player.sendMessage(ChatColor.RED + "You have left that party");
    			return;
    		}
    		if(split.length >= 2){
	    		if(PP.inParty())
	    			Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
		    	PP.setParty(split[1]);
		    	player.sendMessage("Joined Party: " + split[1]);
		    	Party.getInstance().informPartyMembers(player, getPlayersOnline());
	    		}
    	}
    	if(split[0].equalsIgnoreCase("/p")){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		if(Config.getInstance().isAdminToggled(player.getName()))
    		Config.getInstance().toggleAdminChat(playerName);
    		Config.getInstance().togglePartyChat(playerName);
    		if(Config.getInstance().isPartyToggled(playerName)){
    			player.sendMessage(ChatColor.GREEN + "Party Chat Toggled On");
    		} else {
    			player.sendMessage(ChatColor.GREEN + "Party Chat Toggled " + ChatColor.RED + "Off");
    		}
    	}
    	if(split[0].equalsIgnoreCase("/a") && (player.isOp() || mcPermissions.getInstance().adminChat(player))){
    		if(!mcPermissions.getInstance().adminChat(player) && !player.isOp()){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
    			return;
    		}
    		event.setCancelled(true);
    		if(Config.getInstance().isPartyToggled(player.getName()))
    			Config.getInstance().togglePartyChat(playerName);
    		Config.getInstance().toggleAdminChat(playerName);
    		if(Config.getInstance().isAdminToggled(playerName)){
    			player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.GREEN + "On");
    		} else {
    			player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.RED + "Off");
    		}
    	}
    	/*
    	 * MYSPAWN
    	 */
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.myspawn)){
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mySpawn(player)){
    			player.sendMessage(ChatColor.YELLOW+"[MMO]"+ChatColor.DARK_RED +" Insufficient mcPermissions.");
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
	    		//player.sendMessage("MMO DEBUG CODE 1");
	    		if(PP.getMySpawnWorld(plugin) != null && !PP.getMySpawnWorld(plugin).equals("")){
	    			mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
	    			//player.sendMessage("MMO DEBUG CODE 2");
	    			} else {
	    				//player.sendMessage("MMO DEBUG CODE 5");
	    				mySpawn.setWorld(plugin.getServer().getWorlds().get(0));
	    		}
	    		player.teleportTo(mySpawn); //It's done twice because teleporting from one world to another is weird
	    		player.teleportTo(mySpawn);
    		} else {
    			player.sendMessage(ChatColor.RED+"Configure your myspawn first with a bed.");
    		}
    	}
    }
 
    
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";
    	if(Config.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		log.log(Level.INFO, "[P]("+PP.getParty()+")"+"<"+player.getName()+"> "+event.getMessage());
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(Users.getProfile(herp).inParty()){
    			if(Party.getInstance().inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if((player.isOp() || mcPermissions.getInstance().adminChat(player)) && Config.getInstance().isAdminToggled(player.getName())){
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
    		if(Users.getProfile(z.getName()).getPartyChatOnlyToggle() == true)
    			event.getRecipients().remove(z);
    	}
    	*/
    	}
}