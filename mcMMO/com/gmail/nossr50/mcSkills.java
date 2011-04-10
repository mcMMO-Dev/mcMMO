package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.PlayerList.PlayerProfile;


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
	public boolean cooldownOver(Player player, long oldTime, int cooldown){
		long currentTime = System.currentTimeMillis();
		if(currentTime - oldTime >= (cooldown * 1000)){
			return true;
		} else {
			return false;
		}
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
    public int calculateTimeLeft(Player player, long deactivatedTimeStamp, int cooldown){
    	long currentTime = System.currentTimeMillis();
    	int x = 0;
    	while(currentTime < deactivatedTimeStamp + (cooldown * 1000)){
    		currentTime += 1000;
    		x++;
    	}
    	return x;
    }
    public void watchCooldowns(Player player){
    	if(!mcUsers.getProfile(player).getTreeFellerInformed() && System.currentTimeMillis() - mcUsers.getProfile(player).getTreeFellerDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			mcUsers.getProfile(player).setTreeFellerInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Tree Feller "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!mcUsers.getProfile(player).getSuperBreakerInformed() && System.currentTimeMillis() - mcUsers.getProfile(player).getSuperBreakerDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			mcUsers.getProfile(player).setSuperBreakerInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Super Breaker "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!mcUsers.getProfile(player).getSerratedStrikesInformed() && System.currentTimeMillis() - mcUsers.getProfile(player).getSerratedStrikesDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			mcUsers.getProfile(player).setSerratedStrikesInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Serrated Strikes "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!mcUsers.getProfile(player).getBerserkInformed() && System.currentTimeMillis() - mcUsers.getProfile(player).getBerserkDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			mcUsers.getProfile(player).setBerserkInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Berserk "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!mcUsers.getProfile(player).getSkullSplitterInformed() && System.currentTimeMillis() - mcUsers.getProfile(player).getSkullSplitterDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			mcUsers.getProfile(player).setSkullSplitterInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Skull Splitter "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!mcUsers.getProfile(player).getGigaDrillBreakerInformed() && System.currentTimeMillis() - mcUsers.getProfile(player).getGigaDrillBreakerDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			mcUsers.getProfile(player).setGigaDrillBreakerInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Giga Drill Breaker "+ChatColor.GREEN+"ability is refreshed!");
    	}
    }
    public void abilityActivationCheck(Player player){
    	PlayerProfile PP = mcUsers.getProfile(player);
    	if(!PP.getAbilityUse())
    		return;
    	if(mcPermissions.getInstance().miningAbility(player) && mcm.getInstance().isMiningPick(player.getItemInHand()) && !mcUsers.getProfile(player).getPickaxePreparationMode()){
    		if(!PP.getSuperBreakerMode() && !cooldownOver(player, PP.getSuperBreakerDeactivatedTimeStamp(), mcLoadProperties.superBreakerCooldown)){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getSuperBreakerDeactivatedTimeStamp(), mcLoadProperties.superBreakerCooldown)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR PICKAXE**");
			PP.setPickaxePreparationATS(System.currentTimeMillis());
			PP.setPickaxePreparationMode(true);
    	}
    	if(mcPermissions.getInstance().excavationAbility(player) && mcm.getInstance().isShovel(player.getItemInHand()) && !PP.getShovelPreparationMode()){
    		if(!PP.getGigaDrillBreakerMode() && !cooldownOver(player, PP.getGigaDrillBreakerDeactivatedTimeStamp(), mcLoadProperties.gigaDrillBreakerCooldown)){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getGigaDrillBreakerDeactivatedTimeStamp(), mcLoadProperties.gigaDrillBreakerCooldown)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR SHOVEL**");
			PP.setShovelPreparationATS(System.currentTimeMillis());
			PP.setShovelPreparationMode(true);
    	}
    	if(mcPermissions.getInstance().swordsAbility(player) && mcm.getInstance().isSwords(player.getItemInHand()) && !PP.getSwordsPreparationMode()){
    		if(!PP.getSerratedStrikesMode() && !cooldownOver(player, PP.getSerratedStrikesDeactivatedTimeStamp(), mcLoadProperties.serratedStrikeCooldown)){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getSerratedStrikesDeactivatedTimeStamp(), mcLoadProperties.serratedStrikeCooldown)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR SWORD**");
			PP.setSwordsPreparationATS(System.currentTimeMillis());
			PP.setSwordsPreparationMode(true);
    	}
    	if(mcPermissions.getInstance().unarmedAbility(player) && player.getItemInHand().getTypeId() == 0 && !PP.getFistsPreparationMode()){
	    	if(!PP.getBerserkMode() && !cooldownOver(player, PP.getBerserkDeactivatedTimeStamp(), mcLoadProperties.berserkCooldown)){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getBerserkDeactivatedTimeStamp(), mcLoadProperties.berserkCooldown)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR FISTS**");
			PP.setFistsPreparationATS(System.currentTimeMillis());
			PP.setFistsPreparationMode(true);
    	}
    	if((mcPermissions.getInstance().axes(player) || mcPermissions.getInstance().woodcutting(player)) && !PP.getAxePreparationMode()){
    		if(mcm.getInstance().isAxes(player.getItemInHand())){
    			player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR AXE**");
    			PP.setAxePreparationATS(System.currentTimeMillis());
    			PP.setAxePreparationMode(true);
    		}
    	}
    }
    public void serratedStrikesActivationCheck(Player player){
		if(mcm.getInstance().isSwords(player.getItemInHand())){
			if(mcUsers.getProfile(player).getSwordsPreparationMode()){
    			mcUsers.getProfile(player).setSwordsPreparationMode(false);
    		}
	    	int ticks = 2;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 50)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 150)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 250)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 350)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 450)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 550)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 650)
    			ticks++;
    		if(mcUsers.getProfile(player).getSwordsInt() >= 750)
    			ticks++;
    		
	    	if(!mcUsers.getProfile(player).getSerratedStrikesMode() && mcUsers.getProfile(player).getSerratedStrikesCooldown() == 0){
	    		player.sendMessage(ChatColor.GREEN+"**SERRATED STRIKES ACTIVATED**");
	    		mcUsers.getProfile(player).setSerratedStrikesTicks(ticks * 1000);
	    		mcUsers.getProfile(player).setSerratedStrikesActivatedTimeStamp(System.currentTimeMillis());
	    		mcUsers.getProfile(player).setSerratedStrikesMode(true);
	    	}
	    	
	    }
	}
    public void berserkActivationCheck(Player player){
		if(player.getItemInHand().getTypeId() == 0){
			if(mcUsers.getProfile(player).getFistsPreparationMode()){
    			mcUsers.getProfile(player).setFistsPreparationMode(false);
    		}
	    	int ticks = 2;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 50)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 150)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 250)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 350)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 450)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 550)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 650)
    			ticks++;
    		if(mcUsers.getProfile(player).getUnarmedInt() >= 750)
    			ticks++;
    		
	    	if(!mcUsers.getProfile(player).getBerserkMode() && cooldownOver(player, mcUsers.getProfile(player).getBerserkDeactivatedTimeStamp(), mcLoadProperties.berserkCooldown)){
	    		player.sendMessage(ChatColor.GREEN+"**BERSERK ACTIVATED**");
	    		mcUsers.getProfile(player).setBerserkTicks(ticks * 1000);
	    		mcUsers.getProfile(player).setBerserkActivatedTimeStamp(System.currentTimeMillis());
	    		mcUsers.getProfile(player).setBerserkMode(true);
	    	}
	    }
	}
    public void skullSplitterCheck(Player player){
    	PlayerProfile PP = mcUsers.getProfile(player);
    	if(mcm.getInstance().isAxes(player.getItemInHand()) && mcPermissions.getInstance().axesAbility(player)){
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode()){
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		if(PP.getAxesInt() >= 50)
    			ticks++;
    		if(PP.getAxesInt() >= 150)
    			ticks++;
    		if(PP.getAxesInt() >= 250)
    			ticks++;
    		if(PP.getAxesInt() >= 350)
    			ticks++;
    		if(PP.getAxesInt() >= 450)
    			ticks++;
    		if(PP.getAxesInt() >= 550)
    			ticks++;
    		if(PP.getAxesInt() >= 650)
    			ticks++;
    		if(PP.getAxesInt() >= 750)
    			ticks++;

    		if(!PP.getSkullSplitterMode() && PP.getSkullSplitterCooldown() == 0){
    			player.sendMessage(ChatColor.GREEN+"**SKULL SPLITTER ACTIVATED**");
    			PP.setSkullSplitterTicks(ticks * 1000);
    			PP.setSkullSplitterActivatedTimeStamp(System.currentTimeMillis());
    			PP.setSkullSplitterMode(true);
    		}
    		if(!PP.getSkullSplitterMode() && !cooldownOver(player, PP.getSkullSplitterDeactivatedTimeStamp(), mcLoadProperties.skullSplitterCooldown)){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getSkullSplitterDeactivatedTimeStamp(), mcLoadProperties.skullSplitterCooldown)+"s)");
    		}
    	}
    }
    public void monitorSkills(Player player){
    	PlayerProfile PP = mcUsers.getProfile(player);
    	/*
    	 * AXE PREPARATION MODE
    	 */
    	if(PP == null)
    		mcUsers.addUser(player);
		if(PP.getAxePreparationMode() && System.currentTimeMillis() - PP.getAxePreparationATS() >= 4000){
				PP.setAxePreparationMode(false);
				player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR AXE**");
		}
		if(PP.getPickaxePreparationMode() && System.currentTimeMillis() - PP.getPickaxePreparationATS() >= 4000){
			PP.setPickaxePreparationMode(false);
			player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR PICKAXE**");
		}
		if(PP.getSwordsPreparationMode() && System.currentTimeMillis() - PP.getSwordsPreparationATS() >= 4000){
			PP.setSwordsPreparationMode(false);
			player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR SWORD**");
		}
		if(PP.getFistsPreparationMode() && System.currentTimeMillis() - PP.getFistsPreparationATS() >= 4000){
			PP.setFistsPreparationMode(false);
			player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR FISTS**");
		}
		if(PP.getShovelPreparationMode() && System.currentTimeMillis() - PP.getShovelPreparationATS() >= 4000){
			PP.setShovelPreparationMode(false);
			player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR AXE**");
		}
    	/*
    	 * AXES ABILITY
    	 */
    	if(mcPermissions.getInstance().axesAbility(player)){
    		if(mcPermissions.getInstance().unarmedAbility(player)){
    			if(PP.getSkullSplitterMode() && PP.getSkullSplitterActivatedTimeStamp() + PP.getSkullSplitterTicks() <= System.currentTimeMillis()){
    					PP.setSkullSplitterMode(false);
    					PP.setSkullSplitterInformed(false);
    					player.sendMessage(ChatColor.RED+"**Skull Splitter has worn off**");
    					PP.setSkullSplitterDeactivatedTimeStamp(System.currentTimeMillis());
    			}
    		}
		}
    	/*
		 * WOODCUTTING ABILITY
		 */
    	if(mcPermissions.getInstance().woodCuttingAbility(player)){
			if(PP.getTreeFellerMode() && PP.getTreeFellerActivatedTimeStamp() + PP.getTreeFellerTicks() <= System.currentTimeMillis()){
					PP.setTreeFellerMode(false);
					PP.setTreeFellerInformed(false);
					player.sendMessage(ChatColor.RED+"**Tree Feller has worn off**");
					PP.setTreeFellerDeactivatedTimeStamp(System.currentTimeMillis());
			}
		}
		/*
		 * MINING ABILITY
		 */
		if(mcPermissions.getInstance().miningAbility(player)){
			if(PP.getSuperBreakerMode() && PP.getSuperBreakerActivatedTimeStamp() + PP.getSuperBreakerTicks() <= System.currentTimeMillis()){
					PP.setSuperBreakerMode(false);
					PP.setSuperBreakerInformed(false);
					player.sendMessage(ChatColor.RED+"**Super Breaker has worn off**");
					PP.setSuperBreakerDeactivatedTimeStamp(System.currentTimeMillis());
			}
		}
		/*
		 * EXCAVATION ABILITY
		 */
		if(mcPermissions.getInstance().excavationAbility(player)){
			if(PP.getGigaDrillBreakerMode() && PP.getGigaDrillBreakerActivatedTimeStamp() + PP.getGigaDrillBreakerTicks() <= System.currentTimeMillis()){
					PP.setGigaDrillBreakerMode(false);
					PP.setGigaDrillBreakerInformed(false);
					player.sendMessage(ChatColor.RED+"**Giga Drill Breaker has worn off**");
					PP.setGigaDrillBreakerDeactivatedTimeStamp(System.currentTimeMillis());
			}
		}
		/*
		 * SWORDS ABILITY
		 */
		if(mcPermissions.getInstance().swordsAbility(player)){
			if(PP.getSerratedStrikesMode() && PP.getSerratedStrikesActivatedTimeStamp() + PP.getSerratedStrikesTicks() <= System.currentTimeMillis()){
					PP.setSerratedStrikesMode(false);
					PP.setSerratedStrikesInformed(false);
					player.sendMessage(ChatColor.RED+"**Serrated Strikes has worn off**");
					PP.setSerratedStrikesDeactivatedTimeStamp(System.currentTimeMillis());
			}
		}
		/*
		 * UNARMED ABILITY
		 */
		if(mcPermissions.getInstance().unarmedAbility(player)){
			if(PP.getBerserkMode() && PP.getBerserkActivatedTimeStamp() + PP.getBerserkTicks() <= System.currentTimeMillis()){
					PP.setBerserkMode(false);
					PP.setBerserkInformed(false);
					player.sendMessage(ChatColor.RED+"**Berserk has worn off**");
					PP.setBerserkDeactivatedTimeStamp(System.currentTimeMillis());
			}
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
    	skillname = skillname.toLowerCase();
    	if(skillname.equals("all")){
    		return true;
    	}
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
    	mcConfig.getInstance().removeArrowTracked(entity);
    }
}
