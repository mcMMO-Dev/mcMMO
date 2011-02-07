package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class mcPlayerListener extends PlayerListener {
	public Location spawn = null;
    private static mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }
    private static volatile mcPlayerListener instance;
    public static mcPlayerListener getInstance() {
    	if (instance == null) {
    	instance = new mcPlayerListener(plugin);
    	}
    	return instance;
    	}
    public Player[] getPlayersOnline() {
    		return plugin.getServer().getOnlinePlayers();
    }
    public void onPlayerJoin(PlayerEvent event) {
    	Player player = event.getPlayer();
    	mcUsers.addUser(player);
    	player.sendMessage(ChatColor.BLUE + "This server is running mcMMO type "+ChatColor.YELLOW+"/mcmmo "+ChatColor.BLUE+ "for help.");
    	player.sendMessage(ChatColor.RED+"WARNING: "+ChatColor.DARK_GRAY+ "Using /myspawn will clear your inventory!"); 
    }
    //Check if string is a player
    
    public void onPlayerItem(PlayerItemEvent event) {
    	Block block = event.getBlockClicked();
    	Player player = event.getPlayer();
    	ItemStack is = player.getItemInHand();
    	if(block != null && block.getTypeId() == 42){
    	short durability = is.getDurability();
    		if(mcm.getInstance().isArmor(is) && block.getTypeId() == 42){
    			if(mcm.getInstance().isDiamondArmor(is) && mcm.getInstance().hasDiamond(player)){
    			mcm.getInstance().removeDiamond(player);
    			player.getItemInHand().setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
    			mcUsers.getProfile(player).skillUpRepair(1);
    			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
    			} else if (mcm.getInstance().isIronArmor(is) && mcm.getInstance().hasIron(player)){
    			player.sendMessage(ChatColor.DARK_RED+"Changing the durability of iron armor is currently bugged.");
    			player.sendMessage(ChatColor.YELLOW+"I'm looking into this issue. -mcMMO Author");
    			/*
    			mcm.getInstance().removeIron(player);
        		is.setDurability(mcm.getInstance().getArmorRepairAmount(is, player));
        		mcUsers.getProfile(player).skillUpRepair(1);
        		player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");	
        		*/
    			}
    		}
    		if(mcm.getInstance().isTools(is) && block.getTypeId() == 42){
    		if(mcm.getInstance().isIronTools(is) && mcm.getInstance().hasIron(player)){
    			is.setDurability(mcm.getInstance().getToolRepairAmount(is, durability, player));
    			mcm.getInstance().removeIron(player);
    			mcUsers.getProfile(player).skillUpRepair(1);
    			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
    		} else if (mcm.getInstance().isDiamondTools(is) && mcm.getInstance().hasDiamond(player) && mcUsers.getProfile(player).getRepairInt() > 50){ //Check if its diamond and the player has diamonds
    			is.setDurability(mcm.getInstance().getToolRepairAmount(is, durability, player));
    			mcm.getInstance().removeDiamond(player);
    			mcUsers.getProfile(player).skillUpRepair(1);
    			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by 1. Total ("+mcUsers.getProfile(player).getRepair()+")");
    		} else if (mcm.getInstance().isDiamondTools(is) && mcUsers.getProfile(player).getRepairInt() < 50){
    			player.sendMessage(ChatColor.DARK_RED +"You're not adept enough to repair Diamond");
    		} else if (mcm.getInstance().isDiamondTools(is) && !mcm.getInstance().hasDiamond(player) || mcm.getInstance().isIronTools(is) && !mcm.getInstance().hasIron(player)){
    			if(!mcm.getInstance().hasDiamond(player))
    				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
    			if(!mcm.getInstance().hasIron(player))
    				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
    		} else if (mcm.getInstance().isDiamondArmor(is) && !mcm.getInstance().hasDiamond(player)){
    			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
    		} else if (mcm.getInstance().isIronArmor(is) && !mcm.getInstance().hasIron(player))
    			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
    	}
    	}
    }
    public void onPlayerCommand(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	mcc.getInstance().CommandCheck(player, split, spawn);
    }
	public void onPlayerChat(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String[] split = event.getMessage().split(" ");
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";
    	if(mcConfig.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(mcUsers.getProfile(herp).inParty()){
    			if(mcm.inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if(player.isOp() && mcConfig.getInstance().isAdminToggled(player.getName())){
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(herp.isOp()){
    				herp.sendMessage(y+event.getMessage());
    			}
    		}
    		return;
    	}
    	if(player.isOp()){
    		event.setCancelled(true);
    		for(Player derp : plugin.getServer().getOnlinePlayers()){
    			String z = ChatColor.RED + "<" + ChatColor.WHITE + player.getName() + ChatColor.RED + "> "+ChatColor.WHITE;
    			derp.sendMessage(z+event.getMessage());
    		}
    	}
    	event.setCancelled(true);
    	
    	}
}