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
import org.bukkit.event.entity.EntityDamageEvent;
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
    		//This may help compatability with NPC mods
    		if(mcUsers.getProfile(defender) == null)
    			mcUsers.addUser(defender);
    		if(mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
				if(mcParty.getInstance().inSameParty(defender, attacker)){
					event.setCancelled(true);
					return;
				}
    		}
    		if(defender != null)
    		mcUsers.getProfile(defender).setRecentlyHurt(30);
    		/*
    		 * AXE CRITICAL CHECK
    		 */
    		axeCriticalCheckPlayer(attacker, event, x, plugin);
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
    		int healthbefore = defender.getHealth();
			if(defender != null && mcPermissions.getInstance().unarmed(attacker) && attacker.getItemInHand().getTypeId() == 0){
				//DMG MODIFIER
				if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
					event.setDamage(calculateDamage(event, 1));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
					event.setDamage(calculateDamage(event, 2));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
					event.setDamage(calculateDamage(event, 3));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
					event.setDamage(calculateDamage(event, 4));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
					event.setDamage(calculateDamage(event, 5));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
					event.setDamage(calculateDamage(event, 6));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
					event.setDamage(calculateDamage(event, 7));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
					event.setDamage(calculateDamage(event, 8));
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
    		 * PVP XP
    		 */
    		if(attacker != null && defender != null && mcLoadProperties.pvpxp && !mcParty.getInstance().inSameParty(attacker, defender)){
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
    		if(mcUsers.getProfile(attacker).getSwordsGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("swords")){
				int skillups = 0;
				while(mcUsers.getProfile(attacker).getSwordsGatherInt() >= mcUsers.getProfile(attacker).getXpToLevel("swords")){
					skillups++;
					mcUsers.getProfile(attacker).removeSwordsGather(mcUsers.getProfile(attacker).getXpToLevel("swords"));
					mcUsers.getProfile(attacker).skillUpSwords(1);
				}
				attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(attacker).getSwords()+")");	
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
					event.setDamage(calculateDamage(event, (4 - axeNerf(attacker.getItemInHand().getTypeId()))));
				}
			}
			/*
			 * UNARMED VS SQUID
			 */
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
    			if(defender.getHealth() <= 0)
    				return;
    			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
					event.setDamage(calculateDamage(event, 1));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
					event.setDamage(calculateDamage(event, 2));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
					event.setDamage(calculateDamage(event, 3));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
					event.setDamage(calculateDamage(event, 4));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
					event.setDamage(calculateDamage(event, 5));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
					event.setDamage(calculateDamage(event, 6));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
					event.setDamage(calculateDamage(event, 7));
				} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
					event.setDamage(calculateDamage(event, 8));
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
					event.setDamage(calculateDamage(event, (4 - axeNerf(attacker.getItemInHand().getTypeId()))));
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
			if(defender.getHealth() <= 0)
				return;
			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
				event.setDamage(calculateDamage(event, 1));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
				event.setDamage(calculateDamage(event, 2));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
				event.setDamage(calculateDamage(event, 3));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
				event.setDamage(calculateDamage(event, 4));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
				event.setDamage(calculateDamage(event, 5));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
				event.setDamage(calculateDamage(event, 6));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
				event.setDamage(calculateDamage(event, 7));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
				event.setDamage(calculateDamage(event, 8));
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
					event.setDamage(calculateDamage(event, (4 - axeNerf(attacker.getItemInHand().getTypeId()))));
				}
			}
			if(type == 0 && mcPermissions.getInstance().unarmed(attacker)){
			if(defender.getHealth() <= 0)
				return;
			if(mcUsers.getProfile(attacker).getUnarmedInt() >= 50 && mcUsers.getProfile(attacker).getUnarmedInt() < 100){
				event.setDamage(calculateDamage(event, 1));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 100 && mcUsers.getProfile(attacker).getUnarmedInt() < 200){
				event.setDamage(calculateDamage(event, 2));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 200 && mcUsers.getProfile(attacker).getUnarmedInt() < 325){
				event.setDamage(calculateDamage(event, 3));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 325 && mcUsers.getProfile(attacker).getUnarmedInt() < 475){
				event.setDamage(calculateDamage(event, 4));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 475 && mcUsers.getProfile(attacker).getUnarmedInt() < 600){
				event.setDamage(calculateDamage(event, 5));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 600 && mcUsers.getProfile(attacker).getUnarmedInt() < 775){
				event.setDamage(calculateDamage(event, 6));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 775 && mcUsers.getProfile(attacker).getUnarmedInt() < 950){
				event.setDamage(calculateDamage(event, 7));
			} else if(mcUsers.getProfile(attacker).getUnarmedInt() >= 950){
				event.setDamage(calculateDamage(event, 8));
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
    			if(event.getDamage() > 0 && Math.random() * 1500 <= mcUsers.getProfile(attacker).getArcheryInt()){
        			if(x instanceof Player){
        				Player Defender = (Player)x;
        				if(!mcParty.getInstance().inSameParty(attacker, Defender)){
        					event.getEntity().setFireTicks(120);
        					attacker.sendMessage(ChatColor.RED+"**IGNITION**");
        					Defender.sendMessage(ChatColor.DARK_RED+"You were struck by a burning arrow!");
        				}
        			} else {
        			event.getEntity().setFireTicks(160);
        			attacker.sendMessage(ChatColor.RED+"**IGNITION**");
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
    			}
    		/*
    		 * Defender is Animals	
    		 */
    		if(x instanceof Animals){
    			Animals defender = (Animals)x;
    			int healthbefore = defender.getHealth();
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
    			Squid defender = (Squid)x;
    			int healthbefore = defender.getHealth();
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
    public int calculateDamage(EntityDamageEvent event, int dmg){
    	return event.getDamage() + dmg;
    }
    public void axeCriticalCheckAnimals(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 50 && mcUsers.getProfile(attacker).getAxesInt() < 250){
    			if(Math.random() * 100 > 95){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					event.setDamage(event.getDamage() + animal.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 250 && mcUsers.getProfile(attacker).getAxesInt() < 500){
    			if(Math.random() * 10 > 9){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					event.setDamage(event.getDamage() + animal.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 500 && mcUsers.getProfile(attacker).getAxesInt() < 750){
    			if(Math.random() * 10 > 8){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					event.setDamage(event.getDamage() + animal.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750 && mcUsers.getProfile(attacker).getAxesInt() < 1000){
    			if(Math.random() * 10 > 7){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					event.setDamage(event.getDamage() + animal.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 1000){
    			if(Math.random() * 10 > 6){
    				if(x instanceof Animals){
    					Animals animal = (Animals)x;
    					event.setDamage(event.getDamage() + animal.getHealth() * 2);
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
    					event.setDamage(event.getDamage() + monster.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 250 && mcUsers.getProfile(attacker).getAxesInt() < 500){
    			if(Math.random() * 10 > 9){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					event.setDamage(event.getDamage() + monster.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 500 && mcUsers.getProfile(attacker).getAxesInt() < 750){
    			if(Math.random() * 10 > 8){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					event.setDamage(event.getDamage() + monster.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750 && mcUsers.getProfile(attacker).getAxesInt() < 1000){
    			if(Math.random() * 10 > 7){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					event.setDamage(event.getDamage() + monster.getHealth() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 1000){
    			if(Math.random() * 10 > 6){
    				if(x instanceof Monster){
    					Monster monster = (Monster)x;
    					event.setDamage(event.getDamage() + monster.getHealth() * 2);
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
    					event.setDamage(event.getDamage() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 250 && mcUsers.getProfile(attacker).getAxesInt() < 500){
    			if(Math.random() * 10 > 9){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					event.setDamage(event.getDamage() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 500 && mcUsers.getProfile(attacker).getAxesInt() < 750){
    			if(Math.random() * 10 > 8){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					event.setDamage(event.getDamage() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750 && mcUsers.getProfile(attacker).getAxesInt() < 1000){
    			if(Math.random() * 10 > 7){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					event.setDamage(event.getDamage() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 1000){
    			if(Math.random() * 10 > 6){
    				if(x instanceof Player){
    					Player player = (Player)x;
    					event.setDamage(event.getDamage() * 2);
    					attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		}
    	}
    }
    public void parryCheck(Player defender, EntityDamageByEntityEvent event, Entity y){
    	if(defender != null && mcm.getInstance().isSwords(defender.getItemInHand()) 
    			&& event.getDamage() > 0 
    			&& mcPermissions.getInstance().swords(defender)){
			if(defender != null && mcUsers.getProfile(defender).getSwordsInt() >= 50 && mcUsers.getProfile(defender).getSwordsInt() < 250){
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
			if(defender != null && mcUsers.getProfile(defender).getSwordsInt() >= 250 && mcUsers.getProfile(defender).getSwordsInt() < 450){
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
			if(defender != null && mcUsers.getProfile(defender).getSwordsInt() >= 450 && mcUsers.getProfile(defender).getSwordsInt() < 775){
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
			if(defender != null && mcUsers.getProfile(defender).getSwordsInt() >= 775){
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
