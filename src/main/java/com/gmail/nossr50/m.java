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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeBlockBreakEvent;
import com.gmail.nossr50.events.McMMOItemSpawnEvent;
import com.gmail.nossr50.skills.Repair;

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
			return Integer.parseInt(string);
		else
			return 0;
	}
	
	public static boolean isInvincible(LivingEntity le, EntityDamageEvent event)
	{
	    //So apparently if you do more damage to a LivingEntity than its last damage int you bypass the invincibility
	    //So yeah, this is for that
	    if(le.getNoDamageTicks() > le.getMaximumNoDamageTicks() / 2.0F && event.getDamage() <= le.getLastDamage())
	        return true;
	    else
	        return false;
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
	
	/**
	 * Checks to see if a block type awards XP.
	 * 
	 * @param material Block type to check
	 * @return true if the block type awards XP, false otherwise
	 */
	public static boolean shouldBeWatched(Material material)
	{
		switch(material){
		case BROWN_MUSHROOM:
		case CACTUS:
		case CLAY:
		case COAL_ORE:
		case DIAMOND_ORE:
		case DIRT:
		case ENDER_STONE:
		case GLOWING_REDSTONE_ORE:
		case GLOWSTONE:
		case GOLD_ORE:
		case GRASS:
		case GRAVEL:
		case IRON_ORE:
		case JACK_O_LANTERN:
		case LAPIS_ORE:
		case LOG:
		case MELON_BLOCK:
		case MOSSY_COBBLESTONE:
		case MYCEL:
		case NETHERRACK:
		case OBSIDIAN:
		case PUMPKIN:
		case RED_MUSHROOM:
		case RED_ROSE:
		case REDSTONE_ORE:
		case SAND:
		case SANDSTONE:
		case SOUL_SAND:
		case STONE:
		case SUGAR_CANE_BLOCK:
		case VINE:
		case WATER_LILY:
		case YELLOW_FLOWER:
			return true;
		}
		return false;
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

	public static boolean blockBreakSimulate(Block block, Player player, Boolean shouldArmSwing)
	{
	    //Support for NoCheat
	    if(shouldArmSwing)
	    {
	        PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
            Bukkit.getPluginManager().callEvent(armswing);
	    }
	    
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
	
	public static Integer getTier(Player player)
	{
		ItemStack is = player.getItemInHand();
		if(Repair.isWoodTools(is))
			return 1;
		if(Repair.isStoneTools(is))
			return 2;
		if(Repair.isIronTools(is))
			return 3;
		if(Repair.isGoldTools(is))
			return 1;
		if(Repair.isDiamondTools(is))
			return 4;
		
		return 1;
	}
	
	public static boolean isNear(Location first, Location second, int maxDistance) {
		double relX = first.getX() - second.getX();
		double relY = first.getY() - second.getY();
		double relZ = first.getZ() - second.getZ();
		double dist = relX * relX + relY * relY + relZ * relZ;
		
		if (dist < maxDistance * maxDistance)
			return true;
		
		return false;
	}
	
	public static boolean abilityBlockCheck(Block block)
	{
		switch(block.getType()){
		case BED_BLOCK:
		case BREWING_STAND:
		case BOOKSHELF:
		case BURNING_FURNACE:
		case CAKE_BLOCK:
		case CHEST:
		case DISPENSER:
		case ENCHANTMENT_TABLE:
		case FENCE_GATE:
		case FURNACE:
		case IRON_DOOR_BLOCK:
		case JUKEBOX:
		case LEVER:
		case NOTE_BLOCK:
		case STONE_BUTTON:
		case TRAP_DOOR:
		case WALL_SIGN:
		case WOODEN_DOOR:
		case WORKBENCH:
			return false;
		}
		
		if(block.getTypeId() == LoadProperties.anvilID)
			return false;
		
		return true;
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
	
	public static void mcDropItems(Location location, ItemStack is, int quantity)
	{
		for(int i = 0; i < quantity; i++)
			mcDropItem(location, is);
	}
	
	public static void mcRandomDropItem(Location location, ItemStack is, int chance)
	{
		if(Math.random() * 100 < chance)
			mcDropItem(location, is);
	}
	
	public static void mcRandomDropItems(Location location, ItemStack is, int chance, int quantity)
	{
		for(int i = 0; i < quantity; i++)
			mcRandomDropItem(location, is, chance);
	}
	
	public static void mcDropItem(Location location, ItemStack itemStack) {
		// We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
		McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		
		location.getWorld().dropItemNaturally(location, itemStack);
	}

	public static boolean isSwords(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_SWORD:
		case GOLD_SWORD:
		case IRON_SWORD:
		case STONE_SWORD:
		case WOOD_SWORD:
			return true;
		}
		return false;
	}
	
	public static boolean isHoe(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_HOE:
		case GOLD_HOE:
		case IRON_HOE:
		case STONE_HOE:
		case WOOD_HOE:
			return true;
		}
		return false;
	}
	
	public static boolean isShovel(ItemStack is)
	{	
		switch(is.getType()){
		case DIAMOND_SPADE:
		case GOLD_SPADE:
		case IRON_SPADE:
		case STONE_SPADE:
		case WOOD_SPADE:
			return true;
		}
		return false;
	}
	
	public static boolean isAxes(ItemStack is)
	{	
		switch(is.getType()){
		case DIAMOND_AXE:
		case GOLD_AXE:
		case IRON_AXE:
		case STONE_AXE:
		case WOOD_AXE:
			return true;
		}
		return false;
	}
	
	public static boolean isMiningPick(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_PICKAXE:
		case GOLD_PICKAXE:
		case IRON_PICKAXE:
		case STONE_PICKAXE:
		case WOOD_PICKAXE:
			return true;
		}
		return false;
	}
	
	public static boolean isHelmet(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_HELMET:
		case GOLD_HELMET:
		case IRON_HELMET:
		case LEATHER_HELMET:
			return true;
		}
		return false;
	}
	
	public static boolean isChestplate(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_CHESTPLATE:
		case GOLD_CHESTPLATE:
		case IRON_CHESTPLATE:
		case LEATHER_CHESTPLATE:
			return true;
		}
		return false;
	}
	
	public static boolean isPants(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_LEGGINGS:
		case GOLD_LEGGINGS:
		case IRON_LEGGINGS:
		case LEATHER_LEGGINGS:
			return true;
		}
		return false;
	}
	
	public static boolean isBoots(ItemStack is)
	{
		switch(is.getType()){
		case DIAMOND_BOOTS:
		case GOLD_BOOTS:
		case IRON_BOOTS:
		case LEATHER_BOOTS:
			return true;
		}
		return false;
	}
	
	public static boolean isOre(Block block)
	{
		switch (block.getType()) {
		case COAL_ORE:
		case DIAMOND_ORE:
		case GLOWING_REDSTONE_ORE:
		case GOLD_ORE:
		case IRON_ORE:
		case LAPIS_ORE:
		case REDSTONE_ORE:
			return true;
		}
		return false;
	}
	
	public static void convertToMySQL()
	{
		if(!LoadProperties.useMySQL)
			return;
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), new Runnable(){
			public void run() {
				String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";
				try {
					//Open the user file
					FileReader file = new FileReader(location);
					BufferedReader in = new BufferedReader(file);
					String line = "";
					String playerName = null, mining = null, party = null, miningXP = null, woodcutting = null, woodCuttingXP = null, repair = null, unarmed = null, herbalism = null, excavation = null, archery = null, swords = null, axes = null, acrobatics = null, repairXP = null, unarmedXP = null, herbalismXP = null, excavationXP = null, archeryXP = null, swordsXP = null, axesXP = null, acrobaticsXP = null, taming = null, tamingXP = null, fishing = null, fishingXP = null;
					int id = 0, theCount = 0;
					while ((line = in.readLine()) != null) {
						//Find if the line contains the player we want.
						String[] character = line.split(":");
						playerName = character[0];
						//Check for things we don't want put in the DB
						if (playerName == null
								|| playerName.equals("null")
								|| playerName
										.equals("#Storage place for user information"))
							continue;

						//Get Mining
						if (character.length > 1)
							mining = character[1];
						//Party
						if (character.length > 3)
							party = character[3];
						//Mining XP
						if (character.length > 4)
							miningXP = character[4];
						if (character.length > 5)
							woodcutting = character[5];
						if (character.length > 6)
							woodCuttingXP = character[6];
						if (character.length > 7)
							repair = character[7];
						if (character.length > 8)
							unarmed = character[8];
						if (character.length > 9)
							herbalism = character[9];
						if (character.length > 10)
							excavation = character[10];
						if (character.length > 11)
							archery = character[11];
						if (character.length > 12)
							swords = character[12];
						if (character.length > 13)
							axes = character[13];
						if (character.length > 14)
							acrobatics = character[14];
						if (character.length > 15)
							repairXP = character[15];
						if (character.length > 16)
							unarmedXP = character[16];
						if (character.length > 17)
							herbalismXP = character[17];
						if (character.length > 18)
							excavationXP = character[18];
						if (character.length > 19)
							archeryXP = character[19];
						if (character.length > 20)
							swordsXP = character[20];
						if (character.length > 21)
							axesXP = character[21];
						if (character.length > 22)
							acrobaticsXP = character[22];
						if (character.length > 24)
							taming = character[24];
						if (character.length > 25)
							tamingXP = character[25];
						if (character.length > 34)
							fishing = character[34];
						if (character.length > 35)
							fishingXP = character[35];

						//Check to see if the user is in the DB
						id = mcMMO.database.GetInt("SELECT id FROM "
								+ LoadProperties.MySQLtablePrefix
								+ "users WHERE user = '" + playerName + "'");

						if (id > 0) {
							theCount++;
							//Update the skill values
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "users SET lastlogin = " + 0
									+ " WHERE id = " + id);
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "skills SET " + "  taming = taming+"
									+ getInt(taming) + ", mining = mining+"
									+ getInt(mining) + ", repair = repair+"
									+ getInt(repair)
									+ ", woodcutting = woodcutting+"
									+ getInt(woodcutting)
									+ ", unarmed = unarmed+" + getInt(unarmed)
									+ ", herbalism = herbalism+"
									+ getInt(herbalism)
									+ ", excavation = excavation+"
									+ getInt(excavation)
									+ ", archery = archery+" + getInt(archery)
									+ ", swords = swords+" + getInt(swords)
									+ ", axes = axes+" + getInt(axes)
									+ ", acrobatics = acrobatics+"
									+ getInt(acrobatics)
									+ ", fishing = fishing+" + getInt(fishing)
									+ " WHERE user_id = " + id);
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "experience SET " + "  taming = "
									+ getInt(tamingXP) + ", mining = "
									+ getInt(miningXP) + ", repair = "
									+ getInt(repairXP) + ", woodcutting = "
									+ getInt(woodCuttingXP) + ", unarmed = "
									+ getInt(unarmedXP) + ", herbalism = "
									+ getInt(herbalismXP) + ", excavation = "
									+ getInt(excavationXP) + ", archery = "
									+ getInt(archeryXP) + ", swords = "
									+ getInt(swordsXP) + ", axes = "
									+ getInt(axesXP) + ", acrobatics = "
									+ getInt(acrobaticsXP) + ", fishing = "
									+ getInt(fishingXP) + " WHERE user_id = "
									+ id);
						} else {
							theCount++;
							//Create the user in the DB
							mcMMO.database.Write("INSERT INTO "
									+ LoadProperties.MySQLtablePrefix
									+ "users (user, lastlogin) VALUES ('"
									+ playerName + "',"
									+ System.currentTimeMillis() / 1000 + ")");
							id = mcMMO.database
									.GetInt("SELECT id FROM "
											+ LoadProperties.MySQLtablePrefix
											+ "users WHERE user = '"
											+ playerName + "'");
							mcMMO.database.Write("INSERT INTO "
									+ LoadProperties.MySQLtablePrefix
									+ "skills (user_id) VALUES (" + id + ")");
							mcMMO.database.Write("INSERT INTO "
									+ LoadProperties.MySQLtablePrefix
									+ "experience (user_id) VALUES (" + id
									+ ")");
							//Update the skill values
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "users SET lastlogin = " + 0
									+ " WHERE id = " + id);
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "users SET party = '" + party
									+ "' WHERE id = " + id);
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "skills SET " + "  taming = "
									+ getInt(taming) + ", mining = "
									+ getInt(mining) + ", repair = "
									+ getInt(repair) + ", woodcutting = "
									+ getInt(woodcutting) + ", unarmed = "
									+ getInt(unarmed) + ", herbalism = "
									+ getInt(herbalism) + ", excavation = "
									+ getInt(excavation) + ", archery = "
									+ getInt(archery) + ", swords = "
									+ getInt(swords) + ", axes = "
									+ getInt(axes) + ", acrobatics = "
									+ getInt(acrobatics) + ", fishing = "
									+ getInt(fishing) + " WHERE user_id = "
									+ id);
							mcMMO.database.Write("UPDATE "
									+ LoadProperties.MySQLtablePrefix
									+ "experience SET " + "  taming = "
									+ getInt(tamingXP) + ", mining = "
									+ getInt(miningXP) + ", repair = "
									+ getInt(repairXP) + ", woodcutting = "
									+ getInt(woodCuttingXP) + ", unarmed = "
									+ getInt(unarmedXP) + ", herbalism = "
									+ getInt(herbalismXP) + ", excavation = "
									+ getInt(excavationXP) + ", archery = "
									+ getInt(archeryXP) + ", swords = "
									+ getInt(swordsXP) + ", axes = "
									+ getInt(axesXP) + ", acrobatics = "
									+ getInt(acrobaticsXP) + ", fishing = "
									+ getInt(fishingXP) + " WHERE user_id = "
									+ id);
						}
					}
					System.out
							.println("[mcMMO] MySQL Updated from users file, "
									+ theCount
									+ " items added/updated to MySQL DB");
					in.close();
				} catch (Exception e) {
					log.log(Level.SEVERE, "Exception while reading " + location
							+ " (Are you sure you formatted it correctly?)", e);
				}
			}
		}, 1);
	}
}