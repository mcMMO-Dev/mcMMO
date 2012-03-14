package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;

public class Misc 
{
    String location = "mcmmo.properties";
    
    public HashSet<Block> blockWatchList = new HashSet<Block>();
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public ArrayList<LivingEntity> bleedTracker = new ArrayList<LivingEntity>();
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();
    mcMMO plugin = null;
    
    //BLEED QUE STUFF
    public LivingEntity[] bleedQue = new LivingEntity[20];
    public int bleedQuePos = 0;
    public LivingEntity[] bleedRemovalQue = new LivingEntity[20];
    public int bleedRemovalQuePos = 0;

    public Misc(mcMMO mcMMO) 
    {
		plugin = mcMMO;
	}
    
    public void addToBleedQue(LivingEntity entity)
    {
    	//Assign entity to empty position
    	bleedQue[bleedQuePos] = entity;
    	
    	//Move position up by 1 increment
    	bleedQuePos++;
    	
    	//Check if array is full
    	if(bleedQuePos >= bleedQue.length)
    	{
    		//Create new temporary array
    		LivingEntity[] temp = new LivingEntity[bleedQue.length*2];
    		//Copy data from bleedQue to temporary array
    		System.arraycopy(bleedQue, 0, temp, 0, bleedQue.length);
    		//Point bleedQue to new array
    		bleedQue = temp;
    	}
    }
    
    public void addToBleedRemovalQue(LivingEntity entity)
    {
    	//Assign entity to empty position
    	bleedRemovalQue[bleedRemovalQuePos] = entity;
    	
    	//Move position up by 1 increment
    	bleedRemovalQuePos++;
    	
    	//Check if array is full
    	if(bleedRemovalQuePos >= bleedRemovalQue.length)
    	{
    		//Create new temporary array
    		LivingEntity[] temp = new LivingEntity[bleedRemovalQue.length*2];
    		//Copy data from bleedRemovalQue to temporary array
    		System.arraycopy(bleedRemovalQue, 0, temp, 0, bleedRemovalQue.length);
    		//Point bleedRemovalQue to new array
    		bleedRemovalQue = temp;
    	}
    }
}