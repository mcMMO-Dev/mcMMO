package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Swords 
{
	public static void serratedStrikesActivationCheck(Player player){
    	PlayerProfile PP = Users.getProfile(player);
		if(m.isSwords(player.getItemInHand()))
		{
			if(PP.getSwordsPreparationMode())
			{
    			PP.setSwordsPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.SWORDS);
    		while(x >= 50)
    		{
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getSerratedStrikesMode() && PP.getSerratedStrikesDeactivatedTimeStamp() < System.currentTimeMillis())
	    	{
	    		player.sendMessage(mcLocale.getString("Skills.SerratedStrikesOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.SerratedStrikesPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setSerratedStrikesActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setSerratedStrikesDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
	    		PP.setSerratedStrikesMode(true);
	    	}
	    	
	    }
	}

	public static void bleedCheck(Player attacker, LivingEntity x, mcMMO pluginx)
	{
    	PlayerProfile PPa = Users.getProfile(attacker);
    	
    	if(x instanceof Wolf)
    	{
    		Wolf wolf = (Wolf)x;
    		if(Taming.getOwner(wolf, pluginx) != null)
    		{
	    		if(Taming.getOwner(wolf, pluginx) == attacker)
	    			return;
	    		if(Party.getInstance().inSameParty(attacker, Taming.getOwner(wolf, pluginx)))
	    			return;
    		}
    	}
    	if(mcPermissions.getInstance().swords(attacker) && m.isSwords(attacker.getItemInHand())){
			if(PPa.getSkillLevel(SkillType.SWORDS) >= 750)
			{
				if(Math.random() * 1000 >= 750)
				{
					if(!(x instanceof Player))
						pluginx.misc.addToBleedQue(x);
					if(x instanceof Player)
					{
						Player target = (Player)x;
						Users.getProfile(target).addBleedTicks(3);
					}
					attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
				}
			} 
			else if (Math.random() * 1000 <= PPa.getSkillLevel(SkillType.SWORDS))
			{
				if(!(x instanceof Player))
					pluginx.misc.addToBleedQue(x);
				if(x instanceof Player)
				{
					Player target = (Player)x;
					Users.getProfile(target).addBleedTicks(2);
				}
				attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
			}
		}
    }
    public static void applySerratedStrikes(Player attacker, EntityDamageByEntityEvent event, mcMMO pluginx)
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
		    				
		    				if(target.getName().equals(attacker.getName()))
		    					continue;
		    				
		    				if(Users.getProfile(target).getGodMode())
		    					continue;
		    				
		    				if(Party.getInstance().inSameParty(attacker, target))
		    					continue;
		    				if(targets >= 1 && derp.getWorld().getPVP())
		    				{
		    					target.damage(event.getDamage() / 4);
		    					target.sendMessage(ChatColor.DARK_RED+"Struck by Serrated Strikes!");
		        				Users.getProfile(target).addBleedTicks(5);
		    					targets--;
		    					continue;
		    				}
		    			} 
	    				else
		    			{
		    				if(!pluginx.misc.bleedTracker.contains(derp))
		    					pluginx.misc.addToBleedQue((LivingEntity)derp);
		    				
		    				LivingEntity target = (LivingEntity)derp;
		    				target.damage(event.getDamage() / 4);
		    				targets--;
		    			}
	    			}
	    		}
	    	}
    	}
    }
    
    public static void parryCheck(EntityDamageByEntityEvent event, Player defender)
    {
    	Entity y = event.getDamager();
    	PlayerProfile PPd = Users.getProfile(defender);
    	if(defender != null && m.isSwords(defender.getItemInHand()) 
    			&& mcPermissions.getInstance().swords(defender)){
			if(PPd.getSkillLevel(SkillType.SWORDS) >= 900)
			{
				if(Math.random() * 3000 <= 900)
				{
					event.setCancelled(true);
					defender.sendMessage(ChatColor.GREEN+"**PARRIED**");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player)
					{
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.GREEN+"**PARRIED**");
					}
				}
			} else 
			{
				if(Math.random() * 3000 <= PPd.getSkillLevel(SkillType.SWORDS))
				{
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player)
					{
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
				}
			}
		}
    }
    public static void counterAttackChecks(EntityDamageByEntityEvent event)
    {
    	//Don't want to counter attack arrows
    	
    	if(event.getDamager() instanceof Arrow)
    		return;
    	
	    if(event instanceof EntityDamageByEntityEvent)
	    {
	    	Entity f = ((EntityDamageByEntityEvent) event).getDamager();
		   	if(event.getEntity() instanceof Player)
		   	{
		   		Player defender = (Player)event.getEntity();
		   		PlayerProfile PPd = Users.getProfile(defender);
		   		if(m.isSwords(defender.getItemInHand()) && mcPermissions.getInstance().swords(defender))
		   		{
		    		if(PPd.getSkillLevel(SkillType.SWORDS) >= 600)
		    		{
		    			if(Math.random() * 2000 <= 600)
		    			{
			    			Combat.dealDamage(f, event.getDamage() / 2);
		    				defender.sendMessage(ChatColor.GREEN+"**COUNTER-ATTACKED**");
			    			if(f instanceof Player)
		    				((Player) f).sendMessage(ChatColor.DARK_RED+"Hit with counterattack!");
		    			}
		    		}
		    		else if (Math.random() * 2000 <= PPd.getSkillLevel(SkillType.SWORDS))
		    		{
			    		Combat.dealDamage(f, event.getDamage() / 2);
			    		defender.sendMessage(ChatColor.GREEN+"**COUNTER-ATTACKED**");
		    			if(f instanceof Player)
		    				((Player) f).sendMessage(ChatColor.DARK_RED+"Hit with counterattack!");
		    		}
		   		}
		    }
    	}
    }
    public static void bleedSimulate(mcMMO plugin)
    {
    	//Add items from Que list to BleedTrack list
    	
    	for(LivingEntity x : plugin.misc.bleedQue)
    	{
    		plugin.misc.bleedTracker.add(x);
    	}
    	
    	//Clear list
    	plugin.misc.bleedQue = new LivingEntity[plugin.misc.bleedQue.length];
    	plugin.misc.bleedQuePos = 0;
    	
    	//Cleanup any dead entities from the list
    	for(LivingEntity x : plugin.misc.bleedRemovalQue)
    	{
    		plugin.misc.bleedTracker.remove(x);
    	}
    	
    	//Clear bleed removal list
    	plugin.misc.bleedRemovalQue = new LivingEntity[plugin.misc.bleedRemovalQue.length];
    	plugin.misc.bleedRemovalQuePos = 0;
    	
    	//Bleed monsters/animals
        for(LivingEntity x : plugin.misc.bleedTracker)
        {
        	if(x == null){continue;}
        	
        	if(x.getHealth() <= 0)
        	{
        		plugin.misc.addToBleedRemovalQue(x);
        		continue;
        	}
        	else
        	{
        		x.damage(2);
        	}
        }
    }
}
