/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.config;

import java.util.*;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.mcMMO;

public class Misc 
{
    String location = "mcmmo.properties";
    
    protected static final Logger log = Logger.getLogger("Minecraft");
    
    public ArrayList<Entity> mobSpawnerList = new ArrayList<Entity>();
    public ArrayList<Block> blockWatchList = new ArrayList<Block>();
    public ArrayList<Block> treeFeller = new ArrayList<Block>();
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public ArrayList<LivingEntity> bleedTracker = new ArrayList<LivingEntity>();
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