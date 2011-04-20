package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;


public class Repair {
	private static mcMMO plugin;
	public Repair(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile Repair instance;
	
	public static void repairCheck(Player player, ItemStack is, Block block){
		PlayerProfile PP = Users.getProfile(player);
		short durabilityBefore = player.getItemInHand().getDurability();
		short durabilityAfter = 0;
		short dif = 0;
    	if(block != null
    			&& mcPermissions.repair(player)){
        	if(player.getItemInHand().getDurability() > 0 && player.getItemInHand().getAmount() < 2){
        		/*
        		 * ARMOR
        		 */
        		if(isArmor(is)){
        			/*
        			 * DIAMOND ARMOR
        			 */
        			if(isDiamondArmor(is) && hasDiamond(player) && PP.getRepairInt() >= LoadProperties.repairdiamondlevel){
	        			removeDiamond(player);
	        			player.getItemInHand().setDurability(getArmorRepairAmount(is, player));
	        			durabilityAfter = player.getItemInHand().getDurability();
	        			player.sendMessage(String.valueOf(durabilityBefore - durabilityAfter));
	        			dif = (short) (durabilityBefore - durabilityAfter);
	        			dif = (short) (dif * 6); //Boost XP
	        			PP.addRepairXP(dif * LoadProperties.xpGainMultiplier);
        			} else if (isIronArmor(is) && hasIron(player)){
        			/*
        			 * IRON ARMOR
        			 */
	        			removeIron(player);
	            		player.getItemInHand().setDurability(getArmorRepairAmount(is, player));
	            		durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		dif = (short) (dif * 2); //Boost XP
	            		PP.addRepairXP(dif * LoadProperties.xpGainMultiplier);
	            	//GOLD ARMOR
        			} else if (isGoldArmor(is) && hasGold(player)){
        				removeGold(player);
        				player.getItemInHand().setDurability(getArmorRepairAmount(is, player));
        				durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		dif = (short) (dif * 4); //Boost XP of Gold to around Iron
        				PP.addRepairXP(dif * LoadProperties.xpGainMultiplier);
        			} else {
        				needMoreVespeneGas(is, player);
        			}
        		}
        		/*
        		 * TOOLS
        		 */
        		if(isTools(is)){
        			/*
        			 * IRON TOOLS
        			 */
            		if(isIronTools(is) && hasIron(player)){
            			removeIron(player);
            			/*
            			 * Repair Durability and calculate dif
            			 */
            			player.getItemInHand().setDurability(getToolRepairAmount(is, player));
            			durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		if(m.isShovel(is))
	        				dif = (short) (dif / 3);
	        			if(m.isSwords(is))
	        				dif = (short) (dif / 2);
	        			if(m.isHoe(is))
	        				dif = (short) (dif / 2);
            			PP.addRepairXP(dif * LoadProperties.xpGainMultiplier);
            		} else if (isDiamondTools(is) && hasDiamond(player) && PP.getRepairInt() >= LoadProperties.repairdiamondlevel){ //Check if its diamond and the player has diamonds
            			/*
            			 * DIAMOND TOOLS
            			 */
            			player.getItemInHand().setDurability(getToolRepairAmount(is, player));
            			removeDiamond(player);
            			durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		if(m.isShovel(is))
	        				dif = (short) (dif / 3);
	        			if(m.isSwords(is))
	        				dif = (short) (dif / 2);
	        			if(m.isHoe(is))
	        				dif = (short) (dif / 2);
            			PP.addRepairXP(dif * LoadProperties.xpGainMultiplier);
            		} else if(isGoldTools(is) && hasGold(player)){
            			player.getItemInHand().setDurability(getToolRepairAmount(is, player));
            			removeGold(player);
            			durabilityAfter = player.getItemInHand().getDurability();
	            		dif = (short) (durabilityBefore - durabilityAfter);
	            		dif = (short) (dif * 7.6); //Boost XP for Gold to that of around Iron
	            		if(m.isShovel(is))
	        				dif = (short) (dif / 3);
	        			if(m.isSwords(is))
	        				dif = (short) (dif / 2);
	        			if(m.isHoe(is))
	        				dif = (short) (dif / 2);
            			PP.addRepairXP(dif * LoadProperties.xpGainMultiplier);
            		} else {
            			needMoreVespeneGas(is, player);
            		}
        		}
        		
        	} else {
        		player.sendMessage("That is at full durability.");
        	}
        	player.updateInventory();
        	/*
        	 * GIVE SKILL IF THERE IS ENOUGH XP
        	 */
        	Skills.XpCheck(player);
        	}
    }
	public static boolean isArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 ||is.getTypeId() == 308 ||is.getTypeId() == 309 ||
    			is.getTypeId() == 310 ||is.getTypeId() == 311 ||is.getTypeId() == 312 ||is.getTypeId() == 313 ||
    			is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317){
    		return true;
    	} else {
    		return false;
    	}
    }
	public static boolean isGoldArmor(ItemStack is){
		if(is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317){
			return true;
		} else {
			return false;
		}
	}
    public static boolean isIronArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 || is.getTypeId() == 308 || is.getTypeId() == 309)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isDiamondArmor(ItemStack is){
    	if(is.getTypeId() == 310 || is.getTypeId() == 311 || is.getTypeId() == 312 || is.getTypeId() == 313)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292 || //IRON
    			is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293 || //DIAMOND
    			is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284) //GOLD
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isGoldTools(ItemStack is){
    	if(is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284 || is.getTypeId() == 294){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static boolean isIronTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public static boolean isDiamondTools(ItemStack is){
    	if(is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public static void removeIron(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 265){
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
    public static void removeGold(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 266){
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
    public static void removeDiamond(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 264){
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
    public static boolean hasGold(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 266){
    			return true;
    		}
    	}
    	return false;
    }
    public static boolean hasDiamond(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 264){
    			return true;
    		}
    	}
    	return false;
    }
    public static boolean hasIron(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 265){
    			return true;
    		}
    	}
    	return false;
    }
    public static short repairCalculate(Player player, short durability, short ramt){
    	PlayerProfile PP = Users.getProfile(player);
    	float bonus = (PP.getRepairInt() / 500);
    	bonus = (ramt * bonus);
    	ramt = ramt+=bonus;
    	if(checkPlayerProcRepair(player)){
    		ramt = (short) (ramt * 2);
    	}
    	durability-=ramt;
    	if(durability < 0){
    		durability = 0;
    	}
    	return durability;
    }
    public static short getToolRepairAmount(ItemStack is, Player player){
    	short durability = is.getDurability();
    	short ramt = 0;
    	switch(is.getTypeId())
		{
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
		}
		return repairCalculate(player, durability, ramt);
    }
    //This determines how much we repair
    public static short getArmorRepairAmount(ItemStack is, Player player){
    		short durability = is.getDurability();
    		short ramt = 0;
    		switch(is.getTypeId())
    		{
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
			if(durability < 0)
				durability = 0;
			return repairCalculate(player, durability, ramt);
    }
    public static void needMoreVespeneGas(ItemStack is, Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	if ((isDiamondTools(is) || isDiamondArmor(is)) && PP.getRepairInt() < LoadProperties.repairdiamondlevel){
			player.sendMessage(ChatColor.DARK_RED +"You're not adept enough to repair Diamond");
		} else if (isDiamondTools(is) && !hasDiamond(player) || isIronTools(is) && !hasIron(player) || isGoldTools(is) && !hasGold(player)){
			if(isDiamondTools(is) && !hasDiamond(player))
				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
			if(isIronTools(is) && !hasIron(player))
				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
			//herp
			if(isGoldTools(is) && !hasGold(player))
				player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GOLD+"Gold");
		} else if (isDiamondArmor(is) && !hasDiamond(player)){
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.BLUE+ "Diamonds");
		} else if (isIronArmor(is) && !hasIron(player)){
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GRAY+ "Iron");
		} else if (isGoldArmor(is) && !hasGold(player)){
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GOLD+"Gold");
		} else if (is.getAmount() > 1)
			player.sendMessage(ChatColor.DARK_RED+"You can't repair stacked items");
    	}
    public static boolean checkPlayerProcRepair(Player player){
    	PlayerProfile PP = Users.getProfile(player);
		if(player != null){
			if(Math.random() * 1000 <= PP.getRepairInt()){
				player.sendMessage(ChatColor.GRAY + "That felt easy.");
				return true;
			}
		}
		return false;
    }
}
