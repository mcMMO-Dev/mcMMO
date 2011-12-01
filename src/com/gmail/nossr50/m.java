/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;
import com.gmail.nossr50.datatypes.SkillType;

public class m 
{
	public static final Logger log = Logger.getLogger("Minecraft"); 
	/*
	 * I'm storing my misc functions/methods in here in an unorganized manner. Spheal with it.
	 * This is probably the most embarrassing part of my code for mcMMO
	 * I really should find an organized place for these things!
	 */
	
	public static String getCapitalized(String target)
	{
		String firstLetter = target.substring(0,1);
		String remainder   = target.substring(1);
		String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
		
		return capitalized;
	}
	public static int getInt(String string)
	{
		if(isInt(string))
		{
			return Integer.parseInt(string);
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
			return Double.parseDouble(string);
		}
		else
		{
			return (double) 0;
		}
	}
	
	public static boolean isDouble(String string)
	{
		try 
		{
			Double.parseDouble(string);
		}
		catch(NumberFormatException nFE) {
			return false;
		}
		return true;
	}
	
	public static boolean shouldBeWatched(Block block)
	{
		int id = block.getTypeId();
		return id == 103 || id == 82 || id == 16 || id == 73 || id == 49 || id == 81 || id == 83 || id == 86 || id == 91 || id == 1 || id == 17 || id == 42 || id == 87 || id == 89 || id == 2 || id == 3 || id == 12 || id == 13 || id == 21 || id == 15 || id == 14 || id == 56 || id == 38 || id == 37 || id == 39 || id == 40 || id == 24;
	}
	
	public static int getPowerLevel(Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		int x = 0;
		if(mcPermissions.getInstance().taming(player))
			x+=PP.getSkillLevel(SkillType.TAMING);
		if(mcPermissions.getInstance().mining(player))
			x+=PP.getSkillLevel(SkillType.MINING);
		if(mcPermissions.getInstance().woodcutting(player))
			x+=PP.getSkillLevel(SkillType.WOODCUTTING);
		if(mcPermissions.getInstance().unarmed(player))
			x+=PP.getSkillLevel(SkillType.UNARMED);
		if(mcPermissions.getInstance().herbalism(player))
			x+=PP.getSkillLevel(SkillType.HERBALISM);
		if(mcPermissions.getInstance().excavation(player))
			x+=PP.getSkillLevel(SkillType.EXCAVATION);
		if(mcPermissions.getInstance().archery(player))
			x+=PP.getSkillLevel(SkillType.ARCHERY);
		if(mcPermissions.getInstance().swords(player))
			x+=PP.getSkillLevel(SkillType.SWORDS);
		if(mcPermissions.getInstance().axes(player))
			x+=PP.getSkillLevel(SkillType.AXES);
		if(mcPermissions.getInstance().acrobatics(player))
			x+=PP.getSkillLevel(SkillType.ACROBATICS);
		if(mcPermissions.getInstance().repair(player))
			x+=PP.getSkillLevel(SkillType.REPAIR);
		if(mcPermissions.getInstance().fishing(player))
			x+=PP.getSkillLevel(SkillType.FISHING);
		return x;
	}

