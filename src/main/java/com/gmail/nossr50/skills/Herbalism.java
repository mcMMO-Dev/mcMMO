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
package com.gmail.nossr50.skills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;


public class Herbalism 
{
	
	public static void greenTerraCheck(Player player, Block block)
	{
		PlayerProfile PP = Users.getProfile(player);
	    if(m.isHoe(player.getItemInHand()))
	    {
	    	if(block != null)
	    	{
		    	if(!m.abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getHoePreparationMode())
	    	{
				PP.setHoePreparationMode(false);
			}
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.HERBALISM);
			while(x >= 50)
			{
				x-=50;
				ticks++;
			}
			
	    	if(!PP.getGreenTerraMode() && Skills.cooldownOver(player, PP.getSkillDATS(AbilityType.GREEN_TERRA), LoadProperties.greenTerraCooldown))
	    	{
	    		player.sendMessage(mcLocale.getString("Skills.GreenTerraOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.GreenTerraPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setSkillDATS(AbilityType.GREEN_TERRA, System.currentTimeMillis()+(ticks*1000));
	    		PP.setGreenTerraMode(true);
	    	}
	    	
	    }
	}
	
	public static void greenTerraWheat(Player player, Block block, BlockBreakEvent event, mcMMO plugin)
	{
		if(block.getType() == Material.WHEAT && block.getData() == (byte) 0x07)
		{
			event.setCancelled(true);
			PlayerProfile PP = Users.getProfile(player);
			Material mat = Material.getMaterial(296);
			Location loc = block.getLocation();
			ItemStack is = new ItemStack(mat, 1, (byte)0, (byte)0);
			PP.addXP(SkillType.HERBALISM, LoadProperties.mwheat, player);
			m.mcDropItem(loc, is);
	    	
	    	//DROP SOME SEEDS
			mat = Material.SEEDS;
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
			m.mcDropItem(loc, is);
			
	    	herbalismProcCheck(block, player, event, plugin, true);
	    	herbalismProcCheck(block, player, event, plugin, true);
			block.setData((byte) 0x03);
		}
	}
	
	public static void greenTerra(Player player, Block block){
		if(block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT){
			if(!hasSeeds(player))
				player.sendMessage("You need more seeds to spread Green Terra");
			if(hasSeeds(player) && block.getType() != Material.WHEAT)
			{
				removeSeeds(player);
			if(block.getType() == Material.SMOOTH_BRICK)
				block.setData((byte)1);
			if(block.getType() == Material.DIRT)
				block.setType(Material.GRASS);
			if(LoadProperties.enableCobbleToMossy && block.getType() == Material.COBBLESTONE)
				block.setType(Material.MOSSY_COBBLESTONE);
			}
		}
	}
	
	public static Boolean canBeGreenTerra(Block block){
    	int t = block.getTypeId();
    	return t == 103 || t == 4 || t == 3 || t == 59 || t == 81 || t == 83 || t == 91 || t == 86 || t == 39 || t == 46 || t == 37 || t == 38;
    }
	
	public static boolean hasSeeds(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 295){
    			return true;
    		}
    	}
    	return false;
    }
	
