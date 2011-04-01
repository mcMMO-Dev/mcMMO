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
    		Player defender = (Player)x;
    		/*
    		 * COMPATABILITY CHECKS (Stuff that wouldn't happen normally in MC basically...)
    		 */
    		if(mcUsers.getProfile(defender) == null)
    			mcUsers.addUser(defender);
    		if(attacker != null && defender != null && mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
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
				if(mcUsers.getProfile(attacker).getUnarmedInt() < 250){
					event.setDamage(calculateDamage(event, 2));
				} else if (mcUsers.getProfile(attacker).getUnarmedInt() < 500 && mcUsers.getProfile(attacker).getUnarmedInt() >= 250){
					event.setDamage(calculateDamage(event, 3));
				} else {
					event.setDamage(calculateDamage(event, 4));
				}
				
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
    			if(mcUsers.getProfile(defender).inParty() && mcUsers.getProfile(attacker).inParty() && mcParty.getInstance().inSameParty(attacker, defender))
    				return;
    			if(mcm.getInstance().isAxes(attacker.getItemInHand()))
    				mcUsers.getProfile(attacker).addAxesGather((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
    			if(mcm.getInstance().isSwords(attacker.getItemInHand()))
    				mcUsers.getProfile(attacker).addSwordsGather((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
    			if(attacker.getItemInHand().getTypeId() == 0)
    				mcUsers.getProfile(attacker).addUnarmedGather((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
    		}
    		/*
    		 * CHECK FOR LEVEL UPS
    		 */
    		mcSkills.getInstance().XpCheck(attacker);
		}
    }
    public void playerVersusSquidChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	if(x instanceof Squid){
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Squid defender = (Squid)event.getEntity();
			if(mcm.getInstance().isSwords(attacker.getItemInHand()) && defender.getHealth() > 0 && mcPermissions.getInstance().swords(attacker)){
					mcUsers.getProfile(attacker).addSwordsGather(10 * mcLoadProperties.xpGainMultiplier);
			}
			mcSkills.getInstance().XpCheck(attacker);
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					mcUsers.getProfile(attacker).addAxesGather(10 * mcLoadProperties.xpGainMultiplier);
					mcSkills.getInstance().XpCheck(attacker);
			}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(mcUsers.getProfile(attacker).getAxesInt() >= 500){
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
				if(mcUsers.getProfile(attacker).getUnarmedInt() < 250){
					event.setDamage(calculateDamage(event, 2));
				} else if (mcUsers.getProfile(attacker).getUnarmedInt() < 500 && mcUsers.getProfile(attacker).getUnarmedInt() >= 250){
					event.setDamage(calculateDamage(event, 3));
				} else {
					event.setDamage(calculateDamage(event, 4));
				}
    			
    			//XP
					if(defender.getHealth() != 0){
					mcUsers.getProfile(attacker).addUnarmedGather(10 * mcLoadProperties.xpGainMultiplier);
					mcSkills.getInstance().XpCheck(attacker);
					}
    			}
		}
    }
    public void playerVersusAnimalsChecks(Entity x, Player attacker, EntityDamageByEntityEvent event, int type){
    	if(x instanceof Animals){
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Animals defender = (Animals)event.getEntity();
    		if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(defender.getHealth() <= 0)
					return;
				if(mcUsers.getProfile(attacker).getAxesInt() >= 500){
					event.setDamage(calculateDamage(event, 4));
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
				//Bonus just for having unarmed
				if(mcUsers.getProfile(attacker).getUnarmedInt() < 250){
					event.setDamage(calculateDamage(event, 2));
				} else if (mcUsers.getProfile(attacker).getUnarmedInt() < 500 && mcUsers.getProfile(attacker).getUnarmedInt() >= 250){
					event.setDamage(calculateDamage(event, 3));
				} else {
					event.setDamage(calculateDamage(event, 4));
				}
			}
		}
    }
    public void playerVersusMonsterChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
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
						mcUsers.getProfile(attacker).addSwordsGather((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						mcUsers.getProfile(attacker).addSwordsGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						mcUsers.getProfile(attacker).addSwordsGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						mcUsers.getProfile(attacker).addSwordsGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						mcUsers.getProfile(attacker).addSwordsGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					}
					mcSkills.getInstance().XpCheck(attacker);
				}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					if(!mcConfig.getInstance().isMobSpawnTracked(x)){
					if(x instanceof Creeper)
					mcUsers.getProfile(attacker).addAxesGather((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						mcUsers.getProfile(attacker).addAxesGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						mcUsers.getProfile(attacker).addAxesGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						mcUsers.getProfile(attacker).addAxesGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						mcUsers.getProfile(attacker).addAxesGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					}
					mcSkills.getInstance().XpCheck(attacker);
			}
			/*
			 * AXE DAMAGE SCALING && LOOT CHECKS
			 */
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(mcUsers.getProfile(attacker).getAxesInt() >= 500){
					event.setDamage(calculateDamage(event, 4));
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
			if(defender.getHealth() <= 0)
				return;
			
			//Bonus just for having unarmed
			if(mcUsers.getProfile(attacker).getUnarmedInt() < 250){
				event.setDamage(calculateDamage(event, 2));
			} else if (mcUsers.getProfile(attacker).getUnarmedInt() < 500 && mcUsers.getProfile(attacker).getUnarmedInt() >= 250){
				event.setDamage(calculateDamage(event, 3));
			} else {
				event.setDamage(calculateDamage(event, 4));
			}
			
			//XP
			if(!mcConfig.getInstance().isMobSpawnTracked(x)){
			if(x instanceof Creeper)
				mcUsers.getProfile(attacker).addUnarmedGather((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof Spider)
				mcUsers.getProfile(attacker).addUnarmedGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof Skeleton)
				mcUsers.getProfile(attacker).addUnarmedGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof Zombie)
				mcUsers.getProfile(attacker).addUnarmedGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
			if(x instanceof PigZombie)
				mcUsers.getProfile(attacker).addUnarmedGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
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
    		if(mcUsers.getProfile(defender) == null)
    			mcUsers.addUser(defender);
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0){
	    		if(defender != null && mcUsers.getProfile(defender).getUnarmedInt() >= 1000){
	    			if(Math.random() * 1000 <= 500){
	    				event.setCancelled(true);
	    				defender.sendMessage(ChatColor.WHITE+"**ARROW DEFLECT**");
	    				return;
	    			}
	    		} else if(defender != null && Math.random() * 1000 <= (mcUsers.getProfile(defender).getUnarmedInt() / 2)){
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
    		if(event.getProjectile().toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker)){
    			if(!mcConfig.getInstance().isTracked(x) && event.getDamage() > 0){
    				mcConfig.getInstance().addArrowTrack(x, 0);
    				if(attacker != null){
    					if(Math.random() * 1000 <= mcUsers.getProfile(attacker).getArcheryInt()){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				}
    			} else {
    				if(event.getDamage() > 0){
    					if(attacker != null){
        					if(Math.random() * 1000 <= mcUsers.getProfile(attacker).getArcheryInt()){
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
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 200)
    					ignition+=20;
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 400)
    					ignition+=20;
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 600)
    					ignition+=20;
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 800)
    					ignition+=20;
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
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
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				event.setDamage(calculateDamage(event, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				event.setDamage(calculateDamage(event, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				event.setDamage(calculateDamage(event, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				event.setDamage(calculateDamage(event, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				event.setDamage(calculateDamage(event, 5));
    			//XP
    			if(!mcConfig.getInstance().isMobSpawnTracked(x)){
    				if(x instanceof Creeper)
					mcUsers.getProfile(attacker).addArcheryGather((event.getDamage() * 4) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						mcUsers.getProfile(attacker).addArcheryGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						mcUsers.getProfile(attacker).addArcheryGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						mcUsers.getProfile(attacker).addArcheryGather((event.getDamage() * 2) * mcLoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						mcUsers.getProfile(attacker).addArcheryGather((event.getDamage() * 3) * mcLoadProperties.xpGainMultiplier);
    			}
    			}
    		/*
    		 * Defender is Animals	
    		 */
    		if(x instanceof Animals){
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				event.setDamage(calculateDamage(event, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				event.setDamage(calculateDamage(event, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				event.setDamage(calculateDamage(event, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				event.setDamage(calculateDamage(event, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				event.setDamage(calculateDamage(event, 5));
    		}
    		/*
    		 * Defender is Squid
    		 */
    		if(x instanceof Squid){
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				event.setDamage(calculateDamage(event, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				event.setDamage(calculateDamage(event, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				event.setDamage(calculateDamage(event, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				event.setDamage(calculateDamage(event, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
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
    			/*
    			 * Stuff for the daze proc
    			 */
    	    		if(mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
    					if(mcParty.getInstance().inSameParty(defender, attacker)){
    						event.setCancelled(true);
    						return;
    					}
    	    		}
    	    		/*
    	    		 * PVP XP
    	    		 */
    	    		if(mcLoadProperties.pvpxp && !mcParty.getInstance().inSameParty(attacker, defender)){
    	    			mcUsers.getProfile(attacker).addArcheryGather((event.getDamage() * 3) * mcLoadProperties.pvpxprewardmodifier);
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
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000){
    	    			if(Math.random() * 1000 <= 500){
    	    				defender.teleportTo(loc);
    	    				defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
    	    				attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    	    			}
    	    		} else if(Math.random() * 2000 <= mcUsers.getProfile(attacker).getArcheryInt()){
    	    			defender.teleportTo(loc);
	    				defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
	    				attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    	    		}
    				
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
	    				event.setDamage(calculateDamage(event, 1));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
	    				event.setDamage(calculateDamage(event, 2));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
	    				event.setDamage(calculateDamage(event, 3));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
	    				event.setDamage(calculateDamage(event, 4));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
	    				event.setDamage(calculateDamage(event, 5));
    			}
    		}
    		mcSkills.getInstance().XpCheck(attacker);
    	}
    }
	public boolean simulateUnarmedProc(Player player){
    	if(mcUsers.getProfile(player).getUnarmedInt() >= 1000){
    		if(Math.random() * 4000 <= 1000){
    			return true;
    		}
    	} else {
    		if(Math.random() * 4000 <= mcUsers.getProfile(player).getUnarmedInt()){
    			return true;
    		}
    	}
    		return false;
    }
    public void bleedCheck(Player attacker, Entity x){
    	if(mcPermissions.getInstance().swords(attacker) && mcm.getInstance().isSwords(attacker.getItemInHand())){
			if(mcUsers.getProfile(attacker).getSwordsInt() >= 750){
				if(Math.random() * 1000 >= 750){
					if(!(x instanceof Player))
						mcConfig.getInstance().addToBleedQue(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target).addBleedTicks(3);
					}
					attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
				}
			} else if (Math.random() * 1000 <= mcUsers.getProfile(attacker).getSwordsInt()){
				if(!(x instanceof Player))
					mcConfig.getInstance().addToBleedQue(x);
				if(x instanceof Player){
					Player target = (Player)x;
					mcUsers.getProfile(target).addBleedTicks(2);
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
    			if(derp instanceof Animals  && targets >= 1){
    				if(derp instanceof Wolf){
    					if(((Wolf) derp).isAngry() && ((Wolf) derp).getTarget() != attacker){
    						continue;
    					}
    				}
    					
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
        				mcUsers.getProfile(target).addBleedTicks(5);
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
    			if(derp instanceof Animals && targets >= 1){
    				if(derp instanceof Wolf)
    					continue;
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
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750){
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
    		} else if(Math.random() * 1000 <= mcUsers.getProfile(attacker).getAxesInt()){
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
    	if(defender != null && mcm.getInstance().isSwords(defender.getItemInHand()) 
    			&& mcPermissions.getInstance().swords(defender)){
			if(mcUsers.getProfile(defender).getSwordsInt() >= 900){
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
				if(Math.random() * 3000 <= mcUsers.getProfile(defender).getSwordsInt()){
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
