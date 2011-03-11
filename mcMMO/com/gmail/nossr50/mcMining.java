package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcMining {
	private static mcMMO plugin;
	public mcMining(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcMining instance;
	public static mcMining getInstance() {
    	if (instance == null) {
    	instance = new mcMining(plugin);
    	}
    	return instance;
    	}
	public void blockProcSimulate(Block block){
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		if(block.getTypeId() != 73 && block.getTypeId() != 74 && block.getTypeId() != 56 && block.getTypeId() != 21 && block.getTypeId() != 1 && block.getTypeId() != 16)
		loc.getWorld().dropItemNaturally(loc, item);
		if(block.getTypeId() == 73 || block.getTypeId() == 74){
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			if(Math.random() * 10 > 5){
				loc.getWorld().dropItemNaturally(loc, item);
			}
		}
			if(block.getTypeId() == 21){
				mat = Material.getMaterial(351);
				item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
				loc.getWorld().dropItemNaturally(loc, item);
				loc.getWorld().dropItemNaturally(loc, item);
				loc.getWorld().dropItemNaturally(loc, item);
				loc.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 56){
				mat = Material.getMaterial(264);
				item = new ItemStack(mat, 1, (byte)0, damage);
				loc.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 1){
				mat = Material.getMaterial(4);
				item = new ItemStack(mat, 1, (byte)0, damage);
				loc.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 16){
				mat = Material.getMaterial(263);
				item = new ItemStack(mat, 1, (byte)0, damage);
				loc.getWorld().dropItemNaturally(loc, item);
			}
    }
    public void blockProcCheck(Block block, Player player){
    	if(player != null){
    		if(Math.random() * 1000 <= mcUsers.getProfile(player).getMiningInt()){
    		blockProcSimulate(block);
			return;
    		}
    	}		
	}
    public void miningBlockCheck(Player player, Block block){
    	if(block.getTypeId() == 1 || block.getTypeId() == 24){
    		mcUsers.getProfile(player).addMiningGather(3);
    		blockProcCheck(block, player);
    		}
    		//COAL
    		if(block.getTypeId() == 16){
    		mcUsers.getProfile(player).addMiningGather(10);
    		blockProcCheck(block, player);
    		}
    		//GOLD
    		if(block.getTypeId() == 14){
    		mcUsers.getProfile(player).addMiningGather(35);
    		blockProcCheck(block, player);
    		}
    		//DIAMOND
    		if(block.getTypeId() == 56){
    		mcUsers.getProfile(player).addMiningGather(75);
    		blockProcCheck(block, player);
    		}
    		//IRON
    		if(block.getTypeId() == 15){
    		mcUsers.getProfile(player).addMiningGather(25);
    		blockProcCheck(block, player);
    		}
    		//REDSTONE
    		if(block.getTypeId() == 73 || block.getTypeId() == 74){
    		mcUsers.getProfile(player).addMiningGather(15);
    		blockProcCheck(block, player);
    		}
    		//LAPUS
    		if(block.getTypeId() == 21){
    		mcUsers.getProfile(player).addMiningGather(40);
    		blockProcCheck(block, player);
    		}
    		if(player != null && mcUsers.getProfile(player).getMiningGatherInt() >= mcUsers.getProfile(player).getXpToLevel("mining")){
    			int skillups = 0;
    			while(mcUsers.getProfile(player).getMiningGatherInt() >= mcUsers.getProfile(player).getXpToLevel("mining")){
    				skillups++;
    				mcUsers.getProfile(player).removeMiningGather(mcUsers.getProfile(player).getXpToLevel("mining"));
    				mcUsers.getProfile(player).skillUpMining(1);
    			}
    			player.sendMessage(ChatColor.YELLOW+"Mining skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getMining()+")");	
    		}
    }

}
