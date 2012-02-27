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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;


public class Herbalism 
{	
	public static void greenTerra(Player player, Block block){
		PlayerInventory inventory = player.getInventory();
		boolean hasSeeds = inventory.contains(Material.SEEDS);
		if(block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT){
			if(!hasSeeds)
				player.sendMessage("You need more seeds to spread Green Terra");
			if(hasSeeds && block.getType() != Material.WHEAT)
			{
				inventory.removeItem(new ItemStack(Material.SEEDS, 1));
				player.updateInventory();
				if(LoadProperties.enableSmoothToMossy && block.getType() == Material.SMOOTH_BRICK)
					block.setData((byte)1);
				if(LoadProperties.enableDirtToGrass && block.getType() == Material.DIRT)
					block.setType(Material.GRASS);
				if(LoadProperties.enableCobbleToMossy && block.getType() == Material.COBBLESTONE)
					block.setType(Material.MOSSY_COBBLESTONE);
			}
		}
	}
	
	public static Boolean canBeGreenTerra(Block block){
    	switch(block.getType()){
    	case BROWN_MUSHROOM:
    	case CACTUS:
    	case COBBLESTONE:
    	case CROPS:
    	case DIRT:
    	case JACK_O_LANTERN:
    	case MELON_BLOCK:
    	case PUMPKIN:
    	case RED_MUSHROOM:
    	case RED_ROSE:
    	case SMOOTH_BRICK:
    	case SUGAR_CANE_BLOCK:
    	case VINE:
    	case WATER_LILY:
    	case YELLOW_FLOWER:
    		return true;
    	}
    	return false;
    }
	
	public static void herbalismProcCheck(final Block block, Player player, BlockBreakEvent event, mcMMO plugin)
	{
		final PlayerProfile PP = Users.getProfile(player);
		int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	PlayerInventory inventory = player.getInventory();
    	boolean hasSeeds = inventory.contains(Material.SEEDS);
    	
    	if(plugin.misc.blockWatchList.contains(block))
    	{
    		return;
    	}
    	
    	//Wheat
    	if(type == 59 && block.getData() == (byte) 0x7)
    	{
			is = new ItemStack(Material.WHEAT, 1);
    		PP.addXP(SkillType.HERBALISM, LoadProperties.mwheat, player);
    		
    		if(player != null)
    		{
    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
	    			m.mcDropItem(loc, is);
    		}
    		
    		//GREEN THUMB
    		if(hasSeeds && PP.getGreenTerraMode() || hasSeeds && (herbLevel >= 1500 || (Math.random() * 1500 <= herbLevel)))
    		{
    			event.setCancelled(true);
    			m.mcDropItem(loc, is);
    			//DROP SOME SEEDS
    			is = new ItemStack(Material.SEEDS, 1);
    			m.mcDropItem(loc, is);
    			
    			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        block.setType(Material.CROPS);
                        //This replants the wheat at a certain stage in development based on Herbalism Skill
                        if(!PP.getGreenTerraMode())
                        {
                            if (PP.getSkillLevel(SkillType.HERBALISM) >= 600)
                                block.setData((byte) 0x4);
                            else if (PP.getSkillLevel(SkillType.HERBALISM) >= 400)
                                block.setData((byte) 0x3);
                            else if (PP.getSkillLevel(SkillType.HERBALISM) >= 200)
                                block.setData((byte) 0x2);
                            else
                                block.setData((byte) 0x1);
                        } else
                            block.setData((byte) 0x4);
                    }
                }, 1);
    			
    			inventory.removeItem(new ItemStack(Material.SEEDS, 1));
    			player.updateInventory();
    		}
    	}
    	
    	//Nether Wart
    	if(type == 115 && block.getData() == (byte) 0x3)
    	{
			is = new ItemStack(Material.NETHER_STALK, 1);
    		PP.addXP(SkillType.HERBALISM, LoadProperties.mnetherwart, player);
    		if(player != null)
    		{
    			if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
	    		{
	    			m.mcDropItems(loc, is, 2);
	    			m.mcRandomDropItems(loc, is, 50, 3);
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
	    				is = new ItemStack(Material.CACTUS, 1);
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
	    				is = new ItemStack(Material.SUGAR_CANE, 1);
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
				is = new ItemStack(mat, 1);
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
				is = new ItemStack(Material.MELON, 1);
				if(player != null)
	    		{
				    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    		{
						m.mcDropItems(loc, is, 3);
						m.mcRandomDropItems(loc, is, 50, 4);
		    		}
	    		}
				PP.addXP(SkillType.HERBALISM, LoadProperties.mmelon, player);
	    	}
    		//Mushroom
	    	if(type == 39 || type == 40)
	    	{
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1);
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
				is = new ItemStack(mat, 1);
	    		if(player != null){
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    			m.mcDropItem(loc, is);
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mflower, player);
	    	}
	    	//Lily Pads
	    	if(type == 111)
	    	{
				is = new ItemStack(Material.WATER_LILY, 1);
	    		if(player != null){
	    		    if(herbLevel > 1000 || (Math.random() * 1000 <= herbLevel))
		    			m.mcDropItem(loc, is);
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mlilypad, player);
	    	}
	    	//Vines
	    	if(type == 106){
				is = new ItemStack(Material.VINE, 1, (byte)0, (byte)0);
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
