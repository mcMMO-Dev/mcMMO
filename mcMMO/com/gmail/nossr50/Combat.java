package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Axes;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;

public class Combat {
	private static mcMMO plugin;
	public Combat(mcMMO instance) {
    	plugin = instance;
    }
	public static void combatChecks(EntityDamageEvent event){
		if(event.isCancelled() || event.getDamage() == 0)
			return;
		/*
		 * OFFENSIVE CHECKS
		 */
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player)
		{
			//Declare Things
			EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			Player attacker = (Player)((EntityDamageByEntityEvent) event).getDamager();
			PlayerProfile PPa = Users.getProfile(attacker);
			
			//If there are any abilities to activate
	    	combatAbilityChecks(attacker, PPa);
	    	
	    	//Modify the event damage if Attacker is Berserk
	    	if(PPa.getBerserkMode())
	    		event.setDamage(event.getDamage() + (event.getDamage() / 2));
	    	//Handle the combat interactions between the Player and the defending entity
	    	if(event.getEntity() instanceof Player)
	    		playerVersusPlayerChecks(eventb, attacker);
	    	if(event.getEntity() instanceof Animals)
	    		playerVersusAnimalsChecks(eventb, attacker);
	    	if(event.getEntity() instanceof Monster)
	    		playerVersusMonsterChecks(eventb, attacker);
	   		if(event.getEntity() instanceof Squid)
	   			playerVersusSquidChecks(eventb, attacker);
       	
	   		//Handle Ability Interactions
	   		if(PPa.getSkullSplitterMode() && m.isAxes(attacker.getItemInHand()))
       			Axes.applyAoeDamage(attacker, eventb);
      		if(PPa.getSerratedStrikesMode() && m.isSwords(attacker.getItemInHand()))
       			Swords.applySerratedStrikes(attacker, eventb);
		}
		//Another offensive check for Archery
		if(event instanceof EntityDamageByProjectileEvent)
			archeryCheck((EntityDamageByProjectileEvent) event);
			
