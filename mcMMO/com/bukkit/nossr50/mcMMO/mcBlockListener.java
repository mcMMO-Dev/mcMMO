package com.bukkit.nossr50.mcMMO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class mcBlockListener extends BlockListener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public void onBlockPlace(BlockPlaceEvent event) {
    	Block block = event.getBlock();
    	mcConfig.getInstance().addBlockWatch(block);
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
			//Since redstone gives 4-5, lets simulate that
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
    //put all Block related code here
    public void onBlockDamage(BlockDamageEvent event) {
    		//STARTED(0), DIGGING(1), BROKEN(3), STOPPED(2);
    		Player player = event.getPlayer();
    		Block block = event.getBlock();
    		Location loc = block.getLocation();
    		int dmg = event.getDamageLevel().getLevel();
    		//Smooth Stone
    		if(dmg == 3 && !mcConfig.getInstance().isBlockWatched(block)){
    		if(block.getTypeId() == 1){
    		mcUsers.getProfile(player).addgather(1);
    		blockProcCheck(block, player);
    		}
    		//COAL
    		if(block.getTypeId() == 16){
    		mcUsers.getProfile(player).addgather(3);
    		blockProcCheck(block, player);
    		}
    		//GOLD
    		if(block.getTypeId() == 14){
    		mcUsers.getProfile(player).addgather(20);
    		blockProcCheck(block, player);
    		}
    		//DIAMOND
    		if(block.getTypeId() == 56){
    		mcUsers.getProfile(player).addgather(50);
    		blockProcCheck(block, player);
    		}
    		//IRON
    		if(block.getTypeId() == 15){
    		mcUsers.getProfile(player).addgather(10);
    		blockProcCheck(block, player);
    		}
    		//REDSTONE
    		if(block.getTypeId() == 73 || block.getTypeId() == 74){
    		mcUsers.getProfile(player).addgather(15);
    		blockProcCheck(block, player);
    		}
    		//LAPUS
    		if(block.getTypeId() == 21){
    		mcUsers.getProfile(player).addgather(50);
    		blockProcCheck(block, player);
    		}
    		//Give skill for woodcutting
    		if(block.getTypeId() == 17)
    		mcUsers.getProfile(player).addwgather(1);
    		
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
    		/*
    		 * WOODCUTTING
    		 */
    		if(block.getTypeId() == 17){
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
    		}
    		
    		//GOLD ORE = 14 
    		//DIAMOND ORE = 56 
    		//REDSTONE = 73 && 74
    		
    	}
    
    
    public void onBlockFlow(BlockFromToEvent event) {
    	//Code borrowed from WorldGuard by sk89q
        World world = event.getBlock().getWorld();
        int radius = 1;
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        
        boolean isWater = blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9;
        boolean isLava = blockFrom.getTypeId() == 10 || blockFrom.getTypeId() == 11;

            int ox = blockTo.getX();
            int oy = blockTo.getY();
            int oz = blockTo.getZ();

            if(blockTo.getTypeId() == 9 || blockTo.getTypeId() == 8){
            	return;
            }

            for (int cx = -radius; cx <= radius; cx++) {
                for (int cy = -radius; cy <= radius; cy++) {
                    for (int cz = -radius; cz <= radius; cz++) {
                        Block dirt = world.getBlockAt(ox + cx, oy + cy, oz + cz);
                        //If block is dirt
                        if (isWater == true &&
                        		dirt.getTypeId() == 13) {
                        	//Change
                        	dirt.setTypeId(82);
                            return;
                        }
                    }
                }
            }
    }
}