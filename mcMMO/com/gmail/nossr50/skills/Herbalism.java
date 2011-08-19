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
			
	    	if(!PP.getGreenTerraMode() && Skills.cooldownOver(player, PP.getGreenTerraDeactivatedTimeStamp(), LoadProperties.greenTerraCooldown))
	    	{
	    		player.sendMessage(mcLocale.getString("Skills.GreenTerraOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.GreenTerraPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setGreenTerraActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setGreenTerraDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
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
			PP.addXP(SkillType.HERBALISM, LoadProperties.mwheat);
	    	loc.getWorld().dropItemNaturally(loc, is);
	    	
	    	//DROP SOME SEEDS
			mat = Material.SEEDS;
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
			loc.getWorld().dropItemNaturally(loc, is);
			
	    	herbalismProcCheck(block, player, event, plugin);
	    	herbalismProcCheck(block, player, event, plugin);
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
			if(block.getType() == Material.DIRT)
				block.setType(Material.GRASS);
			if(LoadProperties.enableCobbleToMossy && block.getType() == Material.COBBLESTONE)
				block.setType(Material.MOSSY_COBBLESTONE);
			}
		}
	}
	public static Boolean canBeGreenTerra(Block block){
    	int t = block.getTypeId();
    	if(t == 4 || t == 3 || t == 59 || t == 81 || t == 83 || t == 91 || t == 86 || t == 39 || t == 46 || t == 37 || t == 38){
    		return true;
    	} else {
    		return false;
    	}
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
	public static void herbalismProcCheck(Block block, Player player, BlockBreakEvent event, mcMMO plugin)
	{
		PlayerProfile PP = Users.getProfile(player);
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	
    	if(plugin.misc.blockWatchList.contains(block))
    	{
    		return;
    	}
    	if(type == 59 && block.getData() == (byte) 0x7)
    	{
    		mat = Material.getMaterial(296);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    		PP.addXP(SkillType.HERBALISM, LoadProperties.mwheat);
    		if(player != null)
    		{
	    		if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.HERBALISM))
	    		{
	    			loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    		//GREEN THUMB
    		if(Math.random() * 1500 <= PP.getSkillLevel(SkillType.HERBALISM))
    		{
    			event.setCancelled(true);
    			loc.getWorld().dropItemNaturally(loc, is);
    			//DROP SOME SEEDS
    			mat = Material.SEEDS;
    			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    			loc.getWorld().dropItemNaturally(loc, is);
    			
    			block.setData((byte) 0x1); //Change it to first stage
    			
    			//Setup the bonuses
    			int bonus = 0;
    			if(PP.getSkillLevel(SkillType.HERBALISM) >= 200)
    				bonus++;
    			if(PP.getSkillLevel(SkillType.HERBALISM) >= 400)
    				bonus++;
    			if(PP.getSkillLevel(SkillType.HERBALISM) >= 600)
    				bonus++;
    			
    			//Change wheat to be whatever stage based on the bonus
    			if(bonus == 1)
    				block.setData((byte) 0x2);
    			if(bonus == 2)
    				block.setData((byte) 0x3);
    			if(bonus == 3)
    				block.setData((byte) 0x4);
    		}
    	}
    	/*
    	 * We need to check not-wheat stuff for if it was placed by the player or not
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
		    		    	if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.HERBALISM))
		    		    	{
		    		    		loc.getWorld().dropItemNaturally(target.getLocation(), is);
		    		    	}
		    		    	PP.addXP(SkillType.HERBALISM, LoadProperties.mcactus);
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
		    		    	if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.HERBALISM))
		    		    	{
		    		    		loc.getWorld().dropItemNaturally(target.getLocation(), is);
		    		    	}
		    		    	PP.addXP(SkillType.HERBALISM, LoadProperties.msugar);
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
		    		if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.HERBALISM))
		    		{
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mpumpkin);
	    	}
    		//Mushroom
	    	if(type == 39 || type == 40)
	    	{
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null)
	    		{
		    		if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.HERBALISM))
		    		{
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mmushroom);
	    	}
	    	//Flower
	    	if(type == 37 || type == 38){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
		    		if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.HERBALISM)){
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addXP(SkillType.HERBALISM, LoadProperties.mflower);
	    	}
    	}
    	Skills.XpCheckSkill(SkillType.HERBALISM, player);
    }
	public static void breadCheck(Player player, ItemStack is)
	{
		PlayerProfile PP = Users.getProfile(player);
		int herbalism = PP.getSkillLevel(SkillType.HERBALISM);
	    if(is.getTypeId() == 297)
	    {
	    	if(herbalism >= 50 && herbalism < 150)
    		{
    			player.setHealth(player.getHealth() + 1);
    		} else if (herbalism >= 150 && herbalism < 250)
    		{
    			player.setHealth(player.getHealth() + 2);
    		} else if (herbalism >= 250 && herbalism < 350)
    		{
    			player.setHealth(player.getHealth() + 3);
    		} else if (herbalism >= 350 && herbalism < 450)
    		{
    			player.setHealth(player.getHealth() + 4);
    		} else if (herbalism >= 450 && herbalism < 550)
    		{
    			player.setHealth(player.getHealth() + 5);
    		} else if (herbalism >= 550 && herbalism < 650)
    		{
    			player.setHealth(player.getHealth() + 6);
    		} else if (herbalism >= 650 && herbalism < 750)
    		{
    			player.setHealth(player.getHealth() + 7);
    		} else if (herbalism >= 750)
    		{
    			player.setHealth(player.getHealth() + 8);
    		}
	   	}
    }
    public static void stewCheck(Player player, ItemStack is)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	int herbalism = PP.getSkillLevel(SkillType.HERBALISM);
    	if(is.getTypeId() == 282)
    	{
    		if(herbalism >= 50 && herbalism < 150)
    		{
    			player.setHealth(player.getHealth() + 1);
    		} else if (herbalism >= 150 && herbalism < 250)
    		{
    			player.setHealth(player.getHealth() + 2);
    		} else if (herbalism >= 250 && herbalism < 350)
    		{
    			player.setHealth(player.getHealth() + 3);
    		} else if (herbalism >= 350 && herbalism < 450)
    		{
    			player.setHealth(player.getHealth() + 4);
    		} else if (herbalism >= 450 && herbalism < 550)
    		{
    			player.setHealth(player.getHealth() + 5);
    		} else if (herbalism >= 550 && herbalism < 650)
    		{
    			player.setHealth(player.getHealth() + 6);
    		} else if (herbalism >= 650 && herbalism < 750)
    		{
    			player.setHealth(player.getHealth() + 7);
    		} else if (herbalism >= 750)
    		{
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
}
