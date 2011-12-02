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
		
    	if(block != null && mcPermissions.getInstance().repair(player))
    	{
    		//Handle the enchantments
        	addEnchants(player.getItemInHand(), enchants, enchantsLevel, PP, player);
        	
        	if(player.getItemInHand().getDurability() > 0 && player.getItemInHand().getAmount() < 2){
        		/*
        		 * ARMOR
        		 */
        		if(isArmor(is)){
        			/*
        			 * DIAMOND ARMOR
        			 */
        			if(isDiamondArmor(is) && hasItem(player, rDiamond) && PP.getSkillLevel(SkillType.REPAIR) >= LoadProperties.repairdiamondlevel){
        				removeItem(player, rDiamond);
        				
	        			player.getItemInHand().setDurability(getRepairAmount(is, player));
	        			
	        			durabilityAfter = player.getItemInHand().getDurability();
	        			dif = (short) (durabilityBefore - durabilityAfter);
	        			dif = (short) (dif * 6); //Boost XP
	        			PP.addXP(SkillType.REPAIR, dif*10, player);
	        			
	        			//CLANG CLANG
	        			if(LoadProperties.spoutEnabled)
	        				SpoutStuff.playRepairNoise(player);
        			} 
        			else if (isIronArmor(is) && hasItem(player, rIron)){
        			/*
        			 * IRON ARMOR
        			 */
        				removeItem(player, rIron);
        				
        				player.getItemInHand().setDurability(getRepairAmount(is, player));
	        			
	            		durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		dif = (short) (dif * 2); //Boost XP
	            		PP.addXP(SkillType.REPAIR, dif*10, player);
	            		
	            		//CLANG CLANG
	        			if(LoadProperties.spoutEnabled)
	        				SpoutStuff.playRepairNoise(player);
	            	//GOLD ARMOR
        			} else if (isGoldArmor(is) && hasItem(player, rGold)){
        				removeItem(player, rGold);
        				
        				player.getItemInHand().setDurability(getRepairAmount(is, player));
        				
        				durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		dif = (short) (dif * 4); //Boost XP of Gold to around Iron
        				PP.addXP(SkillType.REPAIR, dif*10, player);
        				
        				//CLANG CLANG
	        			if(LoadProperties.spoutEnabled)
	        				SpoutStuff.playRepairNoise(player);
        			} else {
        				needMoreVespeneGas(is, player);
        			}
        		}
        		/*
        		 * TOOLS
        		 */
        		if(isTools(is)){
        			if(isStoneTools(is) && hasItem(player, rStone)){
        				removeItem(player, rStone);
            			/*
            			 * Repair Durability and calculate dif
            			 */
        				
        				player.getItemInHand().setDurability(getRepairAmount(is, player));
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
        			} else if(isWoodTools(is) && hasItem(player,rWood)){
        				removeItem(player,rWood);
            			/*
            			 * Repair Durability and calculate dif
            			 */
        				player.getItemInHand().setDurability(getRepairAmount(is, player));
	        			
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
        			} else if(isIronTools(is) && hasItem(player, rIron)){
            			removeItem(player, rIron);
            			/*
            			 * Repair Durability and calculate dif
            			 */
            			player.getItemInHand().setDurability(getRepairAmount(is, player));
	        			
            			durabilityAfter = (short) (player.getItemInHand().getDurability()-getRepairAmount(is, player));
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
            		} else if (isDiamondTools(is) && hasItem(player, rDiamond) && PP.getSkillLevel(SkillType.REPAIR) >= LoadProperties.repairdiamondlevel){ //Check if its diamond and the player has diamonds
            			/*
            			 * DIAMOND TOOLS
            			 */
            			player.getItemInHand().setDurability(getRepairAmount(is, player));
	        			
            			removeItem(player, rDiamond);
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
            		} else if(isGoldTools(is) && hasItem(player, rGold)){
            			player.getItemInHand().setDurability(getRepairAmount(is, player));
	        			
            			removeItem(player, rGold);
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
            		} else {
            			needMoreVespeneGas(is, player);
            		}
        		}
        		
        	} else {
        		player.sendMessage(mcLocale.getString("Skills.FullDurability"));
        	}
        	//player.updateInventory();
        	/*
        	 * GIVE SKILL IF THERE IS ENOUGH XP
        	 */
        	Skills.XpCheckSkill(SkillType.REPAIR, player);
        	}
    }
	public static int getArcaneForgingRank(PlayerProfile PP)
	{
		int rank = 0;
		
		if(PP.getSkillLevel(SkillType.REPAIR) >= 750)
		{
			rank = 4;
		} else if (PP.getSkillLevel(SkillType.REPAIR) >= 500)
		{
			rank = 3;
		} else if(PP.getSkillLevel(SkillType.REPAIR) >= 250)
		{
			rank = 2;
		} else if (PP.getSkillLevel(SkillType.REPAIR) >= 100)
		{
			rank = 1;
		}
		return rank;
	}
	public static void addEnchants(ItemStack is, Enchantment[] enchants, int[] enchantsLvl, PlayerProfile PP, Player player)
	{
		if(is.getEnchantments().keySet().size() == 0)
			return;
		
		int pos = 0;
		int rank = getArcaneForgingRank(PP);
		
		if(rank == 0)
		{
			player.sendMessage(mcLocale.getString("Repair.LostEnchants"));
			for(Enchantment x : enchants)
			{
				is.removeEnchantment(x);
			}
			return;
		}
		
		boolean failure = false, downgrade = false;

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
						if(Math.random() * 100 <= getDowngradeChance(rank))
						{
							is.addEnchantment(x, enchantsLvl[pos]-1);
							downgrade = true;
						} else
						{
							is.addEnchantment(x, enchantsLvl[pos]);
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
	public static int getEnchantChance(int rank)
	{
		switch(rank)
		{
		case 4:
			return 40;
		case 3:
			return 30;
		case 2:
			return 20;
		case 1:
			return 10;
		default:
			return 0;
		}
	}
	public static int getDowngradeChance(int rank)
	{
		switch(rank)
		{
		case 4:
			return 15;
		case 3:
			return 25;
		case 2:
			return 50;
		case 1:
			return 75;
		default:
			return 100;
		}
	}
	public static boolean isArmor(ItemStack is){
    	return is.getTypeId() == 306 || is.getTypeId() == 307 ||is.getTypeId() == 308 ||is.getTypeId() == 309 ||
    			is.getTypeId() == 310 ||is.getTypeId() == 311 ||is.getTypeId() == 312 ||is.getTypeId() == 313 ||
    			is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317;
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
    			is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284 || //GOLD
    			is.getTypeId() == 268 || is.getTypeId() == 269 || is.getTypeId() == 270 || is.getTypeId() == 271 || is.getTypeId() == 290 ||//WOOD
    			is.getTypeId() == 272 || is.getTypeId() == 273 || is.getTypeId() == 274 || is.getTypeId() == 275|| is.getTypeId() == 291;  //STONE
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
    	if(is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293)
    	{
    		return true;
    	} else {
    		return false;
    	}
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
    public static short repairCalculate(Player player, short durability, short ramt){
    	PlayerProfile PP = Users.getProfile(player);
    	float bonus = (PP.getSkillLevel(SkillType.REPAIR) / 500);
    	bonus = (ramt * bonus);
    	ramt = ramt+=bonus;
    	if(checkPlayerProcRepair(player)){
    		ramt = (short) (ramt * 2);
    	}
        //player.sendMessage(ChatColor.DARK_RED + "test " +ChatColor.BLUE+ );
    	durability-=ramt;
       // player.sendMessage(ChatColor.DARK_RED + "durability " +ChatColor.BLUE+ durability);
    	if(durability < 0){
    		durability = 0;
    	}
    	return durability;
    }
    public static short getRepairAmount(ItemStack is, Player player){
    	short durability = is.getDurability();
    	short ramt = 0;
    	switch(is.getTypeId())
		{
    	/*
    	 * TOOLS
    	 */
    	//SHEARS
    	case 359:
    		ramt = 119;
    		break;
    	//WOOD SWORD
		case 268:
    		ramt = 30;
    		break;
    	//WOOD SHOVEL
		case 269:
    		ramt = 60;
    		break;
    	//WOOD PICKAXE
		case 270:
    		ramt = 20;
    		break;
    	//WOOD AXE
		case 271:
    		ramt = 20;
    		break;
    	//WOOD HOE
		case 290:
			ramt = 30;
			break;
    	//STONE SWORD
		case 272:
    		ramt = 66;
    		break;
    	//STONE SHOVEL
		case 273:
    		ramt = 132;
    		break;
    	//STONE PICKAXE
		case 274:
    		ramt = 44;
    		break;
    	//STONE AXE
		case 275:
    		ramt = 44;
    		break;
		//STONE HOE
		case 291:
			ramt = 66;
			break;
    	//GOLD SHOVEL
    	case 284:
    		ramt = 33;
    		break;
    	//IRON SHOVEL
    	case 256:
    		ramt = 251;
    		break;
    	//DIAMOND SHOVEL
    	case 277:
    		ramt = 1562;
    		break;
    	//IRON PICK
    	case 257:
    		ramt = 84;
    		break;
    	//IRON AXE
    	case 258:
    		ramt = 84;
    		break;
    	//IRON SWORD
    	case 267:
    		ramt = 126;
    		break;
    	//IRON HOE
    	case 292:
    		ramt = 126;
    		break;
    	//DIAMOND SWORD
    	case 276:
    		ramt = 781;
    		break;
    	//DIAMOND PICK
    	case 278:
    		ramt = 521;
    		break;
    	//DIAMOND AXE
    	case 279:
    		ramt = 521;
    		break;
    	//DIAMOND HOE
    	case 293:
    		ramt = 781;
    		break;
    	//GOLD SWORD
    	case 283:
    		ramt = 17;
    		break;
    	//GOLD PICK
    	case 285:
    		ramt = 11;
    		break;
    	//GOLD AXE
    	case 286:
    		ramt = 11;
    		break;
    	//GOLD HOE
    	case 294:
    		ramt = 17;
    		break;
    	/*
    	 * ARMOR
    	 */
    	case 306:
			ramt = 27;
			break;
		case 310:
			ramt = 55;
    		break;
		case 307:
			ramt = 24;
    		break;
		case 311:
			ramt = 48;
    		break;
		case 308:
			ramt = 27;
    		break;
		case 312:
			ramt = 53;
    		break;
		case 309:
			ramt = 40;
    		break;
		case 313:
			ramt = 80;
    		break;
		case 314:
    		ramt = 13;
    		break;
		case 315:
    		ramt = 12;
    		break;
		case 316:
    		ramt = 14;
    		break;
		case 317:
    		ramt = 20;
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
		} else if (is.getAmount() > 1)
			player.sendMessage(mcLocale.getString("Skills.StackedItems"));
    }
    public static boolean checkPlayerProcRepair(Player player)
    {
    	PlayerProfile PP = Users.getProfile(player);
		if(player != null)
		{
			if(Math.random() * 1000 <= PP.getSkillLevel(SkillType.REPAIR))
			{
				player.sendMessage(mcLocale.getString("Skills.FeltEasy"));
				return true;
			}
		}
		return false;
    }
}