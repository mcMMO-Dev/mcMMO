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

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;


public class Repair {

	/*
	 * Repair requirements for each material
	 */
	private static int rGold =  LoadProperties.rGold;
	private static String nGold =  LoadProperties.nGold;        
	private static int rStone =  LoadProperties.rStone;
	private static String nStone =  LoadProperties.nStone;        
	private static int rWood =  LoadProperties.rWood;
	private static String nWood =  LoadProperties.nWood;        
	private static int rDiamond =  LoadProperties.rDiamond;
	private static String nDiamond =  LoadProperties.nDiamond;        
	private static int rIron =  LoadProperties.rIron;
	private static String nIron =  LoadProperties.nIron;
	private static int rString =  LoadProperties.rString;
	private static String nString =  LoadProperties.nString;
	private static int rLeather =  LoadProperties.rLeather;
	private static String nLeather =  LoadProperties.nLeather;

	public static void repairCheck(Player player, ItemStack is, Block block){
		PlayerProfile PP = Users.getProfile(player);
		short durabilityBefore = player.getItemInHand().getDurability();
		short durabilityAfter = 0;
		short dif = 0;

		//Stuff for keeping enchants
		Enchantment[] enchants = new Enchantment[is.getEnchantments().size()];
		int[] enchantsLevel = new int[is.getEnchantments().size()];

		int pos = 0;
		for(Enchantment x : is.getEnchantments().keySet())
		{
			enchants[pos] = x;
			enchantsLevel[pos] = is.getEnchantmentLevel(x);
			pos++;
		}

		if(block != null && mcPermissions.getInstance().repair(player)){
			if(player.getItemInHand().getDurability() > 0 && player.getItemInHand().getAmount() < 2){

				/*
				 * REPAIR ARMOR
				 */
				if(isArmor(is)){

					//DIAMOND ARMOR
					if(isDiamondArmor(is) && hasItem(player, rDiamond) && PP.getSkillLevel(SkillType.REPAIR) >= LoadProperties.repairdiamondlevel){
						removeItem(player, rDiamond);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						dif = (short) (dif * 6); //Boost XP
						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);
					}

					//IRON ARMOR
					else if (isIronArmor(is) && hasItem(player, rIron)){
						removeItem(player, rIron);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						dif = (short) (dif * 2); //Boost XP
						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);
					}

					//GOLD ARMOR
					else if (isGoldArmor(is) && hasItem(player, rGold)){
						removeItem(player, rGold);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						dif = (short) (dif * 4); //Boost XP
						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);
					} 

					//LEATHER ARMOR
					else if (isLeatherArmor(is) && hasItem(player, rLeather)){
						removeItem(player, rLeather);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						dif = (short) (dif * 1); //Boost XP
						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);
					} 

