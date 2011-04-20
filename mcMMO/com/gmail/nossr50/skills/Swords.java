package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;

public class Swords {

	public static void bleedCheck(Player attacker, Entity x){
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(mcPermissions.getInstance().swords(attacker) && m.isSwords(attacker.getItemInHand())){
			if(PPa.getSwordsInt() >= 750){
				if(Math.random() * 1000 >= 750){
					if(!(x instanceof Player))
						Config.getInstance().addToBleedQue(x);
					if(x instanceof Player){
						Player target = (Player)x;
						Users.getProfile(target).addBleedTicks(3);
					}
					attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
				}
			} else if (Math.random() * 1000 <= PPa.getSwordsInt()){
				if(!(x instanceof Player))
					Config.getInstance().addToBleedQue(x);
				if(x instanceof Player){
					Player target = (Player)x;
					Users.getProfile(target).addBleedTicks(2);
				}
				attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
			}
		}
    }
    public static void applySerratedStrikes(Player attacker, EntityDamageByEntityEvent event){
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
    					target.damage(event.getDamage() / 4);
    					target.sendMessage(ChatColor.DARK_RED+"Struck by Serrated Strikes!");
        				Users.getProfile(target).addBleedTicks(5);
    					targets--;
    				}
    			}
    			if(derp instanceof Monster && targets >= 1){
    				if(!Config.getInstance().isBleedTracked(derp))
    					Config.getInstance().addToBleedQue(x);
    				Monster target = (Monster)derp;
    				target.damage(event.getDamage() / 4);
    				targets--;
    			}
    			if(derp instanceof Wolf){
					continue;
				}
    			if(derp instanceof Animals && targets >= 1){
    				if(!Config.getInstance().isBleedTracked(derp))
    					Config.getInstance().addToBleedQue(x);
    				Animals target = (Animals)derp;
    				target.damage(event.getDamage() / 4);
    				targets--;
    			}
    		}
    	}
    }
    public static void parryCheck(EntityDamageByEntityEvent event, Player defender){
    	Entity y = event.getDamager();
    	PlayerProfile PPd = Users.getProfile(defender);
    	if(defender != null && m.isSwords(defender.getItemInHand()) 
    			&& mcPermissions.getInstance().swords(defender)){
			if(PPd.getSwordsInt() >= 900){
				if(Math.random() * 3000 <= 900){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.GREEN+"**PARRIED**");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.GREEN+"**PARRIED**");
					}
				}
			} else {
				if(Math.random() * 3000 <= PPd.getSwordsInt()){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
				}
			}
		}
    }
    public static void counterAttackChecks(EntityDamageEvent event){
    	//Don't want to counter attack arrows
    	if(event instanceof EntityDamageByProjectileEvent)
    		return;
	    if(event instanceof EntityDamageByEntityEvent)
	    {
	    	Entity f = ((EntityDamageByEntityEvent) event).getDamager();
		   	if(event.getEntity() instanceof Player)
		   	{
		   		Player defender = (Player)event.getEntity();
		   		PlayerProfile PPd = Users.getProfile(defender);
		    		if(PPd.getSwordsInt() >= 600)
		    		{
		    			if(Math.random() * 2000 <= 600)
		    			{
			    			Combat.dealDamage(f, event.getDamage() / 2);
		    				defender.sendMessage(ChatColor.GREEN+"**COUNTER-ATTACKED**");
			    			if(f instanceof Player)
		    				((Player) f).sendMessage(ChatColor.DARK_RED+"Hit with counterattack!");
		    			}
		    		}
		    		else if (Math.random() * 2000 <= PPd.getSwordsInt())
		    		{
			    		Combat.dealDamage(f, event.getDamage() / 2);
			    		defender.sendMessage(ChatColor.GREEN+"**COUNTER-ATTACKED**");
		    			if(f instanceof Player)
		    				((Player) f).sendMessage(ChatColor.DARK_RED+"Hit with counterattack!");
		    		}
		    }
    	}
    }
    public static void bleedSimulate(){
    	
    	//Add items from Que list to BleedTrack list
    	for(Entity x : Config.getInstance().getBleedQue()){
    		Config.getInstance().addBleedTrack(x);
    	}
    	//Clear list
    	Config.getInstance().clearBleedQue();
    	
    	//Cleanup any dead entities from the list
    	for(Entity x : Config.getInstance().getBleedRemovalQue()){
    		Config.getInstance().removeBleedTrack(x);
    	}
    	
    	//Clear bleed removal list
    	Config.getInstance().clearBleedRemovalQue();
    	
    	//Bleed monsters/animals
        for(Entity x : Config.getInstance().getBleedTracked()){
        	if(x == null){
        		continue;
        	}
        	
        	if(m.getHealth(x) <= 0){
        		continue;
        	}
        	
    	    if(x instanceof Animals){
    	    	((Animals) x).damage(2);
    	    }
    	    
    	    if(x instanceof Monster){((Monster) x).damage(2);}
        }
        
    }
}
