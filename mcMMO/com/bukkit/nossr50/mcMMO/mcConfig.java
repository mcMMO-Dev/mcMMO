package com.bukkit.nossr50.mcMMO;

import java.util.ArrayList;

import org.bukkit.block.Block;

public class mcConfig {
	private static volatile mcConfig instance;    
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
}