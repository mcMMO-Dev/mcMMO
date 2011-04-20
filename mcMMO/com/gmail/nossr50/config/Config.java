package com.gmail.nossr50.config;

import java.util.*;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class Config {
	private static volatile Config instance;
    String location = "mcmmo.properties";
    protected static final Logger log = Logger.getLogger("Minecraft");
    static ArrayList<String> adminChatList = new ArrayList<String>();
    static ArrayList<Block> blockWatchList = new ArrayList<Block>();
    static ArrayList<Block> treeFeller = new ArrayList<Block>();
    static ArrayList<String> partyChatList = new ArrayList<String>();
    static ArrayList<String> godModeList = new ArrayList<String>();
    HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    static ArrayList<Entity> bleedTracker = new ArrayList<Entity>();
    static ArrayList<Entity> mobSpawnTracker = new ArrayList<Entity>();
    
    /*
     * The Bleed Que Stuff
     */
    public Entity[] bleedQue = new Entity[20];
    public int bleedQuePos = 0;
    
    public void addToBleedQue(Entity entity){
    	//Assign entity to empty position
    	bleedQue[bleedQuePos] = entity;
    	//Move position up by 1 increment
    	bleedQuePos++;
    	
    	//Check if array is full
    	if(bleedQuePos >= bleedQue.length){
    		//Create new temporary array
    		Entity[] temp = new Entity[bleedQue.length*2];
    		//Copy data from bleedQue to temporary array
    		System.arraycopy(bleedQue, 0, temp, 0, bleedQue.length);
    		//Point bleedQue to new array
    		bleedQue = temp;
    	}
    }
    
    public Entity[] getBleedQue(){return bleedQue;}
    
    public void clearBleedQue(){
    	bleedQue = new Entity[bleedQue.length];
    	setBleedQuePos(0);
    }
    public void setBleedQuePos(int x){bleedQuePos = x;}
    
    /*
     * The Bleed Removal Que Stuff
     */
    
    public Entity[] bleedRemovalQue = new Entity[20];
    public int bleedRemovalQuePos = 0;
    
    public void addToBleedRemovalQue(Entity entity){
    	//Assign entity to empty position
    	bleedRemovalQue[bleedRemovalQuePos] = entity;
    	//Move position up by 1 increment
    	bleedRemovalQuePos++;
    	
    	//Check if array is full
    	if(bleedRemovalQuePos >= bleedRemovalQue.length){
    		//Create new temporary array
    		Entity[] temp = new Entity[bleedRemovalQue.length*2];
    		//Copy data from bleedRemovalQue to temporary array
    		System.arraycopy(bleedRemovalQue, 0, temp, 0, bleedRemovalQue.length);
    		//Point bleedRemovalQue to new array
    		bleedRemovalQue = temp;
    	}
    }
    
    public Entity[] getBleedRemovalQue(){return bleedRemovalQue;}
    
    public void clearBleedRemovalQue(){
    	bleedQue = new Entity[bleedRemovalQue.length];
    	setBleedQuePos(0);
    }
    public void setBleedRemovalQuePos(int x){bleedRemovalQuePos = x;}
    
    
    public boolean isBlockWatched(Block block) {return blockWatchList.contains(block);}
    public boolean isTreeFellerWatched(Block block) {return treeFeller.contains(block);}
    public ArrayList<Block> getTreeFeller() {return treeFeller;}
    public void removeBlockWatch(Block block) {blockWatchList.remove(blockWatchList.indexOf(block));}
    public void addBlockWatch(Block block) {blockWatchList.add(block);}
    public void removeTreeFeller(Block block) {treeFeller.remove(treeFeller.indexOf(block));}
    public void addTreeFeller(Block block) {treeFeller.add(block);}
    public void addBleedTrack(Entity entity) {bleedTracker.add(entity);}
    public void addMobSpawnTrack(Entity entity) {mobSpawnTracker.add(entity);}
    public void removeMobSpawnTrack(Entity entity) {mobSpawnTracker.remove(entity);}
    public ArrayList<Entity> getBleedTracked() {return bleedTracker;}
    public void addArrowTrack(Entity entity, Integer arrowcount) {arrowTracker.put(entity, arrowcount);}
    public Integer getArrowCount(Entity entity) {return arrowTracker.get(entity);}
    public void removeArrowTracked(Entity entity){
    	if(arrowTracker.containsKey(entity)){
    		arrowTracker.remove(entity);
    	}
    }
    public void removeBleedTrack(Entity entity){
    	bleedTracker.remove(entity);
    }
    public void clearTreeFeller(){
    	treeFeller.clear();
    }
    public void setBleedCount(Entity entity, Integer newvalue){
    	bleedTracker.add(entity);
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
    public boolean isMobSpawnTracked(Entity entity) {
    	if(mobSpawnTracker.contains(entity)){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isBleedTracked(Entity entity) {
    	if(bleedTracker.contains(entity)){
    		return true;
    	} else {
    		return false;
    	}
    }
	public boolean isAdminToggled(String playerName) {return adminChatList.contains(playerName);}
    public boolean isPartyToggled(String playerName) {return partyChatList.contains(playerName);}
    public boolean isGodModeToggled(String playerName) {return godModeList.contains(playerName);}
    public void removeGodModeToggled(String playerName) {godModeList.remove(godModeList.indexOf(playerName));}
    public void removePartyToggled(String playerName) {partyChatList.remove(partyChatList.indexOf(playerName));}
    public void removeAdminToggled(String playerName) {adminChatList.remove(adminChatList.indexOf(playerName));}
    public void addGodModeToggled(String playerName) {godModeList.add(playerName);}
    public void addPartyToggled(String playerName) {partyChatList.add(playerName);}
    public void addAdminToggled(String playerName) {adminChatList.add(playerName);}

    public static Config getInstance() {
    	if (instance == null) {
    	instance = new Config();
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
    public void toggleGodMode(String playerName){
    	if(isGodModeToggled(playerName)){
    		removeGodModeToggled(playerName);
    	} else {
    		addGodModeToggled(playerName);
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