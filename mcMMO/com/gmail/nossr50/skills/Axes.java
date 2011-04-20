package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.m;
import com.gmail.nossr50.party.Party;

public class Axes {
	public static void applyAoeDamage(Player attacker, EntityDamageByEntityEvent event){
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
