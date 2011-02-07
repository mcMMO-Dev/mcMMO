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
	
	public boolean isPlayer(String playerName){
    	for(Player herp :  mcPlayerListener.getInstance().getPlayersOnline()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return true;
    		}
    	}
    		return false;
    }
    public int partyCount(Player player){
    	Player players[] =  mcPlayerListener.getInstance().getPlayersOnline();
        int x = 0;
        for(Player hurrdurr: players){
        	if(mcUsers.getProfile(player).getParty().equals(mcUsers.getProfile(hurrdurr).getParty()))
        	x++;
        }
        return x;
    }
    public static void informPartyMembers(Player player){
        int x = 0;
        for(Player p :  mcPlayerListener.getInstance().getPlayersOnline()){
                if(inSameParty(player, p) && !p.getName().equals(player.getName())){
                p.sendMessage(player.getName() + ChatColor.GREEN + " has joined your party");
                x++;
                }
            }
    }
    public static void informPartyMembersQuit(Player player){
        int x = 0;
        for(Player p :  mcPlayerListener.getInstance().getPlayersOnline()){
                if(inSameParty(player, p) && !p.getName().equals(player.getName())){
                p.sendMessage(player.getName() + ChatColor.GREEN + " has left your party");
                x++;
                }
            }
    }
    public static boolean inSameParty(Player playera, Player playerb){
        if(mcUsers.getProfile(playera).getParty().equals(mcUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
    
    public Player getPlayer(String playerName){
    	for(Player herp :  mcPlayerListener.getInstance().getPlayersOnline()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return herp;
    		}
    	}
    	return null;
    }
    
    //This determines how much we repair
    public short getRepairAmount(ItemStack is, short durability, Player player){
    	//IRON SHOVEL
    	if(is.getTypeId() == 256){
    		return 0; //full repair
    	}
    	//DIAMOND SHOVEL
    	if(is.getTypeId() == 277){
    		return 0; //full repair
    	}
    	//IRON TOOLS
    	if(is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267){
    		if(durability < 84){
    			return 0;
    		}else {
    			if(mcUsers.getProfile(player).getRepairInt() > 750){
    				if(Math.random() * 10 > 2){
    					player.sendMessage(ChatColor.GRAY + "That took no effort.");
    					return 0;
    				}
    			} else if (mcUsers.getProfile(player).getRepairInt() > 450){
    				if(Math.random() * 10 > 4){
    					player.sendMessage(ChatColor.GRAY + "That felt really easy.");
    					return 0;
    				}
    			} else if (mcUsers.getProfile(player).getRepairInt() > 150){
    				if(Math.random() * 10 > 6){
    					player.sendMessage(ChatColor.GRAY + "That felt pretty easy.");
    					return 0;
    				}
    			} else if (mcUsers.getProfile(player).getRepairInt() > 50){
    				if(Math.random() * 10 > 8){
    					player.sendMessage(ChatColor.GRAY + "That felt easy.");
    					return 0;
    				}
    			} 
    			return (short) (durability-84);
    		}
    	//DIAMOND TOOLS
    	} else if(is.getTypeId() == 276 || is.getTypeId() == 278 || is.getTypeId() == 279){
    		if(durability < 509){
    			return 0;
    		} else {
    			if(mcUsers.getProfile(player).getRepairInt() > 750){
    				if(Math.random() * 10 > 2){
    					player.sendMessage(ChatColor.GRAY + "That took no effort.");
    					return 0;
    				}
    			} else if (mcUsers.getProfile(player).getRepairInt() > 450){
    				if(Math.random() * 10 > 4){
    					player.sendMessage(ChatColor.GRAY + "That was simple.");
    					return 0;
    				}
    			} else if (mcUsers.getProfile(player).getRepairInt() > 150){
    				if(Math.random() * 10 > 6){
    					player.sendMessage(ChatColor.GRAY + "That felt pretty easy.");
    					return 0;
    				}
    			} else if (mcUsers.getProfile(player).getRepairInt() > 50){
    				if(Math.random() * 10 > 8){
    					player.sendMessage(ChatColor.GRAY + "That felt easy.");
    					return 0;
    				}
    			}
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
    public boolean isTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267 || //IRON
    			is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279) //DIAMOND 
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isIronTools(ItemStack is){
    	if(is.getTypeId() == 256 || is.getTypeId() == 257 || is.getTypeId() == 258 || is.getTypeId() == 267)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isDiamondTools(ItemStack is){
    	if(is.getTypeId() == 276 || is.getTypeId() == 277 || is.getTypeId() == 278 || is.getTypeId() == 279)
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
