package com.bukkit.nossr50.mcMMO;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcConfig {
	private static volatile mcConfig instance;
    String location = "mcmmo.properties";
    protected static final Logger log = Logger.getLogger("Minecraft");
    static ArrayList<String> adminChatList = new ArrayList<String>();
    static ArrayList<Block> blockWatchList = new ArrayList<Block>();
    static ArrayList<String> partyChatList = new ArrayList<String>();
    public boolean isBlockWatched(Block block) {return blockWatchList.contains(block);}
    public void removeBlockWatch(Block block) {blockWatchList.remove(blockWatchList.indexOf(block));}
    public void addBlockWatch(Block block) {blockWatchList.add(block);}
	public boolean isAdminToggled(String playerName) {return adminChatList.contains(playerName);}
    public boolean isPartyToggled(String playerName) {return partyChatList.contains(playerName);}
    public void removePartyToggled(String playerName) {partyChatList.remove(partyChatList.indexOf(playerName));}
    public void removeAdminToggled(String playerName) {adminChatList.remove(adminChatList.indexOf(playerName));}
    public void addPartyToggled(String playerName) {partyChatList.add(playerName);}
    public void addAdminToggled(String playerName) {adminChatList.add(playerName);}
    public static mcConfig getInstance() {
    	if (instance == null) {
    	instance = new mcConfig();
    	}
    	return instance;
    	}
    public void toggleAdminChat(String playerName){
    	if(isAdminToggled(playerName)){
    		removeAdminToggled(playerName);
    	} else {
    		addAdminToggled(playerName);
    	}
    }
    public void togglePartyChat(String playerName){
    	if(isPartyToggled(playerName)){
    		removePartyToggled(playerName);
    	} else {
    		addPartyToggled(playerName);
    	}
    }
    public void woodProcChecks(Player player, Block block, Location loc){
    	if(mcUsers.getProfile(player).getWoodCuttingint() > 1000){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 750){
		if((Math.random() * 10) > 2){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 300){
		if((Math.random() * 10) > 4){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 100){
		if((Math.random() * 10) > 6){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 10){
		if((Math.random() * 10) > 8){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
    }
}