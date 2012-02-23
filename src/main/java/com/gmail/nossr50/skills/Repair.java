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

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;


public class Repair {

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
	
	private static int dLevel = LoadProperties.repairdiamondlevel;
	private static int iLevel = LoadProperties.repairIronLevel;
	private static int gLevel = LoadProperties.repairGoldLevel;
	private static int sLevel = LoadProperties.repairStoneLevel;
	private static boolean spout = LoadProperties.spoutEnabled;
	
	public static void repairCheck(Player player, ItemStack is, Block block){
		PlayerProfile PP = Users.getProfile(player);
		short durabilityBefore = is.getDurability();
		PlayerInventory inventory = player.getInventory();
		int skillLevel = PP.getSkillLevel(SkillType.REPAIR);
		
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
			if(durabilityBefore > 0 && is.getAmount() < 2){

				/*
				 * REPAIR ARMOR
				 */
				if(isArmor(is) && LoadProperties.repairArmor){

					//DIAMOND ARMOR
					if(isDiamondArmor(is) && inventory.contains(rDiamond) && skillLevel >= dLevel){
						inventory.removeItem(new ItemStack(rDiamond, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 6, true);
					}

					//IRON ARMOR
					else if (isIronArmor(is) && inventory.contains(rIron) && skillLevel >= iLevel){
						inventory.removeItem(new ItemStack(rIron, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 2, true);
					}

					//GOLD ARMOR
					else if (isGoldArmor(is) && inventory.contains(rGold) && skillLevel >= gLevel){
						inventory.removeItem(new ItemStack(rGold, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 4, true);
					} 

					//LEATHER ARMOR
					else if (isLeatherArmor(is) && inventory.contains(rLeather)){
						inventory.removeItem(new ItemStack(rLeather, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 1, true);
					} 

					//UNABLE TO REPAIR
					else {
						needMoreVespeneGas(is, player);
					}
				}

				/*
				 * REPAIR TOOLS
				 */
				if(isTools(is) && LoadProperties.repairTools){

					//STONE TOOLS
					if(isStoneTools(is) && inventory.contains(rStone) && skillLevel >= sLevel){
						inventory.removeItem(new ItemStack(rStone, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 2, false);
					} 

					//WOOD TOOLS
					else if(isWoodTools(is) && inventory.contains(rWood)){
						inventory.removeItem(new ItemStack(rWood, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 2, false);
					}

					//IRON TOOLS
					else if(isIronTools(is) && inventory.contains(rIron) && skillLevel >= iLevel){
						inventory.removeItem(new ItemStack(rIron, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 1, true);
					}

					//DIAMOND TOOLS
					else if (isDiamondTools(is) && inventory.contains(rDiamond) && skillLevel >= dLevel){
						inventory.removeItem(new ItemStack(rDiamond, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 1, true);
					}
					
					//GOLD TOOLS
					else if(isGoldTools(is) && inventory.contains(rGold) && skillLevel >= gLevel){
						inventory.removeItem(new ItemStack(rGold, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 8, true);
					}
					
					//BOW
					else if(isBow(is) && inventory.contains(rString)){
						inventory.removeItem(new ItemStack(rString, 1));
						repairItem(player, enchants, enchantsLevel);
						xpHandler(player, PP, is, durabilityBefore, 2, false);
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
	
	public static void xpHandler(Player player, PlayerProfile PP, ItemStack is, short durabilityBefore, int modify, boolean boost)
	{
		short durabilityAfter = is.getDurability();
		short dif = (short) (durabilityBefore - durabilityAfter);
		if(boost)
			dif = (short) (dif * modify);
		if(!boost)
			dif = (short) (dif / modify);
		if(m.isShovel(is))
			dif = (short) (dif / 3);
		if(m.isSwords(is))
			dif = (short) (dif / 2);
		if(m.isHoe(is))
			dif = (short) (dif / 2);
		
		PP.addXP(SkillType.REPAIR, dif*10, player);
		
		//CLANG CLANG
		if(spout)
			SpoutStuff.playRepairNoise(player);
	}
	
	/**
	 * Get current Arcane Forging rank.
	 * 
	 * @param skillLevel The skill level of the player whose rank is being checked
	 * @return The player's current Arcane Forging rank
	 */
	public static int getArcaneForgingRank(int skillLevel)
	{
		if(skillLevel >= LoadProperties.arcaneRank4)
			return 4;
		if (skillLevel >= LoadProperties.arcaneRank3)
			return 3;
		if(skillLevel >= LoadProperties.arcaneRank2)
			return 2;
		if (skillLevel >= LoadProperties.arcaneRank1)
			return 1;
		
		return 0;
	}
	
	public static void addEnchants(ItemStack is, Enchantment[] enchants, int[] enchantsLvl, PlayerProfile PP, Player player){
		if(is.getEnchantments().keySet().size() == 0)
			return;

		int pos = 0;
		int rank = getArcaneForgingRank(PP.getSkillLevel(SkillType.REPAIR));

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
	
	/**
	 * Gets chance of keeping enchantment during repair.
	 * 
	 * @param rank Arcane Forging rank
	 * @return The chance of keeping the enchantment 
	 */
	public static int getEnchantChance(int rank)
	{
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
	
	/**
	 * Gets chance of enchantment being downgraded during repair.
	 * 
	 * @param rank Arcane Forging rank
	 * @return The chance of the enchantment being downgraded
	 */
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
	
	/**
	 * Computes repair bonuses.
	 * 
	 * @param player The player repairing an item
	 * @param durability The durability of the item being repaired
	 * @param ramt The base amount of durability repaired to the item 
	 * @return The final amount of durability repaired to the item
	 */
	public static short repairCalculate(Player player, short durability, int ramt){
		int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);
		float bonus = (float)(skillLevel/500);
		bonus = (ramt * bonus);
		ramt+=bonus;
		if(checkPlayerProcRepair(player))
			ramt = (short) (ramt * 2);
		durability-=ramt;
		if(durability < 0)
			durability = 0;
		return durability;
	}
	
	/**
	 * Gets the base durability amount to repair an item.
	 * 
	 * @param is The item being repaired
	 * @param player The player repairing the item
	 * @return The final amount of durability repaired to the item
	 */
	public static short getRepairAmount(ItemStack is, Player player){
		short durability = is.getDurability();
		short maxDurability = is.getType().getMaxDurability();
		int ramt = 0;
		
		if(m.isShovel(is))
			ramt = maxDurability;
		else if(m.isHoe(is) || m.isSwords(is) || is.getTypeId() == 359)
			ramt = maxDurability / 2;
		else if(m.isAxes(is) || m.isMiningPick(is) || isBow(is))
			ramt = maxDurability / 3;
		else if(m.isBoots(is))
			ramt = maxDurability / 4;
		else if(m.isHelmet(is))
			ramt = maxDurability / 5;
		else if(m.isPants(is))
			ramt = maxDurability / 7;
		else if(m.isChestplate(is))
			ramt = maxDurability / 8;
				
		return repairCalculate(player, durability, ramt);
	}
	
	/**
	 * Informs a player that the repair has failed.
	 * 
	 * @param is The item being repaired
	 * @param player The player repairing the item
	 */
	public static void needMoreVespeneGas(ItemStack is, Player player)
	{
		int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);
		
		if(is.getAmount() > 1)
			player.sendMessage(mcLocale.getString("Skills.StackedItems"));
		else
		{
			if(isDiamondTools(is) || isDiamondArmor(is))
			{
				if(skillLevel < LoadProperties.repairdiamondlevel)
					player.sendMessage(mcLocale.getString("Skills.AdeptDiamond"));
				else
					player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.BLUE+ nDiamond);
			}
			else if(isIronTools(is) || isIronArmor(is))
			{
				if(skillLevel < LoadProperties.repairIronLevel)
					player.sendMessage(mcLocale.getString("Skills.AdeptIron"));
				else
					player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GRAY+ nIron);
			}
			else if(isGoldTools(is) || isGoldArmor(is))
			{
				if(skillLevel < LoadProperties.repairGoldLevel)
					player.sendMessage(mcLocale.getString("Skills.AdeptGold"));
				else
					player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GOLD+nGold);
			}
			else if(isStoneTools(is))
			{
				if(skillLevel < LoadProperties.repairStoneLevel)
					player.sendMessage(mcLocale.getString("Skills.AdeptStone"));
				else
					player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.GRAY+nStone);
			}
			else if(isWoodTools(is))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.DARK_GREEN+ nWood);
			else if (isLeatherArmor(is))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.YELLOW+ nLeather);
			else if (isBow(is))
				player.sendMessage(mcLocale.getString("Skills.NeedMore")+" "+ChatColor.YELLOW+ nString);
		}
	}
	
	/**
	 * Checks for Super Repair bonus.
	 * 
	 * @param player The player repairing an item.
	 * @return true if bonus granted, false otherwise
	 */
	public static boolean checkPlayerProcRepair(Player player)
	{
		int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.REPAIR);
		if(skillLevel > 1000 || (Math.random() * 1000 <= skillLevel))
		{
			player.sendMessage(mcLocale.getString("Skills.FeltEasy"));
			return true;
		}
		return false;
	}
	
	/**
	 * Repairs an item.
	 * 
	 * @param player The player repairing an item
	 * @param enchants The enchantments on the item
	 * @param enchantsLevel The level of the enchantments on the item
	 */
	public static void repairItem(Player player, Enchantment[] enchants, int[] enchantsLevel)
	{
		PlayerProfile PP = Users.getProfile(player);
		ItemStack is = player.getItemInHand();
		//Handle the enchantments
		addEnchants(is, enchants, enchantsLevel, PP, player);
		is.setDurability(getRepairAmount(is, player));
	}
}