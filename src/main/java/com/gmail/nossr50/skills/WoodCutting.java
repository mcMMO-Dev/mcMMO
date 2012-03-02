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

import com.gmail.nossr50.Combat;
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
        processTreeFelling(firstBlock, world, toBeFelled, plugin);
        removeBlocks(toBeFelled, player, PP, plugin);
    }
    
    private static void removeBlocks(ArrayList<Block> toBeFelled, Player player, PlayerProfile PP, mcMMO plugin)
    {
        if(toBeFelled.size() > LoadProperties.treeFellerThreshold)
        {
            player.sendMessage(mcLocale.getString("Skills.Woodcutting.TreeFellerThreshold"));
            return;
        }
        int durabilityLoss = toBeFelled.size(), xp = 0;
        
        //Damage the tool
        player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability()+durabilityLoss));
        
        //This is to prevent using wood axes everytime you tree fell
        if(player.getItemInHand().getDurability() >= Repair.getMaxDurability(player.getItemInHand()) 
                || player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null)
        {
            player.sendMessage(ChatColor.RED+"YOUR AXE SPLINTERS INTO DOZENS OF PIECES");
            
            if(player.getHealth() >= 2)
                Combat.dealDamage(player, player.getHealth()-1);
            return;
        }
        
        //Prepare ItemStacks
        ItemStack item;
        ItemStack oak = new ItemStack(Material.LOG, 1, (byte)0, (byte)0);
        ItemStack spruce = new ItemStack(Material.LOG, 1, (byte)0, (byte)1);
        ItemStack birch = new ItemStack(Material.LOG, 1, (byte)0, (byte)2);
        ItemStack jungle = new ItemStack(Material.LOG, 1, (byte)0, (byte)3);
        
        for(Block x : toBeFelled)
        {
            if(m.blockBreakSimulate(x, player, true))
            {
                if(x.getType() == Material.LOG)
                {
                    switch(x.getData())
                    {
                    case 0:
                        item = oak;
                        break;
                    case 1:
                        item = spruce;
                        break;
                    case 2:
                        item = birch;
                        break;
                    case 3:
                        item = jungle;
                        break;
                    default:
                        item = oak;
                        break;
                    }
                    
                    //ItemStack item = new ItemStack(x.getType(), 1, (byte)0, type);
                        
                    if(!plugin.misc.blockWatchList.contains(x))
                    {
                        WoodCutting.woodCuttingProcCheck(player, x);
                            
                        switch(x.getData())
                        {
                            case 0:
                                xp += LoadProperties.moak;
                                break;
                            case 1:
                                xp += LoadProperties.mspruce;
                                break;
                            case 2:
                                xp += LoadProperties.mbirch;
                                break;
                            case 3:
                                xp += LoadProperties.mjungle;
                                break;
                        }
                    }
                    
                    //Remove the block
                    x.setData((byte) 0);
                    x.setType(Material.AIR);
                    
                    //Drop the block
                    m.mcDropItem(x.getLocation(), item);    
                } else if(x.getType() == Material.LEAVES) 
                {
                    Material mat = Material.SAPLING;
                    item = new ItemStack(mat, 1, (short)0, (byte)(x.getData()-8));
                        
                    //90% chance to drop sapling
                    if(Math.random() * 10 > 9)
                        m.mcRandomDropItem(x.getLocation(), item, 90);
                        
                    //Remove the block
                    x.setData((byte) 0);
                    x.setType(Material.AIR);
                }
            }
        }
        
        PP.addXP(SkillType.WOODCUTTING, xp/3, player); //Tree Feller gives nerf'd XP
        Skills.XpCheckSkill(SkillType.WOODCUTTING, player);
        
        if(LoadProperties.toolsLoseDurabilityFromAbilities)
        {
            if(!player.getItemInHand().containsEnchantment(Enchantment.DURABILITY))
            {
                short durability = player.getItemInHand().getDurability();
                durability += (LoadProperties.abilityDurabilityLoss * durabilityLoss);
                player.getItemInHand().setDurability(durability);
            }
        }
    }
    
    private static boolean treeFellerCompatible(Block block)
    {
        return block.getType() == Material.LOG || block.getType() == Material.LEAVES || block.getType() == Material.AIR;
    }
    
    private static void processTreeFelling(Block currentBlock, World world, ArrayList<Block> toBeFelled, mcMMO plugin)
    {
        int x = currentBlock.getX(), y = currentBlock.getY(), z = currentBlock.getZ();
        
        if(currentBlock.getType() == Material.LOG || currentBlock.getType() == Material.LEAVES)
            toBeFelled.add(currentBlock);
        
        //These 2 are to make sure that Tree Feller isn't so aggressive
        boolean isAirOrLeaves = currentBlock.getType() == Material.LEAVES || currentBlock.getType() == Material.AIR;
        
        Block xPositive = world.getBlockAt(x+1, y, z);
        Block xNegative = world.getBlockAt(x-1, y, z);
        Block zPositive = world.getBlockAt(x, y, z+1);
        Block zNegative = world.getBlockAt(x, y, z-1);
        
        if(!plugin.misc.blockWatchList.contains(currentBlock) &&
                !isTooAgressive(isAirOrLeaves, xPositive) && treeFellerCompatible(xPositive) && !toBeFelled.contains(xPositive))
            processTreeFelling(xPositive, world, toBeFelled, plugin);
        if(!plugin.misc.blockWatchList.contains(currentBlock) &&
                !isTooAgressive(isAirOrLeaves, xNegative) && treeFellerCompatible(xNegative) && !toBeFelled.contains(xNegative))
            processTreeFelling(xNegative, world, toBeFelled, plugin);
        if(!plugin.misc.blockWatchList.contains(currentBlock) &&
                !isTooAgressive(isAirOrLeaves, zPositive) && treeFellerCompatible(zPositive) && !toBeFelled.contains(zPositive))
            processTreeFelling(zPositive, world, toBeFelled, plugin);
        if(!plugin.misc.blockWatchList.contains(currentBlock) &&
                !isTooAgressive(isAirOrLeaves, zNegative) && treeFellerCompatible(zNegative) && !toBeFelled.contains(zNegative))
            processTreeFelling(zNegative, world, toBeFelled, plugin);
        
        //Finally go Y+
        Block yPositive = world.getBlockAt(x, y+1, z);
        
        if(treeFellerCompatible(yPositive))
        {
            if(!plugin.misc.blockWatchList.contains(currentBlock) && !toBeFelled.contains(yPositive))
            {
                processTreeFelling(yPositive, world, toBeFelled, plugin);
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
    			xp += LoadProperties.moak;
    			break;
    		case 1:
    			xp += LoadProperties.mspruce;
    			break;
    		case 2:
    			xp += LoadProperties.mbirch;
    			break;
    		case 3:
    		    xp += LoadProperties.mjungle;
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
				short durability = player.getItemInHand().getDurability();
				durability += LoadProperties.abilityDurabilityLoss;
				player.getItemInHand().setDurability(durability);
	    	}
	    }
		
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    }
}
