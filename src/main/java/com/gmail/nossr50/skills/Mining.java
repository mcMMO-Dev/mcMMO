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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerAnimationEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.AbilityType;
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
    		
	    	if(!PP.getSuperBreakerMode() && Skills.cooldownOver(player, PP.getSkillDATS(AbilityType.SUPER_BREAKER), LoadProperties.superBreakerCooldown)){
	    		player.sendMessage(mcLocale.getString("Skills.SuperBreakerOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.SuperBreakerPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setSkillDATS(AbilityType.SUPER_BREAKER, System.currentTimeMillis()+(ticks*1000));
	    		PP.setSuperBreakerMode(true);
	    	}
	    	
	    }
	}
	public static void blockProcSimulate(Block block, Player player)
	{
    	Location loc = block.getLocation();
    	int id = block.getTypeId();
    	Material mat = Material.getMaterial(id);
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		
		if(id != 89 && id != 73 && id != 74 && id != 56 && id != 21 && id != 1 && id != 16 && id != 112 && id != 121 && id != 48) {
			m.mcDropItem(loc, item);
			return;
		}
		
		//Drop natural block with Silk Touch
		if(player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
			m.mcDropItem(loc, item);
			return;
		}
			
		switch (id){
			//GLOWSTONE
			case 89:
				mat = Material.getMaterial(348);
				item = new ItemStack(mat, 1, (byte)0, damage);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				break;
			//REDSTONE
			case 73:
				mat = Material.getMaterial(331);
				item = new ItemStack(mat, 1, (byte)0, damage);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				break;
			case 74:
				mat = Material.getMaterial(331);
				item = new ItemStack(mat, 1, (byte)0, damage);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				break;
			//LAPIS
			case 21:
				mat = Material.getMaterial(351);
				item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				if(Math.random() * 10 > 5)
					m.mcDropItem(loc, item);
				break;
			//DIAMOND
			case 56:
				mat = Material.getMaterial(264);
				item = new ItemStack(mat, 1, (byte)0, damage);
				m.mcDropItem(loc, item);
				break;
			//STONE
			case 1:
				mat = Material.getMaterial(4);
				item = new ItemStack(mat, 1, (byte)0, damage);
				m.mcDropItem(loc, item);
				break;
			//COAL
			case 16:
				mat = Material.getMaterial(263);
				item = new ItemStack(mat, 1, (byte)0, damage);
				m.mcDropItem(loc, item);
				break;
		}
    }

    public static void blockProcCheck(Block block, Player player)
    {
    	PlayerProfile PP = Users.getProfile(player);

    	if(PP.getSkillLevel(SkillType.MINING) > 1000 || (Math.random() * 1000 <= PP.getSkillLevel(SkillType.MINING))) 
    	{
	    	blockProcSimulate(block, player);
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
			//NETHER BRICK
			case 112:
				xp += LoadProperties.mnetherbrick;
				break;
			//END STONE
			case 121:
				xp += LoadProperties.mendstone;
				break;
			//MOSS STONE
			case 48:
				xp += LoadProperties.mmossstone;
				break;
			//STONE BRICK
			case 98:
				xp += LoadProperties.mstonebrick;
				break;
		}
		
		if(canBeSuperBroken(block))
			blockProcCheck(block, player);
    	PP.addXP(SkillType.MINING, xp, player);
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
    
    /*
     * Handling SuperBreaker stuff
     */
    public static Boolean canBeSuperBroken(Block block)
    {
    	int id = block.getTypeId();
    	return id == 1 || id == 14 || id == 15 || id == 16 || id == 21 || id == 24 || id == 49 || id == 56 || id == 73 || id == 74 || id == 87 || id == 89 || id == 112 || id == 121 || id == 48 || id == 98;
    }
    
    public static void SuperBreakerBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	if(LoadProperties.toolsLoseDurabilityFromAbilities)
    	{
    		if(!player.getItemInHand().containsEnchantment(Enchantment.DURABILITY))
    			m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    	}
    	
    	//Pre-processing
    	int id = block.getTypeId();
    	int xp = 0;
		PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
		
		//STONE
    	if(id == 1 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mstone;
    		blockProcCheck(block, player);
    		blockProcCheck(block, player);
    	}
    	//SANDSTONE
    	else if(id == 24 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.msandstone;
   			blockProcCheck(block, player);
   			blockProcCheck(block, player);
    	}
    	//NETHERRACK
    	else if(id == 87 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mnetherrack;
   			blockProcCheck(block, player);
   			blockProcCheck(block, player);
    	}
    	//GLOWSTONE
    	else if(id == 89 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mglowstone;
    		blockProcCheck(block, player);
    		blockProcCheck(block, player); 
    	}
    	//COAL
    	else if(id == 16 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mcoal;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//GOLD
    	else if(id == 14 && m.getTier(player) >= 3 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mgold;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//OBSIDIAN
    	else if(id == 49 && m.getTier(player) >= 4 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mobsidian;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//DIAMOND
    	else if(id == 56 && m.getTier(player) >= 3 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mdiamond;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//IRON
    	else if(id == 15 && m.getTier(player) >= 2 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.miron;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//REDSTONE
    	else if((id == 73 || id == 74) && m.getTier(player) >= 3 && !plugin.misc.blockWatchList.contains(block))
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mredstone;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//LAPIS
    	else if(id == 21 && m.getTier(player) >= 3 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mlapis;
       		blockProcCheck(block, player);
      		blockProcCheck(block, player);
    	}
    	//NETHER BRICK
    	else if(id == 112 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mnetherbrick;
       		blockProcCheck(block, player);
       		blockProcCheck(block, player);
    	}
    	//END STONE
    	else if(id == 121 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mendstone;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	//MOSS STONE
    	else if(id == 48 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mmossstone;
       		blockProcCheck(block, player);
       		blockProcCheck(block, player);
    	}
    	//STONE BRICK
    	else if(id == 98 && !plugin.misc.blockWatchList.contains(block))
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mstonebrick;
       		blockProcCheck(block, player);
       		blockProcCheck(block, player);
    	}
    	
    	if(!plugin.misc.blockWatchList.contains(block) && block.getData() != (byte) 5)
    		PP.addXP(SkillType.MINING, xp, player);
    	if(LoadProperties.spoutEnabled)
    		SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
}
