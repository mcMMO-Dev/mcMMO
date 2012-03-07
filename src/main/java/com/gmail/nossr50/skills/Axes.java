package com.gmail.nossr50.skills;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Axes {
    public static void axesBonus(Player attacker, EntityDamageByEntityEvent event)
    {
        int bonus = 0;
        
        //Add 1 DMG for every 50 skill levels
        bonus += Users.getProfile(attacker).getSkillLevel(SkillType.AXES)/50;
        
        if(bonus > 4)
            bonus = 4;
        
        event.setDamage(event.getDamage() + bonus);
    }
	public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event)
	{
    	Entity x = event.getEntity();
    	
    	if(x instanceof Wolf){
    		Wolf wolf = (Wolf)x;
    		if(wolf.getOwner() instanceof Player)
    		{
    			Player owner = (Player) wolf.getOwner();
	    		if(owner == attacker)
	    			return;
	    		if(Party.getInstance().inSameParty(attacker, owner))
	    			return;
    		}
    	}
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(ItemChecks.isAxe(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(PPa.getSkillLevel(SkillType.AXES) >= 750){
    			if(Math.random() * 2000 <= 750 && !x.isDead()){
    				if(x instanceof Player){
    					int damage = (event.getDamage() * 2) - (event.getDamage() / 2);
    					event.setDamage(damage);
    					Player player = (Player)x;
    					player.sendMessage(mcLocale.getString("Axes.HitCritically"));
    				}
    				else {
    					int damage = event.getDamage() * 2;
        				event.setDamage(damage);
        			}
    				attacker.sendMessage(mcLocale.getString("Axes.CriticalHit"));
    			}
    		} else if(Math.random() * 2000 <= PPa.getSkillLevel(SkillType.AXES) && !x.isDead()){
    			if(x instanceof Player){
    				int damage = (event.getDamage() * 2) - (event.getDamage() / 2);
					event.setDamage(damage);
    				Player player = (Player)x;
    				player.sendMessage(mcLocale.getString("Axes.HitCritically"));
    			}
    			else {
    				int damage = event.getDamage() * 2;
    				event.setDamage(damage);
    			}
				attacker.sendMessage(mcLocale.getString("Axes.CriticalHit"));
    		}
    	}
    }
	
	public static void impact(Player attacker, LivingEntity target, EntityDamageByEntityEvent event)
	{
	    //TODO: Finish this skill, the idea is you will greatly damage an opponents armor and when they are armor less you have a proc that will stun them and deal additional damage.
	    if(target instanceof Player)
	    {
	        Player targetPlayer = (Player) target;
	        int emptySlots = 0;
	        short durDmg = 5; //Start with 5 durability dmg
	        
	        durDmg+=Users.getProfile(attacker).getSkillLevel(SkillType.AXES)/30; //Every 30 Skill Levels you gain 1 durability dmg
	        
	        for(ItemStack x : targetPlayer.getInventory().getArmorContents())
	        {
	            if(x.getType() == Material.AIR)
	            {
	                emptySlots++;
	            } else {
	                x.setDurability((short) (x.getDurability()+durDmg)); //Damage armor piece
	            }
	        }
	        
	        if(emptySlots == 4)
	            applyImpact(attacker, target, event);
	    }
	    else
	        //Since mobs are technically unarmored this will always trigger
	        applyImpact(attacker, target, event);
	}
	
	public static void applyImpact(Player attacker, LivingEntity target, EntityDamageByEntityEvent event)
	{
	    if(Math.random() * 100 > 75)
        {
            event.setDamage(event.getDamage()+2);
            target.setVelocity(attacker.getLocation().getDirection().normalize().multiply(1.5D));
            attacker.sendMessage(mcLocale.getString("Axes.GreaterImpactOnEnemy"));
        }
	}
	
}
