package com.gmail.nossr50;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcSkills {
	private static mcMMO plugin;
	public mcSkills(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcSkills instance;
	public static mcSkills getInstance() {
    	if (instance == null) {
    		instance = new mcSkills(plugin);
    	}
    	return instance;
    }
    public boolean hasArrows(Player player){
    	for(ItemStack x : player.getInventory().getContents()){
    		if (x.getTypeId() == 262){
    			return true;
    		}
    	}
    	return false;
    }
    public void addArrows(Player player){
    	for(ItemStack x : player.getInventory().getContents()){
    		if (x.getTypeId() == 262){
    			x.setAmount(x.getAmount() + 1);
    			return;
    		}
    	}
    }
    public boolean isSkill(String skillname){
		if(skillname.equals("mining")){
			return true;
		}
		else if(skillname.equals("woodcutting")){
			return true;
		}
		else if(skillname.equals("excavation")){
			return true;
		}
		else if(skillname.equals("repair")){
			return true;
		}
		else if(skillname.equals("herbalism")){
			return true;
		}
		else if(skillname.equals("acrobatics")){
			return true;
		}
		else if(skillname.equals("swords")){
			return true;
		}
		else if(skillname.equals("archery")){
			return true;
		}
		else if(skillname.equals("unarmed")){
			 return true;
		}
		else if(skillname.equals("axes")){
			return true;
		}
		else {
			return false;
		}
}
    public void arrowRetrievalCheck(Entity entity){
    	if(mcConfig.getInstance().isTracked(entity)){
    		Integer x = 0;
    		while(x < mcConfig.getInstance().getArrowCount(entity)){
    		mcm.getInstance().mcDropItem(entity.getLocation(), 262);
    		x++;
    		}
    	}
    }
}
