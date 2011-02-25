package com.gmail.nossr50;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Map.Entry;

public class mcConfig {
	private static volatile mcConfig instance;
    String location = "mcmmo.properties";
    protected static final Logger log = Logger.getLogger("Minecraft");
    static ArrayList<String> adminChatList = new ArrayList<String>();
    static ArrayList<String> coordsWatchList = new ArrayList<String>();
    static ArrayList<Block> blockWatchList = new ArrayList<Block>();
    static ArrayList<String> partyChatList = new ArrayList<String>();
    HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    HashMap<Entity, Integer> bleedTracker = new HashMap<Entity, Integer>();
    public boolean isBlockWatched(Block block) {return blockWatchList.contains(block);}
    public boolean isCoordsWatched(String xyz) {return coordsWatchList.contains(xyz);}
    public void removeBlockWatch(Block block) {blockWatchList.remove(blockWatchList.indexOf(block));}
    public void removeCoordsWatch(String xyz) {coordsWatchList.remove(coordsWatchList.indexOf(xyz));}
    public void addBlockWatch(Block block) {blockWatchList.add(block);}
    public void addCoordsWatch(String xyz) {coordsWatchList.add(xyz);}
    public void addArrowTrack(Entity entity, Integer arrowcount) {arrowTracker.put(entity, arrowcount);}
    public void addBleedTrack(Entity entity, Integer duration) {bleedTracker.put(entity, duration);}
    public Integer getArrowCount(Entity entity) {return arrowTracker.get(entity);}
    public Integer getBleedCount(Entity entity) {return bleedTracker.get(entity);}
    public void removeBleedTrack(Entity entity){
    	bleedTracker.remove(entity);
    }
    public void setBleedCount(Entity entity, Integer newvalue){
    	bleedTracker.put(entity, newvalue);
    }
    public void removeBleedCount(Entity entity, Integer newvalue) {
    	bleedTracker.put(entity, bleedTracker.get(entity) - newvalue);
    }
    public void addArrowCount(Entity entity, Integer newvalue) {
    	arrowTracker.put(entity, arrowTracker.get(entity) + newvalue);
    }
    public boolean isTracked(Entity entity) {
    	if(arrowTracker.containsKey(entity)){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isBleedTracked(Entity entity) {
    	if(bleedTracker.containsKey(entity)){
    		return true;
    	} else {
    		return false;
    	}
    }
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