package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class mcEntityListener extends EntityListener {
	private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	Entity x = event.getEntity(); //Defender
    	Entity y = event.getDamager(); //Attacker
    	//If attacker is player...
    	if(y instanceof Player){
    		int type = ((Player) y).getItemInHand().getTypeId();
    		Player attacker = (Player)y;
    		if(x instanceof Squid){
    			if(type == 0){
        			Squid defender = (Squid)event.getEntity();
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
    			if(type == 0){
    			Monster defender = (Monster)event.getEntity();
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
    public void onEntityDamage(EntityDamageEvent event) {
    	//Thanks to TimberJaw for sharing his source code!
    	Entity x = event.getEntity();
    	if(x instanceof Player){
    		Player player = (Player)x;
    		if((player.getHealth() - event.getDamage()) <= 0){
    		Location deathLoc = player.getLocation();
    		ItemStack[] items = player.getInventory().getContents();
    		for(int i = 0; i < items.length; i++)
    		{
	    		ItemStack is = items[i];
	    		if(is != null && is.getAmount() > 0)
	    		{
	    			player.getWorld().dropItemNaturally(deathLoc, is);
	    		}
    		}
    		player.setHealth(20);
			player.teleportTo(mcUsers.getProfile(player).getMySpawn(player));
			if(mcUsers.getProfile(player).isDead()){
    			mcUsers.getProfile(player).setDead(false);
    			return;
    		}
			for(Player derp : plugin.getServer().getOnlinePlayers()){
				derp.sendMessage(ChatColor.GRAY+player.getName() + " has died.");
			}
    		}
    	}
    }
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity x = event.getEntity();
    	if(x instanceof Player){
    		Player player = (Player)x;
    		if(mcUsers.getProfile(player).isDead()){
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
