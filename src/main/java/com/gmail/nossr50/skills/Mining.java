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

import net.minecraft.server.Enchantment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;


public class Mining 
{	
	public static void superBreakerCheck(Player player, Block block)
	{
		PlayerProfile PP = Users.getProfile(player);
	    if(m.isMiningPick(player.getItemInHand()))
	    {
	    	if(block != null)
	    	{
		    	if(!m.abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getPickaxePreparationMode())
	    	{
    			PP.setPickaxePreparationMode(false);
    		}
	    	
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.MINING);
	    	
    		while(x >= 50)
    		{
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getSuperBreakerMode() && Skills.cooldownOver(player, PP.getSuperBreakerDeactivatedTimeStamp(), LoadProperties.superBreakerCooldown)){
	    		player.sendMessage(mcLocale.getString("Skills.SuperBreakerOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.SuperBreakerPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setSuperBreakerActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setSuperBreakerDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
	    		PP.setSuperBreakerMode(true);
	    	}
	    	
	    }
	}
	public static void blockProcSimulate(Block block)
	{
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		int id = block.getTypeId();
		
		if(id != 89 && id != 73 && id != 74 && id != 56 && id != 21 && id != 1 && id != 16) {
			m.mcDropItem(loc, item);
			return;
		}
		
		//GLOWSTONE
		if(id == 89)
		{
			mat = Material.getMaterial(348);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
		}
		//REDSTONE
		else if(id == 73 || id == 74)
		{
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5){
				m.mcDropItem(loc, item);
			}
		}
		//LAPUS
		else if(id == 21)
		{
			mat = Material.getMaterial(351);
			item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
		}
		//DIAMOND
		else if(id == 56)
		{
			mat = Material.getMaterial(264);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
		}
		//STONE
		else if(id == 1)
		{
			mat = Material.getMaterial(4);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
		}
		//COAL
		else if(id == 16)
		{
			mat = Material.getMaterial(263);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
		}
    }

    public static void blockProcCheck(Block block, Player player)
    {
    	PlayerProfile PP = Users.getProfile(player);

    	if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.MINING))
    	{
	    	blockProcSimulate(block);
			return;
    	}	
	}
    
    public static void miningBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	if(plugin.misc.blockWatchList.contains(block) || block.getData() == (byte) 5)
    		return;
    	int xp = 0;
		int id = block.getTypeId();
		
		switch (id) {
			//STONE
			case 1: 
				xp += LoadProperties.mstone;
				break;
			//SANDSTONE
			case 24:
				xp += LoadProperties.msandstone;
				break;
			//OBSIDIAN
			case 49:
				xp += LoadProperties.mobsidian;
				break;
			//NETHERRACK
			case 87:
				xp += LoadProperties.mnetherrack;
				break;
			//GLOWSTONE
			case 89:
				xp += LoadProperties.mglowstone;
				break;
			//COAL
			case 16:
				xp += LoadProperties.mcoal;
				break;
			//GOLD
			case 14:
				xp += LoadProperties.mgold;
				break;
			//DIAMOND
			case 56:
				xp += LoadProperties.mdiamond;
				break;
			//IRON
			case 15:
				xp += LoadProperties.miron;
				break;
			//REDSTONE
			case 73:
				xp += LoadProperties.mredstone;
				break;
			case 74:
				xp += LoadProperties.mredstone;
				break;
			//LAPIS
			case 21:
				xp += LoadProperties.mlapis;
				break;
		}
		
    	blockProcCheck(block, player);
    	PP.addXP(SkillType.MINING, xp, player);
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
    
    /*
     * Handling SuperBreaker stuff
     */
    public static Boolean canBeSuperBroken(Block block)
    {
    	int t = block.getTypeId();
    	if(t == 49 || t == 87 || t == 89 || t == 73 || t == 74 || t == 56 || t == 21 || t == 1 || t == 16 || t == 14 || t == 15 || t == 112)
    		return true;
    	else
    		return false;
    }
    
    public static void SuperBreakerBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	if(LoadProperties.toolsLoseDurabilityFromAbilities)
    	{
    		if(player.getItemInHand().getEnchantments().containsKey(Enchantment.DURABILITY))
    		{
    			
    		}
    		m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    	}
    	
    	//Pre-processing
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
    	int xp = 0;
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		int id = block.getTypeId();
		
		
    	if(id == 1 || id == 24)
    	{
    		if(id == 1)
    		{
    			mat = Material.COBBLESTONE;
    			if(!plugin.misc.blockWatchList.contains(block) && block.getData() != (byte) 5)
        		{
        			xp += LoadProperties.mstone;
        			blockProcCheck(block, player);
        			blockProcCheck(block, player);
        		}
    		} else 
    		{
    			mat = Material.SANDSTONE;
    			if(!plugin.misc.blockWatchList.contains(block) && block.getData() != (byte) 5)
        		{
        			xp += LoadProperties.msandstone;
        			blockProcCheck(block, player);
        			blockProcCheck(block, player);
        		}
    		}
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//NETHERRACK
    	else if(id == 87)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mnetherrack;
    			blockProcCheck(block, player);
    			blockProcCheck(block, player);
    		}
    		mat = Material.getMaterial(87);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//GLOWSTONE
    	else if(id == 89)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mglowstone;
    			blockProcCheck(block, player);
    			blockProcCheck(block, player);
    		}
    		mat = Material.getMaterial(348);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//COAL
    	else if(id == 16)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mcoal;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        		}
    		mat = Material.getMaterial(263);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//GOLD
    	else if(id == 14 && m.getTier(player) >= 3)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mgold;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        		}
    		item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//OBSIDIAN
    	else if(id == 49 && m.getTier(player) >= 4)
    	{
    		if(LoadProperties.toolsLoseDurabilityFromAbilities)
        		m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mobsidian;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(49);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//DIAMOND
    	else if(id == 56 && m.getTier(player) >= 3)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mdiamond;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(264);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//IRON
    	else if(id == 15 && m.getTier(player) >= 2)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.miron;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//REDSTONE
    	else if((id == 73 || id == 74) && m.getTier(player) >= 3)
    	{
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5)
    		{
    			xp += LoadProperties.mredstone;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
			{
				m.mcDropItem(loc, item);
			}
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	//LAPUS
    	else if(id == 21 && m.getTier(player) >= 3){
    		if(!plugin.misc.blockWatchList.contains(block)&& block.getData() != (byte) 5){
    			xp += LoadProperties.mlapis;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(351);
			item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
    		block.setType(Material.AIR);
    	}
    	if(block.getData() != (byte) 5)
    		PP.addXP(SkillType.MINING, xp, player);
    	if(LoadProperties.spoutEnabled)
    		SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
}
