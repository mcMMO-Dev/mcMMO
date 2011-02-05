package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
    		Player attacker = (Player)y;
    		//If defender is player
    		if(x instanceof Player){
    			Player defender = (Player)x;
    			if(mcUsers.getProfile(defender).getParty().equals(mcUsers.getProfile(attacker).getParty())){
    				event.setCancelled(true);
    			}
    			if((defender.getHealth() - event.getDamage()) <= 0){
    				for(Player derp : plugin.getServer().getOnlinePlayers()){
    					derp.sendMessage(ChatColor.GRAY+attacker.getName() + " has " +ChatColor.DARK_RED+"slain "+ChatColor.GRAY+defender.getName());
    					mcUsers.getProfile(defender).setDead(true);
    				}
    			}
    		}
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
