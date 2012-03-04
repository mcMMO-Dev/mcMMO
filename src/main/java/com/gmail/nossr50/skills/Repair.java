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

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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

		if(block != null && mcPermissions.getInstance().repair(player)){
			if(durabilityBefore > 0 && is.getAmount() < 2){

				/*
				 * REPAIR ARMOR
				 */
				if(isArmor(is) && LoadProperties.repairArmor){

					//DIAMOND ARMOR
					if(isDiamondArmor(is) && inventory.contains(rDiamond) && skillLevel >= dLevel){
						inventory.removeItem(new ItemStack(rDiamond, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 6, true);
					}

					//IRON ARMOR
					else if (isIronArmor(is) && inventory.contains(rIron) && skillLevel >= iLevel){
						inventory.removeItem(new ItemStack(rIron, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 2, true);
					}

					//GOLD ARMOR
					else if (isGoldArmor(is) && inventory.contains(rGold) && skillLevel >= gLevel){
						inventory.removeItem(new ItemStack(rGold, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 4, true);
					} 

					//LEATHER ARMOR
					else if (isLeatherArmor(is) && inventory.contains(rLeather)){
						inventory.removeItem(new ItemStack(rLeather, 1));
						repairItem(player, is);
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
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 2, false);
					} 

					//WOOD TOOLS
					else if(isWoodTools(is) && inventory.contains(rWood)){
						inventory.removeItem(new ItemStack(rWood, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 2, false);
					}

					//IRON TOOLS
					else if(isIronTools(is) && inventory.contains(rIron) && skillLevel >= iLevel){
						inventory.removeItem(new ItemStack(rIron, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 1, true);
					}

					//DIAMOND TOOLS
					else if (isDiamondTools(is) && inventory.contains(rDiamond) && skillLevel >= dLevel){
						inventory.removeItem(new ItemStack(rDiamond, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 1, true);
					}
					
					//GOLD TOOLS
					else if(isGoldTools(is) && inventory.contains(rGold) && skillLevel >= gLevel){
						inventory.removeItem(new ItemStack(rGold, 1));
						repairItem(player, is);
						xpHandler(player, PP, is, durabilityBefore, 8, true);
					}
					
					//BOW
					else if(isBow(is) && inventory.contains(rString)){
						inventory.removeItem(new ItemStack(rString, 1));
						repairItem(player, is);
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
	
	public static void addEnchants(Player player, ItemStack is)
	{
		Map<Enchantment, Integer> enchants = is.getEnchantments();
		if(enchants.size() == 0)
			return;
		
		int rank = getArcaneForgingRank(Users.getProfile(player).getSkillLevel(SkillType.REPAIR));
		if(rank == 0)
		{
			for(Enchantment x : enchants.keySet())
				is.removeEnchantment(x);
			player.sendMessage(mcLocale.getString("Repair.LostEnchants"));
			return;
		}
		
		boolean downgraded = false;
		for(Entry<Enchantment, Integer> enchant : enchants.entrySet())
		{
			if(Math.random() * 100 <= getEnchantChance(rank))
			{
				int enchantLevel = enchant.getValue();
				if(LoadProperties.mayDowngradeEnchants && enchantLevel > 1)
				{
					if(Math.random() * 100 <= getDowngradeChance(rank))
					{
						is.addEnchantment(enchant.getKey(), enchantLevel--);
						downgraded = true;
					}
				}
			}
			else
				is.removeEnchantment(enchant.getKey());
		}
		
		Map<Enchantment, Integer> newEnchants = is.getEnchantments();
		if(newEnchants.isEmpty())
			player.sendMessage(mcLocale.getString("Repair.ArcaneFailed"));
		else if(downgraded || newEnchants.size() < enchants.size())
			player.sendMessage(mcLocale.getString("Repair.Downgraded"));
		else
			player.sendMessage(mcLocale.getString("Repair.ArcanePerfect"));

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
		return isLeatherArmor(is) || isGoldArmor(is) || isIronArmor(is) || isDiamondArmor(is);
	}
	
	public static boolean isLeatherArmor(ItemStack is){
		switch(is.getType()){
		case LEATHER_BOOTS:
		case LEATHER_CHESTPLATE:
		case LEATHER_HELMET:
		case LEATHER_LEGGINGS:
			return true;
		}
		return false;
	}
	
	public static boolean isGoldArmor(ItemStack is){
		switch(is.getType()){
		case GOLD_BOOTS:
		case GOLD_CHESTPLATE:
		case GOLD_HELMET:
		case GOLD_LEGGINGS:
			return true;
		}
		return false;
	}
	
	public static boolean isIronArmor(ItemStack is){
		switch(is.getType()){
		case IRON_BOOTS:
		case IRON_CHESTPLATE:
		case IRON_HELMET:
		case IRON_LEGGINGS:
			return true;
		}
		return false;
	}
	
	public static boolean isDiamondArmor(ItemStack is){
		switch(is.getType()){
		case DIAMOND_BOOTS:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_HELMET:
		case DIAMOND_LEGGINGS:
			return true;
		}
		return false;
	}
	
	public static boolean isTools(ItemStack is)
	{
		return isStoneTools(is) || isWoodTools(is) || isGoldTools(is) || isIronTools(is) || isDiamondTools(is) || isBow(is);
	}
	
	public static boolean isStoneTools(ItemStack is){
		switch(is.getType()){
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SPADE:
		case STONE_SWORD:
			return true;
		}
		return false;
	}
	public static boolean isWoodTools(ItemStack is){
		switch(is.getType()){
		case WOOD_AXE:
		case WOOD_HOE:
		case WOOD_PICKAXE:
		case WOOD_SPADE:
		case WOOD_SWORD:
			return true;
		}
		return false;
	}
	public static boolean isGoldTools(ItemStack is){
		switch(is.getType()){
		case GOLD_AXE:
		case GOLD_HOE:
		case GOLD_PICKAXE:
		case GOLD_SPADE:
		case GOLD_SWORD:
			return true;
		}
		return false;
	}
	public static boolean isIronTools(ItemStack is){
		switch(is.getType()){
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SPADE:
		case IRON_SWORD:
		case SHEARS:
			return true;
		}
		return false;
	}
	public static boolean isDiamondTools(ItemStack is){
		switch(is.getType()){
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SPADE:
		case DIAMOND_SWORD:
			return true;
		}
		return false;
	}
	
	public static boolean isBow(ItemStack is){
		return is.getType() == Material.BOW;
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
	public static void repairItem(Player player, ItemStack is)
	{
		//Handle the enchantments
		if(LoadProperties.mayLoseEnchants)
			addEnchants(player, is);
		is.setDurability(getRepairAmount(is, player));
	}
}