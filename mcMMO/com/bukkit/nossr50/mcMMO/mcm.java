package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcm {
	private static volatile mcm instance;
	public static mcm getInstance() {
    	if (instance == null) {
    	instance = new mcm();
    	}
    	return instance;
    	}
    public boolean inSameParty(Player playera, Player playerb){
        if(mcUsers.getProfile(playera).getParty().equals(mcUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
    public boolean checkPlayerProcRepair(Player player){
			if(mcUsers.getProfile(player).getRepairInt() >= 750){
				if(Math.random() * 10 > 2){
					player.sendMessage(ChatColor.GRAY + "That took no effort.");
					return true;
				}
			} else if (mcUsers.getProfile(player).getRepairInt() >= 450 && mcUsers.getProfile(player).getRepairInt() < 750){
				if(Math.random() * 10 > 4){
					player.sendMessage(ChatColor.GRAY + "That felt really easy.");
					return true;
				}
			} else if (mcUsers.getProfile(player).getRepairInt() >= 150 && mcUsers.getProfile(player).getRepairInt() < 450){
				if(Math.random() * 10 > 6){
					player.sendMessage(ChatColor.GRAY + "That felt pretty easy.");
					return true;
				}
			} else if (mcUsers.getProfile(player).getRepairInt() >= 50  && mcUsers.getProfile(player).getRepairInt() < 150){
				if(Math.random() * 10 > 8){
					player.sendMessage(ChatColor.GRAY + "That felt easy.");
					return true;
				}
			}
			return false;
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
    public short getToolRepairAmount(ItemStack is, short durability, Player player){
    	//IRON SHOVEL
    	if(is.getTypeId() == 256){
    		return 0; //full repair
    	}
    	//DIAMOND SHOVEL
    	if(is.getTypeId() == 277){
    		return 0; //full repair
    	}
    	//IRON TOOLS
    	if(is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292){
    		if(durability < 84){
    			return 0;
    		}else {
    			if(checkPlayerProcRepair(player))
    				return 0; 
    			return (short) (durability-84);
    		}
    	//DIAMOND TOOLS
    	} else if(is.getTypeId() == 276 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293){
    		if(durability < 509){
    			return 0;
    		} else {
    			if(checkPlayerProcRepair(player))
    			return 0;
    			return (short) (durability-509);
    		}
    	} else { 
    		return durability;
    	}
    }
	public void blockProcSimulate(Block block){
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		if(block.getTypeId() != 73 && block.getTypeId() != 74 && block.getTypeId() != 56 && block.getTypeId() != 21 && block.getTypeId() != 1 && block.getTypeId() != 16)
		block.getWorld().dropItemNaturally(loc, item);
		if(block.getTypeId() == 73 || block.getTypeId() == 74){
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			block.getWorld().dropItemNaturally(loc, item);
			block.getWorld().dropItemNaturally(loc, item);
			block.getWorld().dropItemNaturally(loc, item);
			if(Math.random() * 10 > 5){
				block.getWorld().dropItemNaturally(loc, item);
			}
		}
			if(block.getTypeId() == 21){
				mat = Material.getMaterial(351);
				item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
				block.getWorld().dropItemNaturally(loc, item);
				block.getWorld().dropItemNaturally(loc, item);
				block.getWorld().dropItemNaturally(loc, item);
				block.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 56){
				mat = Material.getMaterial(264);
				item = new ItemStack(mat, 1, (byte)0, damage);
				block.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 1){
				mat = Material.getMaterial(4);
				item = new ItemStack(mat, 1, (byte)0, damage);
				block.getWorld().dropItemNaturally(loc, item);
			}
			if(block.getTypeId() == 16){
				mat = Material.getMaterial(263);
				item = new ItemStack(mat, 1, (byte)0, damage);
				block.getWorld().dropItemNaturally(loc, item);
			}
    }
    public void blockProcCheck(Block block, Player player){
    	if(mcUsers.getProfile(player).getMiningInt() > 3000){
    		blockProcSimulate(block);
			return;
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 2000){
    		if((Math.random() * 10) > 2){
    		blockProcSimulate(block);
    		return;
    		}
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 750){
    		if((Math.random() * 10) > 4){
    		blockProcSimulate(block);
			return;
    		}
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 150){
    		if((Math.random() * 10) > 6){
    		blockProcSimulate(block);
			return;
    		}
    	}
    	if(mcUsers.getProfile(player).getMiningInt() > 25){
    		if((Math.random() * 10) > 8){
    		blockProcSimulate(block);
			return;
    		}
    	}
    			
	}
    public void miningBlockCheck(Player player, Block block){
    	if(block.getTypeId() == 1){
    		mcUsers.getProfile(player).addgather(1);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//COAL
    		if(block.getTypeId() == 16){
    		mcUsers.getProfile(player).addgather(3);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//GOLD
    		if(block.getTypeId() == 14){
    		mcUsers.getProfile(player).addgather(20);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//DIAMOND
    		if(block.getTypeId() == 56){
    		mcUsers.getProfile(player).addgather(50);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//IRON
    		if(block.getTypeId() == 15){
    		mcUsers.getProfile(player).addgather(10);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//REDSTONE
    		if(block.getTypeId() == 73 || block.getTypeId() == 74){
    		mcUsers.getProfile(player).addgather(15);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    		//LAPUS
    		if(block.getTypeId() == 21){
    		mcUsers.getProfile(player).addgather(50);
    		mcm.getInstance().blockProcCheck(block, player);
    		}
    }
    public void herbalismProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(type == 39 || type == 40){
    			mcUsers.getProfile(player).skillUpHerbalism(3);
    			player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by 3. Total ("+mcUsers.getProfile(player).getHerbalismInt()+")");
    		}
    	if(type == 37 || type == 38){
    		if(Math.random() * 10 > 8){
    			mcUsers.getProfile(player).skillUpHerbalism(1);
    			player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by 1. Total ("+mcUsers.getProfile(player).getHerbalismInt()+")");
    		}
    	}
    	if(type == 59){
    		mcUsers.getProfile(player).skillUpHerbalism(1);
    		player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by 1. Total ("+mcUsers.getProfile(player).getHerbalismInt()+")");
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    		if(Math.random() * 10 > 8){
    		mat = Material.getMaterial(59);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
			loc.getWorld().dropItemNaturally(loc, is);
    		}
    		}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 350 ){
    			if(Math.random() * 10 > 6){
    	    		mat = Material.getMaterial(59);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    	    		}
    		}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 500 ){
    			if(Math.random() * 10 > 4){
    	    		mat = Material.getMaterial(59);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    	    		}
    		}
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 750 ){
    			if(Math.random() * 10 > 2){
    	    		mat = Material.getMaterial(59);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    	    		}
    		}
    	}
    }
    public void excavationProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(type == 2 && mcUsers.getProfile(player).getExcavationInt() > 250){
    		//CHANCE TO GET APPLES
    		if(Math.random() * 100 > 99){
    			mat = Material.getMaterial(260);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//DIRT SAND OR GRAVEL
    	if(type == 3 || type == 13 || type == 2){
    		if(Math.random() * 10 > 9){
    			mcUsers.getProfile(player).skillUpExcavation(1);
    			player.sendMessage(ChatColor.YELLOW+"Excavation skill increased by 1. Total ("+mcUsers.getProfile(player).getExcavationInt()+")");
    			
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 750){
    			//CHANCE TO GET CAKE
    			if(Math.random() * 2000 > 1999){
    				mat = Material.getMaterial(354);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 500){
    			//CHANCE TO GET MUSIC
    			if(Math.random() * 1000 > 999){
    				mat = Material.getMaterial(2256);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    			//CHANCE TO GET MUSIC
    			if(Math.random() * 1000 > 999){
    				mat = Material.getMaterial(2257);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    			//CHANCE TO GET DIAMOND
    			if(Math.random() * 500 > 499){
        				mat = Material.getMaterial(264);
        				is = new ItemStack(mat, 1, (byte)0, (byte)0);
        				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    	}
    	//SAND
    	if(type == 12){
    		if(Math.random() * 10 > 9){
    			mcUsers.getProfile(player).skillUpExcavation(1);
    			player.sendMessage(ChatColor.YELLOW+"Excavation skill increased by 1. Total ("+mcUsers.getProfile(player).getExcavationInt()+")");
    			
    		}
    		//CHANCE TO GET GLOWSTONE
    		if(mcUsers.getProfile(player).getExcavationInt() > 50 && Math.random() * 100 > 95){
				mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET DIAMOND
    		if(mcUsers.getProfile(player).getExcavationInt() > 500 && Math.random() * 500 > 499){
				mat = Material.getMaterial(264);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET COAL
    		if(mcUsers.getProfile(player).getExcavationInt() > 125){
    			if(Math.random() * 2000 > 1999){
    				mat = Material.getMaterial(263);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    	}
    	//GRASS OR DIRT
    	if((type == 2 || type == 3) && mcUsers.getProfile(player).getExcavationInt() > 25){
    		//CHANCE TO GET GLOWSTONE
    		if(Math.random() * 10 > 7){
    			mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRAVEL
    	if(type == 13){
    		//CHANCE TO GET SULPHUR
    		if(mcUsers.getProfile(player).getExcavationInt() > 75){
    		if(Math.random() * 10 > 7){
    			mat = Material.getMaterial(289);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 175){
        		if(Math.random() * 10 > 6){
        			mat = Material.getMaterial(352);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
        		}
        		}
    	}
    }
    public void woodCuttingProcCheck(Player player, Block block, Location loc){
    	if(mcUsers.getProfile(player).getWoodCuttingint() > 1000){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 750){
		if((Math.random() * 10) > 2){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 300){
		if((Math.random() * 10) > 4){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 100){
		if((Math.random() * 10) > 6){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingint() > 10){
		if((Math.random() * 10) > 8){
			Material mat = Material.getMaterial(block.getTypeId());
			byte damage = 0;
			ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
			block.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
    }
    public void simulateSkillUp(Player player){
    	if(mcUsers.getProfile(player).getwgatheramt() > 10){
			while(mcUsers.getProfile(player).getwgatheramt() > 10){
			mcUsers.getProfile(player).removewgather(10);
			mcUsers.getProfile(player).skillUpWoodcutting(1);
			player.sendMessage(ChatColor.YELLOW+"Wood Cutting skill increased by 1. Total ("+mcUsers.getProfile(player).getWoodCutting()+")");
			}
		}
		if(mcUsers.getProfile(player).getgatheramt() > 50){
			while(mcUsers.getProfile(player).getgatheramt() > 50){
			mcUsers.getProfile(player).removegather(50);
			mcUsers.getProfile(player).skillUpMining(1);
			player.sendMessage(ChatColor.YELLOW+"Mining skill increased by 1. Total ("+mcUsers.getProfile(player).getMining()+")");
			}
		}
    }
    // IS TOOLS FUNCTION
    public boolean isArmor(ItemStack is){
    	if(is.getTypeId() == 306 || is.getTypeId() == 307 ||is.getTypeId() == 308 ||is.getTypeId() == 309 ||
    			is.getTypeId() == 310 ||is.getTypeId() == 311 ||is.getTypeId() == 312 ||is.getTypeId() == 313){
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
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || is.getTypeId() == 292 ||//IRON
    			is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279 || is.getTypeId() == 293) //DIAMOND 
    	{
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
    		}
    	}
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
}
