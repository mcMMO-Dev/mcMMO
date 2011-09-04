package com.gmail.nossr50.datatypes;

import java.util.HashMap;

import org.bukkit.entity.*;

public class Mob
{
	public HashMap<Integer, Short> mobDiff = new HashMap<Integer, Short>();
	public HashMap<Integer, Boolean> isAggressive = new HashMap<Integer, Boolean>();
	
	public void assignDifficulty(Entity entity)
	{
		short x = 0;
		
		if(entity.getLocation().getY() >= 45)
		{
			//LEVEL 2
			if(Math.random() * 100 > 50)
			{
				x = 0;
			}
			//LEVEL 3
			else
			{
				x = 1;
			}
		}
		else
		{
			double y = Math.random() * 100;
			//HIGH CHANCE FOR 5's
			if(entity.getLocation().getY() < 20)
			{
				//ASSIGN INTO THE 5 RANKS
				if(y >= 0 && y < 50)
					x = 0;
				if(y >= 50 && y < 80)
					x = 1;
				if(y >= 80 && y < 95)
					x = 2;
				if(y >= 95 && y < 98)
					x = 3;
				if(y >= 98 && y <= 100)
					x = 4;
			}
			//HIGH CHANCE FOR 4's
			else
			{
				//ASSIGN INTO THE 5 RANKS
				if(y >= 0 && y < 50)
					x = 0;
				if(y >= 50 && y < 74)
					x = 1;
				if(y >= 74 && y < 89)
					x = 2;
				if(y >= 89 && y < 99)
					x = 3;
				if(y >= 99 && y <= 100)
					x = 4;
			}
				if(x > 1)
				{
					isAggressive.put(entity.getEntityId(), false);
				}
				
		}
		
		if(!mobDiff.containsKey(entity.getEntityId()))
		{
			mobDiff.put(entity.getEntityId(), x);
			//System.out.println("Mob "+entity.getEntityId()+" (DIFFICULTY) "+
					//(x +1)+"(DEPTH) "+entity.getLocation().getY());
		}
	}
}
