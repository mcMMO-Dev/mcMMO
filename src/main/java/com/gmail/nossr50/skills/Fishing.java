package com.gmail.nossr50.skills;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import com.gmail.nossr50.Users;
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
		player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1));
		Users.getProfile(player).addXP(SkillType.FISHING, LoadProperties.mfishing, player);
		Skills.XpCheckSkill(SkillType.FISHING, player);
	}

	private static void getFishingResultsTier1(Player player, PlayerFishEvent event)
	{
		int randomNum = (int)(Math.random() * 15);
		CraftItem theCatch = (CraftItem)event.getCaught();

		if(Math.random() * 100 < 20)
		{
			switch(randomNum)
			{
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
		CraftItem theCatch = (CraftItem)event.getCaught();

		if(Math.random() * 100 < 25)
		{
			switch(randomNum)
			{
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
			case 5:
				theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				break;
			case 6:
				theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				break;
			case 7:
				theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				break;
			case 8:
				theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				break;
			case 9:
				theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				break;
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
			case 15:
				theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 16:
				theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				break;
			case 17:
				theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				break;
			case 18:
				theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				break;
			case 19:
				theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
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
		CraftItem theCatch = (CraftItem)event.getCaught();

		if(Math.random() * 100 < 30)
		{
			switch(randomNum)
			{
			case 1:
				theCatch.setItemStack(new ItemStack(Material.GOLD_BOOTS, 1));
				break;
			case 2:
				theCatch.setItemStack(new ItemStack(Material.GOLD_HELMET, 1));
				break;
			case 3:
				theCatch.setItemStack(new ItemStack(Material.GOLD_LEGGINGS, 1));
				break;
			case 4:
				theCatch.setItemStack(new ItemStack(Material.GOLD_CHESTPLATE, 1));
				break;
			case 5:
				theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				break;
			case 6:
				theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				break;
			case 7:
				theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				break;
			case 8:
				theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				break;
			case 9:
				theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				break;
			case 10:
				theCatch.setItemStack(new ItemStack(Material.GOLD_AXE, 1));
				break;
			case 11:
				theCatch.setItemStack(new ItemStack(Material.GOLD_PICKAXE, 1));
				break;
			case 12:
				theCatch.setItemStack(new ItemStack(Material.GOLD_SWORD, 1));
				break;
			case 13:
				theCatch.setItemStack(new ItemStack(Material.GOLD_HOE, 1));
				break;
			case 14:
				theCatch.setItemStack(new ItemStack(Material.GOLD_SPADE, 1));
				break;
			case 15:
				theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 16:
				theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				break;
			case 17:
				theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				break;
			case 18:
				theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				break;
			case 19:
				theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				break;
			case 20:
				theCatch.setItemStack(new ItemStack(Material.BLAZE_ROD, 1));
				break;
			case 21:
				theCatch.setItemStack(new ItemStack(Material.RECORD_3, 1));
				break;
			case 22:
				theCatch.setItemStack(new ItemStack(Material.RECORD_4, 1));
				break;
			case 23:
				theCatch.setItemStack(new ItemStack(Material.RECORD_5, 1));
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
		CraftItem theCatch = (CraftItem)event.getCaught();

		if(Math.random() * 100 < 35)
		{
			switch(randomNum)
			{
			case 1:
				theCatch.setItemStack(new ItemStack(Material.GOLD_BOOTS, 1));
				break;
			case 2:
				theCatch.setItemStack(new ItemStack(Material.GOLD_HELMET, 1));
				break;
			case 3:
				theCatch.setItemStack(new ItemStack(Material.GOLD_LEGGINGS, 1));
				break;
			case 4:
				theCatch.setItemStack(new ItemStack(Material.GOLD_CHESTPLATE, 1));
				break;
			case 5:
				theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				break;
			case 6:
				theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				break;
			case 7:
				theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				break;
			case 8:
				theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				break;
			case 9:
				theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				break;
			case 10:
				theCatch.setItemStack(new ItemStack(Material.GOLD_AXE, 1));
				break;
			case 11:
				theCatch.setItemStack(new ItemStack(Material.GOLD_PICKAXE, 1));
				break;
			case 12:
				theCatch.setItemStack(new ItemStack(Material.GOLD_SWORD, 1));
				break;
			case 13:
				theCatch.setItemStack(new ItemStack(Material.GOLD_HOE, 1));
				break;
			case 14:
				theCatch.setItemStack(new ItemStack(Material.GOLD_SPADE, 1));
				break;
			case 15:
				theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 16:
				theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				break;
			case 17:
				theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				break;
			case 18:
				theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				break;
			case 19:
				theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				break;
			case 20:
				theCatch.setItemStack(new ItemStack(Material.BLAZE_ROD, 1));
				break;
			case 21:
				theCatch.setItemStack(new ItemStack(Material.RECORD_3, 1));
				break;
			case 22:
				theCatch.setItemStack(new ItemStack(Material.RECORD_4, 1));
				break;
			case 23:
				theCatch.setItemStack(new ItemStack(Material.RECORD_5, 1));
				break;
			case 24:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_BOOTS, 1));
				break;
			case 25:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_HELMET, 1));
				break;
			case 26:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
				break;
			case 27:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				break;
			case 28:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_AXE, 1));
				break;
			case 29:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_PICKAXE, 1));
				break;
			case 30:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_SWORD, 1));
				break;
			case 31:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_HOE, 1));
				break;
			case 32:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_SPADE, 1));
				break;
			case 33:
				theCatch.setItemStack(new ItemStack(Material.RECORD_6, 1));
				break;
			case 34:
				theCatch.setItemStack(new ItemStack(Material.RECORD_7, 1));
				break;
			case 35:
				theCatch.setItemStack(new ItemStack(Material.RECORD_8, 1));
				break;
			case 36:
				theCatch.setItemStack(new ItemStack(Material.RECORD_9, 1));
				break;
			case 37:
				theCatch.setItemStack(new ItemStack(Material.RECORD_10, 1));
				break;
			case 38:
				theCatch.setItemStack(new ItemStack(Material.RECORD_11, 1));
				break;
			case 39:
				theCatch.setItemStack(new ItemStack(Material.REDSTONE_WIRE, 64));
				break;
			case 40:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND, (int)(Math.random() * 10)));
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
		CraftItem theCatch = (CraftItem)event.getCaught();

		if(Math.random() * 100 < 40)
		{
			switch(randomNum)
			{
			case 1:
				theCatch.setItemStack(new ItemStack(Material.GOLD_BOOTS, 1));
				break;
			case 2:
				theCatch.setItemStack(new ItemStack(Material.GOLD_HELMET, 1));
				break;
			case 3:
				theCatch.setItemStack(new ItemStack(Material.GOLD_LEGGINGS, 1));
				break;
			case 4:
				theCatch.setItemStack(new ItemStack(Material.GOLD_CHESTPLATE, 1));
				break;
			case 5:
				theCatch.setItemStack(new ItemStack(Material.IRON_AXE, 1));
				break;
			case 6:
				theCatch.setItemStack(new ItemStack(Material.IRON_PICKAXE, 1));
				break;
			case 7:
				theCatch.setItemStack(new ItemStack(Material.IRON_SWORD, 1));
				break;
			case 8:
				theCatch.setItemStack(new ItemStack(Material.IRON_HOE, 1));
				break;
			case 9:
				theCatch.setItemStack(new ItemStack(Material.IRON_SPADE, 1));
				break;
			case 10:
				theCatch.setItemStack(new ItemStack(Material.GOLD_AXE, 1));
				break;
			case 11:
				theCatch.setItemStack(new ItemStack(Material.GOLD_PICKAXE, 1));
				break;
			case 12:
				theCatch.setItemStack(new ItemStack(Material.GOLD_SWORD, 1));
				break;
			case 13:
				theCatch.setItemStack(new ItemStack(Material.GOLD_HOE, 1));
				break;
			case 14:
				theCatch.setItemStack(new ItemStack(Material.GOLD_SPADE, 1));
				break;
			case 15:
				theCatch.setItemStack(new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 16:
				theCatch.setItemStack(new ItemStack(Material.IRON_LEGGINGS, 1));
				break;
			case 17:
				theCatch.setItemStack(new ItemStack(Material.IRON_CHESTPLATE, 1));
				break;
			case 18:
				theCatch.setItemStack(new ItemStack(Material.IRON_HELMET, 1));
				break;
			case 19:
				theCatch.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
				break;
			case 20:
				theCatch.setItemStack(new ItemStack(Material.BLAZE_ROD, 1));
				break;
			case 21:
				theCatch.setItemStack(new ItemStack(Material.RECORD_3, 1));
				break;
			case 22:
				theCatch.setItemStack(new ItemStack(Material.RECORD_4, 1));
				break;
			case 23:
				theCatch.setItemStack(new ItemStack(Material.RECORD_5, 1));
				break;
			case 24:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_BOOTS, 1));
				break;
			case 25:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_HELMET, 1));
				break;
			case 26:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
				break;
			case 27:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				break;
			case 28:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_AXE, 1));
				break;
			case 29:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_PICKAXE, 1));
				break;
			case 30:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_SWORD, 1));
				break;
			case 31:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_HOE, 1));
				break;
			case 32:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_SPADE, 1));
				break;
			case 33:
				theCatch.setItemStack(new ItemStack(Material.RECORD_6, 1));
				break;
			case 34:
				theCatch.setItemStack(new ItemStack(Material.RECORD_7, 1));
				break;
			case 35:
				theCatch.setItemStack(new ItemStack(Material.RECORD_8, 1));
				break;
			case 36:
				theCatch.setItemStack(new ItemStack(Material.RECORD_9, 1));
				break;
			case 37:
				theCatch.setItemStack(new ItemStack(Material.RECORD_10, 1));
				break;
			case 38:
				theCatch.setItemStack(new ItemStack(Material.RECORD_11, 1));
				break;
			case 39:
				theCatch.setItemStack(new ItemStack(Material.REDSTONE_WIRE, 64));
				break;
			case 40:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND, (int)(Math.random() * 20)));
				break;
			case 41:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_BOOTS, 1));
				break;
			case 42:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_HELMET, 1));
				break;
			case 43:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
				break;
			case 44:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
				break;
			case 45:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_AXE, 1));
				break;
			case 46:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_PICKAXE, 1));
				break;
			case 47:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_SWORD, 1));
				break;
			case 48:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_HOE, 1));
				break;
			case 49:
				theCatch.setItemStack(new ItemStack(Material.DIAMOND_SPADE, 1));
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
		CraftItem theCatch = (CraftItem)event.getCaught();

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
							if((fishingResults.containsEnchantment(PROTECTION_ENVIRONMENTAL) || fishingResults.containsEnchantment(PROTECTION_EXPLOSIONS) || 
									fishingResults.containsEnchantment(PROTECTION_FIRE) || fishingResults.containsEnchantment(PROTECTION_PROJECTILE)) && 
									(x.equals(PROTECTION_EXPLOSIONS) || x.equals(PROTECTION_PROJECTILE) || x.equals(PROTECTION_FIRE) || x.equals(PROTECTION_ENVIRONMENTAL))){
								return;
							}
							
							//More impossible enchantment combinations
							else if((fishingResults.containsEnchantment(DAMAGE_ALL) || fishingResults.containsEnchantment(DAMAGE_ARTHROPODS) || fishingResults.containsEnchantment(DAMAGE_UNDEAD)) && 
									(x.equals(DAMAGE_ALL) || x.equals(DAMAGE_ARTHROPODS) || x.equals(DAMAGE_UNDEAD))){
								return;
							}
							
							//Even more impossible enchantment combinations
							else if((fishingResults.containsEnchantment(SILK_TOUCH) || fishingResults.containsEnchantment(LOOT_BONUS_BLOCKS)) &&
									(x.equals(SILK_TOUCH) || x.equals(LOOT_BONUS_BLOCKS))){
								return;
							}
							
							else{		
								//Actual chance to have an enchantment is related to your fishing skill
								if(Math.random() * 15 < Fishing.getFishingLootTier(PP))
								{
									enchanted = true;
									int randomEnchantLevel = (int)(Math.random() * x.getMaxLevel());
									
									if(randomEnchantLevel == 0)
										randomEnchantLevel = 1;
									if(randomEnchantLevel > x.getMaxLevel())
										randomEnchantLevel = x.getMaxLevel();

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
		if(le instanceof Player)
			return;
		le.damage(1);
		World world = le.getWorld();

		if(le instanceof Sheep)
		{
			Sheep sheep = (Sheep)le;
			if(!sheep.isSheared())
			{
				Wool wool = new Wool();
				wool.setColor(sheep.getColor());
				ItemStack theWool = wool.toItemStack();
				theWool.setAmount((int)(Math.random() * 6));
				world.dropItemNaturally(le.getLocation(), theWool);
				sheep.setSheared(true);
			}
		} else if(le instanceof Pig)
		{
			world.dropItemNaturally(le.getLocation(), new ItemStack(Material.PORK, 1));
		} else if(le instanceof Skeleton)
		{
			if(Math.random() * 10 < 5)
				world.dropItemNaturally(le.getLocation(), new ItemStack(Material.BONE, 1));
			else
				world.dropItemNaturally(le.getLocation(), new ItemStack(Material.ARROW, 3));
		} else if(le instanceof Cow)
		{
			world.dropItemNaturally(le.getLocation(), new ItemStack(Material.LEATHER, 1));
		} else if(le instanceof Spider)
		{
			if(Math.random() * 10 < 5)
				world.dropItemNaturally(le.getLocation(), new ItemStack(Material.SPIDER_EYE, 1));
			else
				world.dropItemNaturally(le.getLocation(), new ItemStack(Material.STRING, 1));
		} else if(le instanceof Chicken)
		{
			if(Math.random() * 10 < 5)
				world.dropItemNaturally(le.getLocation(), new ItemStack(Material.FEATHER, 1));
			else
				world.dropItemNaturally(le.getLocation(), new ItemStack(Material.EGG, 1));
		} else if(le instanceof Creeper)
		{
			world.dropItemNaturally(le.getLocation(), new ItemStack(Material.SULPHUR, 1));
		}
	}
}
