package com.gmail.nossr50;

import org.bukkit.ChatColor;
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
    public void decreaseCooldowns(Player player){
    	mcUsers.getProfile(player).decreaseTreeFellerCooldown();
		if(mcUsers.getProfile(player).getTreeFellerCooldown() == 0){
			player.sendMessage(ChatColor.GREEN+"Your Tree Felling ability is refreshed!");
		}
		mcUsers.getProfile(player).decreaseSuperBreakerCooldown();
		if(mcUsers.getProfile(player).getSuperBreakerCooldown() == 0){
			player.sendMessage(ChatColor.GREEN+"Your Super Breaker ability is refreshed!");
		}
    }
    public void XpCheck(Player player){
    	/*
    	 * ACROBATICS
    	 */
    	if(player != null && mcUsers.getProfile(player).getAcrobaticsGatherInt() >= mcUsers.getProfile(player).getXpToLevel("acrobatics")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getAcrobaticsGatherInt() >= mcUsers.getProfile(player).getXpToLevel("acrobatics")){
				skillups++;
				mcUsers.getProfile(player).removeAcrobaticsGather(mcUsers.getProfile(player).getXpToLevel("acrobatics"));
				mcUsers.getProfile(player).skillUpAcrobatics(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Acrobatics skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getAcrobatics()+")");	
		}
    	/*
    	 * ARCHERY
    	 */
    	if(mcUsers.getProfile(player).getArcheryGatherInt() >= mcUsers.getProfile(player).getXpToLevel("archery")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getArcheryGatherInt() >= mcUsers.getProfile(player).getXpToLevel("archery")){
				skillups++;
				mcUsers.getProfile(player).removeArcheryGather(mcUsers.getProfile(player).getXpToLevel("archery"));
				mcUsers.getProfile(player).skillUpArchery(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Archery skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getArchery()+")");	
		}
    	/*
    	 * SWORDS
    	 */
    	if(mcUsers.getProfile(player).getSwordsGatherInt() >= mcUsers.getProfile(player).getXpToLevel("swords")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getSwordsGatherInt() >= mcUsers.getProfile(player).getXpToLevel("swords")){
				skillups++;
				mcUsers.getProfile(player).removeSwordsGather(mcUsers.getProfile(player).getXpToLevel("swords"));
				mcUsers.getProfile(player).skillUpSwords(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Swords skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getSwords()+")");	
		}
    	/*
    	 * AXES
    	 */
		if(mcUsers.getProfile(player).getAxesGatherInt() >= mcUsers.getProfile(player).getXpToLevel("axes")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getAxesGatherInt() >= mcUsers.getProfile(player).getXpToLevel("axes")){
				skillups++;
				mcUsers.getProfile(player).removeAxesGather(mcUsers.getProfile(player).getXpToLevel("axes"));
				mcUsers.getProfile(player).skillUpAxes(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Axes skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getAxes()+")");	
		}
		/*
		 * UNARMED
		 */
		if(mcUsers.getProfile(player).getUnarmedGatherInt() >= mcUsers.getProfile(player).getXpToLevel("unarmed")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getUnarmedGatherInt() >= mcUsers.getProfile(player).getXpToLevel("unarmed")){
				skillups++;
				mcUsers.getProfile(player).removeUnarmedGather(mcUsers.getProfile(player).getXpToLevel("unarmed"));
				mcUsers.getProfile(player).skillUpUnarmed(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getUnarmed()+")");	
		}
		/*
		 * HERBALISM
		 */
		if(mcUsers.getProfile(player).getHerbalismGatherInt() >= mcUsers.getProfile(player).getXpToLevel("herbalism")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getHerbalismGatherInt() >= mcUsers.getProfile(player).getXpToLevel("herbalism")){
				skillups++;
				mcUsers.getProfile(player).removeHerbalismGather(mcUsers.getProfile(player).getXpToLevel("herbalism"));
				mcUsers.getProfile(player).skillUpHerbalism(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getHerbalism()+")");	
		}
		/*
		 * MINING
		 */
		if(player != null && mcUsers.getProfile(player).getMiningGatherInt() >= mcUsers.getProfile(player).getXpToLevel("mining")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getMiningGatherInt() >= mcUsers.getProfile(player).getXpToLevel("mining")){
				skillups++;
				mcUsers.getProfile(player).removeMiningGather(mcUsers.getProfile(player).getXpToLevel("mining"));
				mcUsers.getProfile(player).skillUpMining(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Mining skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getMining()+")");	
		}
		/*
		 * WOODCUTTING
		 */
		if(player != null && mcUsers.getProfile(player).getWoodCuttingGatherInt() >= mcUsers.getProfile(player).getXpToLevel("woodcutting")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getWoodCuttingGatherInt() >= mcUsers.getProfile(player).getXpToLevel("woodcutting")){
				skillups++;
				mcUsers.getProfile(player).removeWoodCuttingGather(mcUsers.getProfile(player).getXpToLevel("woodcutting"));
				mcUsers.getProfile(player).skillUpWoodCutting(1);
			}
			player.sendMessage(ChatColor.YELLOW+"WoodCutting skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getWoodCutting()+")");	
		}
		/*
		 * REPAIR
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
		/*
		 * EXCAVATION
		 */
		if(mcUsers.getProfile(player).getExcavationGatherInt() >= mcUsers.getProfile(player).getXpToLevel("excavation")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getExcavationGatherInt() >= mcUsers.getProfile(player).getXpToLevel("excavation")){
				skillups++;
				mcUsers.getProfile(player).removeExcavationGather(mcUsers.getProfile(player).getXpToLevel("excavation"));
				mcUsers.getProfile(player).skillUpExcavation(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Excavation skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getExcavation()+")");	
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
