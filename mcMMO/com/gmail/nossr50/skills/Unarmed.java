package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Unarmed {
	public static void unarmedBonus(Player attacker, EntityDamageByEntityEvent event)
	{
		PlayerProfile PPa = Users.getProfile(attacker);
		int bonus = 2;
		if (PPa.getUnarmedInt() >= 250)
			bonus++;
		if (PPa.getUnarmedInt() >= 500)
			bonus++;
		event.setDamage(event.getDamage()+bonus);
	}
	public static void disarmProcCheck(Player attacker, Player defender)
	{
		PlayerProfile PP = Users.getProfile(attacker);
		if(PP.getUnarmedInt() >= 1000){
    		if(Math.random() * 4000 <= 1000){
    			Location loc = defender.getLocation();
    			if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0)
    			{
    				attacker.sendMessage(ChatColor.DARK_RED+"You have hit with great force.");
    				defender.sendMessage(ChatColor.DARK_RED+"You have been disarmed!");
    				ItemStack item = defender.getItemInHand();
	    			if(item != null)
	    			{
	    				loc.getWorld().dropItemNaturally(loc, item);
	    				ItemStack itemx = null;
	    				defender.setItemInHand(itemx);
	    			}
    			}
    		}
    	} else {
    		if(Math.random() * 4000 <= PP.getUnarmedInt()){
    			Location loc = defender.getLocation();
    			if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0)
    			{
    				attacker.sendMessage(ChatColor.DARK_RED+"You have hit with great force.");
    				defender.sendMessage(ChatColor.DARK_RED+"You have been disarmed!");
    				ItemStack item = defender.getItemInHand();
	    			if(item != null)
	    			{
	    				loc.getWorld().dropItemNaturally(loc, item);
	    				ItemStack itemx = null;
	    				defender.setItemInHand(itemx);
    				}
    			}
    		}
    	}
	}
}
