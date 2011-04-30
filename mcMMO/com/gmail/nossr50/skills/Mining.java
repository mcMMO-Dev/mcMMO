package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;


public class Mining {
	private static mcMMO plugin;
	public Mining(mcMMO instance) {
    	plugin = instance;
    }
	
	public static void superBreakerCheck(Player player, Block block, Plugin pluginx){
		PlayerProfile PP = Users.getProfile(player);
	    if(m.isMiningPick(player.getItemInHand())){
	    	if(block != null){
		    	if(!m.abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getPickaxePreparationMode()){
    			PP.setPickaxePreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getMiningInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getSuperBreakerMode() && Skills.cooldownOver(player, PP.getSuperBreakerDeactivatedTimeStamp(), LoadProperties.superBreakerCooldown)){
	    		player.sendMessage(ChatColor.GREEN+"**SUPER BREAKER ACTIVATED**");
	    		for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Super Breaker!");
	    		}
	    		PP.setSuperBreakerTicks(ticks * 1000);
	    		PP.setSuperBreakerActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setSuperBreakerDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
	    		PP.setSuperBreakerMode(true);
	    	}
	    	
	    }
	}
	public static void blockProcSimulate(Block block){
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
    public static void blockProcCheck(Block block, Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	if(player != null){
    		if(Math.random() * 1000 <= PP.getMiningInt()){
    		blockProcSimulate(block);
			return;
    		}
    	}		
	}
    public static void miningBlockCheck(Player player, Block block){
    	PlayerProfile PP = Users.getProfile(player);
    	if(Config.getInstance().isBlockWatched(block) || block.getData() == (byte) 5)
    		return;
    	int xp = 0;
    	if(block.getTypeId() == 1 || block.getTypeId() == 24){
    		xp += 3;
    		blockProcCheck(block, player);
    	}
    	//OBSIDIAN
    	if(block.getTypeId() == 49){
    		xp += 15;
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
    	PP.addMiningXP(xp * LoadProperties.xpGainMultiplier);
    	Skills.XpCheck(player);
    }
    /*
     * Handling SuperBreaker stuff
     */
    public static Boolean canBeSuperBroken(Block block){
    	int t = block.getTypeId();
    	if(t == 49 || t == 87 || t == 89 || t == 73 || t == 74 || t == 56 || t == 21 || t == 1 || t == 16 || t == 14 || t == 15){
    		return true;
    	} else {
    		return false;
    	}
    }
    public static void SuperBreakerBlockCheck(Player player, Block block){
    	PlayerProfile PP = Users.getProfile(player);
    	if(LoadProperties.toolsLoseDurabilityFromAbilities)
    		m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    	Location loc = block.getLocation();
    	Material mat = Material.getMaterial(block.getTypeId());
    	int xp = 0;
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
    	if(block.getTypeId() == 1 || block.getTypeId() == 24){
    		if(!Config.getInstance().isBlockWatched(block) && block.getData() != (byte) 5){
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
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
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
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
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
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
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
    	if(block.getTypeId() == 14 && m.getTier(player) >= 3){
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
    			xp += 35;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        		}
    		item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//OBSIDIAN
    	if(block.getTypeId() == 49 && m.getTier(player) >= 4){
    		if(LoadProperties.toolsLoseDurabilityFromAbilities)
        		m.damageTool(player, (short) 104);
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
    			xp += 15;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		mat = Material.getMaterial(49);
			item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//DIAMOND
    	if(block.getTypeId() == 56 && m.getTier(player) >= 3){
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
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
    	if(block.getTypeId() == 15 && m.getTier(player) >= 2){
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
    			xp += 25;
        		blockProcCheck(block, player);
        		blockProcCheck(block, player);
        	}
    		item = new ItemStack(mat, 1, (byte)0, damage);
			loc.getWorld().dropItemNaturally(loc, item);
    		block.setType(Material.AIR);
    	}
    	//REDSTONE
    	if((block.getTypeId() == 73 || block.getTypeId() == 74) && m.getTier(player) >= 4){
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
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
    	if(block.getTypeId() == 21 && m.getTier(player) >= 3){
    		if(!Config.getInstance().isBlockWatched(block)&& block.getData() != (byte) 5){
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
    	if(block.getData() != (byte) 5)
    		PP.addMiningXP(xp * LoadProperties.xpGainMultiplier);
    	Skills.XpCheck(player);
    }
}