	public static void removeSeeds(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 295){
    			if(x.getAmount() == 1){
    				x.setTypeId(0);
    				x.setAmount(0);
    				player.getInventory().setContents(inventory);
    			} else{
    			x.setAmount(x.getAmount() - 1);
    			player.getInventory().setContents(inventory);
    			}
    			return;
    		}
    	}
    }
	
	public static void herbalismProcCheck(Block block, Player player, BlockBreakEvent event, mcMMO plugin, boolean isGreenTerra)
	{
		PlayerProfile PP = Users.getProfile(player);
		int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	
    	if(plugin.misc.blockWatchList.contains(block))
    	{
    		return;
    	}
    	
    	//Wheat
    	if(type == 59 && block.getData() == (byte) 0x7)
    	{
    		mat = Material.getMaterial(296);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    		PP.addXP(SkillType.HERBALISM, LoadProperties.mwheat, player);
    		if(player != null)
    		{
    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
	    			m.mcDropItem(loc, is);
    		}
    		//GREEN THUMB
    		if(!isGreenTerra && (herbLevel > 1500 || (Math.random() * 1500 <= herbLevel)))
    		{
    			event.setCancelled(true);
    			m.mcDropItem(loc, is);
    			//DROP SOME SEEDS
    			mat = Material.SEEDS;
    			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    			m.mcDropItem(loc, is);
    			
    			//This replants the wheat at a certain stage in development based on Herbalism Skill
    			if(PP.getSkillLevel(SkillType.HERBALISM) >= 600)
    			    block.setData((byte) 0x4);
    			else if(PP.getSkillLevel(SkillType.HERBALISM) >= 400)
    				block.setData((byte) 0x3);
    			else if(PP.getSkillLevel(SkillType.HERBALISM) >= 200)
    				block.setData((byte) 0x2);
    			else
    				block.setData((byte) 0x1);
    		}
    	}
    	
    	//Nether Wart
    	if(type == 115 && block.getData() == (byte) 0x3)
    	{
    		mat = Material.getMaterial(372);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    		PP.addXP(SkillType.HERBALISM, LoadProperties.mnetherwart, player);
    		if(player != null)
    		{
    			if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
	    		{
	    			m.mcDropItem(loc, is);
	    			m.mcDropItem(loc, is);
					if(Math.random() * 10 > 5)
						m.mcDropItem(loc, is);
					if(Math.random() * 10 > 5)
						m.mcDropItem(loc, is);
					if(Math.random() * 10 > 5)
						m.mcDropItem(loc, is);
	    		}
    		}
    	}
    	
    	/*
    	 * We need to check not-wheat and not-netherwart stuff for if it was placed by the player or not
    	 */
    	if(block.getData() != (byte) 5)
    	{
    		//Cactus
	    	if(type == 81){
	    		//Setup the loop
	    		World world = block.getWorld();
	    		Block[] blockArray = new Block[3];
	    		blockArray[0] = block;
	    		blockArray[1] = world.getBlockAt(block.getX(), block.getY()+1, block.getZ());
	    		blockArray[2] = world.getBlockAt(block.getX(), block.getY()+2, block.getZ());
	    		
	    		Material[] materialArray = new Material[3];
	    		materialArray[0] = blockArray[0].getType();
	    		materialArray[1] = blockArray[1].getType();
	    		materialArray[2] = blockArray[2].getType();
	    		
	    		byte[] byteArray = new byte[3];
	    		byteArray[0] = blockArray[0].getData();
	    		byteArray[1] = blockArray[0].getData();
	    		byteArray[2] = blockArray[0].getData();
	    		
	    		int x = 0;
	    		for(Block target : blockArray)
	    		{
	    			if(materialArray[x] == Material.CACTUS)
	    			{
	    				is = new ItemStack(Material.CACTUS, 1, (byte)0, (byte)0);
	    				if(byteArray[x] != (byte) 5)
	    				{
	    					if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    		    	{
		    		    		m.mcDropItem(target.getLocation(), is);
		    		    	}
		    		    	PP.addXP(SkillType.HERBALISM, LoadProperties.mcactus, player);
	    				}
	    			}
	    			x++;
	    		}
	    	}
    		//Sugar Canes
	    	if(type == 83)
	    	{
	    		//Setup the loop
	    		World world = block.getWorld();
	    		Block[] blockArray = new Block[3];
	    		blockArray[0] = block;
	    		blockArray[1] = world.getBlockAt(block.getX(), block.getY()+1, block.getZ());
	    		blockArray[2] = world.getBlockAt(block.getX(), block.getY()+2, block.getZ());
	    		
	    		Material[] materialArray = new Material[3];
	    		materialArray[0] = blockArray[0].getType();
	    		materialArray[1] = blockArray[1].getType();
	    		materialArray[2] = blockArray[2].getType();
	    		
	    		byte[] byteArray = new byte[3];
	    		byteArray[0] = blockArray[0].getData();
	    		byteArray[1] = blockArray[0].getData();
	    		byteArray[2] = blockArray[0].getData();
	    		
	    		int x = 0;
	    		for(Block target : blockArray)
	    		{
	    			if(materialArray[x] == Material.SUGAR_CANE_BLOCK)
	    			{
	    				is = new ItemStack(Material.SUGAR_CANE, 1, (byte)0, (byte)0);
	    				//Check for being placed by the player
	    				if(byteArray[x] != (byte) 5)
	    				{
	    					if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    		    	{
		    		    		m.mcDropItem(target.getLocation(), is);
		    		    	}
		    		    	PP.addXP(SkillType.HERBALISM, LoadProperties.msugar, player);
	    				}
	    			}
	    			x++;
	    		}
	    	}
	    	
    		//Pumpkins
	    	if((type == 91 || type == 86))
	    	{
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null)
	    		{
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    		{
		    			m.mcDropItem(loc, is);
		    		}
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mpumpkin, player);
	    	}
	    	//Melon
	    	if(type == 103)
	    	{
	    		mat = Material.getMaterial(360);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				if(player != null)
	    		{
				    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    		{
						m.mcDropItem(loc, is);
						m.mcDropItem(loc, is);
						m.mcDropItem(loc, is);
						if(Math.random() * 10 > 5)
							m.mcDropItem(loc, is);
						if(Math.random() * 10 > 5)
							m.mcDropItem(loc, is);
						if(Math.random() * 10 > 5)
							m.mcDropItem(loc, is);
						if(Math.random() * 10 > 5)
							m.mcDropItem(loc, is);
		    		}
	    		}
				PP.addXP(SkillType.HERBALISM, LoadProperties.mmelon, player);
	    	}
    		//Mushroom
	    	if(type == 39 || type == 40)
	    	{
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null)
	    		{
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    		{
		    			m.mcDropItem(loc, is);
		    		}
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mmushroom, player);
	    	}
	    	//Flower
	    	if(type == 37 || type == 38){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    			m.mcDropItem(loc, is);
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mflower, player);
	    	}
	    	//Lily Pads
	    	if(type == 111)
	    	{
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    			m.mcDropItem(loc, is);
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mlilypad, player);
	    	}
	    	//Vines
	    	if(type == 106){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    			m.mcDropItem(loc, is);
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mvines, player);
	    	}
    	}
    	Skills.XpCheckSkill(SkillType.HERBALISM, player);
    }
}
