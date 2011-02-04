package com.bukkit.nossr50.mcMMO;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

public class mcBlockListener extends BlockListener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    //put all Block related code here
    public void onBlockDamage(BlockDamageEvent event) {
    	//STARTED(0), DIGGING(1), BROKEN(3), STOPPED(2);
    	Player player = event.getPlayer();
    	Block block = event.getBlock();
    	int dmg = event.getDamageLevel().getLevel();
    	if(dmg == 3){
    		mcUsers.getProfile(player).addgather(1);
    		//GOLD ORE = 14 
    		//DIAMOND ORE = 56 
    		//REDSTONE = 73 && 74
    		
    		if(block.getTypeId() == 1){
    			int t = player.getItemInHand().getTypeId();
    			int q;
    			//If stone tools
    			if(t == 272 || t == 273 || t == 274 || t == 275){
    				q = 3;
    			//If iron tools	
    			} else if (t == 256 || t == 257 || t == 258 || t == 267){
    				q = 2;
    			//If wooden tools	
    			} else if (t == 268 || t == 269 || t == 270 || t == 271){
    				q = 4;
    			//If Diamond tools	
    			} else if (t == 276 || t == 277 || t == 278 || t == 279){
    				q = 1;
    			} else {
    				q = 5;
    			}
    			
    		}
    	}
    }
}