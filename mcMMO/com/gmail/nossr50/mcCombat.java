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
    		if(attacker != null && defender != null && mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
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
    		axeCriticalCheck(attacker, event, x);
    		if(!mcConfig.getInstance().isBleedTracked(x)){
    			bleedCheck(attacker, x);
    		}
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
					mcUsers.getProfile(attacker).addSwordsGather(10);
			}
			mcSkills.getInstance().XpCheck(attacker);
			if(mcm.getInstance().isAxes(attacker.getItemInHand()) 
					&& defender.getHealth() > 0 
					&& mcPermissions.getInstance().axes(attacker)){
					mcUsers.getProfile(attacker).addAxesGather(10);
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
					mcSkills.getInstance().XpCheck(attacker);
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
			mcSkills.getInstance().XpCheck(attacker);
			}
		}
    }
	public void archeryCheck(EntityDamageByProjectileEvent event){
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	if(x instanceof Player){
    		Player defender = (Player)x;
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0){
	    		if(mcUsers.getProfile(defender).getUnarmedInt() >= 1000){
	    			if(Math.random() * 1000 >= 500){
	    				event.setCancelled(true);
	    				defender.sendMessage(ChatColor.WHITE+"**ARROW DEFLECT**");
	    				return;
	    			}
	    		} else if(Math.random() * 1000 <= (mcUsers.getProfile(defender).getUnarmedInt() / 2)){
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
    		mcSkills.getInstance().XpCheck(attacker);
    	}
    }
	public boolean simulateUnarmedProc(Player player){
    	if(mcUsers.getProfile(player).getUnarmedInt() >= 750){
    		if(Math.random() * 1000 >= 750){
    			return true;
    		}
    	} else {
    		if(Math.random() * 1000 <= mcUsers.getProfile(player).getUnarmedInt()){
    			return true;
    		}
    	}
    		return false;
    }
    public void bleedCheck(Player attacker, Entity x){
    	if(mcPermissions.getInstance().swords(attacker) && mcm.getInstance().isSwords(attacker.getItemInHand()) && !mcConfig.getInstance().isBleedTracked(x)){
			if(mcUsers.getProfile(attacker).getSwordsInt() >= 750){
				if(Math.random() * 1000 >= 750){
					mcConfig.getInstance().addBleedTrack(x);
					if(x instanceof Player){
						Player target = (Player)x;
						mcUsers.getProfile(target).setBleedTicks(6);
					}
					attacker.sendMessage(ChatColor.RED+"**Your target is bleeding**");
				}
			} else if (Math.random() * 1000 <= mcUsers.getProfile(attacker).getSwordsInt()){
				mcConfig.getInstance().addBleedTrack(x);
				if(x instanceof Player){
					Player target = (Player)x;
					mcUsers.getProfile(target).setBleedTicks(4);
				}
				attacker.sendMessage(ChatColor.RED+"**Your target is bleeding**");
			}
		}
    }
    public int calculateDamage(EntityDamageEvent event, int dmg){
    	return event.getDamage() + dmg;
    }
    public void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event, Entity x){
    	if(mcm.getInstance().isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(mcUsers.getProfile(attacker).getAxesInt() >= 750){
    			if(Math.random() * 1000 >= 750){
    				event.setDamage(event.getDamage() * 2);
    				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				if(x instanceof Player){
    					Player player = (Player)x;
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    			}
    		} else if(Math.random() * 1000 <= mcUsers.getProfile(attacker).getAxesInt()){
    			if(x instanceof Player){
    				Player player = (Player)x;
    				event.setDamage(event.getDamage() * 2);
    				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    				player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
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
    	    		animals.damage(1);
    	    	}
    	    	if(animals.getHealth() <= 0){
    	    		animals.damage(1);
    	    	}
    	    }
    	    if(x instanceof Monster){
    	    	Monster monster = (Monster)x;
    	    	if(monster.getHealth() >= 1){
    	    		monster.damage(1);
    	    	}
    	    	if(monster.getHealth() <= 0){
    	    		monster.damage(1);
    	    	}
    	    }
    	    
    	    if(x instanceof Player){
    	    	Player player = (Player)x;
    	    	if(player.getHealth() >= 1 && mcUsers.getProfile(player).getBleedTicks() >= 1){
    	    		player.damage(1);
    	    		player.sendMessage(ChatColor.RED+"**BLEED**");
    	    		if(player.getHealth() <= 0){
    	    			mcUsers.getProfile(player).setBleedTicks(0);	
    	    		}
    	    		if(mcUsers.getProfile(player).getBleedTicks() >= 1){
    	    			mcUsers.getProfile(player).setBleedTicks(mcUsers.getProfile(player).getBleedTicks() - 1);
    	    		}
    	    	}
    	    }
        }
    }
	
	
}
