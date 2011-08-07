package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.party.Party;

public class Axes {
	public static void skullSplitterCheck(Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	if(m.isAxes(player.getItemInHand()) && mcPermissions.getInstance().axesAbility(player)){
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode())
    		{
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		int x = PP.getSkillLevel(SkillType.AXES);
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}

    		if(!PP.getSkullSplitterMode() && Skills.cooldownOver(player, (PP.getSkullSplitterDeactivatedTimeStamp()*1000), LoadProperties.skullSplitterCooldown))
    		{
    			player.sendMessage(mcLocale.getString("Skills.SkullSplitterOn"));
    			for(Player y : player.getWorld().getPlayers()){
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.SkullSplitterPlayer", new Object[] {player.getName()}));
	    		}
    			PP.setSkullSplitterActivatedTimeStamp(System.currentTimeMillis());
    			PP.setSkullSplitterDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
    			PP.setSkullSplitterMode(true);
    		}
    		if(!PP.getSkullSplitterMode() && !Skills.cooldownOver(player, (PP.getSkullSplitterDeactivatedTimeStamp()*1000), LoadProperties.skullSplitterCooldown)){
    			player.sendMessage(mcLocale.getString("Skills.TooTired")
    					+ChatColor.YELLOW+" ("+Skills.calculateTimeLeft(player, (PP.getSkullSplitterDeactivatedTimeStamp()*1000), LoadProperties.skullSplitterCooldown)+"s)");
    		}
    	}
    }
	public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event, Plugin pluginx){
    	Entity x = event.getEntity();
    	if(x instanceof Wolf){
    		Wolf wolf = (Wolf)x;
    		if(Taming.getOwner(wolf, pluginx) != null)
    		{
	    		if(Taming.getOwner(wolf, pluginx) == attacker)
	    			return;
	    		if(Party.getInstance().inSameParty(attacker, Taming.getOwner(wolf, pluginx)))
	    			return;
    		}
    	}
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(PPa.getSkillLevel(SkillType.AXES) >= 750){
    			if(Math.random() * 1000 <= 750){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    				if(x instanceof Player){
        				event.setDamage(event.getDamage() * 2 - event.getDamage() / 2);
        			} else {
        				event.setDamage(event.getDamage() * 2);
        			}
    				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    			}
    		} else if(Math.random() * 1000 <= PPa.getSkillLevel(SkillType.AXES)){
    			if(x instanceof Player){
    				Player player = (Player)x;
    				player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    			}
    			if(x instanceof Player){
    				event.setDamage(event.getDamage() * 2 - event.getDamage() / 2);
    			} else {
    				event.setDamage(event.getDamage() * 2);
    			}
				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    		}
    	}
    }
	
	public static void applyAoeDamage(Player attacker, EntityDamageByEntityEvent event, Plugin pluginx)
	{
		int targets = 0;
    	
    	if(event.getEntity() instanceof LivingEntity)
    	{
    		LivingEntity x = (LivingEntity) event.getEntity();
	    	targets = m.getTier(attacker);
	    	
	    	for(Entity derp : x.getWorld().getEntities())
	    	{
	    		if(m.getDistance(x.getLocation(), derp.getLocation()) < 5)
	    		{
	    			
	    			
	    			//Make sure the Wolf is not friendly
	    			if(derp instanceof Wolf)
	    			{
						Wolf hurrDurr = (Wolf)derp;
						if(Taming.getOwner(hurrDurr, pluginx) == attacker)
							continue;
						if(Party.getInstance().inSameParty(attacker, Taming.getOwner(hurrDurr, pluginx)))
							continue;
					}
	    			//Damage nearby LivingEntities
	    			if(derp instanceof LivingEntity && targets >= 1)
	    			{
	    				if(derp instanceof Player)
		    			{
		    				Player target = (Player)derp;
		    				
		    				if(Users.getProfile(target).getGodMode())
		    					continue;

		    				if(target.getName().equals(attacker.getName()))
		    					continue;
		    				
		    				if(Party.getInstance().inSameParty(attacker, target))
		    					continue;
		    				if(targets >= 1 && derp.getWorld().getPVP())
		    				{
		    					target.damage(event.getDamage() / 2);
		    					target.sendMessage(ChatColor.DARK_RED+"Struck by CLEAVE!");
		    					targets--;
		    					continue;
		    				}
		    			} 
	    				else
		    			{			
		    				LivingEntity target = (LivingEntity)derp;
		    				target.damage(event.getDamage() / 2);
		    				targets--;
		    			}
	    			}
	    		}
	    	}
    	}
    }
}
