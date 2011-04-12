package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(!PP.getGreenTerraInformed() && System.currentTimeMillis() - PP.getGreenTerraDeactivatedTimeStamp() >= (mcLoadProperties.greenTerraCooldown * 1000)){
			PP.setGreenTerraInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Green Terra "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!PP.getTreeFellerInformed() && System.currentTimeMillis() - PP.getTreeFellerDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			PP.setTreeFellerInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Tree Feller "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!PP.getSuperBreakerInformed() && System.currentTimeMillis() - PP.getSuperBreakerDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			PP.setSuperBreakerInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Super Breaker "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!PP.getSerratedStrikesInformed() && System.currentTimeMillis() - PP.getSerratedStrikesDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			PP.setSerratedStrikesInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Serrated Strikes "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!PP.getBerserkInformed() && System.currentTimeMillis() - PP.getBerserkDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			PP.setBerserkInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Berserk "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!PP.getSkullSplitterInformed() && System.currentTimeMillis() - PP.getSkullSplitterDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			PP.setSkullSplitterInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Skull Splitter "+ChatColor.GREEN+"ability is refreshed!");
    	}
    	if(!PP.getGigaDrillBreakerInformed() && System.currentTimeMillis() - PP.getGigaDrillBreakerDeactivatedTimeStamp() >= (mcLoadProperties.berserkCooldown * 1000)){
			PP.setGigaDrillBreakerInformed(true);
    		player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Giga Drill Breaker "+ChatColor.GREEN+"ability is refreshed!");
    	}
    }
    public void hoeReadinessCheck(Player player){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(mcPermissions.getInstance().herbalismAbility(player) && mcm.getInstance().isHoe(player.getItemInHand()) && !PP.getHoePreparationMode()){
    		if(!PP.getGreenTerraMode() && !cooldownOver(player, PP.getGreenTerraDeactivatedTimeStamp(), mcLoadProperties.greenTerraCooldown)){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getGreenTerraDeactivatedTimeStamp(), mcLoadProperties.greenTerraCooldown)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR HOE**");
			PP.setHoePreparationATS(System.currentTimeMillis());
			PP.setHoePreparationMode(true);
    	}
    }
    public void abilityActivationCheck(Player player){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(!PP.getAbilityUse())
    		return;
    	if(mcPermissions.getInstance().miningAbility(player) && mcm.getInstance().isMiningPick(player.getItemInHand()) && !PP.getPickaxePreparationMode()){
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
    public void serratedStrikesActivationCheck(Player player, Plugin pluginx){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
		if(mcm.getInstance().isSwords(player.getItemInHand())){
			if(PP.getSwordsPreparationMode()){
    			PP.setSwordsPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getSwordsInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getSerratedStrikesMode() && PP.getSerratedStrikesCooldown() == 0){
	    		player.sendMessage(ChatColor.GREEN+"**SERRATED STRIKES ACTIVATED**");
	    		for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && mcm.getInstance().getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Serrated Strikes!");
	    		}
	    		PP.setSerratedStrikesTicks((ticks * 2) * 1000);
	    		PP.setSerratedStrikesActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setSerratedStrikesMode(true);
	    	}
	    	
	    }
	}
    public void berserkActivationCheck(Player player, Plugin pluginx){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
		if(player.getItemInHand().getTypeId() == 0){
			if(PP.getFistsPreparationMode()){
    			PP.setFistsPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getUnarmedInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getBerserkMode() && cooldownOver(player, PP.getBerserkDeactivatedTimeStamp(), mcLoadProperties.berserkCooldown)){
	    		player.sendMessage(ChatColor.GREEN+"**BERSERK ACTIVATED**");
	    		for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && mcm.getInstance().getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Berserk!");
	    		}
	    		PP.setBerserkTicks(ticks * 1000);
	    		PP.setBerserkActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setBerserkMode(true);
	    	}
	    }
	}
    public void skullSplitterCheck(Player player, Plugin pluginx){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(mcm.getInstance().isAxes(player.getItemInHand()) && mcPermissions.getInstance().axesAbility(player)){
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode()){
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		int x = PP.getAxesInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}

    		if(!PP.getSkullSplitterMode() && cooldownOver(player, PP.getSkullSplitterDeactivatedTimeStamp(), mcLoadProperties.skullSplitterCooldown)){
    			player.sendMessage(ChatColor.GREEN+"**SKULL SPLITTER ACTIVATED**");
    			for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && mcm.getInstance().getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Skull Splitter!");
	    		}
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
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(PP == null)
    		mcUsers.addUser(player);
    	if(PP.getHoePreparationMode() && System.currentTimeMillis() - PP.getHoePreparationATS() >= 4000){
			PP.setHoePreparationMode(false);
			player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR HOE**");
    	}
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
			player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR SHOVEL**");
		}
		/*
		 * HERBALISM ABILITY
		 */
		if(mcPermissions.getInstance().herbalismAbility(player)){
    		if(PP.getGreenTerraMode() && PP.getGreenTerraActivatedTimeStamp() + PP.getGreenTerraTicks() <= System.currentTimeMillis()){
    				PP.setGreenTerraMode(false);
    				PP.setGreenTerraInformed(false);
    				player.sendMessage(ChatColor.RED+"**Green Terra has worn off**");
    				PP.setGreenTerraDeactivatedTimeStamp(System.currentTimeMillis());
    		}
		}
    	/*
    	 * AXES ABILITY
    	 */
    	if(mcPermissions.getInstance().axesAbility(player)){
    		if(PP.getSkullSplitterMode() && PP.getSkullSplitterActivatedTimeStamp() + PP.getSkullSplitterTicks() <= System.currentTimeMillis()){
    				PP.setSkullSplitterMode(false);
    				PP.setSkullSplitterInformed(false);
    				player.sendMessage(ChatColor.RED+"**Skull Splitter has worn off**");
    				PP.setSkullSplitterDeactivatedTimeStamp(System.currentTimeMillis());
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
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	/*
    	 * ACROBATICS
    	 */
    	if(player != null && PP.getAcrobaticsGatherInt() >= PP.getXpToLevel("acrobatics")){
			int skillups = 0;
			while(PP.getAcrobaticsGatherInt() >= PP.getXpToLevel("acrobatics")){
				skillups++;
				PP.removeAcrobaticsGather(PP.getXpToLevel("acrobatics"));
				PP.skillUpAcrobatics(1);
			}
			if(player != null && PP.getAcrobatics() != null)
				player.sendMessage(ChatColor.YELLOW+"Acrobatics skill increased by "+skillups+"."+" Total ("+PP.getAcrobatics()+")");	
		}
    	/*
    	 * ARCHERY
    	 */
    	if(PP.getArcheryGatherInt() >= PP.getXpToLevel("archery")){
			int skillups = 0;
			while(PP.getArcheryGatherInt() >= PP.getXpToLevel("archery")){
				skillups++;
				PP.removeArcheryGather(PP.getXpToLevel("archery"));
				PP.skillUpArchery(1);
			}
			if(player != null && PP.getArchery() != null)
				player.sendMessage(ChatColor.YELLOW+"Archery skill increased by "+skillups+"."+" Total ("+PP.getArchery()+")");	
		}
    	/*
    	 * SWORDS
    	 */
    	if(PP.getSwordsGatherInt() >= PP.getXpToLevel("swords")){
			int skillups = 0;
			while(PP.getSwordsGatherInt() >= PP.getXpToLevel("swords")){
				skillups++;
				PP.removeSwordsGather(PP.getXpToLevel("swords"));
				PP.skillUpSwords(1);
			}
			if(player != null && PP.getSwords() != null)
				player.sendMessage(ChatColor.YELLOW+"Swords skill increased by "+skillups+"."+" Total ("+PP.getSwords()+")");	
		}
    	/*
    	 * AXES
    	 */
		if(PP.getAxesGatherInt() >= PP.getXpToLevel("axes")){
			int skillups = 0;
			while(PP.getAxesGatherInt() >= PP.getXpToLevel("axes")){
				skillups++;
				PP.removeAxesGather(PP.getXpToLevel("axes"));
				PP.skillUpAxes(1);
			}
			if(player != null && PP.getAxes() != null)
				player.sendMessage(ChatColor.YELLOW+"Axes skill increased by "+skillups+"."+" Total ("+PP.getAxes()+")");	
		}
		/*
		 * UNARMED
		 */
		if(PP.getUnarmedGatherInt() >= PP.getXpToLevel("unarmed")){
			int skillups = 0;
			while(PP.getUnarmedGatherInt() >= PP.getXpToLevel("unarmed")){
				skillups++;
				PP.removeUnarmedGather(PP.getXpToLevel("unarmed"));
				PP.skillUpUnarmed(1);
			}
			if(player != null && PP.getUnarmed() != null)
				player.sendMessage(ChatColor.YELLOW+"Unarmed skill increased by "+skillups+"."+" Total ("+PP.getUnarmed()+")");	
		}
		/*
		 * HERBALISM
		 */
		if(PP.getHerbalismGatherInt() >= PP.getXpToLevel("herbalism")){
			int skillups = 0;
			while(PP.getHerbalismGatherInt() >= PP.getXpToLevel("herbalism")){
				skillups++;
				PP.removeHerbalismGather(PP.getXpToLevel("herbalism"));
				PP.skillUpHerbalism(1);
			}
			if(player != null && PP.getHerbalism() != null)
				player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by "+skillups+"."+" Total ("+PP.getHerbalism()+")");	
		}
		/*
		 * MINING
		 */
		if(player != null && PP.getMiningGatherInt() >= PP.getXpToLevel("mining")){
			int skillups = 0;
			while(PP.getMiningGatherInt() >= PP.getXpToLevel("mining")){
				skillups++;
				PP.removeMiningGather(PP.getXpToLevel("mining"));
				PP.skillUpMining(1);
			}
			if(player != null && PP.getMining() != null)
				player.sendMessage(ChatColor.YELLOW+"Mining skill increased by "+skillups+"."+" Total ("+PP.getMining()+")");	
		}
		/*
		 * WOODCUTTING
		 */
		if(player != null && PP.getWoodCuttingGatherInt() >= PP.getXpToLevel("woodcutting")){
			int skillups = 0;
			while(PP.getWoodCuttingGatherInt() >= PP.getXpToLevel("woodcutting")){
				skillups++;
				PP.removeWoodCuttingGather(PP.getXpToLevel("woodcutting"));
				PP.skillUpWoodCutting(1);
			}
			if(player != null && PP.getWoodCutting() != null)
				player.sendMessage(ChatColor.YELLOW+"WoodCutting skill increased by "+skillups+"."+" Total ("+PP.getWoodCutting()+")");	
		}
		/*
		 * REPAIR
		 */
		if(PP.getRepairGatherInt() >= PP.getXpToLevel("repair")){
			int skillups = 0;
			while(PP.getRepairGatherInt() >= PP.getXpToLevel("repair")){
				skillups++;
				PP.removeRepairGather(PP.getXpToLevel("repair"));
				PP.skillUpRepair(1);
			}
			if(player != null && PP.getRepair() != null)
				player.sendMessage(ChatColor.YELLOW+"Repair skill increased by "+skillups+"."+" Total ("+PP.getRepair()+")");	
		}
		/*
		 * EXCAVATION
		 */
		if(PP.getExcavationGatherInt() >= PP.getXpToLevel("excavation")){
			int skillups = 0;
			while(PP.getExcavationGatherInt() >= PP.getXpToLevel("excavation")){
				skillups++;
				PP.removeExcavationGather(PP.getXpToLevel("excavation"));
				PP.skillUpExcavation(1);
			}
			if(player != null && PP.getExcavation() != null)
				player.sendMessage(ChatColor.YELLOW+"Excavation skill increased by "+skillups+"."+" Total ("+PP.getExcavation()+")");	
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
