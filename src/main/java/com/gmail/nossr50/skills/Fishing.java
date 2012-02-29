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

import java.util.List;

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
import com.gmail.nossr50.config.LoadTreasures;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.locale.mcLocale;

public class Fishing {

	//Return the fishing tier for the player
	public static int getFishingLootTier(PlayerProfile PP)
	{
		int lvl = PP.getSkillLevel(SkillType.FISHING);

		if(lvl >= LoadProperties.fishingTier1 && lvl < LoadProperties.fishingTier2)
		{
			return 1;
		} 
		else if (lvl >= LoadProperties.fishingTier2 && lvl < LoadProperties.fishingTier3)
		{
			return 2;
		} 
		else if (lvl >= LoadProperties.fishingTier3 && lvl < LoadProperties.fishingTier4)
		{
			return 3;
		} 
		else if (lvl >= LoadProperties.fishingTier4 && lvl < LoadProperties.fishingTier5)
		{
			return 4;
		}
		else
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
		Item theCatch = (Item)event.getCaught();
		if(LoadProperties.fishingDrops)
		{
			List<FishingTreasure> rewards = LoadTreasures.fishingRewardsTier1;
			FishingTreasure treasure = rewards.get((int)(Math.random() * rewards.size()));
			
			if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
			{
				Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp(), player);
				theCatch.setItemStack(treasure.getDrop());
			}
		} 
		else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value		
	}

	private static void getFishingResultsTier2(Player player, PlayerFishEvent event)
	{
		Item theCatch = (Item)event.getCaught();
		if(LoadProperties.fishingDrops)
		{
			List<FishingTreasure> rewards = LoadTreasures.fishingRewardsTier2;
			FishingTreasure treasure = rewards.get((int)(Math.random() * rewards.size()));
			
			if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
			{
				Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp(), player);
				theCatch.setItemStack(treasure.getDrop());
			}
		} 
		else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}

	private static void getFishingResultsTier3(Player player, PlayerFishEvent event)
	{
		Item theCatch = (Item)event.getCaught();
		if(LoadProperties.fishingDrops)
		{
			List<FishingTreasure> rewards = LoadTreasures.fishingRewardsTier3;
			FishingTreasure treasure = rewards.get((int)(Math.random() * rewards.size()));
			
			if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
			{
				Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp(), player);
				theCatch.setItemStack(treasure.getDrop());
			}
		} 
		else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}

	private static void getFishingResultsTier4(Player player, PlayerFishEvent event)
	{
		Item theCatch = (Item)event.getCaught();
		if(LoadProperties.fishingDrops)
		{
			List<FishingTreasure> rewards = LoadTreasures.fishingRewardsTier4;
			FishingTreasure treasure = rewards.get((int)(Math.random() * rewards.size()));
			
			if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
			{
				Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp(), player);
				theCatch.setItemStack(treasure.getDrop());
			}
		} 
		else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
		}
		//Change durability to random value
		theCatch.getItemStack().setDurability((short) (Math.random() * theCatch.getItemStack().getType().getMaxDurability())); //Change the damage value
	}

	private static void getFishingResultsTier5(Player player, PlayerFishEvent event)
	{
		Item theCatch = (Item)event.getCaught();
		if(LoadProperties.fishingDrops)
		{
			List<FishingTreasure> rewards = LoadTreasures.fishingRewardsTier5;
			FishingTreasure treasure = rewards.get((int)(Math.random() * rewards.size()));
			
			if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
			{
				Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp(), player);
				theCatch.setItemStack(treasure.getDrop());
			}
		} 
		else
		{
			theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
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
		
		Combat.dealDamage(le, 1);
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
			if(Math.random() * 100 >= 99){
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
			if(Math.random() * 100 >= 99){
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
			if(Math.random() * 100 >= 99){
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
