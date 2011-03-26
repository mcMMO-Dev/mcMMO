package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcMining {
	private static mcMMO plugin;
	public mcMining(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcMining instance;
	public static mcMining getInstance() {
    	if (instance == null) {
    	instance = new mcMining(plugin);
    	}
    	return instance;
    	}
	public void superBreakerCheck(Player player, Block block){
	    if(mcm.getInstance().isMiningPick(player.getItemInHand())){
	    	if(block != null){
		    	if(!mcm.getInstance().abilityBlockCheck(block))
		    		return;
	    	}
	    	if(mcUsers.getProfile(player).getPickaxePreparationMode()){
    			mcUsers.getProfile(player).setPickaxePreparationMode(false);
    			mcUsers.getProfile(player).setPickaxePreparationTicks(0);
    		}
	    	int miningticks = 2;
    		if(mcUsers.getProfile(player).getMiningInt() >= 50)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 150)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 250)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 350)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 450)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 550)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 650)
    			miningticks++;
    		if(mcUsers.getProfile(player).getMiningInt() >= 750)
    			miningticks++;
    		
	    	if(!mcUsers.getProfile(player).getSuperBreakerMode() && mcUsers.getProfile(player).getSuperBreakerCooldown() == 0){
	    		player.sendMessage(ChatColor.GREEN+"**SUPER BREAKER ACTIVATED**");
	    		mcUsers.getProfile(player).setSuperBreakerTicks(miningticks);
	    		mcUsers.getProfile(player).setSuperBreakerMode(true);
	    	}
	    	
	    }
	}
	public void blockProcSimulate(Block block){
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		if(block.getTypeId() != 89 && block.getTypeId() != 73 && block.getTypeId() != 74 && block.getTypeId() != 56 && block.getTypeId() != 21 && block.getTypeId() != 1 && block.getTypeId() != 16)
			loc.getWorld().dropItemNaturally(loc, item);
		if(block.getTypeId() == 89){
			mat = Material.getMaterial(348);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
		}
		if(block.getTypeId() == 73 || block.getTypeId() == 74){
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			if(Math.random() * 10 > 5){
				loc.getWorld().dropItemNaturally(loc, item);
			}
		}
		if(block.getTypeId() == 21){
			mat = Material.getMaterial(351);
			item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
		}
		if(block.getTypeId() == 56){
			mat = Material.getMaterial(264);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
		}
		if(block.getTypeId() == 1){
			mat = Material.getMaterial(4);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
		}
		if(block.getTypeId() == 16){
			mat = Material.getMaterial(263);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
		}
    }
    public void blockProcCheck(Block block, Player player){
    	if(player != null){
    		if(Math.random() * 1000 <= mcUsers.getProfile(player).getMiningInt()){
    		blockProcSimulate(block);
			return;
    		}
    	}		
	}
    public void miningBlockCheck(Player player, Block block){
    	int xp = 0;
    	if(block.getTypeId() == 1 || block.getTypeId() == 24){
    		xp += 3;
    		blockProcCheck(block, player);
    	}
    	//NETHERRACK
    	if(block.getTypeId() == 87){
    		xp += 3;
    		blockProcCheck(block, player);
    	}
    	//GLOWSTONE
    	if(block.getTypeId() == 89){
    		xp += 3;
    		blockProcCheck(block, player);
    	}
    	//COAL
    	if(block.getTypeId() == 16){
    		xp += 10;
    		blockProcCheck(block, player);
    	}
    	//GOLD
    	if(block.getTypeId() == 14){
    		xp += 35;
    		blockProcCheck(block, player);
    	}
    	//DIAMOND
    	if(block.getTypeId() == 56){
    		xp += 75;
    		blockProcCheck(block, player);
    	}
    	//IRON
    	if(block.getTypeId() == 15){
    		xp += 25;
    		blockProcCheck(block, player);
    	}
    	//REDSTONE
    	if(block.getTypeId() == 73 || block.getTypeId() == 74){
    		xp += 15;
    		blockProcCheck(block, player);
    	}
    	//LAPUS
    	if(block.getTypeId() == 21){
    		xp += 40;
    		blockProcCheck(block, player);
    	}
    	mcUsers.getProfile(player).addMiningGather(xp * mcLoadProperties.xpGainMultiplier);
    	mcSkills.getInstance().XpCheck(player);
    }
    /*
     * Handling SuperBreaker stuff
     */
    public Boolean canBeSuperBroken(Block block){
    	int t = block.getTypeId();
    	if(t == 87 || t == 89 || t == 73 || t == 74 || t == 56 || t == 21 || t == 1 || t == 16 || t == 14 || t == 15){
    		return true;
    	} else {
    		return false;
    	}
    }
    public void SuperBreakerBlockCheck(Player player, Block block){
    	if(mcLoadProperties.toolsLoseDurabilityFromAbilities)
    		mcm.getInstance().damageTool(player, (short) mcLoadProperties.abilityDurabilityLoss);
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
    	int xp = 0;
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
    	if(block.getTypeId() == 1 || block.getTypeId() == 24){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 3;
    			blockProcCheck(block, player);
    			blockProcCheck(block, player);
    		}
    		if(block.getTypeId() == 1){
    			mat = Material.COBBLESTONE;
    		} else {
    			mat = Material.SANDSTONE;
    		}
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//NETHERRACK
    	if(block.getTypeId() == 87){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 3;
    			blockProcCheck(block, player);
    			blockProcCheck(block, player);
    		}
    		mat = Material.getMaterial(87);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//GLOWSTONE
    	if(block.getTypeId() == 89){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 3;
    			blockProcCheck(block, player);
    			blockProcCheck(block, player);
    		}
    		mat = Material.getMaterial(348);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//COAL
    	if(block.getTypeId() == 16){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 10;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        		}
    		mat = Material.getMaterial(263);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//GOLD
    	if(block.getTypeId() == 14 && mcm.getInstance().getTier(player) >= 3){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 35;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        		}
    		item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//DIAMOND
    	if(block.getTypeId() == 56 && mcm.getInstance().getTier(player) >= 3){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 75;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(264);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//IRON
    	if(block.getTypeId() == 15 && mcm.getInstance().getTier(player) >= 2){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 25;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//REDSTONE
    	if((block.getTypeId() == 73 || block.getTypeId() == 74) && mcm.getInstance().getTier(player) >= 4){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 15;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			if(Math.random() * 10 > 5){
				loc.getWorld().dropItemNaturally(loc, item);
			}
    		block.setType(Material.AIR);
    	}
    	//LAPUS
    	if(block.getTypeId() == 21 && mcm.getInstance().getTier(player) >= 3){
    		if(!mcConfig.getInstance().isBlockWatched(block)){
    			xp += 40;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(351);
			item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	mcUsers.getProfile(player).addMiningGather(xp * mcLoadProperties.xpGainMultiplier);
    	mcSkills.getInstance().XpCheck(player);
    }
}