					//UNABLE TO REPAIR
					else {
						needMoreVespeneGas(is, player);
					}
				}

				/*
				 * REPAIR TOOLS
				 */
				if(isTools(is)){

					//STONE TOOLS
					if(isStoneTools(is) && hasItem(player, rStone)){
						removeItem(player, rStone);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						if(m.isShovel(is))
							dif = (short) (dif / 3);
						if(m.isSwords(is))
							dif = (short) (dif / 2);
						if(m.isHoe(is))
							dif = (short) (dif / 2);
						//STONE NERF
						dif = (short) (dif / 2);

						PP.addXP(SkillType.REPAIR, dif*10, player);
					} 

					//WOOD TOOLS
					else if(isWoodTools(is) && hasItem(player,rWood)){
						removeItem(player,rWood);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						if(m.isShovel(is))
							dif = (short) (dif / 3);
						if(m.isSwords(is))
							dif = (short) (dif / 2);
						if(m.isHoe(is))
							dif = (short) (dif / 2);
						//WOOD NERF
						dif = (short) (dif / 2);

						PP.addXP(SkillType.REPAIR, dif*10, player);
					}

					//IRON TOOLS
					else if(isIronTools(is) && hasItem(player, rIron)){
						removeItem(player, rIron);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						if(m.isShovel(is))
							dif = (short) (dif / 3);
						if(m.isSwords(is))
							dif = (short) (dif / 2);
						if(m.isHoe(is))
							dif = (short) (dif / 2);

						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);

					}

					//DIAMOND TOOLS
					else if (isDiamondTools(is) && hasItem(player, rDiamond) && PP.getSkillLevel(SkillType.REPAIR) >= LoadProperties.repairdiamondlevel){
						removeItem(player, rDiamond);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						if(m.isShovel(is))
							dif = (short) (dif / 3);
						if(m.isSwords(is))
							dif = (short) (dif / 2);
						if(m.isHoe(is))
							dif = (short) (dif / 2);
						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);

					}
					
					//GOLD TOOLS
					else if(isGoldTools(is) && hasItem(player, rGold)){
						removeItem(player, rGold);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						dif = (short) (dif * 7.6); //Boost XP for Gold to that of around Iron
						if(m.isShovel(is))
							dif = (short) (dif / 3);
						if(m.isSwords(is))
							dif = (short) (dif / 2);
						if(m.isHoe(is))
							dif = (short) (dif / 2);
						PP.addXP(SkillType.REPAIR, dif*10, player);

						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);
					}
					
					//BOW
					else if(isBow(is) && hasItem(player, rString)){
						removeItem(player, rString);
						repairItem(player, enchants, enchantsLevel);

						durabilityAfter = player.getItemInHand().getDurability();
						dif = (short) (durabilityBefore - durabilityAfter);
						
						//STRING NERF
						dif = (short) (dif / 2);
						
						PP.addXP(SkillType.REPAIR, dif*10, player);

						
						//CLANG CLANG
						if(LoadProperties.spoutEnabled)
							SpoutStuff.playRepairNoise(player);
					}

					//UNABLE TO REPAIR
					else {
						needMoreVespeneGas(is, player);
					}
				}
			}

			else {
				player.sendMessage(mcLocale.getString("Skills.FullDurability"));
			}

			/*
			 * GIVE SKILL IF THERE IS ENOUGH XP
			 */
			Skills.XpCheckSkill(SkillType.REPAIR, player);
		}
	}
	
	public static int getArcaneForgingRank(PlayerProfile PP){
		int rank = 0;

		if(PP.getSkillLevel(SkillType.REPAIR) >= 750)
			rank = 4;
		
		else if (PP.getSkillLevel(SkillType.REPAIR) >= 500)
			rank = 3;
		
		else if(PP.getSkillLevel(SkillType.REPAIR) >= 250)
			rank = 2;
		
		else if (PP.getSkillLevel(SkillType.REPAIR) >= 100)
			rank = 1;
		
		return rank;
	}
	
	public static void addEnchants(ItemStack is, Enchantment[] enchants, int[] enchantsLvl, PlayerProfile PP, Player player){
		if(is.getEnchantments().keySet().size() == 0)
			return;

		int pos = 0;
		int rank = getArcaneForgingRank(PP);

		if(rank == 0)
		{
			if(LoadProperties.mayLoseEnchants)
			{
				player.sendMessage(mcLocale.getString("Repair.LostEnchants"));
				for(Enchantment x : enchants)
				{
					is.removeEnchantment(x);
				}
			}
			return;
		}

		boolean failure = false, downgrade = false;

		if(LoadProperties.mayLoseEnchants)
		{
			for(Enchantment x : enchants)
			{
				//Remove enchant
				is.removeEnchantment(x);
	
				if(x.canEnchantItem(is))
				{
					if(Math.random() * 100 <= getEnchantChance(rank))
					{
						if(enchantsLvl[pos] > 1)
						{
							if(LoadProperties.mayDowngradeEnchants)
							{
								if(Math.random() * 100 <= getDowngradeChance(rank))
								{
									is.addEnchantment(x, enchantsLvl[pos]-1);
									downgrade = true;
								} else
								{
									is.addEnchantment(x, enchantsLvl[pos]);
								}
							}
						}
						else {
							is.addEnchantment(x, enchantsLvl[pos]);
						}
					} else {
						failure = true;	
					}
				}
				pos++;
			}
		}

		if(failure == false && downgrade == false)
		{
			player.sendMessage(mcLocale.getString("Repair.ArcanePerfect"));
		} else {
			if(failure == true)
				player.sendMessage(mcLocale.getString("Repair.ArcaneFailed"));
			if(downgrade == true)
				player.sendMessage(mcLocale.getString("Repair.Downgraded"));
		}
	}
	public static int getEnchantChance(int rank){
		switch(rank)
		{
		case 4:
			return LoadProperties.keepEnchantsRank4;
		case 3:
			return LoadProperties.keepEnchantsRank3;
		case 2:
			return LoadProperties.keepEnchantsRank2;
		case 1:
			return LoadProperties.keepEnchantsRank1;
		default:
			return 0;
		}
	}
	public static int getDowngradeChance(int rank)
	{
		switch(rank)
		{
		case 4:
			return LoadProperties.downgradeRank4;
		case 3:
			return LoadProperties.downgradeRank3;
		case 2:
			return LoadProperties.downgradeRank2;
		case 1:
			return LoadProperties.downgradeRank1;
		default:
			return 100;
		}
	}
	public static boolean isArmor(ItemStack is){
		return is.getTypeId() == 306 || is.getTypeId() == 307 ||is.getTypeId() == 308 ||is.getTypeId() == 309 || //IRON
				is.getTypeId() == 310 ||is.getTypeId() == 311 ||is.getTypeId() == 312 ||is.getTypeId() == 313 || //DIAMOND
				is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317 || //GOLD
				is.getTypeId() == 298 || is.getTypeId() == 299 || is.getTypeId() == 300 || is.getTypeId() == 301; //LEATHER
	}
	public static boolean isLeatherArmor(ItemStack is){
		return is.getTypeId() == 298 || is.getTypeId() == 299 || is.getTypeId() == 300 || is.getTypeId() == 301;
	}
	public static boolean isGoldArmor(ItemStack is){
		return is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317;
	}
	public static boolean isIronArmor(ItemStack is){
		return is.getTypeId() == 306 || is.getTypeId() == 307 || is.getTypeId() == 308 || is.getTypeId() == 309;
	}
	public static boolean isDiamondArmor(ItemStack is){
		return is.getTypeId() == 310 || is.getTypeId() == 311 || is.getTypeId() == 312 || is.getTypeId() == 313;
	}
	public static boolean isTools(ItemStack is)
	{
		return is.getTypeId() == 359 || is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292 || //IRON
				is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293 || //DIAMOND
				is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284 || is.getTypeId() == 294 || //GOLD
				is.getTypeId() == 268 || is.getTypeId() == 269 || is.getTypeId() == 270 || is.getTypeId() == 271 || is.getTypeId() == 290 ||//WOOD
				is.getTypeId() == 272 || is.getTypeId() == 273 || is.getTypeId() == 274 || is.getTypeId() == 275|| is.getTypeId() == 291 ||  //STONE
				is.getTypeId() == 261; //BOW
	}
	public static boolean isStoneTools(ItemStack is){
		return is.getTypeId() == 272 || is.getTypeId() == 273 || is.getTypeId() == 274 || is.getTypeId() == 275 || is.getTypeId() == 291;
	}
	public static boolean isWoodTools(ItemStack is){
		return is.getTypeId() == 268 || is.getTypeId() == 269 || is.getTypeId() == 270 || is.getTypeId() == 271 || is.getTypeId() == 290;
	}
	public static boolean isGoldTools(ItemStack is){
		return is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284 || is.getTypeId() == 294;
	}
	public static boolean isIronTools(ItemStack is){
		return is.getTypeId() == 359 || is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292;
	}
	public static boolean isDiamondTools(ItemStack is){
		return is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293;
	}
	public static boolean isBow(ItemStack is){
		return is.getTypeId() == 261;
	}
	public static void removeItem(Player player, int typeid)
	{
		ItemStack[] inventory = player.getInventory().getContents();
		for(ItemStack x : inventory){
			if(x != null && x.getTypeId() == typeid){
				if(x.getAmount() == 1){
					x.setTypeId(0);
					x.setAmount(0);
					player.getInventory().setContents(inventory);
				} else{
					x.setAmount(x.getAmount() - 1);
					player.getInventory().setContents(inventory);
				}
				return;
			}
		}
	}
	public static boolean hasItem(Player player, int typeid){
		ItemStack[] inventory = player.getInventory().getContents();
		for(ItemStack x : inventory){
			if(x != null && x.getTypeId() == typeid){
				return true;
			}
		}
		return false;
	}
	public static short repairCalculate(Player player, short durability, int ramt){
		PlayerProfile PP = Users.getProfile(player);
		float bonus = (float)(PP.getSkillLevel(SkillType.REPAIR)) / 500;
		bonus = (ramt * bonus);
		ramt+=bonus;
		if(checkPlayerProcRepair(player)){
			ramt = (short) (ramt * 2);
		}
		durability-=ramt;
		if(durability < 0){
			durability = 0;
		}
		return durability;
	}
	public static short getRepairAmount(ItemStack is, Player player){
		short durability = is.getDurability();
		int ramt = 0;
		switch(is.getTypeId())
		{
		/*
		 * TOOLS
		 */
		
		//SHEARS
		case 359:
			ramt = Material.SHEARS.getMaxDurability() / 2;
			break;
			
		//BOW
		case 261:
			ramt = Material.BOW.getMaxDurability() / 3;
			break;
			
		/* WOOD TOOLS */
			
		//WOOD SWORD
		case 268:
			ramt = Material.WOOD_SWORD.getMaxDurability() / 2;
			break;
		//WOOD SHOVEL
		case 269:
			ramt = Material.WOOD_SPADE.getMaxDurability();
			break;
		//WOOD PICKAXE
		case 270:
			ramt = Material.WOOD_PICKAXE.getMaxDurability() / 3;
			break;
		//WOOD AXE
		case 271:
			ramt = Material.WOOD_AXE.getMaxDurability() / 3;
			break;
		//WOOD HOE
		case 290:
			ramt = Material.WOOD_HOE.getMaxDurability() / 2;
			break;
			
		/* STONE TOOLS */
			
		//STONE SWORD
		case 272:
			ramt = Material.STONE_SWORD.getMaxDurability() / 2;
			break;
		//STONE SHOVEL
		case 273:
			ramt = Material.STONE_SPADE.getMaxDurability();
			break;
		//STONE PICKAXE
		case 274:
			ramt = Material.STONE_PICKAXE.getMaxDurability() / 3;
			break;
		//STONE AXE
		case 275:
			ramt = Material.STONE_AXE.getMaxDurability() / 3;
			break;
		//STONE HOE
		case 291:
			ramt = Material.STONE_HOE.getMaxDurability() / 2;
			break;
			
		/* IRON TOOLS */
			
		//IRON SWORD
		case 267:
			ramt = Material.IRON_SWORD.getMaxDurability() / 2;
			break;
		//IRON SHOVEL
		case 256:
			ramt = Material.IRON_SPADE.getMaxDurability();
			break;
		//IRON PICK
		case 257:
			ramt = Material.IRON_PICKAXE.getMaxDurability() / 3;
			break;
		//IRON AXE
		case 258:
			ramt = Material.IRON_AXE.getMaxDurability() / 3;
			break;
		//IRON HOE
		case 292:
			ramt = Material.IRON_HOE.getMaxDurability() / 2;
			break;
		
		/* DIAMOND TOOLS */
			
		//DIAMOND SWORD
		case 276:
			ramt = Material.DIAMOND_SWORD.getMaxDurability() / 2;
			break;
		//DIAMOND SHOVEL
		case 277:
			ramt = Material.DIAMOND_SPADE.getMaxDurability();
			break;
		//DIAMOND PICK
		case 278:
			ramt = Material.DIAMOND_PICKAXE.getMaxDurability() / 3;
			break;
		//DIAMOND AXE
		case 279:
			ramt = Material.DIAMOND_AXE.getMaxDurability() / 3;
			break;
		//DIAMOND HOE
		case 293:
			ramt = Material.DIAMOND_HOE.getMaxDurability() / 2;
			break;
			
		/* GOLD TOOLS */
			
		//GOLD SWORD
		case 283:
			ramt = Material.GOLD_SWORD.getMaxDurability() / 2;
			break;
		//GOLD SHOVEL
		case 284:
			ramt = Material.GOLD_SPADE.getMaxDurability();
			break;
		//GOLD PICK
		case 285:
			ramt = Material.GOLD_PICKAXE.getMaxDurability() / 3;
			break;
		//GOLD AXE
		case 286:
			ramt = Material.GOLD_AXE.getMaxDurability() / 3;
			break;
		//GOLD HOE
		case 294:
			ramt = Material.GOLD_HOE.getMaxDurability() / 2;
			break;
		/*
		 * ARMOR
		 */
			
		/* IRON ARMOR */

		//IRON HELMET
		case 306:
			ramt = Material.IRON_HELMET.getMaxDurability() / 5;
			break;
		//IRON CHESTPLATE
		case 307:
			ramt = Material.IRON_CHESTPLATE.getMaxDurability() / 8;
			break;
		//IRON LEGGINGS
		case 308:
			ramt = Material.IRON_LEGGINGS.getMaxDurability() / 7;
			break;
		//IRON BOOTS
		case 309:
			ramt = Material.IRON_BOOTS.getMaxDurability() / 4;
			break;
			
		/* DIAMOND ARMOR */

		//DIAMOND HELMET
		case 310:
			ramt = Material.DIAMOND_HELMET.getMaxDurability() / 5;
			break;
		//DIAMOND CHESTPLATE
		case 311:
			ramt = Material.DIAMOND_CHESTPLATE.getMaxDurability() / 8;
			break;
		//DIAMOND LEGGINGS
		case 312:
			ramt = Material.DIAMOND_LEGGINGS.getMaxDurability() / 7;
			break;
		//DIAMOND BOOTS
		case 313:
			ramt = Material.DIAMOND_BOOTS.getMaxDurability() / 4;
			break;
			
		/* GOLD ARMOR */

		//GOLD HELMET
		case 314:
			ramt = Material.GOLD_HELMET.getMaxDurability() / 5;
			break;
		//GOLD CHESTPLATE
		case 315:
			ramt = Material.GOLD_CHESTPLATE.getMaxDurability() / 8;
			break;
		//GOLD LEGGINGS
		case 316:
			ramt = Material.GOLD_LEGGINGS.getMaxDurability() / 7;
			break;
		//GOLD BOOTS
		case 317:
			ramt = Material.GOLD_BOOTS.getMaxDurability() / 4;
			break;			
		
		/* LEATHER ARMOR */
		
		//LEATHER HELMET
		case 298:
			ramt = Material.LEATHER_HELMET.getMaxDurability() / 5;
			break;
		//LEATHER CHESTPLATE
		case 299:
			ramt = Material.LEATHER_CHESTPLATE.getMaxDurability() / 8;
			break;
		//LEATHER LEGGINGS
		case 300:
			ramt = Material.LEATHER_LEGGINGS.getMaxDurability() / 7;
			break;
		//LEATHER BOOTS
		case 301:
			ramt = Material.LEATHER_BOOTS.getMaxDurability() / 4;
			break;			
		}
		
		return repairCalculate(player, durability, ramt);
	}
	
	public static void needMoreVespeneGas(ItemStack is, Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		if ((isDiamondTools(is) || isDiamondArmor(is)) && PP.getSkillLevel(SkillType.REPAIR) < LoadProperties.repairdiamondlevel)
		{
			player.sendMessage(mcLocale.getString("Skills.AdeptDiamond"));
		} else if (isDiamondTools(is) && !hasItem(player, rDiamond) || isIronTools(is) && !hasItem(player, rIron) || isGoldTools(is) && !hasItem(player, rGold)){
			if(isDiamondTools(is) && !hasItem(player, rDiamond))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.BLUE+ nDiamond);
			if(isIronTools(is) && !hasItem(player, rIron))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GRAY+ nIron);
			if(isGoldTools(is) && !hasItem(player, rGold))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GOLD+nGold);
			if(isWoodTools(is) && !hasItem(player,rWood))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.DARK_GREEN+ nWood);
			if(isStoneTools(is) && !hasItem(player, rStone))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GRAY+nStone);
		} else if (isDiamondArmor(is) && !hasItem(player, rDiamond)){
			player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.BLUE+ nDiamond);
		} else if (isIronArmor(is) && !hasItem(player, rIron)){
			player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GRAY+ nIron);
		} else if (isGoldArmor(is) && !hasItem(player, rGold)){
			player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GOLD+ nGold);
		} else if (isLeatherArmor(is) && !hasItem(player, rLeather)){
			player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.YELLOW+ nLeather);
		} else if (isBow(is) && !hasItem(player, rString)){
			player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.YELLOW+ nString);
		} else if (is.getAmount() > 1)
			player.sendMessage(mcLocale.getString("Skills.StackedItems"));
	}
	public static boolean checkPlayerProcRepair(Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		if(player != null)
		{
			if((Math.random() * 1000 <= PP.getSkillLevel(SkillType.REPAIR)) || PP.getSkillLevel(SkillType.REPAIR) > 1000)
			{
				player.sendMessage(mcLocale.getString("Skills.FeltEasy"));
				return true;
			}
		}
		return false;
	}
	public static void repairItem(Player player, Enchantment[] enchants, int[] enchantsLevel)
	{
		PlayerProfile PP = Users.getProfile(player);
		ItemStack is = player.getItemInHand();
		//Handle the enchantments
		addEnchants(player.getItemInHand(), enchants, enchantsLevel, PP, player);
		player.getItemInHand().setDurability(getRepairAmount(is, player));
	}
}