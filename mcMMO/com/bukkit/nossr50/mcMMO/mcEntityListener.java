package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class mcEntityListener extends EntityListener {
	private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
    	Block block = event.getDamager();
    	Entity x = event.getEntity();
    	if(x instanceof Player){
    	Player player = (Player)x;
    	if(block != null && block.getTypeId() == 81){
    		if(mcUsers.getProfile(player).isDead())
    			return;
    		if(player.getHealth() - event.getDamage() <= 0){
    			mcUsers.getProfile(player).setDead(true);
    			for(Player bidoof : plugin.getServer().getOnlinePlayers()){
    				bidoof.sendMessage(ChatColor.GRAY+player.getName()+" has been"+ChatColor.DARK_GREEN+" cactus tickled "+ChatColor.GRAY+"to death.");
    			}
    		}
    	}
    	}
    	}
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	Entity x = event.getEntity(); //Defender
    	Entity y = event.getDamager(); //Attacker
    	//If attacker is player...
    	if(x instanceof Player){
    		Player defender = (Player)x;
    		/*
    		 * PARRYING
    		 */
    		if(isSwords(defender.getItemInHand())){
				if(mcUsers.getProfile(defender).getSwordsInt() >= 50 && mcUsers.getProfile(defender).getSwordsInt() < 250){
					if(Math.random() * 10 > 8){
						event.setCancelled(true);
						defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
						defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
						mcUsers.getProfile(defender).skillUpSwords(1);
    					defender.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(defender).getSwords()+")");
						if(y instanceof Player){
							Player attacker = (Player)y;
							attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
						}
					}
				}
				if(mcUsers.getProfile(defender).getSwordsInt() >= 250 && mcUsers.getProfile(defender).getSwordsInt() < 450){
					if(Math.random() * 10 > 6){
						event.setCancelled(true);
						defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
						defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
						mcUsers.getProfile(defender).skillUpSwords(1);
    					defender.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(defender).getSwords()+")");
						if(y instanceof Player){
							Player attacker = (Player)y;
							attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
						}
					}
				}
				if(mcUsers.getProfile(defender).getSwordsInt() >= 450 && mcUsers.getProfile(defender).getSwordsInt() < 775){
					if(Math.random() * 10 > 5){
						event.setCancelled(true);
						defender.sendMessage(ChatColor.YELLOW+"*CLANG* SUCCESSFUL PARRY *CLANG*");
						defender.getItemInHand().setDurability((short) (defender.getItemInHand().getDurability() + 1));
						mcUsers.getProfile(defender).skillUpSwords(1);
    					defender.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(defender).getSwords()+")");
						if(y instanceof Player){
							Player attacker = (Player)y;
							attacker.sendMessage(ChatColor.DARK_RED+"**TARGET HAS PARRIED THAT ATTACK**");
						}
					}
				}
				if(mcUsers.getProfile(defender).getSwordsInt() >= 775){
					if(Math.random() * 10 > 4){
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
    		/*
    		 * DEATH MESSAGES
    		 */
    		if(y instanceof Monster){
    			if(mcUsers.getProfile(defender).isDead())
    				return;
    			if(defender.getHealth() - event.getDamage() <= 0){
    				defender.setHealth(0);
    				mcm.getInstance().simulateNaturalDrops(defender);
    				if(y instanceof Creeper){
    					mcUsers.getProfile(defender).setDead(true);
    					for(Player derp : plugin.getServer().getOnlinePlayers()){
        					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.DARK_GREEN+"Creeper"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
        				}
    				}
    				if(y instanceof Skeleton){
    					mcUsers.getProfile(defender).setDead(true);
    					for(Player derp : plugin.getServer().getOnlinePlayers()){
        					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.WHITE+"Skeleton"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
        				}
    				}
    				if(y instanceof Spider){
    					mcUsers.getProfile(defender).setDead(true);
    					for(Player derp : plugin.getServer().getOnlinePlayers()){
        					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.DARK_PURPLE+"Spider"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
        				}
    				}
    				if(y instanceof Zombie){
    					mcUsers.getProfile(defender).setDead(true);
    					for(Player derp : plugin.getServer().getOnlinePlayers()){
        					derp.sendMessage(ChatColor.GRAY + "A "+ChatColor.DARK_BLUE+"Zombie"+ChatColor.GRAY+" has killed "+ChatColor.DARK_RED+defender.getName());
        				}
    				}
    			}
    		}
    		if(defender.getHealth() <= 0){
				for(ItemStack i : defender.getInventory().getContents()){
					if(i != null && i.getTypeId() != 0)
					defender.getLocation().getWorld().dropItemNaturally(defender.getLocation(), i);
				}
			}
    	}
    	if(y instanceof Player){
    		int type = ((Player) y).getItemInHand().getTypeId();
    		Player attacker = (Player)y;
    		if(x instanceof Squid){
    			Squid defender = (Squid)event.getEntity();
    			if(isSwords(attacker.getItemInHand()) && defender.getHealth() > 0){
    				if(Math.random() * 10 > 9){
    					mcUsers.getProfile(attacker).skillUpSwords(1);
    					attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(attacker).getSwords()+")");
    				}
    			}
    			/*
    			 * UNARMED VS SQUID
    			 */
    			if(type == 0){
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
    				if(attacker.getItemInHand().getTypeId() == 0 && Math.random() * 10 > 8){
    					if(defender.getHealth() != 0){
    					mcUsers.getProfile(attacker).skillUpUnarmed(1);
    					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
    					}
    				}
    				if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
        			}
    		}
    		if(x instanceof Monster){
    			Monster defender = (Monster)event.getEntity();
    			if(isSwords(attacker.getItemInHand()) && defender.getHealth() > 0){
    				if(Math.random() * 10 > 9){
    					mcUsers.getProfile(attacker).skillUpSwords(1);
    					attacker.sendMessage(ChatColor.YELLOW+"Swords skill increased by 1. Total ("+mcUsers.getProfile(attacker).getSwords()+")");
    				}
    			}
    			if(type == 0){
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
    			if(x instanceof Skeleton && Math.random() * 100 > 95){
    				if(defender.getHealth() != 0){
    					mcUsers.getProfile(attacker).skillUpUnarmed(1);
    					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
    			}
    			}
    			if(x instanceof Spider&& Math.random() * 10 > 9){
    				if(defender.getHealth() != 0){
    					mcUsers.getProfile(attacker).skillUpUnarmed(1);
    					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
    			}
    			}
    			if(x instanceof Zombie && Math.random() * 100 > 95){
    				if(defender.getHealth() != 0){
    					mcUsers.getProfile(attacker).skillUpUnarmed(1);
    					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
    			}
    			}
    			if(x instanceof Creeper && Math.random() * 100 > 90){
        				if(defender.getHealth() != 0){
        					mcUsers.getProfile(attacker).skillUpUnarmed(2);
        					attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 2. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
        		}
    			}
    			if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    		}
    		if(x instanceof Animals){
    			if(type == 0){
    			Animals defender = (Animals)event.getEntity();
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
    		//If defender is player
    		if(x instanceof Player){
    			Player defender = (Player)x;
    			if(attacker.getItemInHand().getTypeId() == 0){
    				//DMG MODIFIER
    				if((mcUsers.getProfile(defender).inParty() && mcUsers.getProfile(attacker).inParty())&& !mcUsers.getProfile(defender).getParty().equals(mcUsers.getProfile(attacker).getParty()) && !mcUsers.getProfile(defender).getParty().equals(mcUsers.getProfile(attacker).getParty())) {
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
    				//XP
    				if(attacker.getItemInHand().getTypeId() == 0 && Math.random() * 10 > 9){
    					if(defender.getHealth() != 0){
    						mcUsers.getProfile(attacker).skillUpUnarmed(1);
    						attacker.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by 1. Total ("+mcUsers.getProfile(attacker).getUnarmed()+")");
    						}
    				}
    				//PROC
    				if(simulateUnarmedProc(attacker)){
    					attacker.sendMessage(ChatColor.DARK_RED+"You have hit with great force.");
    					Location loc = defender.getLocation();
    					if(defender.getItemInHand() != null && defender.getItemInHand().getTypeId() != 0){
    					ItemStack item = defender.getItemInHand();
    					if(item != null){
    					loc.getWorld().dropItemNaturally(loc, item);
    					item.setTypeId(0);
    					item.setAmount(0);
    					}
    					}
    				}
    				if(defender.getHealth() <= 0){
    					for(ItemStack i : defender.getInventory().getContents()){
    						if(i != null && i.getTypeId() != 0)
    						defender.getLocation().getWorld().dropItemNaturally(defender.getLocation(), i);
    					}
        				for(Player derp : plugin.getServer().getOnlinePlayers()){
        					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName());
        					mcUsers.getProfile(defender).setDead(true);
        				}
        			}
    				}
    				return;
    			}
    			if(mcUsers.getProfile(defender).isDead())
    				return;
    			if((defender.getHealth() - event.getDamage()) <= 0 && defender.getHealth() != 0){
    				for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName());
    					mcUsers.getProfile(defender).setDead(true);
    				}
    			}
    			//Moving this below the death message for now, seems to have issues when the defender is not in a party
    			if((mcUsers.getProfile(defender).inParty() && mcUsers.getProfile(attacker).inParty())&& mcUsers.getProfile(defender).getParty().equals(mcUsers.getProfile(attacker).getParty()))
    				event.setCancelled(true);
    		}
    	}
    	}
    public boolean isSwords(ItemStack is){
    	if(is.getTypeId() == 268 || is.getTypeId() == 267 || is.getTypeId() == 271 || is.getTypeId() == 283 || is.getTypeId() == 276){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isBow(ItemStack is){
    	if (is.getTypeId() == 261){
    		return true;
    	} else {
    		return false;
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
    public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	/*
    	 * Defender is player
    	 */
    	if(y instanceof Player){
    		Player attacker = (Player)y;
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			Monster defender = (Monster)x;
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 150)
    				defender.setHealth(defender.getHealth() - 1);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 150 && mcUsers.getProfile(attacker).getArcheryInt() < 375)
    				defender.setHealth(defender.getHealth() - 2);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 375 && mcUsers.getProfile(attacker).getArcheryInt() < 525)
    				defender.setHealth(defender.getHealth() - 3);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 525 && mcUsers.getProfile(attacker).getArcheryInt() < 800)
    				defender.setHealth(defender.getHealth() - 4);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 800 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(defender.getHealth() - 6);
    			if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
    			//XP
    			if(Math.random() * 10 > 7){
    				mcUsers.getProfile(attacker).skillUpArchery(1);
    				attacker.sendMessage(ChatColor.YELLOW+"Archery skill increased by 1. Total ("+mcUsers.getProfile(attacker).getArchery()+")");
    			}
    			}
    		/*
    		 * Defender is Animals	
    		 */
    		if(x instanceof Animals){
    			Monster defender = (Monster)x;
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 150)
    				defender.setHealth(defender.getHealth() - 1);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 150 && mcUsers.getProfile(attacker).getArcheryInt() < 375)
    				defender.setHealth(defender.getHealth() - 2);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 375 && mcUsers.getProfile(attacker).getArcheryInt() < 525)
    				defender.setHealth(defender.getHealth() - 3);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 525 && mcUsers.getProfile(attacker).getArcheryInt() < 800)
    				defender.setHealth(defender.getHealth() - 4);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 800 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(defender.getHealth() - 6);
    			if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    		/*
    		 * Defender is Squid
    		 */
    		if(x instanceof Squid){
    			Monster defender = (Monster)x;
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 150)
    				defender.setHealth(defender.getHealth() - 1);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 150 && mcUsers.getProfile(attacker).getArcheryInt() < 375)
    				defender.setHealth(defender.getHealth() - 2);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 375 && mcUsers.getProfile(attacker).getArcheryInt() < 525)
    				defender.setHealth(defender.getHealth() - 3);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 525 && mcUsers.getProfile(attacker).getArcheryInt() < 800)
    				defender.setHealth(defender.getHealth() - 4);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 800 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(defender.getHealth() - 6);
    			if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    		/*
    		 * Attacker is Player
    		 */
    		if(x instanceof Player){
    			Player defender = (Player)x;
    				Location loc = defender.getLocation();
					loc.setPitch(90);
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 200 && mcUsers.getProfile(attacker).getArcheryInt() < 400){
    				if(Math.random() * 10 > 7){
    					defender.teleportTo(loc);
    					defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
    					attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    				}
					}
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 400){
	    				if(Math.random() * 10 > 4){
	    					defender.teleportTo(loc);
	    					defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
	    					attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
	    				}
						}
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 150)
    				defender.setHealth(defender.getHealth() - 1);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 150 && mcUsers.getProfile(attacker).getArcheryInt() < 375)
    				defender.setHealth(defender.getHealth() - 2);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 375 && mcUsers.getProfile(attacker).getArcheryInt() < 525)
    				defender.setHealth(defender.getHealth() - 3);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 525 && mcUsers.getProfile(attacker).getArcheryInt() < 800)
    				defender.setHealth(defender.getHealth() - 4);
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 800 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(defender.getHealth() - 6);
    				if(defender.getHealth() >= 0){
    					if(mcUsers.getProfile(defender).isDead())
            				return;
    					if(defender.getHealth() <= 0){
        					for(ItemStack i : defender.getInventory().getContents()){
        						if(i != null && i.getTypeId() != 0)
        						defender.getLocation().getWorld().dropItemNaturally(defender.getLocation(), i);
        					}
            				for(Player derp : plugin.getServer().getOnlinePlayers()){
            					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName() + " with an arrow.");
            					mcUsers.getProfile(defender).setDead(true);
            				}
            			}
    				}

    			if(mcUsers.getProfile(defender).isDead())
    				return;
    			if(defender.getHealth() - event.getDamage() <= 0){
    				for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName() + " with an arrow.");
    					mcUsers.getProfile(defender).setDead(true);
    				}
    				}
    			}
    		}
    	
    	}
    public void onEntityDamage(EntityDamageEvent event) {
    	Entity x = event.getEntity();
    	if(x instanceof Player){
    	Player player = (Player)x;
    	DamageCause type = event.getCause();
    	Location loc = player.getLocation();
    	int xx = loc.getBlockX();
    	int y = loc.getBlockY();
    	int z = loc.getBlockZ();
    	if(type == DamageCause.FALL){
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 50 && mcUsers.getProfile(player).getAcrobaticsInt() < 150 ){
    			if(Math.random() * 10 > 8){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 150 && mcUsers.getProfile(player).getAcrobaticsInt() < 250 ){
    			if(Math.random() * 10 > 6){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 250 && mcUsers.getProfile(player).getAcrobaticsInt() < 350 ){
    			if(Math.random() * 10 > 4){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 350 && mcUsers.getProfile(player).getAcrobaticsInt() < 450 ){
    			if(Math.random() * 10 > 2){
    				event.setCancelled(true);
    				player.sendMessage("**BARREL ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 450){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED... LIKE A BOSS**");
    				return;
    			}
    		if(player.getHealth() - event.getDamage() <= 0)
    			return;
    		if(!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z))){
    		if(event.getDamage() >= 2 && event.getDamage() < 6){
    		mcUsers.getProfile(player).skillUpAcrobatics(1);
    		player.sendMessage(ChatColor.YELLOW+"Acrobatics skill increased by 1. Total ("+mcUsers.getProfile(player).getAcrobatics()+")");
    		}
    		if(event.getDamage() >= 6 && event.getDamage() < 19){
        		mcUsers.getProfile(player).skillUpAcrobatics(2);
    			player.sendMessage(ChatColor.YELLOW+"Acrobatics skill increased by 2. Total ("+mcUsers.getProfile(player).getAcrobatics()+")");
    		}
    		if(event.getDamage() >= 19){
        		mcUsers.getProfile(player).skillUpAcrobatics(3);
    			player.sendMessage(ChatColor.YELLOW+"Acrobatics skill increased by 3. Total ("+mcUsers.getProfile(player).getAcrobatics()+")");
    		}
    		}
    		mcConfig.getInstance().addBlockWatch(loc.getWorld().getBlockAt(xx, y, z));
    		if(player.getHealth() - event.getDamage() <= 0){
    			if(mcUsers.getProfile(player).isDead())
        			return;
    			mcUsers.getProfile(player).setDead(true);
    			for(Player bidoof : plugin.getServer().getOnlinePlayers()){
    				bidoof.sendMessage(ChatColor.GRAY+player.getName()+" has "+ChatColor.DARK_RED+"fallen "+ChatColor.GRAY+"to death.");
    			}
    		}
    		}
    	if(type == DamageCause.DROWNING){
    		if(mcUsers.getProfile(player).isDead())
    			return;
    		if(player.getHealth() - event.getDamage() <= 0){
    			mcUsers.getProfile(player).setDead(true);
    			for(Player slipslap : plugin.getServer().getOnlinePlayers()){
    				slipslap.sendMessage(ChatColor.GRAY+player.getName()+" has "+ChatColor.AQUA+"drowned.");
    			}
    		}
    	}
    	if(type == DamageCause.FIRE || type == DamageCause.FIRE_TICK){
    		if(mcUsers.getProfile(player).isDead())
    			return;
    		if(player.getHealth() - event.getDamage() <= 0){
    			mcUsers.getProfile(player).setDead(true);
    			for(Player slipslap : plugin.getServer().getOnlinePlayers()){
    				slipslap.sendMessage(ChatColor.GRAY+player.getName()+" has "+ChatColor.RED+"burned "+ChatColor.GRAY+"to death.");
    			}
    		}
    	}
    	if(type == DamageCause.LAVA){
    		if(mcUsers.getProfile(player).isDead())
    			return;
    		if(player.getHealth() - event.getDamage() <= 0){
    			mcUsers.getProfile(player).setDead(true);
    			for(Player slipslap : plugin.getServer().getOnlinePlayers()){
    				slipslap.sendMessage(ChatColor.GRAY+player.getName()+" has "+ChatColor.RED+"melted "+ChatColor.GRAY+".");
    			}
    		}
    	}
    	}
    }
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity x = event.getEntity();
    	if(x instanceof Player){
    		Player player = (Player)x;
    		if(mcUsers.getProfile(player).isDead()){
    			 mcUsers.getProfile(player).setDead(false);
    			 return;
    		}
    		for(Player derp : plugin.getServer().getOnlinePlayers()){
    		derp.sendMessage(ChatColor.GRAY+player.getName()+" has died.");
    		}
    	}
    }
    public boolean isPlayer(Entity entity){
    	if (entity instanceof Player) {
    	    return true;
    	} else{
    		return false;
    	}
    }
}
