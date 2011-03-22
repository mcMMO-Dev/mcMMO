package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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
    	if(mcUsers.getProfile(player).getTreeFellerCooldown() >= 1){
    		mcUsers.getProfile(player).decreaseTreeFellerCooldown();
    		if(mcUsers.getProfile(player).getTreeFellerCooldown() == 0){
    			player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Tree Feller "+ChatColor.GREEN+"ability is refreshed!");
    		}
    	}
    	if(mcUsers.getProfile(player).getSuperBreakerCooldown() >= 1){
    		mcUsers.getProfile(player).decreaseSuperBreakerCooldown();
			if(mcUsers.getProfile(player).getSuperBreakerCooldown() == 0){
				player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Super Breaker "+ChatColor.GREEN+"ability is refreshed!");
			}
    	}
    	if(mcUsers.getProfile(player).getSerratedStrikesCooldown() >= 1){
    		mcUsers.getProfile(player).decreaseSerratedStrikesCooldown();
			if(mcUsers.getProfile(player).getSerratedStrikesCooldown() == 0){
				player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Serrated Strikes "+ChatColor.GREEN+"ability is refreshed!");
			}
    	}
    	if(mcUsers.getProfile(player).getBerserkCooldown() >= 1){
    		mcUsers.getProfile(player).decreaseBerserkCooldown();
			if(mcUsers.getProfile(player).getBerserkCooldown() == 0){
				player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Berserk "+ChatColor.GREEN+"ability is refreshed!");
			}
    	}
    	if(mcUsers.getProfile(player).getSkullSplitterCooldown() >= 1){
    		mcUsers.getProfile(player).decreaseSkullSplitterCooldown();
			if(mcUsers.getProfile(player).getSkullSplitterCooldown() == 0){
				player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Skull Splitter "+ChatColor.GREEN+"ability is refreshed!");
			}
    	}
    	if(mcUsers.getProfile(player).getGigaDrillBreakerCooldown() >= 1){
    		mcUsers.getProfile(player).decreaseGigaDrillBreakerCooldown();
			if(mcUsers.getProfile(player).getGigaDrillBreakerCooldown() == 0){
				player.sendMessage(ChatColor.GREEN+"Your "+ChatColor.YELLOW+"Giga Drill Breaker "+ChatColor.GREEN+"ability is refreshed!");
			}
    	}
    }
    public void abilityActivationCheck(Player player, Block block){
    	if(!mcUsers.getProfile(player).getAbilityUse())
    		return;
    	if(mcPermissions.getInstance().miningAbility(player) && mcm.getInstance().isMiningPick(player.getItemInHand()) && !mcUsers.getProfile(player).getPickaxePreparationMode()){
    		if(!mcUsers.getProfile(player).getSuperBreakerMode() && mcUsers.getProfile(player).getSuperBreakerCooldown() >= 1){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."+ChatColor.YELLOW+" ("
	    				+(mcUsers.getProfile(player).getSuperBreakerCooldown() * 2)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR PICKAXE**");
			mcUsers.getProfile(player).setPickaxePreparationTicks(2);
			mcUsers.getProfile(player).setPickaxePreparationMode(true);
    	}
    	if(mcPermissions.getInstance().excavationAbility(player) && mcm.getInstance().isShovel(player.getItemInHand()) && !mcUsers.getProfile(player).getShovelPreparationMode()){
    		if(!mcUsers.getProfile(player).getGigaDrillBreakerMode() && mcUsers.getProfile(player).getGigaDrillBreakerCooldown() >= 1){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+(mcUsers.getProfile(player).getGigaDrillBreakerCooldown() * 2)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR SHOVEL**");
			mcUsers.getProfile(player).setShovelPreparationTicks(2);
			mcUsers.getProfile(player).setShovelPreparationMode(true);
    	}
    	if(mcPermissions.getInstance().swordsAbility(player) && mcm.getInstance().isSwords(player.getItemInHand()) && !mcUsers.getProfile(player).getSwordsPreparationMode()){
    		if(!mcUsers.getProfile(player).getSerratedStrikesMode() && mcUsers.getProfile(player).getSerratedStrikesCooldown() >= 1){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+(mcUsers.getProfile(player).getSerratedStrikesCooldown() * 2)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR SWORD**");
			mcUsers.getProfile(player).setSwordsPreparationTicks(2);
			mcUsers.getProfile(player).setSwordsPreparationMode(true);
    	}
    	if(mcPermissions.getInstance().unarmedAbility(player) && player.getItemInHand().getTypeId() == 0 && !mcUsers.getProfile(player).getFistsPreparationMode()){
	    	if(!mcUsers.getProfile(player).getBerserkMode() && mcUsers.getProfile(player).getBerserkCooldown() >= 1){
	    		player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
	    				+ChatColor.YELLOW+" ("+(mcUsers.getProfile(player).getBerserkCooldown() * 2)+"s)");
	    		return;
	    	}
    		player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR FISTS**");
			mcUsers.getProfile(player).setFistsPreparationTicks(2);
			mcUsers.getProfile(player).setFistsPreparationMode(true);
    	}
    	if((mcPermissions.getInstance().axes(player) || mcPermissions.getInstance().woodcutting(player)) && !mcUsers.getProfile(player).getAxePreparationMode()){
    		if(mcm.getInstance().isAxes(player.getItemInHand())){
    			player.sendMessage(ChatColor.GREEN+"**YOU READY YOUR AXE**");
    			mcUsers.getProfile(player).setAxePreparationTicks(2);
    			mcUsers.getProfile(player).setAxePreparationMode(true);
    		}
    	}
    }
    public void serratedStrikesActivationCheck(Player player){
		if(mcm.getInstance().isSwords(player.getItemInHand())){
			if(mcUsers.getProfile(player).getSwordsPreparationMode()){
    			mcUsers.getProfile(player).setSwordsPreparationMode(false);
    			mcUsers.getProfile(player).setSwordsPreparationTicks(0);
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
	    		mcUsers.getProfile(player).setSerratedStrikesTicks(ticks);
	    		mcUsers.getProfile(player).setSerratedStrikesMode(true);
	    	}
	    	
	    }
	}
    public void berserkActivationCheck(Player player){
		if(player.getItemInHand().getTypeId() == 0){
			if(mcUsers.getProfile(player).getFistsPreparationMode()){
    			mcUsers.getProfile(player).setFistsPreparationMode(false);
    			mcUsers.getProfile(player).setFistsPreparationTicks(0);
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
    		
	    	if(!mcUsers.getProfile(player).getBerserkMode() && mcUsers.getProfile(player).getBerserkCooldown() == 0){
	    		player.sendMessage(ChatColor.GREEN+"**BERSERK ACTIVATED**");
	    		mcUsers.getProfile(player).setBerserkTicks(ticks);
	    		mcUsers.getProfile(player).setBerserkMode(true);
	    	}

	    }
	}
    public void skullSplitterCheck(Player player){
    	if(mcm.getInstance().isAxes(player.getItemInHand())){
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(mcUsers.getProfile(player).getAxePreparationMode()){
    			mcUsers.getProfile(player).setAxePreparationMode(false);
    			mcUsers.getProfile(player).setAxePreparationTicks(0);
    		}
    		int ticks = 2;
    		if(mcUsers.getProfile(player).getAxesInt() >= 50)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 150)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 250)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 350)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 450)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 550)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 650)
    			ticks++;
    		if(mcUsers.getProfile(player).getAxesInt() >= 750)
    			ticks++;

    		if(!mcUsers.getProfile(player).getSkullSplitterMode() && mcUsers.getProfile(player).getSkullSplitterCooldown() == 0){
    			player.sendMessage(ChatColor.GREEN+"**SKULL SPLITTER ACTIVATED**");
    			mcUsers.getProfile(player).setSkullSplitterTicks(ticks);
    			mcUsers.getProfile(player).setSkullSplitterMode(true);
    		}
    		if(!mcUsers.getProfile(player).getSkullSplitterMode() && mcUsers.getProfile(player).getSkullSplitterCooldown() >= 1){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+(mcUsers.getProfile(player).getSkullSplitterCooldown() * 2)+"s)");
    		}
    	}
    }
    public void monitorSkills(Player player){
    	/*
    	 * AXE PREPARATION MODE
    	 */
    	if(mcUsers.getProfile(player) == null)
    		mcUsers.addUser(player);
		if(mcUsers.getProfile(player).getAxePreparationMode()){
			mcUsers.getProfile(player).decreaseAxePreparationTicks();
			if(mcUsers.getProfile(player).getAxePreparationTicks() <= 0){
				mcUsers.getProfile(player).setAxePreparationMode(false);
				player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR AXE**");
			}
		}
		if(mcUsers.getProfile(player).getPickaxePreparationMode()){
			mcUsers.getProfile(player).decreasePickaxePreparationTicks();
			if(mcUsers.getProfile(player).getPickaxePreparationTicks() <= 0){
				mcUsers.getProfile(player).setPickaxePreparationMode(false);
				player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR PICKAXE**");
			}
		}
		if(mcUsers.getProfile(player).getSwordsPreparationMode()){
			mcUsers.getProfile(player).decreaseSwordsPreparationTicks();
			if(mcUsers.getProfile(player).getSwordsPreparationTicks() <= 0){
				mcUsers.getProfile(player).setSwordsPreparationMode(false);
				player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR SWORD**");
			}
		}
		if(mcUsers.getProfile(player).getFistsPreparationMode()){
			mcUsers.getProfile(player).decreaseFistsPreparationTicks();
			if(mcUsers.getProfile(player).getFistsPreparationTicks() <= 0){
				mcUsers.getProfile(player).setFistsPreparationMode(false);
				player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR FISTS**");
			}
		}
		if(mcUsers.getProfile(player).getShovelPreparationMode()){
			mcUsers.getProfile(player).decreaseShovelPreparationTicks();
			if(mcUsers.getProfile(player).getShovelPreparationTicks() <= 0){
				mcUsers.getProfile(player).setShovelPreparationMode(false);
				player.sendMessage(ChatColor.GRAY+"**YOU LOWER YOUR SHOVEL**");
			}
		}
    	/*
    	 * AXES ABILITY
    	 */
    	if(mcPermissions.getInstance().axesAbility(player)){
			//Monitor the length of Skull Splitter mode
			if(mcUsers.getProfile(player).getSkullSplitterMode()){
				mcUsers.getProfile(player).decreaseSkullSplitterTicks();
				if(mcUsers.getProfile(player).getSkullSplitterTicks() <= 0){
					mcUsers.getProfile(player).setSkullSplitterMode(false);
					mcUsers.getProfile(player).setSkullSplitterCooldown(120);
					player.sendMessage(ChatColor.RED+"**Skull Splitter has worn off**");
				}
			}
		}
    	/*
		 * WOODCUTTING ABILITY
		 */
		if(mcPermissions.getInstance().woodCuttingAbility(player)){
			if(mcUsers.getProfile(player).getTreeFellerMode()){
				mcUsers.getProfile(player).decreaseTreeFellerTicks();
				if(mcUsers.getProfile(player).getTreeFellerTicks() <= 0){
					mcUsers.getProfile(player).setTreeFellerMode(false);
					mcUsers.getProfile(player).setTreeFellerCooldown(120);
					player.sendMessage(ChatColor.RED+"**Tree Feller has worn off**");
				}
			}
		}
		/*
		 * MINING ABILITY
		 */
		if(mcPermissions.getInstance().miningAbility(player)){
			if(mcUsers.getProfile(player).getSuperBreakerMode()){
				mcUsers.getProfile(player).decreaseSuperBreakerTicks();
				if(mcUsers.getProfile(player).getSuperBreakerTicks() <= 0){
					mcUsers.getProfile(player).setSuperBreakerMode(false);
					mcUsers.getProfile(player).setSuperBreakerCooldown(120);
					player.sendMessage(ChatColor.RED+"**Super Breaker has worn off**");
				}
			}
		}
		/*
		 * EXCAVATION ABILITY
		 */
		if(mcPermissions.getInstance().excavationAbility(player)){
			if(mcUsers.getProfile(player).getGigaDrillBreakerMode()){
				mcUsers.getProfile(player).decreaseGigaDrillBreakerTicks();
				if(mcUsers.getProfile(player).getGigaDrillBreakerTicks() <= 0){
					mcUsers.getProfile(player).setGigaDrillBreakerMode(false);
					mcUsers.getProfile(player).setGigaDrillBreakerCooldown(120);
					player.sendMessage(ChatColor.RED+"**You feel spiral energy leaving you**");
				}
			}
		}
		/*
		 * SWORDS ABILITY
		 */
		if(mcPermissions.getInstance().swordsAbility(player)){
			if(mcUsers.getProfile(player).getSerratedStrikesMode()){
				mcUsers.getProfile(player).decreaseSerratedStrikesTicks();
				if(mcUsers.getProfile(player).getSerratedStrikesTicks() <= 0){
					mcUsers.getProfile(player).setSerratedStrikesMode(false);
					mcUsers.getProfile(player).setSerratedStrikesCooldown(120);
					player.sendMessage(ChatColor.RED+"**Serrated Strikes has worn off**");
				}
			}
		}
		/*
		 * UNARMED ABILITY
		 */
		if(mcPermissions.getInstance().unarmedAbility(player)){
			if(mcUsers.getProfile(player).getBerserkMode()){
				mcUsers.getProfile(player).decreaseBerserkTicks();
				if(mcUsers.getProfile(player).getBerserkTicks() <= 0){
					mcUsers.getProfile(player).setBerserkMode(false);
					mcUsers.getProfile(player).setBerserkCooldown(120);
					player.sendMessage(ChatColor.RED+"**Berserk has worn off**");
				}
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
