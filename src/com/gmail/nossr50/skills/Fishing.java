package com.gmail.nossr50.skills;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class Fishing {
	
	//Return the fishing tier for the player
	public static int getFishingLootTier(PlayerProfile PP)
	{
		int lvl = PP.getSkillLevel(SkillType.FISHING);
		
		if(lvl < 100)
		{
			return 1;
		} else if (lvl >= 100 && lvl < 300)
		{
			return 2;
		} else if (lvl >= 300 && lvl < 600)
		{
			return 3;
		} else if (lvl >= 600 && lvl < 900)
		{
			return 4;
		} else
		{
			return 5;
		}
	}
	
	public static short getItemMaxDurability(Material mat)
	{
		//Return the max durability of an item
		
		//KEY
		//TOOLS
		//GOLD = 33, WOOD = 60, STONE = 132, IRON = 251, DIAMOND = 1562
		//HELMETS
		//LEATHER = 34, CHAINMAIL = 67, GOLD = 68, IRON = 136, DIAMOND = 272
		//CHESTPLATES
		//LEATHER = 49, CHAINMAIL = 96, GOLD = 96, IRON = 192, DIAMOND = 384
		//LEGGINGS
		//LEATHER = 46, CHAINMAIL = 92, GOLD = 92, IRON = 184, DIAMOND = 368
		//BOOTS
		//LEATHER = 40, CHAINMAIL = 79, GOLD = 80, IRON = 160, DIAMOND = 320
		
		
		switch(mat)
		{
		//Leather Items
		case LEATHER_BOOTS:
			return (short) 40;
		case LEATHER_LEGGINGS:
			return (short) 46;
		case LEATHER_HELMET:
			return (short) 34;
		case LEATHER_CHESTPLATE:
			return (short) 49;
		default:
			return 0;
		}
	}
	
	public static void getFishingResults(Player player, PlayerFishEvent event)
	{
		switch(getFishingLootTier(Users.getProfile(player)))
		{
		case 1:
			getFishingResultsTier1(player, event);
			Users.getProfile(player).addXP(SkillType.FISHING, 200, player);
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1));
			break;
		case 2:
			getFishingResultsTier2(player, event);
			Users.getProfile(player).addXP(SkillType.FISHING, 400, player);
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 2));
			break;
		case 3:
			getFishingResultsTier3(player, event);
			Users.getProfile(player).addXP(SkillType.FISHING, 600, player);
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 3));
			break;
		case 4:
			getFishingResultsTier4(player, event);
			Users.getProfile(player).addXP(SkillType.FISHING, 800, player);
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 4));
			break;
		case 5:
			getFishingResultsTier5(player, event);
			Users.getProfile(player).addXP(SkillType.FISHING, 1000, player);
			player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 5));
			break;
		}
		Skills.XpCheckSkill(SkillType.FISHING, player);
	}
	
	private static void getFishingResultsTier1(Player player, PlayerFishEvent event)
	{
		//About 12 items for Tier 1
		int randomNum = (int)(Math.random() * 14);
		CraftItem theCatch = (CraftItem)event.getCaught();
		
		switch(randomNum)
		{
		//Armors
		case 1:
			theCatch.setItemStack(new ItemStack(Material.LEATHER_BOOTS, 1));
			break;
		case 2:
			theCatch.setItemStack(new ItemStack(Material.LEATHER_HELMET, 1));
			break;
		case 3:
			theCatch.setItemStack(new ItemStack(Material.LEATHER_LEGGINGS, 1));
			break;
		case 4:
			theCatch.setItemStack(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
			break;
		//Tools
		//WOOD TOOLS
		case 5:
			theCatch.setItemStack(new ItemStack(Material.WOOD_AXE, 1));
			break;
		case 6:
			theCatch.setItemStack(new ItemStack(Material.WOOD_PICKAXE, 1));
			break;
		case 7:
			theCatch.setItemStack(new ItemStack(Material.WOOD_SWORD, 1));
			break;
		case 8:
			theCatch.setItemStack(new ItemStack(Material.WOOD_HOE, 1));
			break;
		case 9:
			theCatch.setItemStack(new ItemStack(Material.WOOD_SPADE, 1));
			break;
		//STONE TOOLS
		case 10:
			theCatch.setItemStack(new ItemStack(Material.STONE_AXE, 1));
			break;
		case 11:
			theCatch.setItemStack(new ItemStack(Material.STONE_PICKAXE, 1));
			break;
		case 12:
			theCatch.setItemStack(new ItemStack(Material.STONE_SWORD, 1));
			break;
		case 13:
			theCatch.setItemStack(new ItemStack(Material.STONE_HOE, 1));
			break;
		case 14:
			theCatch.setItemStack(new ItemStack(Material.STONE_SPADE, 1));
			break;
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * Fishing.getItemMaxDurability(theCatch.getItemStack().getType()))); //Change the damage value

	}

	private static void getFishingResultsTier2(Player player, PlayerFishEvent event)
	{
		
	}
	
	private static void getFishingResultsTier3(Player player, PlayerFishEvent event)
	{
		
	}
	
	private static void getFishingResultsTier4(Player player, PlayerFishEvent event)
	{
		
	}
	
	private static void getFishingResultsTier5(Player player, PlayerFishEvent event)
	{
		
	}
}