	public static boolean blockBreakSimulate(Block block, Player player)
	{
		FakeBlockBreakEvent event = new FakeBlockBreakEvent(block, player);
		if(block != null && player != null){
			Bukkit.getServer().getPluginManager().callEvent(event);
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

	public static void damageTool(Player player, short damage)
	{
		if(player.getItemInHand().getTypeId() == 0)
			return;
		player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + damage));
		if(player.getItemInHand().getDurability() >= getMaxDurability(getTier(player), player.getItemInHand()))
		{
			ItemStack[] inventory = player.getInventory().getContents();
			for(ItemStack x : inventory)
			{
				if(x != null && x.getTypeId() == player.getItemInHand().getTypeId() && x.getDurability() == player.getItemInHand().getDurability()){
					x.setTypeId(0);
					x.setAmount(0);
					player.getInventory().setContents(inventory);
					return;
				}
			}
		}
	}
	public static Integer getTier(Player player)
	{
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
	public static Integer getMaxDurability(Integer tier, ItemStack item)
	{
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
	public static boolean isInt(String string)
	{
		try 
		{
			Integer.parseInt(string);
		}
		catch(NumberFormatException nFE) 
		{
			return false;
		}
		return true;
	}
	public static void mcDropItem(Location loc, int id)
	{
		if(loc != null)
		{
			Material mat = Material.getMaterial(id);
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
		}
	}

	public static boolean isSwords(ItemStack is)
	{
		return is.getTypeId() == 268 || is.getTypeId() == 267 || is.getTypeId() == 272 || is.getTypeId() == 283 || is.getTypeId() == 276;
	}
	
	public static boolean isHoe(ItemStack is)
	{
		int id = is.getTypeId();
		return id == 290 || id == 291 || id == 292 || id == 293 || id == 294;
	}
	
	public static boolean isShovel(ItemStack is){
		return is.getTypeId() == 269 || is.getTypeId() == 273 || is.getTypeId() == 277 || is.getTypeId() == 284 || is.getTypeId() == 256;
	}
	
	public static boolean isAxes(ItemStack is){
		if(is.getTypeId() == 271 || is.getTypeId() == 258 || is.getTypeId() == 286 || is.getTypeId() == 279 || is.getTypeId() == 275){
			return true;
		} else {
			return false;
		}
	}
	public static boolean isMiningPick(ItemStack is)
	{
		if(is.getTypeId() == 270 || is.getTypeId() == 274 || is.getTypeId() == 285 || is.getTypeId() == 257 || is.getTypeId() == 278)
		{
			return true;
		} else {
			return false;
		}
	}
	public boolean isGold(ItemStack is)
	{
		int i = is.getTypeId();
		if(i == 283 || i == 284 || i == 285 || i == 286 || i == 294 || i == 314 || i == 315 || i == 316 || i == 317){
			return true;
		} else {
			return false;
		}
	}
	public static void convertToMySQL()
	{
		if(!LoadProperties.useMySQL)
			return;
		String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users"; 
		try {
			//Open the user file
			FileReader file = new FileReader(location);
			BufferedReader in = new BufferedReader(file);
			String line = ""; 
			String playerName = null, mining = null, party = null, miningXP = null, woodcutting = null, woodCuttingXP = null, repair = null, unarmed = null, herbalism = null,
			excavation = null, archery = null, swords = null, axes = null, acrobatics = null, repairXP = null, unarmedXP = null, herbalismXP = null, excavationXP = null, archeryXP = null, swordsXP = null, axesXP = null,
			acrobaticsXP = null, taming = null, tamingXP = null;
			int id = 0, theCount = 0;
			while((line = in.readLine()) != null)
			{
				//Find if the line contains the player we want.
				String[] character = line.split(":"); 
				playerName = character[0];
				//Check for things we don't want put in the DB
				if(playerName == null || playerName.equals("null") || playerName.equals("#Storage place for user information")) 
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
				id = mcMMO.database.GetInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'"); 
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
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + 0 + " WHERE id = " + id); 
					//if(getDouble(x) > 0 && getDouble(y) > 0 && getDouble(z) > 0)
					//mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"spawn SET world = '" + myspawnworld + "', x = " +getDouble(x)+", y = "+getDouble(y)+", z = "+getDouble(z)+" WHERE user_id = "+id);
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET " 
							+"  taming = taming+"+getInt(taming) 
							+", mining = mining+"+getInt(mining) 
							+", repair = repair+"+getInt(repair) 
							+", woodcutting = woodcutting+"+getInt(woodcutting) 
							+", unarmed = unarmed+"+getInt(unarmed) 
							+", herbalism = herbalism+"+getInt(herbalism) 
							+", excavation = excavation+"+getInt(excavation) 
							+", archery = archery+" +getInt(archery) 
							+", swords = swords+" +getInt(swords) 
							+", axes = axes+"+getInt(axes) 
							+", acrobatics = acrobatics+"+getInt(acrobatics) 
							+" WHERE user_id = "+id); 
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET " 
							+"  taming = "+getInt(tamingXP) 
							+", mining = "+getInt(miningXP) 
							+", repair = "+getInt(repairXP) 
							+", woodcutting = "+getInt(woodCuttingXP) 
							+", unarmed = "+getInt(unarmedXP) 
							+", herbalism = "+getInt(herbalismXP) 
							+", excavation = "+getInt(excavationXP) 
							+", archery = " +getInt(archeryXP) 
							+", swords = " +getInt(swordsXP) 
							+", axes = "+getInt(axesXP) 
							+", acrobatics = "+getInt(acrobaticsXP) 
							+" WHERE user_id = "+id); 
				}
				else
				{
					theCount++;
					//Create the user in the DB
					mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"users (user, lastlogin) VALUES ('" + playerName + "'," + System.currentTimeMillis() / 1000 +")"); 
					id = mcMMO.database.GetInt("SELECT id FROM "+LoadProperties.MySQLtablePrefix+"users WHERE user = '" + playerName + "'"); 
					mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"spawn (user_id) VALUES ("+id+")"); 
					mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"skills (user_id) VALUES ("+id+")"); 
					mcMMO.database.Write("INSERT INTO "+LoadProperties.MySQLtablePrefix+"experience (user_id) VALUES ("+id+")"); 
					//Update the skill values
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET lastlogin = " + 0 + " WHERE id = " + id); 
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"users SET party = '"+party+"' WHERE id = " +id); 
					/*
    				if(getDouble(x) > 0 && getDouble(y) > 0 && getDouble(z) > 0)
    					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"spawn SET world = '" + myspawnworld + "', x = " +getDouble(x)+", y = "+getDouble(y)+", z = "+getDouble(z)+" WHERE user_id = "+id);
					 */
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"skills SET " 
							+"  taming = "+getInt(taming) 
							+", mining = "+getInt(mining) 
							+", repair = "+getInt(repair) 
							+", woodcutting = "+getInt(woodcutting) 
							+", unarmed = "+getInt(unarmed) 
							+", herbalism = "+getInt(herbalism) 
							+", excavation = "+getInt(excavation) 
							+", archery = " +getInt(archery) 
							+", swords = " +getInt(swords) 
							+", axes = "+getInt(axes) 
							+", acrobatics = "+getInt(acrobatics) 
							+" WHERE user_id = "+id); 
					mcMMO.database.Write("UPDATE "+LoadProperties.MySQLtablePrefix+"experience SET " 
							+"  taming = "+getInt(tamingXP) 
							+", mining = "+getInt(miningXP) 
							+", repair = "+getInt(repairXP) 
							+", woodcutting = "+getInt(woodCuttingXP) 
							+", unarmed = "+getInt(unarmedXP) 
							+", herbalism = "+getInt(herbalismXP) 
							+", excavation = "+getInt(excavationXP) 
							+", archery = " +getInt(archeryXP) 
							+", swords = " +getInt(swordsXP) 
							+", axes = "+getInt(axesXP) 
							+", acrobatics = "+getInt(acrobaticsXP) 
							+" WHERE user_id = "+id); 
				}
			}
			System.out.println("[mcMMO] MySQL Updated from users file, "+theCount+" items added/updated to MySQL DB"); 
			in.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading " 
					+ location + " (Are you sure you formatted it correctly?)", e); 
		}
	}
}
