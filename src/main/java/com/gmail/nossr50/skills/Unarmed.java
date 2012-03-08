package com.gmail.nossr50.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Unarmed {
	
	public static void unarmedBonus(Player attacker, EntityDamageByEntityEvent event)
	{
		int bonus = 3;
		
		//Add 1 DMG for every 50 skill levels
		bonus += Users.getProfile(attacker).getSkillLevel(SkillType.UNARMED)/50;
		
		if(bonus > 8)
		    bonus = 8;
        
		event.setDamage(event.getDamage() + bonus);
	}
	
	public static void disarmProcCheck(Player attacker, Player defender)
	{
		int skillLevel = Users.getProfile(attacker).getSkillLevel(SkillType.UNARMED);
		if(defender.getItemInHand() != null && defender.getItemInHand().getType() != Material.AIR)
		{
			if(skillLevel >= 1000)
			{
				if(Math.random() * 3000 <= 1000)
				{
	    			ItemStack item = defender.getItemInHand();
	    			defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
		    		m.mcDropItem(defender.getLocation(), item);
		    		defender.setItemInHand(null);
				}
	    	} 
			else
    		{
				if(Math.random() * 3000 <= skillLevel)
				{
	    			ItemStack item = defender.getItemInHand();
	    			defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
		    		m.mcDropItem(defender.getLocation(), item);
		    		defender.setItemInHand(null);
				}
    		}
		}
	}
}