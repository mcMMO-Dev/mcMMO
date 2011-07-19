package com.gmail.nossr50.skills;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Leaderboard;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;


public class Skills {
	
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	public void updateSQLfromFile(Player player){
		
	}
	public static boolean cooldownOver(Player player, long oldTime, int cooldown){
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
    
    public static int calculateTimeLeft(Player player, long deactivatedTimeStamp, int cooldown)
    {	
    	return (int) (((deactivatedTimeStamp + (cooldown * 1000)) - System.currentTimeMillis())/1000);
    }
    
    public static boolean isAllCooldownsOver(PlayerProfile PP)
    {
    	long t = System.currentTimeMillis();
    	if(t - PP.getGreenTerraDeactivatedTimeStamp() >= (LoadProperties.greenTerraCooldown * 1000) && 
    		t - PP.getTreeFellerDeactivatedTimeStamp() >= (LoadProperties.treeFellerCooldown * 1000) &&
    		t - PP.getSuperBreakerDeactivatedTimeStamp() >= (LoadProperties.superBreakerCooldown * 1000) &&
    		t - PP.getSerratedStrikesDeactivatedTimeStamp() >= (LoadProperties.serratedStrikeCooldown * 1000) &&
    		t - PP.getBerserkDeactivatedTimeStamp() >= (LoadProperties.berserkCooldown * 1000) &&
    		t - PP.getSkullSplitterDeactivatedTimeStamp() >= (LoadProperties.skullSplitterCooldown * 1000) &&
    		t - PP.getGigaDrillBreakerDeactivatedTimeStamp() >= (LoadProperties.gigaDrillBreakerCooldown * 1000))
    	{
    		return true;
    	}
    	else
    		return false;
    }
    public static void watchCooldowns(Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	if(!PP.getGreenTerraInformed() && System.currentTimeMillis() - PP.getGreenTerraDeactivatedTimeStamp() >= (LoadProperties.greenTerraCooldown * 1000)){
			PP.setGreenTerraInformed(true);
    		player.sendMessage(mcLocale.getString("Skills.YourGreenTerra"));
    	}
    	if(!PP.getTreeFellerInformed() && System.currentTimeMillis() - PP.getTreeFellerDeactivatedTimeStamp() >= (LoadProperties.greenTerraCooldown * 1000)){
			PP.setTreeFellerInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourTreeFeller"));
    	}
    	if(!PP.getSuperBreakerInformed() && System.currentTimeMillis() - PP.getSuperBreakerDeactivatedTimeStamp() >= (LoadProperties.superBreakerCooldown * 1000)){
			PP.setSuperBreakerInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourSuperBreaker"));
    	}
    	if(!PP.getSerratedStrikesInformed() && System.currentTimeMillis() - PP.getSerratedStrikesDeactivatedTimeStamp() >= (LoadProperties.serratedStrikeCooldown * 1000)){
			PP.setSerratedStrikesInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourSerratedStrikes"));
    	}
    	if(!PP.getBerserkInformed() && System.currentTimeMillis() - PP.getBerserkDeactivatedTimeStamp() >= (LoadProperties.berserkCooldown * 1000)){
			PP.setBerserkInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourBerserk"));
    	}
    	if(!PP.getSkullSplitterInformed() && System.currentTimeMillis() - PP.getSkullSplitterDeactivatedTimeStamp() >= (LoadProperties.skullSplitterCooldown * 1000)){
			PP.setSkullSplitterInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourSkullSplitter"));
    	}
    	if(!PP.getGigaDrillBreakerInformed() && System.currentTimeMillis() - PP.getGigaDrillBreakerDeactivatedTimeStamp() >= (LoadProperties.gigaDrillBreakerCooldown * 1000)){
			PP.setGigaDrillBreakerInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourGigaDrillBreaker"));
    	}
    }
    public static void hoeReadinessCheck(Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	if(mcPermissions.getInstance().herbalismAbility(player) && m.isHoe(player.getItemInHand()) && !PP.getHoePreparationMode()){
    		if(!PP.getGreenTerraMode() && !cooldownOver(player, PP.getGreenTerraDeactivatedTimeStamp(), LoadProperties.greenTerraCooldown)){
	    		player.sendMessage(mcLocale.getString("Skills.TooTired")
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getGreenTerraDeactivatedTimeStamp(), LoadProperties.greenTerraCooldown)+"s)");
	    		return;
	    	}
    		player.sendMessage(mcLocale.getString("Skills.ReadyHoe"));
			PP.setHoePreparationATS(System.currentTimeMillis());
			PP.setHoePreparationMode(true);
    	}
    }
    public static void monitorSkills(Player player){
		PlayerProfile PP = Users.getProfile(player);
		if(PP != null) 
		{
			if(PP.getHoePreparationMode() && System.currentTimeMillis() - PP.getHoePreparationATS() >= 4000){
				PP.setHoePreparationMode(false);
				player.sendMessage(mcLocale.getString("Skills.LowerHoe"));
			}
			if(PP.getAxePreparationMode() && System.currentTimeMillis() - PP.getAxePreparationATS() >= 4000){
				PP.setAxePreparationMode(false);
				player.sendMessage(mcLocale.getString("Skills.LowerAxe"));
			}
			if(PP.getPickaxePreparationMode() && System.currentTimeMillis() - PP.getPickaxePreparationATS() >= 4000){
				PP.setPickaxePreparationMode(false);
				player.sendMessage(mcLocale.getString("Skills.LowerPickAxe"));
			}
			if(PP.getSwordsPreparationMode() && System.currentTimeMillis() - PP.getSwordsPreparationATS() >= 4000){
				PP.setSwordsPreparationMode(false);
				player.sendMessage(mcLocale.getString("Skills.LowerSword"));
			}
			if(PP.getFistsPreparationMode() && System.currentTimeMillis() - PP.getFistsPreparationATS() >= 4000){
				PP.setFistsPreparationMode(false);
				player.sendMessage(mcLocale.getString("Skills.LowerFists"));
			}
			if(PP.getShovelPreparationMode() && System.currentTimeMillis() - PP.getShovelPreparationATS() >= 4000){
				PP.setShovelPreparationMode(false);
				player.sendMessage(mcLocale.getString("Skills.LowerShovel"));
			}
			
			/*
			 * HERBALISM ABILITY
			 */
			if(mcPermissions.getInstance().herbalismAbility(player)){
				if(PP.getGreenTerraMode() && PP.getGreenTerraDeactivatedTimeStamp() <= System.currentTimeMillis()){
					PP.setGreenTerraMode(false);
					PP.setGreenTerraInformed(false);
					player.sendMessage(mcLocale.getString("Skills.GreenTerraOff"));
				}
			}
			/*
			 * AXES ABILITY
			 */
			if(mcPermissions.getInstance().axesAbility(player)){
				if(PP.getSkullSplitterMode() && PP.getSkullSplitterDeactivatedTimeStamp() <= System.currentTimeMillis()){
						PP.setSkullSplitterMode(false);
						PP.setSkullSplitterInformed(false);
						player.sendMessage(mcLocale.getString("Skills.SkullSplitterOff"));
				}
			}
			/*
			 * WOODCUTTING ABILITY
			 */
			if(mcPermissions.getInstance().woodCuttingAbility(player)){
				if(PP.getTreeFellerMode() && PP.getTreeFellerDeactivatedTimeStamp() <= System.currentTimeMillis()){
						PP.setTreeFellerMode(false);
						PP.setTreeFellerInformed(false);
						player.sendMessage(mcLocale.getString("Skills.TreeFellerOff"));
				}
			}
			/*
			 * MINING ABILITY
			 */
			if(mcPermissions.getInstance().miningAbility(player)){
				if(PP.getSuperBreakerMode() && PP.getSuperBreakerDeactivatedTimeStamp() <= System.currentTimeMillis()){
						PP.setSuperBreakerMode(false);
						PP.setSuperBreakerInformed(false);
						player.sendMessage(mcLocale.getString("Skills.SuperBreakerOff"));
				}
			}
			/*
			 * EXCAVATION ABILITY
			 */
			if(mcPermissions.getInstance().excavationAbility(player)){
				if(PP.getGigaDrillBreakerMode() && PP.getGigaDrillBreakerDeactivatedTimeStamp() <= System.currentTimeMillis()){
						PP.setGigaDrillBreakerMode(false);
						PP.setGigaDrillBreakerInformed(false);
						player.sendMessage(mcLocale.getString("Skills.GigaDrillBreakerOff"));
				}
			}
			/*
			 * SWORDS ABILITY
			 */
			if(mcPermissions.getInstance().swordsAbility(player)){
				if(PP.getSerratedStrikesMode() && PP.getSerratedStrikesDeactivatedTimeStamp() <= System.currentTimeMillis()){
						PP.setSerratedStrikesMode(false);
						PP.setSerratedStrikesInformed(false);
						player.sendMessage(mcLocale.getString("Skills.SerratedStrikesOff"));
				}
			}
			/*
			 * UNARMED ABILITY
			 */
			if(mcPermissions.getInstance().unarmedAbility(player)){
				if(PP.getBerserkMode() && PP.getBerserkDeactivatedTimeStamp() <= System.currentTimeMillis()){
						PP.setBerserkMode(false);
						PP.setBerserkInformed(false);
						player.sendMessage(mcLocale.getString("Skills.BerserkOff"));
				}
			}
		}
	}
	public static void abilityActivationCheck(Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	if(PP != null){
	    	if(!PP.getAbilityUse())
	    		return;
	    	if(mcPermissions.getInstance().miningAbility(player) && m.isMiningPick(player.getItemInHand()) && !PP.getPickaxePreparationMode()){
	    		if(!PP.getSuperBreakerMode() && !cooldownOver(player, PP.getSuperBreakerDeactivatedTimeStamp(), LoadProperties.superBreakerCooldown)){
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getSuperBreakerDeactivatedTimeStamp(), LoadProperties.superBreakerCooldown)+"s)");
		    		return;
		    	}
	    		player.sendMessage(mcLocale.getString("Skills.ReadyPickAxe"));
				PP.setPickaxePreparationATS(System.currentTimeMillis());
				PP.setPickaxePreparationMode(true);
	    	}
	    	if(mcPermissions.getInstance().excavationAbility(player) && m.isShovel(player.getItemInHand()) && !PP.getShovelPreparationMode()){
	    		if(!PP.getGigaDrillBreakerMode() && !cooldownOver(player, PP.getGigaDrillBreakerDeactivatedTimeStamp(), LoadProperties.gigaDrillBreakerCooldown)){
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getGigaDrillBreakerDeactivatedTimeStamp(), LoadProperties.gigaDrillBreakerCooldown)+"s)");
		    		return;
		    	}
	    		player.sendMessage(mcLocale.getString("Skills.ReadyShovel"));
				PP.setShovelPreparationATS(System.currentTimeMillis());
				PP.setShovelPreparationMode(true);
	    	}
	    	if(mcPermissions.getInstance().swordsAbility(player) && m.isSwords(player.getItemInHand()) && !PP.getSwordsPreparationMode()){
	    		if(!PP.getSerratedStrikesMode() && !cooldownOver(player, PP.getSerratedStrikesDeactivatedTimeStamp(), LoadProperties.serratedStrikeCooldown)){
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getSerratedStrikesDeactivatedTimeStamp(), LoadProperties.serratedStrikeCooldown)+"s)");
		    		return;
		    	}
	    		player.sendMessage(mcLocale.getString("Skills.ReadySword"));
				PP.setSwordsPreparationATS(System.currentTimeMillis());
				PP.setSwordsPreparationMode(true);
	    	}
	    	if(mcPermissions.getInstance().unarmedAbility(player) && player.getItemInHand().getTypeId() == 0 && !PP.getFistsPreparationMode()){
		    	if(!PP.getBerserkMode() && !cooldownOver(player, PP.getBerserkDeactivatedTimeStamp(), LoadProperties.berserkCooldown)){
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, PP.getBerserkDeactivatedTimeStamp(), LoadProperties.berserkCooldown)+"s)");
		    		return;
		    	}
		    	player.sendMessage(mcLocale.getString("Skills.ReadyFists"));
				PP.setFistsPreparationATS(System.currentTimeMillis());
				PP.setFistsPreparationMode(true);
	    	}
	    	if((mcPermissions.getInstance().axes(player) || mcPermissions.getInstance().woodcutting(player)) && !PP.getAxePreparationMode()){
	    		if(m.isAxes(player.getItemInHand())){
	    			player.sendMessage(mcLocale.getString("Skills.ReadyAxe"));
	    			PP.setAxePreparationATS(System.currentTimeMillis());
	    			PP.setAxePreparationMode(true);
	    		}
	    	}
    	}
    }
	
	public static void ProcessLeaderboardUpdate(SkillType skillType, Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		
		PlayerStat ps = new PlayerStat();
		ps.statVal = PP.getSkillLevel(skillType);
		ps.name = player.getName();
		Leaderboard.updateLeaderboard(ps, skillType);
	}
	
	public static void XpCheckSkill(SkillType skillType, Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		
		if(PP.getSkillXpLevel(skillType) >= PP.getXpToLevel(skillType))
    	{
			int skillups = 0;
			
			while(PP.getSkillXpLevel(skillType) >= PP.getXpToLevel(skillType))
			{
				skillups++;
				PP.removeXP(skillType, PP.getXpToLevel(skillType));
				PP.skillUp(skillType, 1);
			}
			
			if(!LoadProperties.useMySQL)
				ProcessLeaderboardUpdate(skillType, player);
			
			String firstLetter = skillType.toString().substring(0,1);
			String remainder   = skillType.toString().substring(1);
			String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
			
			player.sendMessage(mcLocale.getString("Skills."+capitalized+"Up", new Object[] {String.valueOf(skillups), PP.getSkillLevel(skillType)}));
		}
	}
	
	public static void XpCheckAll(Player player)
	{
		for(SkillType x : SkillType.values())
		{
			//Don't want to do anything with this one
			if(x == SkillType.ALL)
				continue;
			
			XpCheckSkill(x, player);
		}
	}
    public static SkillType getSkillType(String skillName)
    {
    	for(SkillType x : SkillType.values())
    	{
    		if(x.toString().equals(skillName.toUpperCase()))
    			return x;
    	}
    	return null;
    }
    public static boolean isSkill(String skillname){
    	skillname = skillname.toLowerCase();
    	if(skillname.equals("all")){
    		return true;
    	}
    	else if(skillname.equals("sorcery")){
    		return true;
    	}
    	else if(skillname.equals("taming")){
			return true;
		}
    	else if(skillname.equals("mining")){
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
    public static void arrowRetrievalCheck(Entity entity, mcMMO plugin)
    {
    	if(plugin.misc.arrowTracker.containsKey(entity))
    	{
    		Integer x = 0;
    		while(x < plugin.misc.arrowTracker.get(entity))
    		{
	    		m.mcDropItem(entity.getLocation(), 262);
	    		x++;
    		}
    	}
    	plugin.misc.arrowTracker.remove(entity);
    }
    public static String getSkillStats(String skillname, Integer level, Integer XP, Integer XPToLevel)
    {
    	ChatColor parColor = ChatColor.DARK_AQUA;
    	ChatColor xpColor = ChatColor.GRAY;
    	ChatColor LvlColor = ChatColor.GREEN;
    	ChatColor skillColor = ChatColor.YELLOW;
    	
		return skillColor+skillname+LvlColor+level+parColor+" XP"+"("+xpColor+XP+parColor+"/"+xpColor+XPToLevel+parColor+")";
    }
    public static boolean hasCombatSkills(Player player)
    {
    	if(mcPermissions.getInstance().axes(player) || mcPermissions.getInstance().archery(player) || mcPermissions.getInstance().sorcery(player) || mcPermissions.getInstance().swords(player) || mcPermissions.getInstance().taming(player) || mcPermissions.getInstance().unarmed(player))
    		return true;
    	else
    		return false;
    }
    public static boolean hasGatheringSkills(Player player)
    {
    	if(mcPermissions.getInstance().excavation(player) || mcPermissions.getInstance().herbalism(player) || mcPermissions.getInstance().mining(player) || mcPermissions.getInstance().woodcutting(player))
    		return true;
    	else
    		return false;
    }
    public static boolean hasMiscSkills(Player player)
    {
    	if(mcPermissions.getInstance().acrobatics(player) || mcPermissions.getInstance().repair(player))
    		return true;
    	else
    		return false;
    }
}
