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
package com.gmail.nossr50.skills;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.config.*;
import org.getspout.spoutapi.sound.SoundEffect;


public class WoodCutting 
{
	static int w = 0;
	private static boolean isdone = false;
	
    public static void woodCuttingProcCheck(Player player, Block block)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	byte type = block.getData();
    	Material mat = Material.getMaterial(block.getTypeId());
    	if(player != null)
    	{
    		if((Math.random() * 1000 <= PP.getSkillLevel(SkillType.WOODCUTTING)) || PP.getSkillLevel(SkillType.WOODCUTTING) > 1000)
    		{
    			ItemStack item = new ItemStack(mat, 1, (short) 0, type);
    			m.mcDropItem(block.getLocation(), item);
    		}
    	}
    }
    public static void treeFellerCheck(Player player, Block block)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	if(m.isAxes(player.getItemInHand()))
    	{
    		if(block != null)
    		{
        		if(!m.abilityBlockCheck(block))
        			return;
        	}
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode())
    		{
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		int x = PP.getSkillLevel(SkillType.WOODCUTTING);
    		while(x >= 50)
    		{
    			x-=50;
    			ticks++;
    		}

    		if(!PP.getTreeFellerMode() && Skills.cooldownOver(player, (PP.getTreeFellerDeactivatedTimeStamp()*1000), LoadProperties.treeFellerCooldown))
    		{
    			player.sendMessage(mcLocale.getString("Skills.TreeFellerOn"));
    			for(Player y : player.getWorld().getPlayers())
    			{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.TreeFellerPlayer", new Object[] {player.getName()}));
	    		}
    			PP.setTreeFellerActivatedTimeStamp(System.currentTimeMillis());
    			PP.setTreeFellerDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
    			PP.setTreeFellerMode(true);
    		}
    		if(!PP.getTreeFellerMode() && !Skills.cooldownOver(player, (PP.getTreeFellerDeactivatedTimeStamp()*1000), LoadProperties.treeFellerCooldown)){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+Skills.calculateTimeLeft(player, (PP.getTreeFellerDeactivatedTimeStamp()*1000), LoadProperties.treeFellerCooldown)+"s)");
    		}
    	}
    }
    public static void treeFeller(Block block, Player player, mcMMO plugin){
    	PlayerProfile PP = Users.getProfile(player);
    	int radius = 1;
    	if(PP.getSkillLevel(SkillType.WOODCUTTING) >= 500)
    		radius++;
    	if(PP.getSkillLevel(SkillType.WOODCUTTING) >= 950)
    		radius++;
        ArrayList<Block> blocklist = new ArrayList<Block>();
        ArrayList<Block> toAdd = new ArrayList<Block>();
        if(block != null)
        	blocklist.add(block);
        while(isdone == false){
        	addBlocksToTreeFelling(blocklist, toAdd, radius);
        }
        //This needs to be a hashmap too!
        isdone = false;
        /*
         * Add blocks from the temporary 'toAdd' array list into the 'treeFeller' array list
         * We use this temporary list to prevent concurrent modification exceptions
         */
        for(Block x : toAdd)
        {
        	if(!plugin.misc.treeFeller.contains(x))
        		plugin.misc.treeFeller.add(x);
        }
        toAdd.clear();
    }
    public static void addBlocksToTreeFelling(ArrayList<Block> blocklist, ArrayList<Block> toAdd, Integer radius)
    {
    	int u = 0;
    	for (Block x : blocklist)
    	{
    		u++;
    		if(toAdd.contains(x))
    			continue;
    		w = 0;
    		Location loc = x.getLocation();
    		int vx = x.getX();
            int vy = x.getY();
            int vz = x.getZ();
            
            /*
             * Run through the blocks around the broken block to see if they qualify to be 'felled'
             */
    		for (int cx = -radius; cx <= radius; cx++) {
	            for (int cy = -radius; cy <= radius; cy++) {
	                for (int cz = -radius; cz <= radius; cz++) {
	                    Block blocktarget = loc.getWorld().getBlockAt(vx + cx, vy + cy, vz + cz);
	                    if (!blocklist.contains(blocktarget) && !toAdd.contains(blocktarget) && (blocktarget.getTypeId() == 17 || blocktarget.getTypeId() == 18)) { 
	                        toAdd.add(blocktarget);
	                        w++;
	                    }
	                }
	            }
	        }
    	}
    	/*
		 * Add more blocks to blocklist so they can be 'felled'
		 */
		for(Block xx : toAdd)
		{
    		if(!blocklist.contains(xx))
        	blocklist.add(xx);
        }
    	if(u >= blocklist.size())
    	{
    		isdone = true;
    	} else {
    		isdone = false;
    	}
    }
    
    public static void woodcuttingBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);    	
    	int xp = 0;
		byte data = block.getData();
		
    	if(plugin.misc.blockWatchList.contains(block))
    		return;
    	
    	switch(data)
    	{
    		case 0:
    			xp += LoadProperties.mpine;
    			break;
    		case 1:
    			xp += LoadProperties.mspruce;
    			break;
    		case 2:
    			xp += LoadProperties.mbirch;
    			break;
    	}
    	
    	if(block.getTypeId() == 17)
    	{
    		WoodCutting.woodCuttingProcCheck(player, block);
    		PP.addXP(SkillType.WOODCUTTING, xp, player);
    		Skills.XpCheckSkill(SkillType.WOODCUTTING, player);
    	}
    }
    
    public static void leafBlower(Player player, Block block){
		if(LoadProperties.toolsLoseDurabilityFromAbilities)
	    {
	    	if(!player.getItemInHand().containsEnchantment(Enchantment.DURABILITY))
	    		m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
	    }
		
		if(Math.random() * 10 > 9)
		{
			ItemStack x = new ItemStack(Material.SAPLING, 1, (short)0, (byte)(block.getData()-8));
			m.mcDropItem(block.getLocation(), x);
		}
		
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    }
}
