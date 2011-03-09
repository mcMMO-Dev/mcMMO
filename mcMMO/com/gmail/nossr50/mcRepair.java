package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcRepair {
	private static mcMMO plugin;
	public mcRepair(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcRepair instance;
	public static mcRepair getInstance() {
    	if (instance == null) {
    		instance = new mcRepair(plugin);
    	}
    	return instance;
    }
	public void repairCheck(Player player, ItemStack is, Block block){
    	if(block != null
    			&& mcPermissions.getInstance().repair(player)){
        	if(player.getItemInHand().getDurability() > 0){
        		/*
        		 * ARMOR
        		 */
        		if(isArmor(is)){
        			/*
        			 * DIAMOND ARMOR
        			 */
        			if(isDiamondArmor(is) && hasDiamond(player) && mcUsers.getProfile(player).getRepairInt() >= mcLoadProperties.repairdiamondlevel){
	        			removeDiamond(player);
	        			player.getItemInHand().setDurability(getArmorRepairAmount(is, player));
	        			mcUsers.getProfile(player).addRepairGather(75);
        			} else if (isIronArmor(is) && hasIron(player)){
        			/*
        			 * IRON ARMOR
        			 */
	        			removeIron(player);
	            		player.getItemInHand().setDurability(getArmorRepairAmount(is, player));
	            		mcUsers.getProfile(player).addRepairGather(20);
	            	//GOLD ARMOR
        			} else if (isGoldArmor(is) && hasGold(player)){
        				removeGold(player);
        				player.getItemInHand().setDurability(getArmorRepairAmount(is, player));
        				mcUsers.getProfile(player).addRepairGather(50);
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
            			is.setDurability(getToolRepairAmount(is, player));
            			removeIron(player);
            			mcUsers.getProfile(player).addRepairGather(20);
            		} else if (isDiamondTools(is) && hasDiamond(player) && mcUsers.getProfile(player).getRepairInt() >= mcLoadProperties.repairdiamondlevel){ //Check if its diamond and the player has diamonds
            			/*
            			 * DIAMOND TOOLS
            			 */
            			is.setDurability(getToolRepairAmount(is, player));
            			removeDiamond(player);
            			mcUsers.getProfile(player).addRepairGather(75);
            		} else if(isGoldTools(is) && hasGold(player)){
            			is.setDurability(getToolRepairAmount(is, player));
            			removeGold(player);
            			mcUsers.getProfile(player).addRepairGather(50);
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
        	if(mcUsers.getProfile(player).getRepairGatherInt() >= mcUsers.getProfile(player).getXpToLevel("repair")){
    			int skillups = 0;
    			while(mcUsers.getProfile(player).getRepairGatherInt() >= mcUsers.getProfile(player).getXpToLevel("repair")){
    				skillups++;
    				mcUsers.getProfile(player).removeRepairGather(mcUsers.getProfile(player).getXpToLevel("repair"));
    				mcUsers.getProfile(player).skillUpRepair(1);
    			}
    			player.sendMessage(ChatColor.YELLOW+"Repair skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getRepair()+")");	
    		}
        	}
    }
	public boolean isArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 ||is.getTypeId() == 308 ||is.getTypeId() == 309 ||
    			is.getTypeId() == 310 ||is.getTypeId() == 311 ||is.getTypeId() == 312 ||is.getTypeId() == 313 ||
    			is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317){
    		return true;
    	} else {
    		return false;
    	}
    }
	public boolean isGoldArmor(ItemStack is){
		if(is.getTypeId() == 314 || is.getTypeId() == 315 || is.getTypeId() == 316 || is.getTypeId() == 317){
			return true;
		} else {
			return false;
		}
	}
    public boolean isIronArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 || is.getTypeId() == 308 || is.getTypeId() == 309)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isDiamondArmor(ItemStack is){
    	if(is.getTypeId() == 310 || is.getTypeId() == 311 || is.getTypeId() == 312 || is.getTypeId() == 313)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292 || //IRON
    			is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293 || //DIAMOND
    			is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284) //GOLD
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isGoldTools(ItemStack is){
    	if(is.getTypeId() == 283 || is.getTypeId() == 285 || is.getTypeId() == 286 || is.getTypeId() == 284 || is.getTypeId() == 294){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isIronTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isDiamondTools(ItemStack is){
    	if(is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    public void removeIron(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 265){
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
    public void removeGold(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 266){
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
    public void removeDiamond(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 264){
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
    public boolean hasGold(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 266){
    			return true;
    		}
    	}
    	return false;
    }
    public boolean hasDiamond(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 264){
    			return true;
    		}
    	}
    	return false;
    }
    public boolean hasIron(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x.getTypeId() == 265){
    			return true;
    		}
    	}
    	return false;
    }
    public short getToolRepairAmount(ItemStack is, Player player){
    	short durability = is.getDurability();
    	switch(is.getTypeId())
		{
    	case 284:
    		durability = 0;
    		break;
    	case 256:
    		durability = 0;
    		break;
    	case 277:
    		durability = 0;
    		break;
    	case 257:
    		durability -= 84;
    		break;
    	case 258:
    		durability -= 84;
    		break;
    	case 267:
    		durability -= 84;
    		break;
    	case 292:
    		durability -= 84;
    		break;
    	case 276:
    		durability -= 509;
    		break;
    	case 278:
    		durability -= 509;
    		break;
    	case 279:
    		durability -= 509;
    		break;
    	case 293:
    		durability -= 509;
    		break;
    	case 283:
    		durability -= 13;
    		break;
    	case 285:
    		durability -= 13;
    		break;
    	case 286:
    		durability -= 13;
    		break;
    	case 294:
    		durability -= 13;
    		break;
		}
    	if(durability < 0)
			durability = 0;
		if(checkPlayerProcRepair(player))
	    	durability = 0;
		return durability;
    }
    //This determines how much we repair
    public short getArmorRepairAmount(ItemStack is, Player player){
    		short durability = is.getDurability();
    		switch(is.getTypeId())
    		{
    		case 306:
    		durability -= 27;
    		break;
    		case 310:
	    	durability -= 55;
	    	break;
    		case 307:
	    	durability -= 24;
	    	break;
    		case 311:
	    	durability -= 48;
	    	break;
    		case 308:
	    	durability -= 27;
	    	break;
    		case 312:
	    	durability -= 53;
	    	break;
    		case 309:
	    	durability -= 40;
	    	break;
    		case 313:
	    	durability -= 80;
	    	break;
    		}
			if(durability < 0)
			durability = 0;
			if(checkPlayerProcRepair(player))
	    	durability = 0;
			return durability;
    }
    public void needMoreVespeneGas(ItemStack is, Player player){
    	if ((isDiamondTools(is) || isDiamondArmor(is)) && mcUsers.getProfile(player).getRepairInt() < mcLoadProperties.repairdiamondlevel){
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
		} else if (isGoldArmor(is) && !hasGold(player))
			player.sendMessage(ChatColor.DARK_RED+"You need more "+ChatColor.GOLD+"Gold");
		}
    public boolean checkPlayerProcRepair(Player player){
		if(player != null){
			if(Math.random() * 1000 <= mcUsers.getProfile(player).getRepairInt()){
				player.sendMessage(ChatColor.GRAY + "That felt easy.");
				return true;
			}
		}
		return false;
    }
}
