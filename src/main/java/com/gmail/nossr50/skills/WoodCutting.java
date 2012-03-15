package com.gmail.nossr50.skills;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        
        //Prepare array
        ArrayList<Block> toBeFelled = new ArrayList<Block>();
        
        //NOTE: Tree Feller will cut upwards like how you actually fell trees
        processTreeFelling(firstBlock, toBeFelled, plugin);
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
        if((player.getItemInHand().getDurability() + durabilityLoss >= player.getItemInHand().getType().getMaxDurability()) 
                || player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null)
        {
            player.sendMessage(mcLocale.getString("TreeFeller.AxeSplinters"));
            
            if(player.getHealth() >= 2)
                Combat.dealDamage(player, (int)(Math.random() * (player.getHealth()-1)));
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
                    
                    if(!x.hasMetadata("mcmmoPlacedBlock"))
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
                                xp += LoadProperties.mjungle/4;
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
        
        PP.addXP(SkillType.WOODCUTTING, xp, player); //Tree Feller gives nerf'd XP
        Skills.XpCheckSkill(SkillType.WOODCUTTING, player);
    }
    
    private static boolean treeFellerCompatible(Block block)
    {
        return block.getType() == Material.LOG || block.getType() == Material.LEAVES || block.getType() == Material.AIR;
    }
    
    private static void processTreeFelling(Block currentBlock, ArrayList<Block> toBeFelled, mcMMO plugin)
    {
        
        if(currentBlock.getType() == Material.LOG || currentBlock.getType() == Material.LEAVES)
            toBeFelled.add(currentBlock);
        
        //These 2 are to make sure that Tree Feller isn't so aggressive
        boolean isAirOrLeaves = currentBlock.getType() == Material.LEAVES || currentBlock.getType() == Material.AIR;
        
        Block xPositive = currentBlock.getRelative(1, 0, 0);
        Block xNegative = currentBlock.getRelative(-1, 0, 0);
        Block zPositive = currentBlock.getRelative(0, 0, 1);
        Block zNegative = currentBlock.getRelative(0, 0, -1);
        
        if(!currentBlock.hasMetadata("mcmmoPlacedBlock") &&
                !isTooAgressive(isAirOrLeaves, xPositive) && treeFellerCompatible(xPositive) && !toBeFelled.contains(xPositive))
            processTreeFelling(xPositive, toBeFelled, plugin);
        if(!currentBlock.hasMetadata("mcmmoPlacedBlock") &&
                !isTooAgressive(isAirOrLeaves, xNegative) && treeFellerCompatible(xNegative) && !toBeFelled.contains(xNegative))
            processTreeFelling(xNegative, toBeFelled, plugin);
        if(!currentBlock.hasMetadata("mcmmoPlacedBlock") &&
                !isTooAgressive(isAirOrLeaves, zPositive) && treeFellerCompatible(zPositive) && !toBeFelled.contains(zPositive))
            processTreeFelling(zPositive, toBeFelled, plugin);
        if(!currentBlock.hasMetadata("mcmmoPlacedBlock") &&
                !isTooAgressive(isAirOrLeaves, zNegative) && treeFellerCompatible(zNegative) && !toBeFelled.contains(zNegative))
            processTreeFelling(zNegative, toBeFelled, plugin);
        
        //Finally go Y+
        Block yPositive = currentBlock.getRelative(0, 1, 0);
        
        if(treeFellerCompatible(yPositive))
        {
            if(!currentBlock.hasMetadata("mcmmoPlacedBlock") && !toBeFelled.contains(yPositive))
            {
                processTreeFelling(yPositive, toBeFelled, plugin);
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
		
    	if(block.hasMetadata("placedBlock"))
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
    	if(LoadProperties.woodcuttingrequiresaxe)
    		Skills.abilityDurabilityLoss(player.getItemInHand(), LoadProperties.abilityDurabilityLoss);
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    }
}
