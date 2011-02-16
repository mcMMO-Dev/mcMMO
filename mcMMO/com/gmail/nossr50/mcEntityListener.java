package com.gmail.nossr50;

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
    		/*
    		if(player.getHealth() - event.getDamage() <= 0){
    			mcUsers.getProfile(player).setDead(true);
    			for(Player bidoof : plugin.getServer().getOnlinePlayers()){
    				bidoof.sendMessage(ChatColor.GRAY+player.getName()+" has been"+ChatColor.DARK_GREEN+" cactus tickled "+ChatColor.GRAY+"to death.");
    			}
    		}
    		*/
    	}
    	}
    	}
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	Entity x = event.getEntity(); //Defender
    	Entity y = event.getDamager(); //Attacker
    	/*
    	 * IF DEFENDER IS PLAYER
    	 */
    	if(x instanceof Player){
    		Player defender = (Player)x;
    		/*
    		 * PARRYING CHECK, CHECK TO SEE IF ITS A SUCCESSFUL PARRY OR NOT
    		 */
    		mcm.getInstance().parryCheck(defender, event, y);
    		/*
    		 * PLAYER DEATH BY MONSTER MESSAGE CHECK, CHECKS TO SEE IF TO REPORT THE DEATH OR NOT
    		 */
    		mcm.getInstance().playerDeathByMonsterMessageCheck(y, defender, plugin);
    		/*
    		 * CHECKS IF THE PLAYER DIES, IF SO DROP HIS SHIT BECAUSE OF THE DAMAGE MODIFIERS
    		 * MIGHT BE A BIT BUGGY, IT SEEMS TO WORK RIGHT NOW AT LEAST...
    		 */

    	}
    	/*
    	 * IF ATTACKER IS PLAYER
    	 */
    	if(y instanceof Player){
    		int type = ((Player) y).getItemInHand().getTypeId();
    		Player attacker = (Player)y;
    		/*
    		 * Player versus Monster checks, this handles all skill damage modifiers and any procs.
    		 */
    		mcm.getInstance().playerVersusMonsterChecks(event, attacker, x, type);
    		/*
    		 * Player versus Squid checks, this handles all skill damage modifiers and any procs.
    		 */
    		mcm.getInstance().playerVersusSquidChecks(event, attacker, x, type);
    		/*
    		 * Player versus Player checks, these checks make sure players are not in the same party, etc. They also check for any procs from skills and handle damage modifiers.
    		 */
    		mcm.getInstance().playerVersusPlayerChecks(x, attacker, event, plugin);
    		/*
    		 * Player versus Animals checks, these checks handle any skill modifiers or procs
    		 */
    		mcm.getInstance().playerVersusAnimalsChecks(x, attacker, event, type);
    	}
    	}
    public boolean isBow(ItemStack is){
    	if (is.getTypeId() == 261){
    		return true;
    	} else {
    		return false;
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
    		if(mcPermissions.getInstance().archery(attacker)){
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			Monster defender = (Monster)x;
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 5));
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
    			Animals defender = (Animals)x;
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 5));
    			if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    		/*
    		 * Defender is Squid
    		 */
    		if(x instanceof Squid){
    			Squid defender = (Squid)x;
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 1));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 2));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 3));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 4));
    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 5));
    			if(defender.getHealth() <= 0)
    				mcm.getInstance().simulateNaturalDrops(defender);
    			}
    		/*
    		 * Attacker is Player
    		 */
    		if(x instanceof Player){
    			Player defender = (Player)x;
    			/*
    			 * Stuff for the daze proc
    			 */
    	    		if(mcUsers.getProfile(attacker).inParty() && mcUsers.getProfile(defender).inParty()){
    					if(mcm.getInstance().inSameParty(defender, attacker)){
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
					if(mcUsers.getProfile(attacker).getArcheryInt() >= 50 && mcUsers.getProfile(attacker).getArcheryInt() < 250)
	    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 1));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 250 && mcUsers.getProfile(attacker).getArcheryInt() < 575)
	    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 2));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 575 && mcUsers.getProfile(attacker).getArcheryInt() < 725)
	    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 3));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 725 && mcUsers.getProfile(attacker).getArcheryInt() < 1000)
	    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 4));
	    			if(mcUsers.getProfile(attacker).getArcheryInt() >= 1000)
	    				defender.setHealth(mcm.getInstance().calculateDamage(defender, 5));
    				if(defender.getHealth() >= 0){
    					if(mcUsers.getProfile(defender).isDead())
            				return;
    					if(defender.getHealth() <= 0){
    						mcUsers.getProfile(defender).setDead(true);
            				for(Player derp : plugin.getServer().getOnlinePlayers()){
            					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName() + " with an arrow.");
            				}
            			}
    				}

    			if(mcUsers.getProfile(defender).isDead())
    				return;
    			if(defender.getHealth() - event.getDamage() <= 0){
    				for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName() + " with the bow and arrow.");
    					mcUsers.getProfile(defender).setDead(true);
    				}
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
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 50 
    				&& mcUsers.getProfile(player).getAcrobaticsInt() < 250
    				&& mcPermissions.getInstance().acrobatics(player)){
    			if(Math.random() * 10 > 8){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 250 
    				&& mcUsers.getProfile(player).getAcrobaticsInt() < 450 
    				&& mcPermissions.getInstance().acrobatics(player)){
    			if(Math.random() * 10 > 6){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 450 
    				&& mcUsers.getProfile(player).getAcrobaticsInt() < 750 
    				&& mcPermissions.getInstance().acrobatics(player)){
    			if(Math.random() * 10 > 4){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 750 
    				&& mcUsers.getProfile(player).getAcrobaticsInt() < 950 
    				&& mcPermissions.getInstance().acrobatics(player)){
    			if(Math.random() * 10 > 2){
    				event.setCancelled(true);
    				player.sendMessage("**BARREL ROLLED**");
    				return;
    			}
    		}
    		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 950
    				&& mcPermissions.getInstance().acrobatics(player)){
    				event.setCancelled(true);
    				player.sendMessage("**ROLLED... LIKE A BOSS**");
    				return;
    			}
    		if(player.getHealth() - event.getDamage() <= 0)
    			return;
    		if(!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z)) 
    				&& mcPermissions.getInstance().acrobatics(player)){
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
    	/*
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
    	*/
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
