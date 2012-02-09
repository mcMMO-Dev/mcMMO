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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

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

	public static void getFishingResults(Player player, PlayerFishEvent event)
	{
		switch(getFishingLootTier(Users.getProfile(player)))
		{
		case 1:
			getFishingResultsTier1(player, event);
			break;
		case 2:
			getFishingResultsTier2(player, event);
			break;
		case 3:
			getFishingResultsTier3(player, event);
			break;
		case 4:
			getFishingResultsTier4(player, event);
			break;
		case 5:
			getFishingResultsTier5(player, event);
			break;
		}
		m.mcDropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1));
		Users.getProfile(player).addXP(SkillType.FISHING, LoadProperties.mfishing, player);
		Skills.XpCheckSkill(SkillType.FISHING, player);
	}

	private static void getFishingResultsTier1(Player player, PlayerFishEvent event)
	{
		int randomNum = (int)(Math.random() * 15);
		Item theCatch = (Item)event.getCaught();
		if(Math.random() * 100 < LoadProperties.fishingDropChanceTier1)
		{
			switch(randomNum)
			{
			case 1:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 2:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 3:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 4:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 5:
				if(LoadProperties.woodenTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.WOOD_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 6:
				if(LoadProperties.woodenTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.WOOD_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 7:
				if(LoadProperties.woodenTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.WOOD_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 8:
				if(LoadProperties.woodenTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.WOOD_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 9:
				if(LoadProperties.woodenTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.WOOD_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 10:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 11:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 12:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 13:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 14:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			}
		} else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value

	}

	private static void getFishingResultsTier2(Player player, PlayerFishEvent event)
	{
		int randomNum = (int)(Math.random() * 20);
		Item theCatch = (Item)event.getCaught();

		if(Math.random() * 100 < LoadProperties.fishingDropChanceTier2)
		{
			switch(randomNum)
			{
			case 1:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 2:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 3:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 4:
				if(LoadProperties.leatherArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 5:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 6:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 7:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 8:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 9:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 10:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 11:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 12:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 13:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 14:
				if(LoadProperties.stoneTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.STONE_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 15:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 16:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 17:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 18:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 19:
				if(LoadProperties.enderPearl && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			}
		} else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH, 1));
		}

		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}

	private static void getFishingResultsTier3(Player player, PlayerFishEvent event)
	{
		int randomNum = (int)(Math.random() * 24);
		Item theCatch = (Item)event.getCaught();

		if(Math.random() * 100 < LoadProperties.fishingDropChanceTier3)
		{
			switch(randomNum)
			{
			case 1:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 2:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 3:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 4:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 5:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 6:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 7:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 8:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 9:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 10:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 11:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 12:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 13:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 14:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 15:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 16:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 17:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 18:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 19:
				if(LoadProperties.enderPearl && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 20:
				if(LoadProperties.blazeRod && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.BLAZE_ROD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 21:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_3, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 22:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_4, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 23:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_5, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			}
		}
		else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH, 1));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}

	private static void getFishingResultsTier4(Player player, PlayerFishEvent event)
	{
		int randomNum = (int)(Math.random() * 41);
		Item theCatch = (Item)event.getCaught();

		if(Math.random() * 100 < LoadProperties.fishingDropChanceTier4)
		{
			switch(randomNum)
			{
			case 1:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 2:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 3:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 4:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 5:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 6:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 7:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 8:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 9:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 10:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 11:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 12:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 13:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 14:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 15:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 16:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 17:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 18:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 19:
				if(LoadProperties.enderPearl && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 20:
				if(LoadProperties.blazeRod && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.BLAZE_ROD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 21:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_3, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 22:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_4, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 23:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_5, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 24:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 25:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 26:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 27:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 28:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 29:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 30:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 31:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 32:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 33:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_6, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 34:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_7, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 35:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_8, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 36:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_9, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 37:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_10, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 38:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_11, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 39:
				if(LoadProperties.glowstoneDust && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GLOWSTONE_DUST, 16));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 40:
				if(LoadProperties.fishingDiamonds && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND, (int)(Math.random() * 10)));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			}
		} else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH, 1));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}

	private static void getFishingResultsTier5(Player player, PlayerFishEvent event)
	{
		int randomNum = (int)(Math.random() * 50);
		Item theCatch = (Item)event.getCaught();

		if(Math.random() * 100 < LoadProperties.fishingDropChanceTier5)
		{
			switch(randomNum)
			{
			case 1:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 2:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 3:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 4:
				if(LoadProperties.goldArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 5:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 6:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 7:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 8:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 9:
				if(LoadProperties.ironTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 10:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 11:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 12:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 13:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 14:
				if(LoadProperties.goldTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GOLD_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 15:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 16:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 17:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 18:
				if(LoadProperties.ironArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 19:
				if(LoadProperties.enderPearl && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 20:
				if(LoadProperties.blazeRod && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.BLAZE_ROD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 21:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_3, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 22:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_4, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 23:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_5, 1));else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 24:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 25:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 26:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 27:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 28:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 29:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 30:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 31:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 32:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 33:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_6, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 34:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_7, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 35:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_8, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 36:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_9, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 37:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_10, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 38:
				if(LoadProperties.records && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.RECORD_11, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 39:
				if(LoadProperties.glowstoneDust && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.GLOWSTONE_DUST, 16));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 40:
				if(LoadProperties.fishingDiamonds && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND, (int)(Math.random() * 20)));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 41:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_BOOTS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 42:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_HELMET, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 43:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 44:
				if(LoadProperties.diamondArmor && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 45:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_AXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 46:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_PICKAXE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 47:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_SWORD, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 48:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_HOE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			case 49:
				if(LoadProperties.diamondTools && LoadProperties.fishingDrops)
					theCatch.setItemStack(new ItemStack(Material.DIAMOND_SPADE, 1));
				else
					theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
				break;
			}
		} else {
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH, 1));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}
	public static void processResults(PlayerFishEvent event)
	{
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);

		Fishing.getFishingResults(player, event);
		Item theCatch = (Item)event.getCaught();

		if(theCatch.getItemStack().getType() != Material.RAW_FISH)
		{
			//Inform the player they retrieved a treasure...
			player.sendMessage(mcLocale.getString("Fishing.ItemFound"));

			//Keep track of whether or not the treasure is enchanted
			boolean enchanted = false;

			ItemStack fishingResults = theCatch.getItemStack();
			if(Repair.isArmor(fishingResults) || Repair.isTools(fishingResults))
			{
				//Fishing up items will have a 10% chance to enter them into random enchantment lottery
				if(Math.random() * 100 < 10)
				{
					for(Enchantment x : Enchantment.values())
					{
						if(x.canEnchantItem(fishingResults))
						{
							//Prevent impossible enchantment combinations
							if((fishingResults.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL) || fishingResults.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS) || 
									fishingResults.containsEnchantment(Enchantment.PROTECTION_FIRE) || fishingResults.containsEnchantment(Enchantment.PROTECTION_PROJECTILE)) && 
									(x.equals(Enchantment.PROTECTION_EXPLOSIONS) || x.equals(Enchantment.PROTECTION_PROJECTILE) || x.equals(Enchantment.PROTECTION_FIRE) || x.equals(Enchantment.PROTECTION_ENVIRONMENTAL))){
								return;
							}
							
							//More impossible enchantment combinations
							else if((fishingResults.containsEnchantment(Enchantment.DAMAGE_ALL) || fishingResults.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS) || fishingResults.containsEnchantment(Enchantment.DAMAGE_UNDEAD)) && 
									(x.equals(Enchantment.DAMAGE_ALL) || x.equals(Enchantment.DAMAGE_ARTHROPODS) || x.equals(Enchantment.DAMAGE_UNDEAD))){
								return;
							}
							
							//Even more impossible enchantment combinations
							else if((fishingResults.containsEnchantment(Enchantment.SILK_TOUCH) || fishingResults.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) &&
									(x.equals(Enchantment.SILK_TOUCH) || x.equals(Enchantment.LOOT_BONUS_BLOCKS))){
								return;
							}
							
							else{		
								//Actual chance to have an enchantment is related to your fishing skill
								if(Math.random() * 15 < Fishing.getFishingLootTier(PP))
								{
									enchanted = true;
									int randomEnchantLevel = (int)(Math.random() * x.getMaxLevel()) + 1;
									
									if(randomEnchantLevel == 0)
										randomEnchantLevel = 1;

									fishingResults.addEnchantment(x, randomEnchantLevel);
								}
							}
						}
					}
				}
			}
			
			//Inform the player of magical properties
			if(enchanted)
			{
				player.sendMessage(mcLocale.getString("Fishing.MagicFound"));
			}
		}
	}
	public static void shakeMob(PlayerFishEvent event)
	{
		LivingEntity le = (LivingEntity)event.getCaught();
		
		//Do nothing to players
		if(le instanceof Player)
			return;
		
		Combat.dealDamage(le, 1, event.getPlayer());
		Location loc = le.getLocation();

		/* Neutral Mobs */
		if(le instanceof Sheep)
		{
			Sheep sheep = (Sheep)le;
			if(!sheep.isSheared())
			{
				Wool wool = new Wool();
				wool.setColor(sheep.getColor());
				ItemStack theWool = wool.toItemStack();
				theWool.setAmount((int)(Math.random() * 6));
				m.mcDropItem(loc, theWool);
				sheep.setSheared(true);
			}
		} 
		
		else if(le instanceof Pig)
		{
			m.mcDropItem(loc, new ItemStack(Material.PORK, 1));
		} 
		
		else if(le instanceof Cow)
		{
			if(Math.random() * 100 < 99){
				m.mcDropItem(loc, new ItemStack(Material.MILK_BUCKET, 1)); //rare chance to drop milk
			}
			else if(Math.random() * 10 < 5){
				m.mcDropItem(loc, new ItemStack(Material.LEATHER, 1));
			}
			else{
				m.mcDropItem(loc, new ItemStack(Material.RAW_BEEF, 1));
			}
		}
		
		else if(le instanceof Chicken)
		{
			if(Math.random() * 10 <= 7){
				if(Math.random() * 10 < 5){
					m.mcDropItem(loc, new ItemStack(Material.FEATHER, 1));
				}
				else{
					m.mcDropItem(loc, new ItemStack(Material.RAW_CHICKEN, 1));
				}
			}
			else{
				m.mcDropItem(loc, new ItemStack(Material.EGG, 1));
			}
		}
		
		//need to implement new shearing method
		else if(le instanceof MushroomCow)
		{
			if(Math.random() * 100 < 99){
				if(Math.random() * 10 < 5){
					m.mcDropItem(loc, new ItemStack(Material.MILK_BUCKET, 1)); //rare chance to drop milk
				}
				else{
					m.mcDropItem(loc, new ItemStack(Material.MUSHROOM_SOUP, 1)); //rare chance to drop soup
				}
			}
			else if(Math.random() * 10 <= 7){
				if(Math.random() * 10 < 5){
					m.mcDropItem(loc, new ItemStack(Material.LEATHER, 1));
				}
				else{
					m.mcDropItem(loc, new ItemStack(Material.RAW_BEEF, 1));
				}
			}
			else{
				m.mcDropItem(loc, new ItemStack(Material.RED_MUSHROOM, 3));
				//need some way to remove MushroomCow & replace with regular cow when sheared
			}
		}
		
		else if(le instanceof Squid)
		{
			m.mcDropItem(loc, new ItemStack(Material.getMaterial(351), 1, (byte)0, (byte)0));
		}
		
		else if(le instanceof Snowman){
			if(Math.random() * 100 < 99){
				m.mcDropItem(loc, new ItemStack(Material.PUMPKIN, 1)); //rare chance to drop pumpkin
			}
			else{
				m.mcDropItem(loc, new ItemStack(Material.SNOW_BALL, 5));
			}
		}
		
		/* Hostile Mobs */
		else if(le instanceof Skeleton)
		{
			if(Math.random() * 10 < 5)
				m.mcDropItem(loc, new ItemStack(Material.BONE, 1));
			else
				m.mcDropItem(loc, new ItemStack(Material.ARROW, 3));
		} 
		
		else if(le instanceof Spider)
		{
			if(Math.random() * 10 < 5)
				m.mcDropItem(loc, new ItemStack(Material.SPIDER_EYE, 1));
			else
				m.mcDropItem(loc, new ItemStack(Material.STRING, 1));
		} 
		
		else if(le instanceof Creeper)
		{
			m.mcDropItem(loc, new ItemStack(Material.SULPHUR, 1));
		}
		
		else if(le instanceof Enderman)
		{
			m.mcDropItem(loc, new ItemStack(Material.ENDER_PEARL, 1));
		}
		
		else if(le instanceof PigZombie)
		{
			if(Math.random() * 10 < 5)
				m.mcDropItem(loc, new ItemStack(Material.ROTTEN_FLESH, 1));
			else
				m.mcDropItem(loc, new ItemStack(Material.GOLD_NUGGET, 1));
		}
		
		else if(le instanceof Blaze)
		{
			m.mcDropItem(loc, new ItemStack(Material.BLAZE_ROD, 1));
		}
		
		else if(le instanceof CaveSpider)
		{
			if(Math.random() * 10 < 5)
				m.mcDropItem(loc, new ItemStack(Material.SPIDER_EYE, 1));
			else
				m.mcDropItem(loc, new ItemStack(Material.STRING, 1));
		}
		
		else if(le instanceof Ghast)
		{
			if(Math.random() * 10 < 5)
				m.mcDropItem(loc, new ItemStack(Material.SULPHUR, 1));
			else
				m.mcDropItem(loc, new ItemStack(Material.GHAST_TEAR, 1));
		}
		
		else if(le instanceof MagmaCube)
		{
			m.mcDropItem(loc, new ItemStack(Material.MAGMA_CREAM, 1));
		}
		
		else if(le instanceof Slime)
		{
			m.mcDropItem(loc, new ItemStack(Material.SLIME_BALL, 1));
		}
		
		else if(le instanceof Zombie)
		{
			m.mcDropItem(loc, new ItemStack(Material.ROTTEN_FLESH, 1));
		}
	}
}
