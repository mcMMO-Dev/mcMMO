package com.gmail.nossr50;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.*;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;
public class m {
	public static final Logger log = Logger.getLogger("Minecraft"); //$NON-NLS-1$
	/*
	 * I'm storing my misc functions/methods in here in an unorganized manner. Spheal with it.
	 */
	
	//The lazy way to default to 0
	public static int getInt(String string)
	{
		if(isInt(string))
		{
			return Integer.valueOf(string);
		}
		else
		{
			return 0;
		}
	}
	public static Double getDouble(String string)
	{
		if(isDouble(string))
		{
			return Double.valueOf(string);
		}
		else
		{
			return (double) 0;
		}
	}
	public static boolean isDouble(String string)
	{
		try {
		    Double x = Double.valueOf(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
	public static boolean shouldBeWatched(Block block){
		int id = block.getTypeId();
		if(id == 49 || id == 81 || id == 83 || id == 86 || id == 91 || id == 1 || id == 17 || id == 42 || id == 87 || id == 89 || id == 2 || id == 3 || id == 12 || id == 13 || id == 21 || id == 15 || id == 14 || id == 56 || id == 38 || id == 37 || id == 39 || id == 40 || id == 24){
			return true;
		} else {
			return false;
		}
	}
	public static int getPowerLevel(Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		int x = 0;
		if(mcPermissions.getInstance().mining(player))
			x+=PP.getSkill("mining");
		if(mcPermissions.getInstance().woodcutting(player))
			x+=PP.getSkill("woodcutting");
		if(mcPermissions.getInstance().unarmed(player))
			x+=PP.getSkill("unarmed");
		if(mcPermissions.getInstance().herbalism(player))
			x+=PP.getSkill("herbalism");
		if(mcPermissions.getInstance().excavation(player))
			x+=PP.getSkill("excavation");
		if(mcPermissions.getInstance().archery(player))
			x+=PP.getSkill("archery");
		if(mcPermissions.getInstance().swords(player))
			x+=PP.getSkill("swords");
		if(mcPermissions.getInstance().axes(player))
			x+=PP.getSkill("axes");
		if(mcPermissions.getInstance().acrobatics(player))
			x+=PP.getSkill("acrobatics");
		if(mcPermissions.getInstance().repair(player))
			x+=PP.getSkill("repair");
		return x;
	}
	public static boolean blockBreakSimulate(Block block, Player player, Plugin plugin){

    	FakeBlockBreakEvent event = new FakeBlockBreakEvent(block, player);
    	if(block != null && plugin != null && player != null){
    		plugin.getServer().getPluginManager().callEvent(event);
	    	if(!event.isCancelled())
	    	{
	    		return true; //Return true if not cancelled
	    	} else {
	    		return false; //Return false if cancelled
	    	}
    	} else {
    		return false; //Return false if something went wrong
    	}
    }
	
	public static void damageTool(Player player, short damage){
		if(player.getItemInHand().getTypeId() == 0)
			return;
		player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + damage));
		if(player.getItemInHand().getDurability() >= getMaxDurability(getTier(player), player.getItemInHand())){
			ItemStack[] inventory = player.getInventory().getContents();
	    	for(ItemStack x : inventory){
	    		if(x != null && x.getTypeId() == player.getItemInHand().getTypeId() && x.getDurability() == player.getItemInHand().getDurability()){
	    			x.setTypeId(0);
	    			x.setAmount(0);
	    			player.getInventory().setContents(inventory);
	    			return;
	    		}
	    	}
		}
	}
	public boolean hasArrows(Player player){
		for(ItemStack x : player.getInventory().getContents()){
			if(x.getTypeId() == 262)
				return true;
		}
		return false;
	}
	public void addArrows(Player player){
		ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 262){
    			if(x.getAmount() >= 1 && x.getAmount() < 64){
    				x.setAmount(x.getAmount() + 1);
    				player.getInventory().setContents(inventory);
    			}
    			return;
    		}
    	}
	}
	public static Integer getTier(Player player){
		int i = player.getItemInHand().getTypeId();
		if(i == 268 || i == 269 || i == 270 || i == 271 || i == 290){
			return 1; //WOOD
		} else if (i == 272 || i == 273 || i == 274 || i == 275 || i == 291){
			return 2; //STONE
		} else if (i == 256 || i == 257 || i == 258 || i == 267 || i == 292){
			return 3; //IRON
		} else if (i == 283 || i == 284 || i == 285 || i == 286 || i == 294){
			return 1; //GOLD
		} else if (i == 276 || i == 277 || i == 278 || i == 279 || i == 293){
			return 4; //DIAMOND
		} else {
			return 1; //UNRECOGNIZED
		}
	}
	public static Integer getMaxDurability(Integer tier, ItemStack item){
		int id = item.getTypeId();
		if(tier == 1){
			if((id == 276 || id == 277 || id == 278 || id == 279 || id == 293)){
				return 33;
			} else {
				return 60;
			}
		} else if (tier == 2){
			return 132;
		} else if (tier == 3){
			return 251;
		} else if (tier == 4){
			return 1562;
		} else {
			return 0;
		}
	}
	
	public static double getDistance(Location loca, Location locb)
    {
		return Math.sqrt(Math.pow(loca.getX() - locb.getX(), 2) + Math.pow(loca.getY() - locb.getY(), 2)
	    + Math.pow(loca.getZ() - locb.getZ(), 2));
    }
	
	public static boolean abilityBlockCheck(Block block)
	{
		int i = block.getTypeId();
		if(i == 96 || i == 68 || i == 355 || i == 26 || i == 323 || i == 25 || i == 54 || i == 69 || i == 92 || i == 77 || i == 58 || i == 61 || i == 62 || i == 42 || i == 71 || i == 64 || i == 84 || i == 324 || i == 330){
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean isBlockAround(Location loc, Integer radius, Integer typeid)
	{
		Block blockx = loc.getBlock();
    	int ox = blockx.getX();
        int oy = blockx.getY();
        int oz = blockx.getZ();
    	for (int cx = -radius; cx <= radius; cx++) {
            for (int cy = -radius; cy <= radius; cy++) {
                for (int cz = -radius; cz <= radius; cz++) {
                    Block block = loc.getWorld().getBlockAt(ox + cx, oy + cy, oz + cz);
                    if (block.getTypeId() == typeid) {
                        return true;
                    }
                }
            }
        }
    	return false;
	}
	
	public static Integer calculateHealth(Integer health, Integer newvalue){
    	if((health + newvalue) > 20){
    		return 20;
    	} else {
    		return health+newvalue;
    	}
    }
    public Integer calculateMinusHealth(Integer health, Integer newvalue){
    	if((health - newvalue) < 1){
    		return 0;
    	} else {
    		return health-newvalue;
    	}
    }
    public static boolean isInt(String string){
		try {
		    int x = Integer.parseInt(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
    public static void mcDropItem(Location loc, int id){
    	if(loc != null){
    	Material mat = Material.getMaterial(id);
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		loc.getWorld().dropItemNaturally(loc, item);
    	}
    }
	
    public static boolean isSwords(ItemStack is){
    	if(is.getTypeId() == 268 || is.getTypeId() == 267 || is.getTypeId() == 272 || is.getTypeId() == 283 || is.getTypeId() == 276){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isHoe(ItemStack is){
    	int id = is.getTypeId();
    	if(id == 290 || id == 291 || id == 292 || id == 293 || id == 294){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isShovel(ItemStack is){
    	if(is.getTypeId() == 269 || is.getTypeId() == 273 || is.getTypeId() == 277 || is.getTypeId() == 284 || is.getTypeId() == 256){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isAxes(ItemStack is){
    	if(is.getTypeId() == 271 || is.getTypeId() == 258 || is.getTypeId() == 286 || is.getTypeId() == 279 || is.getTypeId() == 275){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isMiningPick(ItemStack is){
    	if(is.getTypeId() == 270 || is.getTypeId() == 274 || is.getTypeId() == 285 || is.getTypeId() == 257 || is.getTypeId() == 278){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isGold(ItemStack is){
    	int i = is.getTypeId();
    	if(i == 283 || i == 284 || i == 285 || i == 286 || i == 294 || i == 314 || i == 315 || i == 316 || i == 317){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static void convertToMySQL(Plugin pluginx)
    {
    	if(!LoadProperties.useMySQL)
    		return;
    	String location = "plugins/mcMMO/mcmmo.users"; //$NON-NLS-1$
    	try {
        	//Open the user file
        	FileReader file = new FileReader(location);
        	BufferedReader in = new BufferedReader(file);
        	String line = ""; //$NON-NLS-1$
        	String playerName = null, mining = null, party = null, miningXP = null, woodcutting = null, woodCuttingXP = null, repair = null, unarmed = null, herbalism = null,
        	excavation = null, archery = null, swords = null, axes = null, acrobatics = null, repairXP = null, unarmedXP = null, herbalismXP = null, excavationXP = null, archeryXP = null, swordsXP = null, axesXP = null,
        	acrobaticsXP = null, taming = null, tamingXP = null;
        	int id = 0, theCount = 0;
        	while((line = in.readLine()) != null)
        	{
        		//Find if the line contains the player we want.
        		String[] character = line.split(":"); //$NON-NLS-1$
        		playerName = character[0];
        		//Check for things we don't want put in the DB
        		if(playerName == null || playerName.equals("null") || playerName.equals("#Storage place for user information")) //$NON-NLS-1$ //$NON-NLS-2$
        			continue;
        		
    			//Get Mining
    			if(character.length > 1)
    				mining = character[1];
    			//Party
    			if(character.length > 3)
    				party = character[3];
    			//Mining XP
    			if(character.length > 4)
    				miningXP = character[4];
    			if(character.length > 5)
    				woodcutting = character[5];
    			if(character.length > 6)
    				woodCuttingXP = character[6];
    			if(character.length > 7)
    				repair = character[7];
    			if(character.length > 8)
    				unarmed = character[8];
    			if(character.length > 9)
    				herbalism = character[9];
    			if(character.length > 10)
    				excavation = character[10];
    			if(character.length > 11)
    				archery = character[11];
    			if(character.length > 12)
    				swords = character[12];
    			if(character.length > 13)
    				axes = character[13];
    			if(character.length > 14)
    				acrobatics = character[14];
    			if(character.length > 15)
    				repairXP = character[15];
    			if(character.length > 16)
    				unarmedXP = character[16];
    			if(character.length > 17)
    				herbalismXP = character[17];
    			if(character.length > 18)
    				excavationXP = character[18];
    			if(character.length > 19)
    				archeryXP = character[19];
    			if(character.length > 20)
    				swordsXP = character[20];
    			if(character.length > 21)
    				axesXP = character[21];
    			if(character.length > 22)
    				acrobaticsXP = character[22];
    			if(character.length > 24)
    				taming = character[24];
    			if(character.length > 25)
    				tamingXP = character[25];
            	//Check to see if the user is in the DB
    			id = mcMMO.database.GetInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    			//Prepare some variables
    			/*
    			if(myspawn != null && myspawn.length() > 0)
    			{
    				String[] split = myspawn.split(",");
    				x = split[0];
    				y = split[1];
    				z = split[2];
    			}
    			*/
    			/*
    		    if(myspawnworld.equals("") || myspawnworld == null)
    		    	myspawnworld = pluginx.getServer().getWorlds().get(0).toString();
    		    */
    			if(id > 0)
    			{
    				theCount++;
    				//Update the skill values
    				mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + 0 + " WHERE id = " + id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				//if(getDouble(x) > 0 && getDouble(y) > 0 && getDouble(z) > 0)
    					//mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"spawn SET world = '" + myspawnworld + "', x = " +getDouble(x)+", y = "+getDouble(y)+", z = "+getDouble(z)+" WHERE user_id = "+id);
    	    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET " //$NON-NLS-1$ //$NON-NLS-2$
    	    				+"  taming = taming+"+getInt(taming) //$NON-NLS-1$
    	    				+", mining = mining+"+getInt(mining) //$NON-NLS-1$
    	    				+", repair = repair+"+getInt(repair) //$NON-NLS-1$
    	    				+", woodcutting = woodcutting+"+getInt(woodcutting) //$NON-NLS-1$
    	    				+", unarmed = unarmed+"+getInt(unarmed) //$NON-NLS-1$
    	    				+", herbalism = herbalism+"+getInt(herbalism) //$NON-NLS-1$
    	    				+", excavation = excavation+"+getInt(excavation) //$NON-NLS-1$
    	    				+", archery = archery+" +getInt(archery) //$NON-NLS-1$
    	    				+", swords = swords+" +getInt(swords) //$NON-NLS-1$
    	    				+", axes = axes+"+getInt(axes) //$NON-NLS-1$
    	    				+", acrobatics = acrobatics+"+getInt(acrobatics) //$NON-NLS-1$
    	    				+" WHERE user_id = "+id); //$NON-NLS-1$
    	    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET " //$NON-NLS-1$ //$NON-NLS-2$
    	    				+"  taming = "+getInt(tamingXP) //$NON-NLS-1$
    	    				+", mining = "+getInt(miningXP) //$NON-NLS-1$
    	    				+", repair = "+getInt(repairXP) //$NON-NLS-1$
    	    				+", woodcutting = "+getInt(woodCuttingXP) //$NON-NLS-1$
    	    				+", unarmed = "+getInt(unarmedXP) //$NON-NLS-1$
    	    				+", herbalism = "+getInt(herbalismXP) //$NON-NLS-1$
    	    				+", excavation = "+getInt(excavationXP) //$NON-NLS-1$
    	    				+", archery = " +getInt(archeryXP) //$NON-NLS-1$
    	    				+", swords = " +getInt(swordsXP) //$NON-NLS-1$
    	    				+", axes = "+getInt(axesXP) //$NON-NLS-1$
    	    				+", acrobatics = "+getInt(acrobaticsXP) //$NON-NLS-1$
    	    				+" WHERE user_id = "+id); //$NON-NLS-1$
    			}
    			else
    			{
    				theCount++;
    				//Create the user in the DB
    				mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"users (user, lastlogin) VALUES ('" + playerName + "'," + System.currentTimeMillis() / 1000 +")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    				id = mcMMO.database.GetInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"spawn (user_id) VALUES ("+id+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"skills (user_id) VALUES ("+id+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"experience (user_id) VALUES ("+id+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				//Update the skill values
    				mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + 0 + " WHERE id = " + id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET party = '"+party+"' WHERE id = " +id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    				/*
    				if(getDouble(x) > 0 && getDouble(y) > 0 && getDouble(z) > 0)
    					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"spawn SET world = '" + myspawnworld + "', x = " +getDouble(x)+", y = "+getDouble(y)+", z = "+getDouble(z)+" WHERE user_id = "+id);
    	    		*/
    	    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET " //$NON-NLS-1$ //$NON-NLS-2$
    	    				+"  taming = "+getInt(taming) //$NON-NLS-1$
    	    				+", mining = "+getInt(mining) //$NON-NLS-1$
    	    				+", repair = "+getInt(repair) //$NON-NLS-1$
    	    				+", woodcutting = "+getInt(woodcutting) //$NON-NLS-1$
    	    				+", unarmed = "+getInt(unarmed) //$NON-NLS-1$
    	    				+", herbalism = "+getInt(herbalism) //$NON-NLS-1$
    	    				+", excavation = "+getInt(excavation) //$NON-NLS-1$
    	    				+", archery = " +getInt(archery) //$NON-NLS-1$
    	    				+", swords = " +getInt(swords) //$NON-NLS-1$
    	    				+", axes = "+getInt(axes) //$NON-NLS-1$
    	    				+", acrobatics = "+getInt(acrobatics) //$NON-NLS-1$
    	    				+" WHERE user_id = "+id); //$NON-NLS-1$
    	    		mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET " //$NON-NLS-1$ //$NON-NLS-2$
    	    				+"  taming = "+getInt(tamingXP) //$NON-NLS-1$
    	    				+", mining = "+getInt(miningXP) //$NON-NLS-1$
    	    				+", repair = "+getInt(repairXP) //$NON-NLS-1$
    	    				+", woodcutting = "+getInt(woodCuttingXP) //$NON-NLS-1$
    	    				+", unarmed = "+getInt(unarmedXP) //$NON-NLS-1$
    	    				+", herbalism = "+getInt(herbalismXP) //$NON-NLS-1$
    	    				+", excavation = "+getInt(excavationXP) //$NON-NLS-1$
    	    				+", archery = " +getInt(archeryXP) //$NON-NLS-1$
    	    				+", swords = " +getInt(swordsXP) //$NON-NLS-1$
    	    				+", axes = "+getInt(axesXP) //$NON-NLS-1$
    	    				+", acrobatics = "+getInt(acrobaticsXP) //$NON-NLS-1$
    	    				+" WHERE user_id = "+id); //$NON-NLS-1$
    			}
        	}
        	System.out.println("[mcMMO] MySQL Updated from users file, "+theCount+" items added/updated to MySQL DB"); //$NON-NLS-1$ //$NON-NLS-2$
        	in.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading " //$NON-NLS-1$
            		+ location + " (Are you sure you formatted it correctly?)", e); //$NON-NLS-1$
        }
    }
    public static void mmoHelpCheck(String[] split, Player player, PlayerChatEvent event){
    	PlayerProfile PP = Users.getProfile(player);
    	if(split[0].equalsIgnoreCase("/taming") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillTaming").toLowerCase())){ //$NON-NLS-1$
			event.setCancelled(true);
			float skillvalue = (float)PP.getSkill("taming");
			
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillTaming")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainTaming")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().taming(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("taming"), PP.getSkillToString("tamingXP"), PP.getXpToLevel("taming")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsTaming1_0"), Messages.getString("m.EffectsTaming1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsTaming2_0"), Messages.getString("m.EffectsTaming2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsTaming3_0"), Messages.getString("m.EffectsTaming3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsTaming4_0"), Messages.getString("m.EffectsTaming4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsTaming5_0"), Messages.getString("m.EffectsTaming5_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsTaming6_0"), Messages.getString("m.EffectsTaming6_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			if(PP.getSkill("taming") < 100)
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockTaming1")})); //$NON-NLS-1$ 
			else
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusTaming1_0"), Messages.getString("m.AbilBonusTaming1_1")})); //$NON-NLS-1$  
			if(PP.getSkill("taming") < 250)
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockTaming2")})); //$NON-NLS-1$ 
			else
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusTaming2_0"), Messages.getString("m.AbilBonusTaming2_1")})); //$NON-NLS-1$  
			if(PP.getSkill("taming") < 500)
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockTaming3")})); //$NON-NLS-1$ 
			else
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusTaming3_0"), Messages.getString("m.AbilBonusTaming3_1")})); //$NON-NLS-1$  
			if(PP.getSkill("taming") < 750)
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockTaming4")})); //$NON-NLS-1$ 
			else
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusTaming4_0"), Messages.getString("m.AbilBonusTaming4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.TamingGoreChance", new Object[] {percentage})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/woodcutting") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillWoodCutting").toLowerCase())){ //$NON-NLS-1$
			event.setCancelled(true);
			float skillvalue = (float)PP.getSkill("woodcutting");
			int ticks = 2;
			int x = PP.getSkill("woodcutting");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillWoodCutting")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainWoodCutting")})); //$NON-NLS-1$
			if(mcPermissions.getInstance().woodcutting(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("woodcutting"), PP.getSkillToString("woodcuttingXP"), PP.getXpToLevel("woodcutting")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsWoodCutting1_0"), Messages.getString("m.EffectsWoodCutting1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsWoodCutting2_0"), Messages.getString("m.EffectsWoodCutting2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsWoodCutting3_0"), Messages.getString("m.EffectsWoodCutting3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			if(PP.getSkill("woodcutting") < 100)
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockWoodCutting1")})); //$NON-NLS-1$ 
			else
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusWoodCutting1_0"), Messages.getString("m.AbilBonusWoodCutting1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.WoodCuttingDoubleDropChance", new Object[] {percentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.WoodCuttingTreeFellerLength", new Object[] {ticks})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/archery") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillArchery").toLowerCase())){ //$NON-NLS-1$
			event.setCancelled(true);
			Integer rank = 0;
			if(PP.getSkill("archery") >= 50)
    			rank++;
    		if(PP.getSkill("archery") >= 250)
    			rank++;
    		if(PP.getSkill("archery") >= 575)
    			rank++;
    		if(PP.getSkill("archery") >= 725)
    			rank++;
    		if(PP.getSkill("archery") >= 1000)
    			rank++;
			float skillvalue = (float)PP.getSkill("archery");
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		
    		int ignition = 20;
			if(PP.getSkill("archery") >= 200)
				ignition+=20;
			if(PP.getSkill("archery") >= 400)
				ignition+=20;
			if(PP.getSkill("archery") >= 600)
				ignition+=20;
			if(PP.getSkill("archery") >= 800)
				ignition+=20;
			if(PP.getSkill("archery") >= 1000)
				ignition+=20;
			
    		String percentagedaze;
			if(PP.getSkill("archery") < 1000){
				percentagedaze = String.valueOf((skillvalue / 2000) * 100);
			} else {
				percentagedaze = "50"; //$NON-NLS-1$
			}
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillArchery")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainArchery")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().archery(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("archery"), PP.getSkillToString("archeryXP"), PP.getXpToLevel("archery")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsArchery1_0"), Messages.getString("m.EffectsArchery1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsArchery2_0"), Messages.getString("m.EffectsArchery2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsArchery3_0"), Messages.getString("m.EffectsArchery3_1")})); //$NON-NLS-1$  
            player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsArchery4_0"), Messages.getString("m.EffectsArchery4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.ArcheryDazeChance", new Object[] {percentagedaze})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.ArcheryRetrieveChance", new Object[] {percentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.ArcheryIgnitionLength", new Object[] {(ignition / 20)})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.ArcheryDamagePlus", new Object[] {rank})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/axes") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillAxes"))){ //$NON-NLS-1$
			event.setCancelled(true);
			String percentage;
			float skillvalue = (float)PP.getSkill("axes");
			if(PP.getSkill("axes") < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75"; //$NON-NLS-1$
			}
			int ticks = 2;
			int x = PP.getSkill("axes");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillAxes")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainAxes")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().axes(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("axes"), PP.getSkillToString("axesXP"), PP.getXpToLevel("axes")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsAxes1_0"), Messages.getString("m.EffectsAxes1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsAxes2_0"), Messages.getString("m.EffectsAxes2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsAxes3_0"), Messages.getString("m.EffectsAxes3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.AxesCritChance", new Object[] {percentage})); //$NON-NLS-1$
			if(PP.getSkill("axes") < 500){
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockAxes1")})); //$NON-NLS-1$ 
			} else {
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusAxes1_0"), Messages.getString("m.AbilBonusAxes1_1")})); //$NON-NLS-1$  
			}
			player.sendMessage(Messages.getString("m.AxesSkullLength", new Object[] {ticks})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/swords") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillSwords").toLowerCase())){ //$NON-NLS-1$
			event.setCancelled(true);
			int bleedrank = 2;
			String percentage, parrypercentage = null, counterattackpercentage;
			float skillvalue = (float)PP.getSkill("swords");
			if(PP.getSkill("swords") < 750){
				percentage = String.valueOf((skillvalue / 1000) * 100);
			} else {
				percentage = "75"; //$NON-NLS-1$
			}
			if(skillvalue >= 750)
				bleedrank+=1;
			
			if(PP.getSkill("swords") <= 900){
				parrypercentage = String.valueOf((skillvalue / 3000) * 100);
			} else {
				parrypercentage = "30"; //$NON-NLS-1$
			}
			
			if(PP.getSkill("swords") <= 600){
				counterattackpercentage = String.valueOf((skillvalue / 2000) * 100);
			} else {
				counterattackpercentage = "30"; //$NON-NLS-1$
			}
			
			int ticks = 2;
			int x = PP.getSkill("swords");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
            player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillSwords")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainSwords")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().swords(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("swords"), PP.getSkillToString("swordsXP"), PP.getXpToLevel("swords")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsSwords1_0"), Messages.getString("m.EffectsSwords1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsSwords2_0"), Messages.getString("m.EffectsSwords2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsSwords3_0"), Messages.getString("m.EffectsSwords3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsSwords4_0"), Messages.getString("m.EffectsSwords4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsSwords5_0"), Messages.getString("m.EffectsSwords5_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.SwordsCounterAttChance", new Object[] {counterattackpercentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.SwordsBleedLength", new Object[] {bleedrank})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.SwordsTickNote")); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.SwordsBleedLength", new Object[] {percentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.SwordsParryChance", new Object[] {parrypercentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.SwordsSSLength", new Object[] {ticks})); //$NON-NLS-1$
			
    	}
    	if(split[0].equalsIgnoreCase("/acrobatics") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillAcrobatics").toLowerCase())){ //$NON-NLS-1$
			event.setCancelled(true);
			String dodgepercentage;
			float skillvalue = (float)PP.getSkill("acrobatics");
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		String gracepercentage = String.valueOf(((skillvalue / 1000) * 100) * 2);
    		if(PP.getSkill("acrobatics") <= 800){
    			dodgepercentage = String.valueOf((skillvalue / 4000 * 100));
    		} else {
    			dodgepercentage = "20"; 
    		}
            player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillAcrobatics")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainAcrobatics")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().acrobatics(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("acrobatics"), PP.getSkillToString("acrobaticsXP"), PP.getXpToLevel("acrobatics")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsAcrobatics1_0"), Messages.getString("m.EffectsAcrobatics1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsAcrobatics2_0"), Messages.getString("m.EffectsAcrobatics2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsAcrobatics3_0"), Messages.getString("m.EffectsAcrobatics3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.AcrobaticsRollChance", new Object[] {percentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.AcrobaticsGracefulRollChance", new Object[] {gracepercentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.AcrobaticsDodgeChance", new Object[] {dodgepercentage})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/mining") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillMining"))){ //$NON-NLS-1$
    		float skillvalue = (float)PP.getSkill("mining");
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		int ticks = 2;
    		int x = PP.getSkill("mining");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
			event.setCancelled(true);
            player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillMining")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainMining")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().mining(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("taming"), PP.getSkillToString("tamingXP"), PP.getXpToLevel("taming")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsMining1_0"), Messages.getString("m.EffectsMining1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsMining2_0"), Messages.getString("m.EffectsMining2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.MiningDoubleDropChance", new Object[] {percentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.MiningSuperBreakerLength", new Object[] {ticks})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/repair") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillRepair").toLowerCase())){ //$NON-NLS-1$
    		float skillvalue = (float)PP.getSkill("repair");
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		String repairmastery = String.valueOf((skillvalue / 500) * 100);
			event.setCancelled(true);
	        player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillRepair")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainRepair")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().repair(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("repair"), PP.getSkillToString("repairXP"), PP.getXpToLevel("repair")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsRepair1_0"), Messages.getString("m.EffectsRepair1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsRepair2_0"), Messages.getString("m.EffectsRepair2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsRepair3_0"), Messages.getString("m.EffectsRepair3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsRepair4_0", new Object[]{LoadProperties.repairdiamondlevel}), Messages.getString("m.EffectsRepair4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.RepairRepairMastery", new Object[] {repairmastery})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.RepairSuperRepairChance", new Object[] {percentage})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/unarmed")){ //$NON-NLS-1$
			event.setCancelled(true);
			String percentage, arrowpercentage;
			float skillvalue = (float)PP.getSkill("unarmed");
			
			if(PP.getSkill("unarmed") < 1000){
				percentage = String.valueOf((skillvalue / 4000) * 100);
			} else {
				percentage = "25"; //$NON-NLS-1$
			}
			
			if(PP.getSkill("unarmed") < 1000){
				arrowpercentage = String.valueOf(((skillvalue / 1000) * 100) / 2);
			} else {
				arrowpercentage = "50"; //$NON-NLS-1$
			}
			
			
			int ticks = 2;
			int x = PP.getSkill("unarmed");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	        player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillUnarmed")})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainUnarmed")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().unarmed(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("unarmed"), PP.getSkillToString("unarmedXP"), PP.getXpToLevel("unarmed")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsUnarmed1_0"), Messages.getString("m.EffectsUnarmed1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsUnarmed2_0"), Messages.getString("m.EffectsUnarmed2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsUnarmed3_0"), Messages.getString("m.EffectsUnarmed3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsUnarmed4_0"), Messages.getString("m.EffectsUnarmed4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsUnarmed5_0"), Messages.getString("m.EffectsUnarmed5_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.UnarmedArrowDeflectChance", new Object[] {arrowpercentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.UnarmedDisarmChance", new Object[] {percentage})); //$NON-NLS-1$
			if(PP.getSkill("unarmed") < 250){
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockUnarmed1")})); //$NON-NLS-1$ 
			} else if(PP.getSkill("unarmed") >= 250 && PP.getSkill("unarmed") < 500){
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusUnarmed1_0"), Messages.getString("m.AbilBonusUnarmed1_1")})); //$NON-NLS-1$  
				player.sendMessage(Messages.getString("m.AbilityLockTemplate", new Object[] {Messages.getString("m.AbilLockUnarmed2")})); //$NON-NLS-1$ 
			} else {
				player.sendMessage(Messages.getString("m.AbilityBonusTemplate", new Object[] {Messages.getString("m.AbilBonusUnarmed2_0"), Messages.getString("m.AbilBonusUnarmed2_1")})); //$NON-NLS-1$  
			}
			player.sendMessage(Messages.getString("m.UnarmedBerserkLength", new Object[] {ticks})); //$NON-NLS-1$
    	}
    	if(split[0].equalsIgnoreCase("/herbalism") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillHerbalism").toLowerCase())){ //$NON-NLS-1$
			event.setCancelled(true);
			int rank = 0;
			if(PP.getSkill("herbalism") >= 50)
    			rank++;
    		if (PP.getSkill("herbalism") >= 150)
    			rank++;
    		if (PP.getSkill("herbalism") >= 250)
    			rank++;
    		if (PP.getSkill("herbalism") >= 350)
    			rank++;
    		if (PP.getSkill("herbalism") >= 450)
    			rank++;
    		if (PP.getSkill("herbalism") >= 550)
    			rank++;
    		if (PP.getSkill("herbalism") >= 650)
    			rank++;
    		if (PP.getSkill("herbalism") >= 750)
    			rank++;
    		int bonus = 0;
    		if(PP.getSkill("herbalism") >= 200)
    			bonus++;
    		if(PP.getSkill("herbalism") >= 400)
    			bonus++;
    		if(PP.getSkill("herbalism") >= 600)
    			bonus++;
    		
    		int ticks = 2;
			int x = PP.getSkill("herbalism");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
			float skillvalue = (float)PP.getSkill("herbalism");
    		String percentage = String.valueOf((skillvalue / 1000) * 100);
    		String gpercentage = String.valueOf((skillvalue / 1500) * 100);
	        player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillHerbalism")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainHerbalism")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().herbalism(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("herbalism"), PP.getSkillToString("herbalismXP"), PP.getXpToLevel("herbalism")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsHerbalism1_0"), Messages.getString("m.EffectsHerbalism1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsHerbalism2_0"), Messages.getString("m.EffectsHerbalism2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsHerbalism3_0"), Messages.getString("m.EffectsHerbalism3_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsHerbalism4_0"), Messages.getString("m.EffectsHerbalism4_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsHerbalism5_0"), Messages.getString("m.EffectsHerbalism5_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.HerbalismGreenTerraLength", new Object[] {ticks})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.HerbalismGreenThumbChance", new Object[] {gpercentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.HerbalismGreenThumbStage", new Object[] {bonus})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.HerbalismDoubleDropChance", new Object[] {percentage})); //$NON-NLS-1$
			player.sendMessage(Messages.getString("m.HerbalismFoodPlus", new Object[] {rank})); //$NON-NLS-1$
    	}
    	
    	if(split[0].equalsIgnoreCase("/excavation") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillExcavation").toLowerCase())) //$NON-NLS-1$
    	{
			event.setCancelled(true);
			int ticks = 2;
			int x = PP.getSkill("excavation");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
	        player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillExcavation")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainExcavation")})); //$NON-NLS-1$ 
			if(mcPermissions.getInstance().excavation(player))
				player.sendMessage(Messages.getString("m.LVL", new Object[] {PP.getSkillToString("excavation"), PP.getSkillToString("excavationXP"), PP.getXpToLevel("excavation")}));
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsExcavation1_0"), Messages.getString("m.EffectsExcavation1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsExcavation2_0"), Messages.getString("m.EffectsExcavation2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.ExcavationGreenTerraLength", new Object[] {ticks})); //$NON-NLS-1$
    	}
    	
    	if(split[0].equalsIgnoreCase("/sorcery") || split[0].toLowerCase().equalsIgnoreCase("/"+Messages.getString("m.SkillSorcery").toLowerCase())) //$NON-NLS-1$
    	{
			event.setCancelled(true);
			
			/*
	        player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.SkillExcavation")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.XPGain", new Object[] {Messages.getString("m.XPGainExcavation")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.Effects")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsExcavation1_0"), Messages.getString("m.EffectsExcavation1_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.EffectsTemplate", new Object[] {Messages.getString("m.EffectsExcavation2_0"), Messages.getString("m.EffectsExcavation2_1")})); //$NON-NLS-1$  
			player.sendMessage(Messages.getString("m.SkillHeader", new Object[] {Messages.getString("m.YourStats")})); //$NON-NLS-1$ 
			player.sendMessage(Messages.getString("m.ExcavationGreenTerraLength", new Object[] {ticks})); //$NON-NLS-1$
			*/
    	}
    	
		if(LoadProperties.mcmmoEnable && split[0].equalsIgnoreCase("/"+LoadProperties.mcmmo)){ 
			event.setCancelled(true);
    		player.sendMessage(ChatColor.RED+"-----[]"+ChatColor.GREEN+"mMO"+ChatColor.RED+"[]-----");   
    		player.sendMessage(ChatColor.YELLOW+"mcMMO is an RPG server mod for minecraft."); 
    		player.sendMessage(ChatColor.YELLOW+"There are many skills added by mcMMO to minecraft."); 
    		player.sendMessage(ChatColor.YELLOW+"They can do anything from giving a chance"); 
    		player.sendMessage(ChatColor.YELLOW+"for double drops to letting you break materials instantly."); 
    		player.sendMessage(ChatColor.YELLOW+"For example, by harvesting logs from trees you will gain"); 
    		player.sendMessage(ChatColor.YELLOW+"Woodcutting xp and once you have enough xp you will gain"); 
    		player.sendMessage(ChatColor.YELLOW+"a skill level in Woodcutting. By raising this skill you will"); 
    		player.sendMessage(ChatColor.YELLOW+"be able to receive benefits like "+ChatColor.RED+"double drops");  
    		player.sendMessage(ChatColor.YELLOW+"and increase the effects of the "+ChatColor.RED+"\"Tree Felling\""+ChatColor.YELLOW+" ability.");   
    		player.sendMessage(ChatColor.YELLOW+"mMO has abilities related to the skill, skills normally"); 
    		player.sendMessage(ChatColor.YELLOW+"provide passive bonuses but they also have activated"); 
    		player.sendMessage(ChatColor.YELLOW+"abilities too. Each ability is activated by holding"); 
    		player.sendMessage(ChatColor.YELLOW+"the appropriate tool and "+ChatColor.RED+"right clicking.");  
    		player.sendMessage(ChatColor.YELLOW+"For example, if you hold a Mining Pick and right click"); 
    		player.sendMessage(ChatColor.YELLOW+"you will ready your Pickaxe, attack mining materials"); 
    		player.sendMessage(ChatColor.YELLOW+"and then "+ChatColor.RED+"Super Breaker "+ChatColor.YELLOW+"will activate.");   
    		player.sendMessage(ChatColor.GREEN+"Find out mcMMO commands with "+ChatColor.DARK_AQUA+"/"+LoadProperties.mcc);  
    		player.sendMessage(ChatColor.GREEN+"You can donate via paypal to"+ChatColor.DARK_RED+" nossr50@gmail.com");  
    	}
    	if(LoadProperties.mccEnable && split[0].equalsIgnoreCase("/"+LoadProperties.mcc)){ 
    		event.setCancelled(true);
    		player.sendMessage(ChatColor.RED+"---[]"+ChatColor.YELLOW+"mcMMO Commands"+ChatColor.RED+"[]---");   
    		if(mcPermissions.getInstance().party(player)){
    			player.sendMessage(Messages.getString("m.mccPartyCommands")); 
    			player.sendMessage("/"+LoadProperties.party+" "+Messages.getString("m.mccParty"));   
    			player.sendMessage("/"+LoadProperties.party+" q "+Messages.getString("m.mccPartyQ"));
    			if(mcPermissions.getInstance().partyChat(player))
    				player.sendMessage("/p "+Messages.getString("m.mccPartyToggle"));  
    			player.sendMessage("/"+LoadProperties.invite+" "+Messages.getString("m.mccPartyInvite"));   
    			player.sendMessage("/"+LoadProperties.accept+" "+Messages.getString("m.mccPartyAccept"));   
    			if(mcPermissions.getInstance().partyTeleport(player))
    				player.sendMessage("/"+LoadProperties.ptp+" "+Messages.getString("m.mccPartyTeleport"));   
    		}
    		player.sendMessage(Messages.getString("m.mccOtherCommands")); 
    		player.sendMessage("/"+LoadProperties.stats+ChatColor.RED+" "+Messages.getString("m.mccStats"));  
    		player.sendMessage("/mctop <skillname> <page> "+ChatColor.RED+Messages.getString("m.mccLeaderboards"));  
    		if(mcPermissions.getInstance().mySpawn(player)){
	    		player.sendMessage("/"+LoadProperties.myspawn+" "+ChatColor.RED+Messages.getString("m.mccMySpawn"));   
	    		player.sendMessage("/"+LoadProperties.clearmyspawn+" "+ChatColor.RED+Messages.getString("m.mccClearMySpawn"));   
    		}
    		if(mcPermissions.getInstance().mcAbility(player))
    			player.sendMessage("/"+LoadProperties.mcability+ChatColor.RED+" "+Messages.getString("m.mccToggleAbility"));  
    		if(mcPermissions.getInstance().adminChat(player)){
    			player.sendMessage("/a "+ChatColor.RED+Messages.getString("m.mccAdminToggle"));  
    		}
    		if(mcPermissions.getInstance().whois(player))
    			player.sendMessage("/"+LoadProperties.whois+" "+Messages.getString("m.mccWhois"));   
    		if(mcPermissions.getInstance().mmoedit(player)){
    			//player.sendMessage("/"+LoadProperties.mmoedit+" [skill] [newvalue] "+ChatColor.RED+"Modify the designated skill value");
    			player.sendMessage("/"+LoadProperties.mmoedit+Messages.getString("m.mccMmoedit"));   
    		}
    		if(mcPermissions.getInstance().mcgod(player))
    			player.sendMessage("/"+LoadProperties.mcgod+ChatColor.RED+" "+Messages.getString("m.mccMcGod"));  
    		player.sendMessage("/"+Messages.getString("m.mccSkillInfo"));  
    		player.sendMessage("/"+LoadProperties.mcmmo+" "+Messages.getString("m.mccModDescription"));   
    	}
    }
}
