package com.gmail.nossr50.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;


public class Acrobatics {
	public static void acrobaticsCheck(Player player, EntityDamageEvent event)
	{
		PlayerProfile PP = Users.getProfile(player);
		int acrovar = PP.getSkillLevel(SkillType.ACROBATICS);
		
		if(player.isSneaking())
			acrovar = acrovar * 2;
		
		if(Math.random() * 1000 <= acrovar)
		{
			int threshold = 7;
			
			if(player.isSneaking())
				threshold = 14;
			
			int newDamage = event.getDamage() - threshold;
			
			if(newDamage < 0)
				newDamage = 0;
			
			/*
			 * Check for death
			 */
			if(player.getHealth() - newDamage >= 1)
			{
				PP.addXP(SkillType.ACROBATICS, (event.getDamage() * 8)*10, player);
				Skills.XpCheckSkill(SkillType.ACROBATICS, player);
				event.setDamage(newDamage);
				if(event.getDamage() <= 0)
					event.setCancelled(true);
				if(player.isSneaking()){
					player.sendMessage(mcLocale.getString("Acrobatics.GracefulRoll"));
				} else {
					player.sendMessage(mcLocale.getString("Acrobatics.Roll"));
				}
			}
		} 
		else if(player.getHealth() - event.getDamage() >= 1)
		{
			PP.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*10, player);
			Skills.XpCheckSkill(SkillType.ACROBATICS, player);
		}
    }
	public static void dodgeChecks(EntityDamageByEntityEvent event){
		Player defender = (Player) event.getEntity();
		PlayerProfile PPd = Users.getProfile(defender);
		
		if(mcPermissions.getInstance().acrobatics(defender)){
			if(PPd.getSkillLevel(SkillType.ACROBATICS) <= 800){
	    		if(Math.random() * 4000 <= PPd.getSkillLevel(SkillType.ACROBATICS)){
	    			defender.sendMessage(mcLocale.getString("Acrobatics.Dodge"));
	    			if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
	    				PPd.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*1, defender);
	    				Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
	    			}
	    			event.setDamage(event.getDamage() / 2);
	    			//Needs to do minimal damage
	    			if(event.getDamage() <= 0)
	    				event.setDamage(1);
	    		}
			} else if(Math.random() * 4000 <= 800) {
				defender.sendMessage(mcLocale.getString("Acrobatics.Dodge"));
				if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
					PPd.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*10, defender);
					Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
				}
				event.setDamage(event.getDamage() / 2);
				//Needs to deal minimal damage
				if(event.getDamage() <= 0)
					event.setDamage(1);
			}
		}
	}
	
}
