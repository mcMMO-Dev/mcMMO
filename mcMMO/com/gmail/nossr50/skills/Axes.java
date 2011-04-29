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
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;

public class Axes {
	
	public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event){
    	Entity x = event.getEntity();
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
