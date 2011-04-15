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

import com.gmail.nossr50.PlayerList.PlayerProfile;


public class mcCombat {
	private static mcMMO plugin;
	public mcCombat(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcCombat instance;
	public static mcCombat getInstance() {
    	if (instance == null) {
    	instance = new mcCombat(plugin);
    	}
    	return instance;
    	}
	public void playerVersusPlayerChecks(Entity x, Player attacker, EntityDamageByEntityEvent event){
    	if(x instanceof Player){
    		if(mcLoadProperties.pvp == false){
    			event.setCancelled(true);
    			return;
    		}
    		PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    		Player defender = (Player)x;
    		PlayerProfile PPd = mcUsers.getProfile(defender.getName());
    		
    		/*
    		 * COMPATABILITY CHECKS (Stuff that wouldn't happen normally in MC basically...)
    		 */
    		if(mcUsers.getProfile(defender.getName()) == null)
    			mcUsers.addUser(defender);
    		if(attacker != null && defender != null && mcUsers.getProfile(attacker.getName()).inParty() && mcUsers.getProfile(defender.getName()).inParty()){
				if(mcParty.getInstance().inSameParty(defender, attacker)){
					event.setCancelled(true);
					return;
				}
    		}
    		/*
    		 * AXE CRITICAL CHECK
    		 */
    		axeCriticalCheck(attacker, event, x);
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
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
    		if(attacker != null && defender != null && mcLoadProperties.pvpxp){
    			if(PPd.inParty() && PPa.inParty() && mcParty.getInstance().inSameParty(attacker, defender))
    				return;
    			if(mcm.getInstance().isAxes(attacker.getItemInHand()))
    				PPa.addAxesXP((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
    			if(mcm.getInstance().isSwords(attacker.getItemInHand()))
    				PPa.addSwordsXP((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
    			if(attacker.getItemInHand().getTypeId() == 0)
    				PPa.addUnarmedXP((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
    		}
    		/*
    		 * CHECK FOR LEVEL UPS
    		 */
    		mcSkills.getInstance().XpCheck(attacker);
		}
    }
    public void playerVersusSquidChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    	if(x instanceof Squid){
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Squid defender = (Squid)event.getEntity();
			if(mcm.getInstance().isSwords(attacker.getItemInHand()) && defender.getHealth() > 0 && mcPermissions.getInstance().swords(attacker)){
					PPa.addSwordsXP(10 * mcLoadProperties.xpGainMultiplier);
			}
			mcSkills.getInstance().XpCheck(attacker);
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					PPa.addAxesXP(10 * mcLoadProperties.xpGainMultiplier);
					mcSkills.getInstance().XpCheck(attacker);
			}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
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
					PPa.addUnarmedXP(10 * mcLoadProperties.xpGainMultiplier);
					mcSkills.getInstance().XpCheck(attacker);
					}
    			}
		}
    }
    public void playerVersusAnimalsChecks(Entity x, Player attacker, EntityDamageByEntityEvent event, int type){
    	PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    	if(x instanceof Animals){
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Animals defender = (Animals)event.getEntity();
    		if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
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
    public void playerVersusMonsterChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    	if(x instanceof Monster){
    		/*
    		 * AXE PROC CHECKS
    		 */
    		axeCriticalCheck(attacker, event, x);
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Monster defender = (Monster)event.getEntity();
			if(mcm.getInstance().isSwords(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().swords(attacker)){
					if(!mcConfig.getInstance().isMobSpawnTracked(x)){
					if(x instanceof Creeper)
						PPa.addSwordsXP((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addSwordsXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addSwordsXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addSwordsXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addSwordsXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					}
					mcSkills.getInstance().XpCheck(attacker);
				}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					if(!mcConfig.getInstance().isMobSpawnTracked(x)){
					if(x instanceof Creeper)
					PPa.addAxesXP((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addAxesXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addAxesXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addAxesXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addAxesXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					}
					mcSkills.getInstance().XpCheck(attacker);
			}
			/*
			 * AXE DAMAGE SCALING && LOOT CHECKS
			 */
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
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
			if(!mcConfig.getInstance().isMobSpawnTracked(x)){
			if(x instanceof Creeper)
				PPa.addUnarmedXP((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof Spider)
				PPa.addUnarmedXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof Skeleton)
				PPa.addUnarmedXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof Zombie)
				PPa.addUnarmedXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof PigZombie)
				PPa.addUnarmedXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
			}
			mcSkills.getInstance().XpCheck(attacker);
			}
		}
    }
	public void archeryCheck(EntityDamageByProjectileEvent event){
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	if(event.getProjectile().toString().equals("CraftArrow") && x instanceof Player){
    		Player defender = (Player)x;
    		PlayerProfile PPd = mcUsers.getProfile(defender.getName());
    		if(PPd == null)
    			mcUsers.addUser(defender);
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
    		PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    		if(event.getProjectile().toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker)){
    			if(!mcConfig.getInstance().isTracked(x) && event.getDamage() > 0){
    				mcConfig.getInstance().addArrowTrack(x, 0);
    				if(attacker != null){
    					if(Math.random() * 1000 <= PPa.getArcheryInt()){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				}
    			} else {
    				if(event.getDamage() > 0){
    					if(attacker != null){
        					if(Math.random() * 1000 <= PPa.getArcheryInt()){
        						mcConfig.getInstance().addArrowCount(x, 1);
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
        				if(!mcParty.getInstance().inSameParty(attacker, Defender)){
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
    			if(!mcConfig.getInstance().isMobSpawnTracked(x)){
    				if(x instanceof Creeper)
					PPa.addArcheryXP((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addArcheryXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addArcheryXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addArcheryXP((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addArcheryXP((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
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
    			if(mcLoadProperties.pvp == false){
    				event.setCancelled(true);
    				return;
    			}
    			Player defender = (Player)x;
    			PlayerProfile PPd = mcUsers.getProfile(defender.getName());
    			/*
    			 * Stuff for the daze proc
    			 */
    	    		if(PPa.inParty() && PPd.inParty()){
    					if(mcParty.getInstance().inSameParty(defender, attacker)){
    						event.setCancelled(true);
    						return;
    					}
    	    		}
    	    		/*
    	    		 * PVP XP
    	    		 */
    	    		if(mcLoadProperties.pvpxp && !mcParty.getInstance().inSameParty(attacker, defender)){
    	    			PPa.addArcheryXP((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
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
    		mcSkills.getInstance().XpCheck(attacker);
    	}
    }
	public boolean simulateUnarmedProc(Player player){
		PlayerProfile PP = mcUsers.getProfile(player.getName());
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
    public void bleedCheck(Player attacker, Entity x){
    	PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    	if(mcPermissions.getInstance().swords(attacker) && mcm.getInstance().isSwords(attacker.getItemInHand())){
			if(PPa.getSwordsInt() >= 750){
				if(Math.random() * 1000 >= 750){
					if(!(x instanceof Player))
						mcConfig.getInstance().addToBleedQue(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target.getName()).addBleedTicks(3);
					}
					attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
				}
			} else if (Math.random() * 1000 <= PPa.getSwordsInt()){
				if(!(x instanceof Player))
					mcConfig.getInstance().addToBleedQue(x);
				if(x instanceof Player){
					Player target = (Player)x;
					mcUsers.getProfile(target.getName()).addBleedTicks(2);
				}
				attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
			}
		}
    }
    public int calculateDamage(EntityDamageEvent event, int dmg){
    	return event.getDamage() + dmg;
    }
    public void dealDamage(Entity target, int dmg){
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
    public void applyAoeDamage(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	int targets = 0;
    	targets = mcm.getInstance().getTier(attacker);
    	for(Entity derp : x.getWorld().getEntities()){
    		if(mcm.getInstance().getDistance(x.getLocation(), derp.getLocation()) < 5){
    			if(derp instanceof Player){
    				Player target = (Player)derp;
    				if(mcParty.getInstance().inSameParty(attacker, target))
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
    public void applySerratedStrikes(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	int targets = 0;
    	targets = mcm.getInstance().getTier(attacker);
    	for(Entity derp : x.getWorld().getEntities()){
    		if(mcm.getInstance().getDistance(x.getLocation(), derp.getLocation()) < 5){
    			if(derp instanceof Player){
    				Player target = (Player)derp;
    				if(mcParty.getInstance().inSameParty(attacker, target))
    					continue;
    				if(!target.getName().equals(attacker.getName()) && targets >= 1){
    					target.damage(event.getDamage() / 4);
    					target.sendMessage(ChatColor.DARK_RED+"Struck by Serrated Strikes!");
        				mcUsers.getProfile(target.getName()).addBleedTicks(5);
    					targets--;
    				}
    			}
    			if(derp instanceof Monster && targets >= 1){
    				if(!mcConfig.getInstance().isBleedTracked(derp))
    					mcConfig.getInstance().addToBleedQue(x);
    				Monster target = (Monster)derp;
    				target.damage(event.getDamage() / 4);
    				targets--;
    			}
    			if(derp instanceof Wolf){
					continue;
				}
    			if(derp instanceof Animals && targets >= 1){
    				if(!mcConfig.getInstance().isBleedTracked(derp))
    					mcConfig.getInstance().addToBleedQue(x);
    				Animals target = (Animals)derp;
    				target.damage(event.getDamage() / 4);
    				targets--;
    			}
    		}
    		//attacker.sendMessage(ChatColor.GREEN+"**SERRATED STRIKES HIT "+(mcm.getInstance().getTier(attacker)-targets)+" FOES**");
    	}
    }
    public void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	PlayerProfile PPa = mcUsers.getProfile(attacker.getName());
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
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
    public void parryCheck(Player defender, EntityDamageByEntityEvent event, Entity y){
    	PlayerProfile PPd = mcUsers.getProfile(defender.getName());
    	if(defender != null && mcm.getInstance().isSwords(defender.getItemInHand()) 
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
    public void bleedSimulate(){
    	
    	//Add items from Que list to BleedTrack list
    	for(Entity x : mcConfig.getInstance().getBleedQue()){
    		mcConfig.getInstance().addBleedTrack(x);
    	}
    	//Clear list
    	mcConfig.getInstance().clearBleedQue();
    	
    	//Cleanup any dead entities from the list
    	for(Entity x : mcConfig.getInstance().getBleedRemovalQue()){
    		mcConfig.getInstance().removeBleedTrack(x);
    	}
    	
    	//Clear bleed removal list
    	mcConfig.getInstance().clearBleedRemovalQue();
    	
    	//Bleed monsters/animals
        for(Entity x : mcConfig.getInstance().getBleedTracked()){
        	if(x == null){
        		continue;
        	}
        	
        	if(mcm.getInstance().getHealth(x) <= 0){
        		continue;
        	}
        	
    	    if(x instanceof Animals){
    	    	((Animals) x).damage(2);
    	    }
    	    
    	    if(x instanceof Monster){
    	    	((Monster) x).damage(2);
    	    }
    	    
    	    /* - Lets try something else...
    	    if(x instanceof Player){
    	    	Player player = (Player)x;
    	    	if(player.getHealth() >= 1){
    	    		player.damage(1);
    	    		player.sendMessage(ChatColor.RED+"**BLEED**");
    	    		if(player.getHealth() <= 0){
    	    			mcUsers.getProfile(player).setBleedTicks(0);
    	    		}
    	    		if(mcUsers.getProfile(player).getBleedTicks() >= 1){
    	    			mcUsers.getProfile(player).setBleedTicks(mcUsers.getProfile(player).getBleedTicks() - 1);
    	    			if(mcUsers.getProfile(player).getBleedTicks() <= 0)
    	    				mcConfig.getInstance().addToBleedRemovalQue(x); //Add for removal if bleedticks are 0
    	    		}
    	    	}
    	    }
    	    */
        }
        
    }
	
	
}