		/*
		 * DEFENSIVE CHECKS
		 */
		if(event instanceof EntityDamageByEntityEvent && event.getEntity() instanceof Player)
		{
			Player defender = (Player)event.getEntity();
			Swords.parryCheck((EntityDamageByEntityEvent) event, defender);
			Swords.counterAttackChecks(event);
			Acrobatics.dodgeChecks((EntityDamageByEntityEvent)event);
		}
	}
	
	public static void combatAbilityChecks(Player attacker, PlayerProfile PPa){
		//Check to see if any abilities need to be activated
		if(PPa.getAxePreparationMode())
			Skills.skullSplitterCheck(attacker, plugin);
		if(PPa.getSwordsPreparationMode())
			Skills.serratedStrikesActivationCheck(attacker, plugin);
		if(PPa.getFistsPreparationMode())
			Skills.berserkActivationCheck(attacker, plugin);
	}
	public static void playerVersusPlayerChecks(EntityDamageByEntityEvent event, Player attacker){
		Entity x = event.getEntity();
    	if(x instanceof Player){
    		if(LoadProperties.pvp == false){
    			event.setCancelled(true);
    			return;
    		}
    		PlayerProfile PPa = Users.getProfile(attacker);
    		Player defender = (Player)x;
    		PlayerProfile PPd = Users.getProfile(defender);
    		
    		/*
    		 * COMPATABILITY CHECKS (Stuff that wouldn't happen normally in  basically...)
    		 */
    		if(Users.getProfile(defender) == null)
    			Users.addUser(defender);
    		if(attacker != null && defender != null && Users.getProfile(attacker).inParty() && Users.getProfile(defender).inParty()){
				if(Party.getInstance().inSameParty(defender, attacker)){
					event.setCancelled(true);
					return;
				}
    		}
    		/*
    		 * AXE CRITICAL CHECK
    		 */
    		axeCriticalCheck(attacker, event, x);
    		if(!Config.getInstance().isBleedTracked(x)){
    			Swords.bleedCheck(attacker, x);
    		}
			if(defender != null && mcPermissions.getInstance().unarmed(attacker) && attacker.getItemInHand().getTypeId() == 0){
				
				//Bonus just for having unarmed
				int bonus = 2;
				if (PPa.getUnarmedInt() >= 250)
					bonus++;
				if (PPa.getUnarmedInt() >= 500)
					bonus++;
				event.setDamage(calculateDamage(event, bonus));
				
				//PROC
				if(simulateUnarmedProc(attacker)){
					Location loc = defender.getLocation();
					if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0){
						attacker.sendMessage(ChatColor.DARK_RED+"You have hit with great force.");
						defender.sendMessage(ChatColor.DARK_RED+"You have been disarmed!");
						ItemStack item = defender.getItemInHand();
					if(item != null){
						loc.getWorld().dropItemNaturally(loc, item);
						ItemStack itemx = null;
						defender.setItemInHand(itemx);
						}
					}
				}
			}
			/*
    		 * PVP XP
    		 */
    		if(attacker != null && defender != null && LoadProperties.pvpxp){
    			if(PPd.inParty() && PPa.inParty() && Party.getInstance().inSameParty(attacker, defender))
    				return;
    			if(System.currentTimeMillis() >= PPd.getRespawnATS() + 5000 && defender.getHealth() >= 1){
	    			if(m.isAxes(attacker.getItemInHand()))
	    				PPa.addAxesXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
	    			if(m.isSwords(attacker.getItemInHand()))
	    				PPa.addSwordsXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
	    			if(attacker.getItemInHand().getTypeId() == 0)
	    				PPa.addUnarmedXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
    			}
    		}
    		/*
    		 * CHECK FOR LEVEL UPS
    		 */
    		Skills.XpCheck(attacker);
		}
    }
    public static void playerVersusSquidChecks(EntityDamageByEntityEvent event, Player attacker){
    	Entity x = event.getEntity();
    	PlayerProfile PPa = Users.getProfile(attacker);
    	int type = attacker.getItemInHand().getTypeId();
    	if(x instanceof Squid){
    		if(!Config.getInstance().isBleedTracked(x)){
    			Swords.bleedCheck(attacker, x);
    		}
			Squid defender = (Squid)event.getEntity();
			if(m.isSwords(attacker.getItemInHand()) && defender.getHealth() > 0 && mcPermissions.getInstance().swords(attacker)){
					PPa.addSwordsXP(10 * LoadProperties.xpGainMultiplier);
			}
			Skills.XpCheck(attacker);
			if(m.isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					PPa.addAxesXP(10 * LoadProperties.xpGainMultiplier);
					Skills.XpCheck(attacker);
			}
			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(PPa.getAxesInt() >= 500){
					event.setDamage(calculateDamage(event, 4));
				}
			}
			/*
			 * UNARMED VS SQUID
			 */
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
    			if(defender.getHealth() <= 0)
    				return;
    			
    			//Bonus just for having unarmed
    			int bonus = 2;
    			if (PPa.getUnarmedInt() >= 250)
    				bonus++;
    			if (PPa.getUnarmedInt() >= 500)
    				bonus++;
    			event.setDamage(calculateDamage(event, bonus));
    			
    			//XP
					if(defender.getHealth() != 0){
					PPa.addUnarmedXP(10 * LoadProperties.xpGainMultiplier);
					Skills.XpCheck(attacker);
					}
    			}
		}
    }
    public static void playerVersusAnimalsChecks(EntityDamageByEntityEvent event, Player attacker){
    	int type = attacker.getItemInHand().getTypeId();
    	Entity x = event.getEntity();
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(x instanceof Animals){
    		if(!Config.getInstance().isBleedTracked(x)){
    			Swords.bleedCheck(attacker, x);
    		}
			Animals defender = (Animals)event.getEntity();
    		if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(defender.getHealth() <= 0)
					return;
				if(PPa.getAxesInt() >= 500){
					event.setDamage(calculateDamage(event, 4));
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
				//Bonus just for having unarmed
				int bonus = 2;
				if (PPa.getUnarmedInt() >= 250)
					bonus++;
				if (PPa.getUnarmedInt() >= 500)
					bonus++;
				event.setDamage(calculateDamage(event, bonus));
			}
		}
    }
    public static void playerVersusMonsterChecks(EntityDamageByEntityEvent event, Player attacker){
    	Entity x = event.getEntity();
    	int type = attacker.getItemInHand().getTypeId();
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(x instanceof Monster){
    		/*
    		 * AXE PROC CHECKS
    		 */
    		axeCriticalCheck(attacker, event, x);
    		if(!Config.getInstance().isBleedTracked(x)){
    			Swords.bleedCheck(attacker, x);
    		}
			Monster defender = (Monster)event.getEntity();
			if(m.isSwords(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().swords(attacker)){
					if(!Config.getInstance().isMobSpawnTracked(x)){
					if(x instanceof Creeper)
						PPa.addSwordsXP((event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addSwordsXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addSwordsXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addSwordsXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addSwordsXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
					}
					Skills.XpCheck(attacker);
				}
			if(m.isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					if(!Config.getInstance().isMobSpawnTracked(x)){
					if(x instanceof Creeper)
					PPa.addAxesXP((event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addAxesXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addAxesXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addAxesXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addAxesXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
					}
					Skills.XpCheck(attacker);
			}
			/*
			 * AXE DAMAGE SCALING && LOOT CHECKS
			 */
			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(PPa.getAxesInt() >= 500){
					event.setDamage(calculateDamage(event, 4));
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
			if(defender.getHealth() <= 0)
				return;
			
			//Bonus just for having unarmed
			int bonus = 2;
			if (PPa.getUnarmedInt() >= 250)
				bonus++;
			if (PPa.getUnarmedInt() >= 500)
				bonus++;
			event.setDamage(calculateDamage(event, bonus));
			
			//XP
			if(!Config.getInstance().isMobSpawnTracked(x)){
			if(x instanceof Creeper)
				PPa.addUnarmedXP((event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
			if(x instanceof Spider)
				PPa.addUnarmedXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
			if(x instanceof Skeleton)
				PPa.addUnarmedXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
			if(x instanceof Zombie)
				PPa.addUnarmedXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
			if(x instanceof PigZombie)
				PPa.addUnarmedXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
			}
			Skills.XpCheck(attacker);
			}
		}
    }
	public static void archeryCheck(EntityDamageByProjectileEvent event){
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	if(event.getProjectile().toString().equals("CraftArrow") && x instanceof Player){
    		Player defender = (Player)x;
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(PPd == null)
    			Users.addUser(defender);
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0){
	    		if(defender != null && PPd.getUnarmedInt() >= 1000){
	    			if(Math.random() * 1000 <= 500){
	    				event.setCancelled(true);
	    				defender.sendMessage(ChatColor.WHITE+"**ARROW DEFLECT**");
	    				return;
	    			}
	    		} else if(defender != null && Math.random() * 1000 <= (PPd.getUnarmedInt() / 2)){
	    			event.setCancelled(true);
	    			defender.sendMessage(ChatColor.WHITE+"**ARROW DEFLECT**");
	    			return;
	    		}
    		}
    	}
    	/*
    	 * If attacker is player
    	 */
    	if(y instanceof Player){
    		Player attacker = (Player)y;
    		PlayerProfile PPa = Users.getProfile(attacker);
    		if(event.getProjectile().toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker)){
    			if(!Config.getInstance().isTracked(x) && event.getDamage() > 0){
    				Config.getInstance().addArrowTrack(x, 0);
    				if(attacker != null){
    					if(Math.random() * 1000 <= PPa.getArcheryInt()){
    						Config.getInstance().addArrowCount(x, 1);
    					}
    				}
    			} else {
    				if(event.getDamage() > 0){
    					if(attacker != null){
        					if(Math.random() * 1000 <= PPa.getArcheryInt()){
        						Config.getInstance().addArrowCount(x, 1);
        					}
        				}
    				}
    			}
    			/*
    			 * IGNITION
    			 */
    			if(Math.random() * 100 >= 75){
    				
    				int ignition = 20;	
    				if(PPa.getArcheryInt() >= 200)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 400)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 600)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 800)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 1000)
    					ignition+=20;
    				
        			if(x instanceof Player){
        				Player Defender = (Player)x;
        				if(!Party.getInstance().inSameParty(attacker, Defender)){
        					event.getEntity().setFireTicks(ignition);
        					attacker.sendMessage(ChatColor.RED+"**IGNITION**");
        					Defender.sendMessage(ChatColor.DARK_RED+"You were struck by a burning arrow!");
        				}
        			} else {
        			event.getEntity().setFireTicks(ignition);
        			attacker.sendMessage(ChatColor.RED+"**IGNITION**");
        			}
        		}
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			/*
    			 * TRACK ARROWS USED AGAINST THE ENTITY
    			 */
    			if(PPa.getArcheryInt() >= 50 && PPa.getArcheryInt() < 250)
    				event.setDamage(calculateDamage(event, 1));
    			if(PPa.getArcheryInt() >= 250 && PPa.getArcheryInt() < 575)
    				event.setDamage(calculateDamage(event, 2));
    			if(PPa.getArcheryInt() >= 575 && PPa.getArcheryInt() < 725)
    				event.setDamage(calculateDamage(event, 3));
    			if(PPa.getArcheryInt() >= 725 && PPa.getArcheryInt() < 1000)
    				event.setDamage(calculateDamage(event, 4));
    			if(PPa.getArcheryInt() >= 1000)
    				event.setDamage(calculateDamage(event, 5));
    			//XP
    			if(!Config.getInstance().isMobSpawnTracked(x)){
    				if(x instanceof Creeper)
					PPa.addArcheryXP((event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addArcheryXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addArcheryXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
    			}
    		}
    		/*
    		 * Defender is Animals	
    		 */
    		if(x instanceof Animals){
    			if(PPa.getArcheryInt() >= 50 && PPa.getArcheryInt() < 250)
    				event.setDamage(calculateDamage(event, 1));
    			if(PPa.getArcheryInt() >= 250 && PPa.getArcheryInt() < 575)
    				event.setDamage(calculateDamage(event, 2));
    			if(PPa.getArcheryInt() >= 575 && PPa.getArcheryInt() < 725)
    				event.setDamage(calculateDamage(event, 3));
    			if(PPa.getArcheryInt() >= 725 && PPa.getArcheryInt() < 1000)
    				event.setDamage(calculateDamage(event, 4));
    			if(PPa.getArcheryInt() >= 1000)
    				event.setDamage(calculateDamage(event, 5));
    		}
    		/*
    		 * Defender is Squid
    		 */
    		if(x instanceof Squid){
    			if(PPa.getArcheryInt() >= 50 && PPa.getArcheryInt() < 250)
    				event.setDamage(calculateDamage(event, 1));
    			if(PPa.getArcheryInt() >= 250 && PPa.getArcheryInt() < 575)
    				event.setDamage(calculateDamage(event, 2));
    			if(PPa.getArcheryInt() >= 575 && PPa.getArcheryInt() < 725)
    				event.setDamage(calculateDamage(event, 3));
    			if(PPa.getArcheryInt() >= 725 && PPa.getArcheryInt() < 1000)
    				event.setDamage(calculateDamage(event, 4));
    			if(PPa.getArcheryInt() >= 1000)
    				event.setDamage(calculateDamage(event, 5));
    		}
    		/*
    		 * Attacker is Player
    		 */
    		if(x instanceof Player){
    			if(LoadProperties.pvp == false){
    				event.setCancelled(true);
    				return;
    			}
    			Player defender = (Player)x;
    			PlayerProfile PPd = Users.getProfile(defender);
    			/*
    			 * Stuff for the daze proc
    			 */
    	    		if(PPa.inParty() && PPd.inParty()){
    					if(Party.getInstance().inSameParty(defender, attacker)){
    						event.setCancelled(true);
    						return;
    					}
    	    		}
    	    		/*
    	    		 * PVP XP
    	    		 */
    	    		if(LoadProperties.pvpxp && !Party.getInstance().inSameParty(attacker, defender)){
    	    			PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
    	    		}
    				/*
    				 * DAZE PROC
    				 */
    	    		Location loc = defender.getLocation();
    				if(Math.random() * 10 > 5){
					loc.setPitch(90);
					} else {
						loc.setPitch(-90);
					}
    				if(PPa.getArcheryInt() >= 1000){
    	    			if(Math.random() * 1000 <= 500){
    	    				defender.teleportTo(loc);
    	    				defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
    	    				attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    	    			}
    	    		} else if(Math.random() * 2000 <= PPa.getArcheryInt()){
    	    			defender.teleportTo(loc);
	    				defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
	    				attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    	    		}
    				
					if(PPa.getArcheryInt() >= 50 && PPa.getArcheryInt() < 250)
	    				event.setDamage(calculateDamage(event, 1));
	    			if(PPa.getArcheryInt() >= 250 && PPa.getArcheryInt() < 575)
	    				event.setDamage(calculateDamage(event, 2));
	    			if(PPa.getArcheryInt() >= 575 && PPa.getArcheryInt() < 725)
	    				event.setDamage(calculateDamage(event, 3));
	    			if(PPa.getArcheryInt() >= 725 && PPa.getArcheryInt() < 1000)
	    				event.setDamage(calculateDamage(event, 4));
	    			if(PPa.getArcheryInt() >= 1000)
	    				event.setDamage(calculateDamage(event, 5));
    			}
    		}
    		Skills.XpCheck(attacker);
    	}
    }
	public static boolean simulateUnarmedProc(Player player){
		PlayerProfile PP = Users.getProfile(player);
    	if(PP.getUnarmedInt() >= 1000){
    		if(Math.random() * 4000 <= 1000){
    			return true;
    		}
    	} else {
    		if(Math.random() * 4000 <= PP.getUnarmedInt()){
    			return true;
    		}
    	}
    		return false;
    }
    
    public static int calculateDamage(EntityDamageEvent event, int dmg){
    	return event.getDamage() + dmg;
    }
    public static void dealDamage(Entity target, int dmg){
    	if(target instanceof Player){
    		((Player) target).damage(dmg);
    	}
    	if(target instanceof Animals){
    		((Animals) target).damage(dmg);
    	}
    	if(target instanceof Monster){
    		((Monster) target).damage(dmg);
    	}
    }
    public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event, Entity x){
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

    
	
	
}
