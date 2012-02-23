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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.Bukkit;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.config.*;

import org.getspout.spoutapi.sound.SoundEffect;


public class WoodCutting 
{
    public static void treeFeller(BlockBreakEvent event, mcMMO plugin)
    {
        //Setup vars
        Player player = event.getPlayer();
        Block firstBlock = event.getBlock();
        PlayerProfile PP = Users.getProfile(player);
        World world = firstBlock.getWorld();
        
        //Prepare array
        ArrayList<Block> toBeFelled = new ArrayList<Block>();
        
        //NOTE: Tree Feller will cut upwards like how you actually fell trees
        processTreeFelling(firstBlock, world, toBeFelled);
        removeBlocks(toBeFelled, player, PP, plugin);
    }
    
    private static void removeBlocks(ArrayList<Block> toBeFelled, Player player, PlayerProfile PP, mcMMO plugin)
    {
        for(Block x : toBeFelled)
        {
            //Stupid NoCheat compatibility stuff
            PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
            Bukkit.getPluginManager().callEvent(armswing);
            
            if(m.blockBreakSimulate(x, player))
            {
                if(x.getType() == Material.LOG || x.getType() == Material.LEAVES)
                {
                    if(x.getType() == Material.LOG)
                    {
                        byte type = x.getData();
                        ItemStack item = new ItemStack(x.getType(), 1, (byte)0, type);
                        
                        if(!plugin.misc.blockWatchList.contains(x))
                        {
                            WoodCutting.woodCuttingProcCheck(player, x);
                            int xp = 0;
                            
                            switch(x.getData())
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
                            
                            PP.addXP(SkillType.WOODCUTTING, xp, player);
                        }
                        
                        //Drop the block
                        x.getWorld().dropItemNaturally(x.getLocation(), item);
                        
                        //Remove the block
                        x.setData((byte) 0);
                        x.setType(Material.AIR);
                        
                    } else if(x.getType() == Material.LEAVES) 
                    {
                        Material mat = Material.SAPLING;
                        ItemStack item = new ItemStack(mat, 1, (short)0, (byte)(x.getData()-8));
                        
                        //1 in 10 chance to drop sapling
                        if(Math.random() * 10 > 9)
                            m.mcDropItem(x.getLocation(), item);
                        
                        //Remove the block
                        x.setData((byte) 0);
                        x.setType(Material.AIR);
                    }
                }
            }
        }
    }
    private static boolean treeFellerCompatible(Block block)
    {
        return block.getType() == Material.LOG || block.getType() == Material.LEAVES || block.getType() == Material.AIR;
    }
    
    private static void processTreeFelling(Block currentBlock, World world, ArrayList<Block> toBeFelled)
    {
        int x = currentBlock.getX(), y = currentBlock.getY(), z = currentBlock.getZ();
        toBeFelled.add(currentBlock);
        
        //These 2 are to make sure that Tree Feller isn't so aggressive
        boolean isAirOrLeaves = currentBlock.getType() == Material.LEAVES || currentBlock.getType() == Material.AIR;
        
        Block xPositive = world.getBlockAt(x+1, y, z);
        Block xNegative = world.getBlockAt(x-1, y, z);
        Block zPositive = world.getBlockAt(x, y, z+1);
        Block zNegative = world.getBlockAt(x, y, z-1);
        
        if(!isTooAgressive(isAirOrLeaves, xPositive) && treeFellerCompatible(xPositive) && !toBeFelled.contains(xPositive))
            processTreeFelling(xPositive, world, toBeFelled);
        if(!isTooAgressive(isAirOrLeaves, xNegative) && treeFellerCompatible(xNegative) && !toBeFelled.contains(xNegative))
            processTreeFelling(xNegative, world, toBeFelled);
        if(!isTooAgressive(isAirOrLeaves, zPositive) && treeFellerCompatible(zPositive) && !toBeFelled.contains(zPositive))
            processTreeFelling(zPositive, world, toBeFelled);
        if(!isTooAgressive(isAirOrLeaves, zNegative) && treeFellerCompatible(zNegative) && !toBeFelled.contains(zNegative))
            processTreeFelling(zNegative, world, toBeFelled);
        
        //Finally go Y+
        Block yPositive = world.getBlockAt(x, y+1, z);
        
        if(treeFellerCompatible(yPositive))
        {
            if(!toBeFelled.contains(yPositive))
            {
                processTreeFelling(yPositive, world, toBeFelled);
            }
        }
    }
    
    private static boolean isTooAgressive(boolean bool, Block block)
    {
        return bool && (block.getType() == Material.AIR || block.getType() == Material.LEAVES);
    }
    
    public static void woodCuttingProcCheck(Player player, Block block)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	byte type = block.getData();
    	Material mat = Material.getMaterial(block.getTypeId());
    	if(player != null)
    	{
    		if(PP.getSkillLevel(SkillType.WOODCUTTING) > 1000 || (Math.random() * 1000 <= PP.getSkillLevel(SkillType.WOODCUTTING)))
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

    		if(!PP.getTreeFellerMode() && Skills.cooldownOver(player, (PP.getSkillDATS(AbilityType.TREE_FELLER)*1000), LoadProperties.treeFellerCooldown))
    		{
    			player.sendMessage(mcLocale.getString("Skills.TreeFellerOn"));
    			for(Player y : player.getWorld().getPlayers())
    			{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.TreeFellerPlayer", new Object[] {player.getName()}));
	    		}
    			PP.setSkillDATS(AbilityType.TREE_FELLER, System.currentTimeMillis()+(ticks*1000));
    			PP.setTreeFellerMode(true);
    		}
    		if(!PP.getTreeFellerMode() && !Skills.cooldownOver(player, (PP.getSkillDATS(AbilityType.TREE_FELLER)*1000), LoadProperties.treeFellerCooldown)){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+Skills.calculateTimeLeft(player, (PP.getSkillDATS(AbilityType.TREE_FELLER)*1000), LoadProperties.treeFellerCooldown)+"s)");
    		}
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
		
    	PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
    	Bukkit.getPluginManager().callEvent(armswing);
    	
    	if(LoadProperties.toolsLoseDurabilityFromAbilities)
	    {
	    	if(!player.getItemInHand().containsEnchantment(Enchantment.DURABILITY))
	    	{
	    		System.out.println("BEFORE");
	    		System.out.println(player.getItemInHand().getDurability());
				short durability = player.getItemInHand().getDurability();
				durability -= LoadProperties.abilityDurabilityLoss;
				player.getItemInHand().setDurability(durability);
				System.out.println("AFTER");
				System.out.println(player.getItemInHand().getDurability());
	    	}
	    }
		
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    }
}
