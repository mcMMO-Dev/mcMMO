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
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Statistic;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.spout.SpoutStuff;
import org.getspout.spoutapi.sound.SoundEffect;

public class Unarmed {
	public static void berserkActivationCheck(Player player)
	{
    	PlayerProfile PP = Users.getProfile(player);
		if(player.getItemInHand().getTypeId() == 0)
		{
			if(PP.getFistsPreparationMode())
			{
    			PP.setFistsPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.UNARMED);
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getBerserkMode() && Skills.cooldownOver(player, PP.getBerserkDeactivatedTimeStamp(), LoadProperties.berserkCooldown))
	    	{
	    		player.sendMessage(mcLocale.getString("Skills.BerserkOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.BerserkPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setBerserkActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setBerserkDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
	    		PP.setBerserkMode(true);
	    	}
	    }
	}
	public static void unarmedBonus(Player attacker, EntityDamageByEntityEvent event)
	{
		PlayerProfile PPa = Users.getProfile(attacker);
		int bonus = 0;
		if (PPa.getSkillLevel(SkillType.UNARMED) >= 250)
			bonus+=2;
		if (PPa.getSkillLevel(SkillType.UNARMED) >= 500)
			bonus+=4;
		event.setDamage(event.getDamage()+bonus);
	}
	public static void disarmProcCheck(Player attacker, Player defender)
	{
		PlayerProfile PP = Users.getProfile(attacker);
		if(attacker.getItemInHand().getTypeId() == 0)
		{
			if(PP.getSkillLevel(SkillType.UNARMED) >= 1000)
			{
	    		if(Math.random() * 4000 <= 1000)
	    		{
	    			Location loc = defender.getLocation();
	    			if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0)
	    			{
	    				defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
	    				ItemStack item = defender.getItemInHand();
		    			if(item != null)
		    			{
		    				m.mcDropItem(loc, item);
		    				ItemStack itemx = null;
		    				defender.setItemInHand(itemx);
		    			}
	    			}
	    		}
	    	} else {
	    		if(Math.random() * 4000 <= PP.getSkillLevel(SkillType.UNARMED)){
	    			Location loc = defender.getLocation();
	    			if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0)
	    			{
	    				defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
	    				ItemStack item = defender.getItemInHand();
		    			if(item != null)
		    			{
		    				m.mcDropItem(loc, item);
		    				ItemStack itemx = null;
		    				defender.setItemInHand(itemx);
	    				}
	    			}
	    		}
	    	}
		}
	}
	
	public static void berserk(Player player, Block block){
		
		Material mat = Material.getMaterial(block.getTypeId());
		byte damage = 0;
		
	   	if(block.getTypeId() == 2 || block.getTypeId() == 110)
	   		mat = Material.DIRT;
	   	if(block.getTypeId() == 78)
	   		mat = Material.SNOW_BALL;
	   	if(block.getTypeId() == 82)
	   		mat = Material.CLAY_BALL;
		
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
		
//		block.setType(Material.AIR);
		
		if(item.getType() == Material.CLAY_BALL)
		{
			m.mcDropItem(block.getLocation(), item);
			m.mcDropItem(block.getLocation(), item);
			m.mcDropItem(block.getLocation(), item);
			m.mcDropItem(block.getLocation(), item);
		} else
		{
			m.mcDropItem(block.getLocation(), item);
		}
		
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
	}
}
