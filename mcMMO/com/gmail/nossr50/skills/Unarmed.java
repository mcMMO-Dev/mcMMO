package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Unarmed {
	public static void berserkActivationCheck(Player player, Plugin pluginx){
    	PlayerProfile PP = Users.getProfile(player);
		if(player.getItemInHand().getTypeId() == 0){
			if(PP.getFistsPreparationMode()){
    			PP.setFistsPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getUnarmedInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getBerserkMode() && Skills.cooldownOver(player, PP.getBerserkDeactivatedTimeStamp(), LoadProperties.berserkCooldown)){
	    		player.sendMessage(ChatColor.GREEN+"**BERSERK ACTIVATED**");
	    		for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Berserk!");
	    		}
	    		PP.setBerserkTicks(ticks * 1000);
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
		if (PPa.getUnarmedInt() >= 250)
			bonus+=2;
		if (PPa.getUnarmedInt() >= 500)
			bonus+=2;
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
