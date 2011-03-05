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
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
    		if(mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
				if(mcParty.getInstance().inSameParty(defender, attacker)){
					event.setCancelled(true);
					return;
				}
    		}
    		mcUsers.getProfile(defender).setRecentlyHurt(30);
    		/*
    		 * AXE CRITICAL CHECK
    		 */
    		axeCriticalCheckPlayer(attacker, event, x, plugin);
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
    		int healthbefore = defender.getHealth();
			if(mcPermissions.getInstance().unarmed(attacker) && attacker.getItemInHand().getTypeId() == 0){
				//DMG MODIFIER
				if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
					defender.setHealth(calculateDamage(defender, 1));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
					defender.setHealth(calculateDamage(defender, 2));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
					defender.setHealth(calculateDamage(defender, 3));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
					defender.setHealth(calculateDamage(defender, 4));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
					defender.setHealth(calculateDamage(defender, 5));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
					defender.setHealth(calculateDamage(defender, 6));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
					defender.setHealth(calculateDamage(defender, 7));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
					defender.setHealth(calculateDamage(defender, 8));
				}
				if(mcUsers.getProfile(defender).isDead())
    				return;
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
			 * Make the defender drop items on death
			 */
			if(defender.getHealth()<= 0 && !mcUsers.getProfile(defender).isDead()){
				mcUsers.getProfile(defender).setDead(true);
				event.setCancelled(true); //SEE IF THIS HELPS
				//If it only would've died from mcMMO damage modifiers
    			if(defender.getHealth() <= 0 && healthbefore - event.getDamage() >= 1){
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
				for(ItemStack herp : defender.getInventory().getContents()){
					if(herp != null && herp.getTypeId() != 0)
					defender.getLocation().getWorld().dropItemNaturally(defender.getLocation(), herp);
				}
			}
		}
    }
    public void playerVersusSquidChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	if(x instanceof Squid){
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Squid defender = (Squid)event.getEntity();
			if(mcm.getInstance().isSwords(attacker.getItemInHand()) && defender.getHealth() > 0 && mcPermissions.getInstance().swords(attacker)){
					mcUsers.getProfile(attacker).addSwordsGather(10);
					if(mcUsers.getProfile(attacker).getSwordsGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("swords")){
						int skillups = 0;
						while(mcUsers.getProfile(attacker).getSwordsGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("swords")){
							skillups++;
							mcUsers.getProfile(attacker).removeSwordsGather(mcUsers.getProfile(attacker).getXpToLevel("swords"));
							mcUsers.getProfile(attacker).skillUpSwords(1);
						}
						attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getSwords()+")");	
					}
			}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					mcUsers.getProfile(attacker).addAxesGather(10);
					if(mcUsers.getProfile(attacker).getAxesGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("axes")){
						int skillups = 0;
						while(mcUsers.getProfile(attacker).getAxesGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("axes")){
							skillups++;
							mcUsers.getProfile(attacker).removeAxesGather(mcUsers.getProfile(attacker).getXpToLevel("axes"));
							mcUsers.getProfile(attacker).skillUpAxes(1);
						}
						attacker.sendMessage(ChatColor.YELLOW+"Axes skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getAxes()+")");	
					}
			}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(defender.getHealth() <= 0)
					return;
				if(mcUsers.getProfile(attacker).getAxesInt() >= 500){
					defender.setHealth(calculateDamage(defender, (4 - axeNerf(attacker.getItemInHand().getTypeId()))));
				}
				if(defender.getHealth() <= 0){
					mcm.getInstance().simulateNaturalDrops(defender);
				}
			}
			/*
			 * UNARMED VS SQUID
			 */
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
    			if(defender.getHealth() <= 0)
    				return;
    			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
					defender.setHealth(calculateDamage(defender, 1));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
					defender.setHealth(calculateDamage(defender, 2));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
					defender.setHealth(calculateDamage(defender, 3));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
					defender.setHealth(calculateDamage(defender, 4));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
					defender.setHealth(calculateDamage(defender, 5));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
					defender.setHealth(calculateDamage(defender, 6));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
					defender.setHealth(calculateDamage(defender, 7));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
					defender.setHealth(calculateDamage(defender, 8));
				}
    			//XP
					if(defender.getHealth() != 0){
					mcUsers.getProfile(attacker).addUnarmedGather(10);
					if(mcUsers.getProfile(attacker).getUnarmedGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("unarmed")){
						int skillups = 0;
						while(mcUsers.getProfile(attacker).getUnarmedGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("unarmed")){
							skillups++;
							mcUsers.getProfile(attacker).removeUnarmedGather(mcUsers.getProfile(attacker).getXpToLevel("unarmed"));
							mcUsers.getProfile(attacker).skillUpUnarmed(1);
						}
						attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");	
					}
					}
				if(defender.getHealth() <= 0){
				mcm.getInstance().simulateNaturalDrops(defender);
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
					defender.setHealth(calculateDamage(defender, (4 - axeNerf(attacker.getItemInHand().getTypeId()))));
				}
				if(defender.getHealth() <= 0){
					mcm.getInstance().simulateNaturalDrops(defender);
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
			if(defender.getHealth() <= 0)
				return;
			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
				defender.setHealth(calculateDamage(defender, 1));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
				defender.setHealth(calculateDamage(defender, 2));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
				defender.setHealth(calculateDamage(defender, 3));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
				defender.setHealth(calculateDamage(defender, 4));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
				defender.setHealth(calculateDamage(defender, 5));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
				defender.setHealth(calculateDamage(defender, 6));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
				defender.setHealth(calculateDamage(defender, 7));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
				defender.setHealth(calculateDamage(defender, 8));
			}
			if(defender.getHealth() <= 0){
				mcm.getInstance().simulateNaturalDrops(defender);
			}
			}
		}
    }
    public void playerVersusMonsterChecks(EntityDamageByEntityEvent event, Player attacker, Entity x, int type){
    	if(x instanceof Monster){
    		/*
    		 * AXE PROC CHECKS
    		 */
    		axeCriticalCheckMonster(attacker, event, x);
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
			Monster defender = (Monster)event.getEntity();
			if(mcm.getInstance().isSwords(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().swords(attacker)){
					if(!mcConfig.getInstance().isMobSpawnTracked(x)){
					if(x instanceof Creeper)
					mcUsers.getProfile(attacker).addSwordsGather(10);
					if(x instanceof Spider)
					mcUsers.getProfile(attacker).addSwordsGather(7);
					if(x instanceof Skeleton)
					mcUsers.getProfile(attacker).addSwordsGather(5);
					if(x instanceof Zombie)
					mcUsers.getProfile(attacker).addSwordsGather(3);
					if(x instanceof PigZombie)
					mcUsers.getProfile(attacker).addSwordsGather(7);
					}
					if(mcUsers.getProfile(attacker).getSwordsGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("swords")){
						int skillups = 0;
						while(mcUsers.getProfile(attacker).getSwordsGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("swords")){
							skillups++;
							mcUsers.getProfile(attacker).removeSwordsGather(mcUsers.getProfile(attacker).getXpToLevel("swords"));
							mcUsers.getProfile(attacker).skillUpSwords(1);
						}
						attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getSwords()+")");	
					}
				}
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					if(!mcConfig.getInstance().isMobSpawnTracked(x)){
				    mcUsers.getProfile(attacker).addAxesGather(1);
					if(x instanceof Creeper)
					mcUsers.getProfile(attacker).addAxesGather(10);
					if(x instanceof Spider)
						mcUsers.getProfile(attacker).addAxesGather(7);
					if(x instanceof Skeleton)
						mcUsers.getProfile(attacker).addAxesGather(5);
					if(x instanceof Zombie)
						mcUsers.getProfile(attacker).addAxesGather(3);
					if(x instanceof PigZombie)
						mcUsers.getProfile(attacker).addAxesGather(7);
					}
					if(mcUsers.getProfile(attacker).getAxesGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("axes")){
						int skillups = 0;
						while(mcUsers.getProfile(attacker).getAxesGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("axes")){
							skillups++;
							mcUsers.getProfile(attacker).removeAxesGather(mcUsers.getProfile(attacker).getXpToLevel("axes"));
							mcUsers.getProfile(attacker).skillUpAxes(1);
						}
						attacker.sendMessage(ChatColor.YELLOW+"Axes skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getAxes()+")");	
					}
			}
			/*
			 * AXE DAMAGE SCALING && LOOT CHECKS
			 */
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
				if(defender.getHealth() <= 0)
					return;
				if(mcUsers.getProfile(attacker).getAxesInt() >= 500){
					defender.setHealth(calculateDamage(defender, (4 - axeNerf(attacker.getItemInHand().getTypeId()))));
				}
				if(defender.getHealth() <= 0 || defender.getHealth() - event.getDamage() <= 0){
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
			if(defender.getHealth() <= 0)
				return;
			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
				defender.setHealth(calculateDamage(defender, 1));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
				defender.setHealth(calculateDamage(defender, 2));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
				defender.setHealth(calculateDamage(defender, 3));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
				defender.setHealth(calculateDamage(defender, 4));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
				defender.setHealth(calculateDamage(defender, 5));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
				defender.setHealth(calculateDamage(defender, 6));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
				defender.setHealth(calculateDamage(defender, 7));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
				defender.setHealth(calculateDamage(defender, 8));
			}
			//XP
			if(!mcConfig.getInstance().isMobSpawnTracked(x)){
			if(x instanceof Creeper)
				mcUsers.getProfile(attacker).addUnarmedGather(20);
			if(x instanceof Spider)
				mcUsers.getProfile(attacker).addUnarmedGather(15);
			if(x instanceof Skeleton)
				mcUsers.getProfile(attacker).addUnarmedGather(10);
			if(x instanceof Zombie)
				mcUsers.getProfile(attacker).addUnarmedGather(5);
			if(x instanceof PigZombie)
				mcUsers.getProfile(attacker).addUnarmedGather(15);
			}
			if(mcUsers.getProfile(attacker).getUnarmedGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("unarmed")){
				int skillups = 0;
				while(mcUsers.getProfile(attacker).getUnarmedGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("unarmed")){
					skillups++;
					mcUsers.getProfile(attacker).removeUnarmedGather(mcUsers.getProfile(attacker).getXpToLevel("unarmed"));
					mcUsers.getProfile(attacker).skillUpUnarmed(1);
				}
				attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");	
			}
			if(defender.getHealth() <= 0 || defender.getHealth() - event.getDamage() <= 0){
				mcm.getInstance().simulateNaturalDrops(defender);
			}
			}
		}
    }
	public void archeryCheck(EntityDamageByProjectileEvent event){
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	/*
    	 * Defender is player
    	 */
    	if(y instanceof Player){
    		Player attacker = (Player)y;
    		if(event.getProjectile().toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker)){
    			if(!mcConfig.getInstance().isTracked(x) && event.getDamage() > 0){
    				mcConfig.getInstance().addArrowTrack(x, 0);
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 200){
    					if(Math.random() * 10 > 8){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 200 && mcUsers.getProfile(attacker).getArcheryInt() < 400){
    					if(Math.random() * 10 > 6){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 400 && mcUsers.getProfile(attacker).getArcheryInt() < 600){
    					if(Math.random() * 10 > 4){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 600 && mcUsers.getProfile(attacker).getArcheryInt() < 800){
    					if(Math.random() * 10 > 2){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 800){
    						mcConfig.getInstance().addArrowCount(x, 1);
    				}
    			} else {
    				if(event.getDamage() > 0){
    				if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 200){
    					if(Math.random() * 10 > 8){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 200 && mcUsers.getProfile(attacker).getArcheryInt() < 400){
    					if(Math.random() * 10 > 6){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 400 && mcUsers.getProfile(attacker).getArcheryInt() < 600){
    					if(Math.random() * 10 > 4){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 600 && mcUsers.getProfile(attacker).getArcheryInt() < 800){
    					if(Math.random() * 10 > 2){
    						mcConfig.getInstance().addArrowCount(x, 1);
    					}
    				} else if(mcUsers.getProfile(attacker).getArcheryInt() >= 800){
    						mcConfig.getInstance().addArrowCount(x, 1);
    				}
    				}
    			}
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			Monster defender = (Monster)x;
    			/*
    			 * TRACK ARROWS USED AGAINST THE ENTITY
    			 */
    			int healthbefore = defender.getHealth();
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				defender.setHealth(calculateDamage(defender, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				defender.setHealth(calculateDamage(defender, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				defender.setHealth(calculateDamage(defender, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(calculateDamage(defender, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				defender.setHealth(calculateDamage(defender, 5));
    			//If it only would've died from mcMMO damage modifiers
    			if(defender.getHealth() <= 0 && healthbefore - event.getDamage() >= 1){
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    			//XP
    			if(!mcConfig.getInstance().isMobSpawnTracked(x)){
    				if(x instanceof Creeper)
					mcUsers.getProfile(attacker).addArcheryGather(10);
					if(x instanceof Spider)
						mcUsers.getProfile(attacker).addArcheryGather(7);
					if(x instanceof Skeleton)
						mcUsers.getProfile(attacker).addArcheryGather(5);
					if(x instanceof Zombie)
						mcUsers.getProfile(attacker).addArcheryGather(3);
					if(x instanceof PigZombie)
						mcUsers.getProfile(attacker).addArcheryGather(7);
    			}
    				if(mcUsers.getProfile(attacker).getArcheryGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("archery")){
						int skillups = 0;
						while(mcUsers.getProfile(attacker).getArcheryGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("archery")){
							skillups++;
							mcUsers.getProfile(attacker).removeArcheryGather(mcUsers.getProfile(attacker).getXpToLevel("archery"));
							mcUsers.getProfile(attacker).skillUpArchery(1);
						}
						attacker.sendMessage(ChatColor.YELLOW+"Archery skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getArchery()+")");	
					}
    			}
    		/*
    		 * Defender is Animals	
    		 */
    		if(x instanceof Animals){
    			Animals defender = (Animals)x;
    			int healthbefore = defender.getHealth();
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				defender.setHealth(calculateDamage(defender, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				defender.setHealth(calculateDamage(defender, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				defender.setHealth(calculateDamage(defender, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(calculateDamage(defender, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				defender.setHealth(calculateDamage(defender, 5));
    			//If it only would've died from mcMMO damage modifiers
    			if(defender.getHealth() <= 0 && healthbefore - event.getDamage() >= 1){
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    		}
    		/*
    		 * Defender is Squid
    		 */
    		if(x instanceof Squid){
    			Squid defender = (Squid)x;
    			int healthbefore = defender.getHealth();
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				defender.setHealth(calculateDamage(defender, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				defender.setHealth(calculateDamage(defender, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				defender.setHealth(calculateDamage(defender, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(calculateDamage(defender, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				defender.setHealth(calculateDamage(defender, 5));
    			//If it only would've died from mcMMO damage modifiers
    			if(defender.getHealth() <= 0 && healthbefore - event.getDamage() >= 1){
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
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
    				Location loc = defender.getLocation();
    				if(Math.random() * 10 > 5){
					loc.setPitch(90);
					} else {
						loc.setPitch(-90);
					}
    				/*
    				 * Check the proc
    				 */
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 300 && mcUsers.getProfile(attacker).getArcheryInt() < 400){
    				if(Math.random() * 10 > 7){
    					defender.teleportTo(loc);
    					defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
    					attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    				}
					}
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 600){
	    				if(Math.random() * 10 > 4){
	    					defender.teleportTo(loc);
	    					defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
	    					attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
	    				}
						}
					int healthbefore = defender.getHealth();
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
	    				defender.setHealth(calculateDamage(defender, 1));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
	    				defender.setHealth(calculateDamage(defender, 2));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
	    				defender.setHealth(calculateDamage(defender, 3));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
	    				defender.setHealth(calculateDamage(defender, 4));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
	    				defender.setHealth(calculateDamage(defender, 5));
	    			//If it only would've died from mcMMO damage modifiers
	    			if(defender.getHealth() <= 0 && healthbefore - event.getDamage() >= 1){
	    				mcm.getInstance().simulateNaturalDrops(defender);
	    			}
    			}
    		}
    	}
    }
	public boolean simulateUnarmedProc(Player player){
    	if(mcUsers.getProfile(player).getUnarmedInt() >= 750){
    		if(Math.random() * 10 > 4){
    			return true;
    		}
    	}if(mcUsers.getProfile(player).getUnarmedInt() >= 350 && mcUsers.getProfile(player).getUnarmedInt() < 750){
    		if(Math.random() * 10 > 4){
    			return true;
    		}
    	}
    		return false;
    }
    public void bleedCheck(Player attacker, Entity x){
    	if(mcPermissions.getInstance().swords(attacker) && mcm.getInstance().isSwords(attacker.getItemInHand()) && !mcConfig.getInstance().isBleedTracked(x)){
			if(mcUsers.getProfile(attacker).getSwordsInt() >= 50 && mcUsers.getProfile(attacker).getSwordsInt() < 200){
				if(Math.random() * 10 > 8){
					mcConfig.getInstance().addBleedTrack(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target).setBleedTicks(4);
					}
					attacker.sendMessage(ChatColor.RED+"**Your target is bleeding**");
				}
			} else if(mcUsers.getProfile(attacker).getSwordsInt() >= 200 && mcUsers.getProfile(attacker).getSwordsInt() < 600){
				if(Math.random() * 10 > 6){
					mcConfig.getInstance().addBleedTrack(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target).setBleedTicks(4);
					}
					attacker.sendMessage(ChatColor.RED+"**Your target is bleeding**");
				}
			} else if(mcUsers.getProfile(attacker).getSwordsInt() >= 600 && mcUsers.getProfile(attacker).getSwordsInt() < 900){
				if(Math.random() * 10 > 4){
					mcConfig.getInstance().addBleedTrack(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target).setBleedTicks(6);
					}
					attacker.sendMessage(ChatColor.RED+"**Your target is bleeding**");
				}
			} else if(mcUsers.getProfile(attacker).getSwordsInt() >= 900){
				if(Math.random() * 100 > 25){
					mcConfig.getInstance().addBleedTrack(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target).setBleedTicks(6);
					}
					attacker.sendMessage(ChatColor.RED+"**Your target is bleeding**");
				}
			}
		}
    }
    public int axeNerf(int type){
    	//GOLD OR WOOD
    	if(type == 271 || type == 286){
    		return 3;
    	} else if (type == 258){
    		return 1;
    	} else if (type == 275){
    		return 1;
    	} else {
    		return 0;
    	}
    }
    public int calculateDamage(Player player, int dmg){
    	int health = player.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public int calculateDamage(Squid squid, int dmg){
    	int health = squid.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public int calculateDamage(Monster monster, int dmg){
    	int health = monster.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public int calculateDamage(Animals animal, int dmg){
    	int health = animal.getHealth();
    	if(health - dmg <0){
    		return 0;
    	} else {
    		health-= dmg;
    		return health;
    	}
    }
    public void axeCriticalCheckAnimals(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 50 && mcUsers.getProfile(attacker).getAxesInt() < 250){
    			if(Math.random() * 100 > 95){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					animal.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 250 && mcUsers.getProfile(attacker).getAxesInt() < 500){
    			if(Math.random() * 10 > 9){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					animal.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 500 && mcUsers.getProfile(attacker).getAxesInt() < 750){
    			if(Math.random() * 10 > 8){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					animal.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750 && mcUsers.getProfile(attacker).getAxesInt() < 1000){
    			if(Math.random() * 10 > 7){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					animal.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 1000){
    			if(Math.random() * 10 > 6){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					animal.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    	}
    }
    public void axeCriticalCheckMonster(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 50 && mcUsers.getProfile(attacker).getAxesInt() < 250){
    			if(Math.random() * 100 > 95){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					monster.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 250 && mcUsers.getProfile(attacker).getAxesInt() < 500){
    			if(Math.random() * 10 > 9){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					monster.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 500 && mcUsers.getProfile(attacker).getAxesInt() < 750){
    			if(Math.random() * 10 > 8){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					monster.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750 && mcUsers.getProfile(attacker).getAxesInt() < 1000){
    			if(Math.random() * 10 > 7){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					monster.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 1000){
    			if(Math.random() * 10 > 6){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					monster.setHealth(0);
    					mcm.getInstance().simulateNaturalDrops(x);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    	}
    }
    public void axeCriticalCheckPlayer(Player attacker, EntityDamageByEntityEvent event, Entity x, Plugin plugin){
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 50 && mcUsers.getProfile(attacker).getAxesInt() < 250){
    			if(Math.random() * 100 > 95){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.setHealth(calculateDamage(player, (player.getHealth() - event.getDamage())));
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 250 && mcUsers.getProfile(attacker).getAxesInt() < 500){
    			if(Math.random() * 10 > 9){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.setHealth(calculateDamage(player, (player.getHealth() - event.getDamage())));
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 500 && mcUsers.getProfile(attacker).getAxesInt() < 750){
    			if(Math.random() * 10 > 8){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.setHealth(calculateDamage(player, (player.getHealth() - event.getDamage())));
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750 && mcUsers.getProfile(attacker).getAxesInt() < 1000){
    			if(Math.random() * 10 > 7){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.setHealth(calculateDamage(player, (player.getHealth() - event.getDamage())));
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 1000){
    			if(Math.random() * 10 > 6){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.setHealth(calculateDamage(player, (player.getHealth() - event.getDamage())));
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(x instanceof Player){
    		Player defender = (Player)x;
    		if(defender.getHealth()<= 0 && !mcUsers.getProfile(defender).isDead()){
				mcUsers.getProfile(defender).setDead(true);
				event.setCancelled(true); //SEE IF THIS HELPS
				for(ItemStack herp : defender.getInventory().getContents()){
					if(herp != null && herp.getTypeId() != 0)
					defender.getLocation().getWorld().dropItemNaturally(defender.getLocation(), herp);
				}
				for(Player derp : plugin.getServer().getOnlinePlayers()){
					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"chopped "+ChatColor.GRAY+defender.getName() + " to death.");
					mcUsers.getProfile(defender).setDead(true);
				}
			}
    	}
    	}
    }
    public void parryCheck(Player defender, EntityDamageByEntityEvent event, Entity y){
    	if(mcm.getInstance().isSwords(defender.getItemInHand()) 
    			&& event.getDamage() > 0 
    			&& mcPermissions.getInstance().swords(defender)){
			if(mcUsers.getProfile(defender).getSwordsInt() >= 50 && mcUsers.getProfile(defender).getSwordsInt() < 250){
				if(Math.random() * 100 > 95){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
			if(mcUsers.getProfile(defender).getSwordsInt() >= 250 && mcUsers.getProfile(defender).getSwordsInt() < 450){
				if(Math.random() * 100 > 90){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
			if(mcUsers.getProfile(defender).getSwordsInt() >= 450 && mcUsers.getProfile(defender).getSwordsInt() < 775){
				if(Math.random() * 100 > 85){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
			if(mcUsers.getProfile(defender).getSwordsInt() >= 775){
				if(Math.random() * 100 > 80){
					event.setCancelled(true);
					defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
					defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
					if(y instanceof Player){
						Player attacker = (Player)y;
						attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
					}
					return;
				}
			}
		}
    }
    public void bleedSimulate(){
        for(Entity x : mcConfig.getInstance().getBleedTracked()){
        	if(x == null)
        		continue;
        	if(mcm.getInstance().getHealth(x) <= 0)
        		continue;
    	    if(x instanceof Animals){
    	    	Animals animals = (Animals)x;
    	    	if(animals.getHealth() >= 1){
    	    		animals.setHealth(mcm.getInstance().calculateMinusHealth(animals.getHealth(), 2));
    	    	}
    	    	if(animals.getHealth() <= 0){
    	    		mcm.getInstance().simulateNaturalDrops(x);
    	    	}
    	    }
    	    if(x instanceof Monster){
    	    	Monster monster = (Monster)x;
    	    	if(monster.getHealth() >= 1){
    	    		monster.setHealth(mcm.getInstance().calculateMinusHealth(monster.getHealth(), 2));
    	    	}
    	    	if(monster.getHealth() <= 0){
    	    		mcm.getInstance().simulateNaturalDrops(x);
    	    	}
    	    }
    	    
    	    if(x instanceof Player){
    	    	Player player = (Player)x;
    	    	if(player.getHealth() >= 1 && mcUsers.getProfile(player).getBleedTicks() >= 1){
    	    		player.setHealth(mcm.getInstance().calculateMinusHealth(player.getHealth(), 1));
    	    		player.sendMessage(ChatColor.RED+"**BLEED**");
    	    		if(player.getHealth() <= 0){
    	    			mcUsers.getProfile(player).setBleedTicks(0);
    	    			for(ItemStack items : player.getInventory().getContents()){
    	    				if(items.getTypeId() != 0)
    	    					player.getLocation().getWorld().dropItemNaturally(player.getLocation(), items);
    	    			}
    	    		}
    	    		if(mcUsers.getProfile(player).getBleedTicks() >= 1){
    	    			mcUsers.getProfile(player).setBleedTicks(mcUsers.getProfile(player).getBleedTicks() - 1);
    	    		}
    	    	}
    	    }
        }
        }
	
	
}
