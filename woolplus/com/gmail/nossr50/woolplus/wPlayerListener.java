package com.gmail.nossr50.woolplus;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class wPlayerListener extends PlayerListener {
    private final woolplus plugin;

    public wPlayerListener(woolplus instance) {
        plugin = instance;
    }
    public void onPlayerItem(PlayerItemEvent event) {
    	Player player = event.getPlayer();
    	ItemStack item = event.getPlayer().getItemInHand();
    	Block block = event.getBlockClicked();
    	if(block != null && item != null && isDye(item) && isWool(block)){
    		dyeWool(block, item, player);
    	}
    }
    public boolean isDye(ItemStack item){
    	int type = item.getTypeId();
    	if(type == 351 || type == 352){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isWool(Block block){
    	int type = block.getTypeId();
    	if(type == 35){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean isBoneMeal(ItemStack item){
    	int type = item.getTypeId();
    	short durability = item.getDurability();
    	if(type == 351 && durability == 15){
    		return true;
    	} else {
    		return false;
    	}
    }
    public void consumeDye(short type, Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for (ItemStack x : inventory){
    		if(x.getTypeId() == 351 && x.getDurability() == type){
    			if(x.getAmount() == 1){
    				x.setAmount(0);
    				x.setTypeId(0);
    			}
    			if(x.getAmount() > 1)
    			x.setAmount(x.getAmount() - 1);
    			player.getInventory().setContents(inventory);
    		}
    	}
    	player.updateInventory();
    }
    public boolean isLightColoredWool(byte wool){
    	if(wool == 4 || wool == 5 || wool == 6 || wool == 9 || wool == 2 || wool == 3){
    		return true;
    	} else { 
    		return false;
    	}
    }
    public void dyeWool(Block block, ItemStack item, Player player){
    	MaterialData mdye = item.getData();
    	byte dye = mdye.getData();
    	byte wool = block.getData();
    	short durability = item.getDurability();
    	/*
    	 * WOOL LIGHTENING
    	 */
    	//Black dyes everything you know!
		if(durability == 0 && wool != 15){
			block.setData((byte) 15);
			consumeDye(item.getDurability(), player);
			return;
		}
		//BLACK -> GRAY
    	if(wool == 15 && isBoneMeal(item)){
    		block.setData((byte) 7);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//GRAY -> LGRAY
    	if(wool == 7 && isBoneMeal(item)){
    		block.setData((byte) 8);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//BROWN -> GRAY
    	if(wool == 12 && isBoneMeal(item)){
    		block.setData((byte) 7);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//LGRAY -> WHITE
    	if(wool == 8 && isBoneMeal(item)){
    		block.setData((byte) 0);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//RED (14) -> PINK (6)
    	if(wool == 14 && isBoneMeal(item)){
    		block.setData((byte) 6);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//GREEN13 -> LIME5
    	if(wool == 13 && isBoneMeal(item)){
    		block.setData((byte) 5);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//BLUE11 -> CYAN9
    	if(wool == 11 && isBoneMeal(item)){
    		block.setData((byte) 9);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//CYAN9 -> LIGHT BLUE3
    	if(wool == 9 && isBoneMeal(item)){
    		block.setData((byte) 3);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//PURPLE10 -> MAGENTA2
    	if(wool == 10 && isBoneMeal(item)){
    		block.setData((byte) 2);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	/*
    	 * WOOL COMBINATIONS
    	 */
    	//Red + Yellow = Orange
    	//If wool is red, dye is yellow
    	if(wool == 14 && durability == 11){
    		block.setData((byte) 1);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//If wool is yellow, dye is red
    	if(wool == 4 && durability == 1){
    		block.setData((byte) 1);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//Lapis + Green = Cyan
    	//if wool is Lapis/Blue, dye is green
    	if(wool == 11 && durability == 2){
    		block.setData((byte) 9);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//if wool is Green, dye is lapis
    	if(wool == 13 && durability == 4){
    		block.setData((byte) 9);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//Red + Lapis = Purple
    	//if wool is Red, dye is Lapis
    	if(wool == 14 && durability == 4){
    		block.setData((byte) 10);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//if wool is Lapis/Blue, dye is red
    	if(wool == 11 && durability == 1){
    		block.setData((byte) 10);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//Purple + Pink = Magenta
    	//if wool is Purple, dye is pink
    	if(wool == 10 && durability == 9){
    		block.setData((byte) 2);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	//if wool is pink, dye is purple
    	if(wool == 6 && durability == 5){
    		block.setData((byte) 2);
    		consumeDye(item.getDurability(), player);
    		return;
    	}
    	/*
    	 * REGULAR DYE SECTION
    	 */
    	if(wool == 0){
    		//orange
    		if(durability == 14){
    			block.setData((byte) 1);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//magenta
    		if (durability == 13){
    			block.setData((byte) 2);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//light blue
    		if(durability == 12){
    			block.setData((byte) 3);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//yellow
    		if(durability == 11){
    			block.setData((byte) 4);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//lime
    		if(durability == 10){
    			block.setData((byte) 5);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//pink
    		if(durability == 9){
    			block.setData((byte) 6);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//gray
    		if(durability == 8){
    			block.setData((byte) 7);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//light gray
    		if(durability == 7){
    			block.setData((byte) 8);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//cyan
    		if(durability == 6){
    			block.setData((byte) 9);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//purple
    		if(durability == 5){
    			block.setData((byte) 10);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//lapis or blue
    		if(durability == 4){
    			block.setData((byte) 11);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//coco or brown
    		if(durability == 3){
    			block.setData((byte) 12);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//green
    		if(durability == 2){
    			block.setData((byte) 13);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    		//red
    		if(durability == 1){
    			block.setData((byte) 14);
    			consumeDye(item.getDurability(), player);
    			return;
    		}
    	}
    	/*
    	 * BROWN CONVERSION
    	 */
    	if(!isBoneMeal(item) && durability != 0 && wool != 12){
    		block.setData((byte) 12);
			consumeDye(item.getDurability(), player);
			return;
    	}
    	if(isBoneMeal(item) && wool != 0 && !isLightColoredWool(wool)){
    		block.setData((byte) 7);
			consumeDye(item.getDurability(), player);
			return;
    	}
    	if(isBoneMeal(item) && wool != 0 && isLightColoredWool(wool)){
    		block.setData((byte) 0);
			consumeDye(item.getDurability(), player);
			return;
    	}
    }
}
