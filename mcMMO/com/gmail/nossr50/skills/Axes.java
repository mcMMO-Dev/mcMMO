package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;

public class Axes {
	public static void skullSplitterCheck(Player player, Plugin pluginx){
    	PlayerProfile PP = Users.getProfile(player);
    	if(m.isAxes(player.getItemInHand()) && mcPermissions.getInstance().axesAbility(player)){
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode()){
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		int x = PP.getAxesInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}

    		if(!PP.getSkullSplitterMode() && Skills.cooldownOver(player, PP.getSkullSplitterDeactivatedTimeStamp(), LoadProperties.skullSplitterCooldown)){
    			player.sendMessage(ChatColor.GREEN+"**SKULL SPLITTER ACTIVATED**");
    			for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Skull Splitter!");
	    		}
    			PP.setSkullSplitterTicks(ticks * 1000);
    			PP.setSkullSplitterActivatedTimeStamp(System.currentTimeMillis());
    			PP.setSkullSplitterDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
    			PP.setSkullSplitterMode(true);
    		}
    		if(!PP.getSkullSplitterMode() && !Skills.cooldownOver(player, PP.getSkullSplitterDeactivatedTimeStamp(), LoadProperties.skullSplitterCooldown)){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+Skills.calculateTimeLeft(player, PP.getSkullSplitterDeactivatedTimeStamp(), LoadProperties.skullSplitterCooldown)+"s)");
    		}
    	}
    }
	public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event, Plugin pluginx){
    	Entity x = event.getEntity();
    	if(x instanceof Wolf){
    		Wolf wolf = (Wolf)x;
    		if(Taming.getOwner(wolf, pluginx) == attacker)
    			return;
    		if(Party.getInstance().inSameParty(attacker, Taming.getOwner(wolf, pluginx)))
    			return;
    	}
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(PPa.getAxesInt() >= 750){
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
    		} else if(Math.random() * 1000 <= PPa.getAxesInt()){
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
	
	public static void applyAoeDamage(Player attacker, EntityDamageByEntityEvent event, Plugin pluginx){
    	int targets = 0;
    	Entity x = event.getEntity();
    	targets = m.getTier(attacker);
    	for(Entity derp : x.getWorld().getEntities()){
    		if(m.getDistance(x.getLocation(), derp.getLocation()) < 5){
    			if(derp instanceof Player){
    				Player target = (Player)derp;
    				if(Party.getInstance().inSameParty(attacker, target))
    					continue;
    				if(!target.getName().equals(attacker.getName()) && targets >= 1){
    					target.damage(event.getDamage() / 2);
    					target.sendMessage(ChatColor.DARK_RED+"Struck by CLEAVE!");
    					targets--;
    				}
    			}
    			if(derp instanceof Monster  && targets >= 1){
    				Monster target = (Monster)derp;
    				target.damage(event.getDamage() / 2);
    				targets--;
    			}
    			if(derp instanceof Wolf){
					Wolf hurrDurr = (Wolf)derp;
					if(Taming.getOwner(hurrDurr, pluginx) == attacker)
						continue;
					if(Party.getInstance().inSameParty(attacker, Taming.getOwner(hurrDurr, pluginx)))
						continue;
				}
    			if(derp instanceof Animals  && targets >= 1){					
    				Animals target = (Animals)derp;
    				target.damage(event.getDamage() / 2);
    				targets--;
    			}
    		}
    	}
    }
}
