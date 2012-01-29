/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.skills;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Leaderboard;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.locale.mcLocale;


public class Skills 
{
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
    
    public static void watchCooldowns(Player player, PlayerProfile PP, long curTime){
    	if(!PP.getGreenTerraInformed() && curTime - (PP.getGreenTerraDeactivatedTimeStamp()*1000) >= (LoadProperties.greenTerraCooldown * 1000)){
			PP.setGreenTerraInformed(true);
    		player.sendMessage(mcLocale.getString("Skills.YourGreenTerra"));
    	}
    	if(!PP.getTreeFellerInformed() && curTime - (PP.getTreeFellerDeactivatedTimeStamp()*1000) >= (LoadProperties.greenTerraCooldown * 1000)){
			PP.setTreeFellerInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourTreeFeller"));
    	}
    	if(!PP.getSuperBreakerInformed() && curTime - (PP.getSuperBreakerDeactivatedTimeStamp()*1000) >= (LoadProperties.superBreakerCooldown * 1000)){
			PP.setSuperBreakerInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourSuperBreaker"));
    	}
    	if(!PP.getSerratedStrikesInformed() && curTime - (PP.getSerratedStrikesDeactivatedTimeStamp()*1000) >= (LoadProperties.serratedStrikeCooldown * 1000)){
			PP.setSerratedStrikesInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourSerratedStrikes"));
    	}
    	if(!PP.getBerserkInformed() && curTime - (PP.getBerserkDeactivatedTimeStamp()*1000) >= (LoadProperties.berserkCooldown * 1000)){
			PP.setBerserkInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourBerserk"));
    	}
    	if(!PP.getSkullSplitterInformed() && curTime - (PP.getSkullSplitterDeactivatedTimeStamp()*1000) >= (LoadProperties.skullSplitterCooldown * 1000)){
			PP.setSkullSplitterInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourSkullSplitter"));
    	}
    	if(!PP.getGigaDrillBreakerInformed() && curTime - (PP.getGigaDrillBreakerDeactivatedTimeStamp()*1000) >= (LoadProperties.gigaDrillBreakerCooldown * 1000)){
			PP.setGigaDrillBreakerInformed(true);
			player.sendMessage(mcLocale.getString("Skills.YourGigaDrillBreaker"));
    	}
    }
    public static void hoeReadinessCheck(Player player)
    {
    	if(LoadProperties.enableOnlyActivateWhenSneaking && !player.isSneaking())
			return;
    	
    	PlayerProfile PP = Users.getProfile(player);
    	if(mcPermissions.getInstance().herbalismAbility(player) && m.isHoe(player.getItemInHand()) && !PP.getHoePreparationMode()){
    		if(!PP.getGreenTerraMode() && !cooldownOver(player, (PP.getGreenTerraDeactivatedTimeStamp()*1000), LoadProperties.greenTerraCooldown)){
	    		player.sendMessage(mcLocale.getString("Skills.TooTired")
	    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, (PP.getGreenTerraDeactivatedTimeStamp()*1000), LoadProperties.greenTerraCooldown)+"s)");
	    		return;
	    	}
    		if(LoadProperties.enableAbilityMessages)
    			player.sendMessage(mcLocale.getString("Skills.ReadyHoe"));
			PP.setHoePreparationATS(System.currentTimeMillis());
			PP.setHoePreparationMode(true);
    	}
    }
    public static void monitorSkills(Player player, PlayerProfile PP, long curTime){
		if(PP.getHoePreparationMode() && curTime - (PP.getHoePreparationATS()*1000) >= 4000){
			PP.setHoePreparationMode(false);
			player.sendMessage(mcLocale.getString("Skills.LowerHoe"));
		}
		if(PP.getAxePreparationMode() && curTime - (PP.getAxePreparationATS()*1000) >= 4000){
			PP.setAxePreparationMode(false);
			player.sendMessage(mcLocale.getString("Skills.LowerAxe"));
		}
		if(PP.getPickaxePreparationMode() && curTime - (PP.getPickaxePreparationATS()*1000) >= 4000){
			PP.setPickaxePreparationMode(false);
			player.sendMessage(mcLocale.getString("Skills.LowerPickAxe"));
		}
		if(PP.getSwordsPreparationMode() && curTime - (PP.getSwordsPreparationATS()*1000) >= 4000){
			PP.setSwordsPreparationMode(false);
			player.sendMessage(mcLocale.getString("Skills.LowerSword"));
		}
		if(PP.getFistsPreparationMode() && curTime - (PP.getFistsPreparationATS()*1000) >= 4000){
			PP.setFistsPreparationMode(false);
			player.sendMessage(mcLocale.getString("Skills.LowerFists"));
		}
		if(PP.getShovelPreparationMode() && curTime - (PP.getShovelPreparationATS()*1000) >= 4000){
			PP.setShovelPreparationMode(false);
			player.sendMessage(mcLocale.getString("Skills.LowerShovel"));
		}
		
		/*
		 * HERBALISM ABILITY
		 */
		if(mcPermissions.getInstance().herbalismAbility(player)){
			if(PP.getGreenTerraMode() && (PP.getGreenTerraDeactivatedTimeStamp()*1000) <= curTime){
				PP.setGreenTerraMode(false);
				PP.setGreenTerraInformed(false);
				player.sendMessage(mcLocale.getString("Skills.GreenTerraOff"));
			}
		}
		/*
		 * AXES ABILITY
		 */
		if(mcPermissions.getInstance().axesAbility(player)){
			if(PP.getSkullSplitterMode() && (PP.getSkullSplitterDeactivatedTimeStamp()*1000) <= curTime){
					PP.setSkullSplitterMode(false);
					PP.setSkullSplitterInformed(false);
					player.sendMessage(mcLocale.getString("Skills.SkullSplitterOff"));
			}
		}
		/*
		 * WOODCUTTING ABILITY
		 */
		if(mcPermissions.getInstance().woodCuttingAbility(player)){
			if(PP.getTreeFellerMode() && (PP.getTreeFellerDeactivatedTimeStamp()*1000) <= curTime){
					PP.setTreeFellerMode(false);
					PP.setTreeFellerInformed(false);
					player.sendMessage(mcLocale.getString("Skills.TreeFellerOff"));
			}
		}
		/*
		 * MINING ABILITY
		 */
		if(mcPermissions.getInstance().miningAbility(player)){
			if(PP.getSuperBreakerMode() && (PP.getSuperBreakerDeactivatedTimeStamp()*1000) <= curTime){
					PP.setSuperBreakerMode(false);
					PP.setSuperBreakerInformed(false);
					player.sendMessage(mcLocale.getString("Skills.SuperBreakerOff"));
			}
		}
		/*
		 * EXCAVATION ABILITY
		 */
		if(mcPermissions.getInstance().excavationAbility(player)){
			if(PP.getGigaDrillBreakerMode() && (PP.getGigaDrillBreakerDeactivatedTimeStamp()*1000) <= curTime){
					PP.setGigaDrillBreakerMode(false);
					PP.setGigaDrillBreakerInformed(false);
					player.sendMessage(mcLocale.getString("Skills.GigaDrillBreakerOff"));
			}
		}
		/*
		 * SWORDS ABILITY
		 */
		if(mcPermissions.getInstance().swordsAbility(player)){
			if(PP.getSerratedStrikesMode() && (PP.getSerratedStrikesDeactivatedTimeStamp()*1000) <= curTime){
					PP.setSerratedStrikesMode(false);
					PP.setSerratedStrikesInformed(false);
					player.sendMessage(mcLocale.getString("Skills.SerratedStrikesOff"));
			}
		}
		/*
		 * UNARMED ABILITY
		 */
		if(mcPermissions.getInstance().unarmedAbility(player)){
			if(PP.getBerserkMode() && (PP.getBerserkDeactivatedTimeStamp()*1000) <= curTime){
					PP.setBerserkMode(false);
					PP.setBerserkInformed(false);
					player.sendMessage(mcLocale.getString("Skills.BerserkOff"));
			}
		}
	}
	public static void abilityActivationCheck(Player player)
	{
		if(LoadProperties.enableOnlyActivateWhenSneaking && !player.isSneaking())
			return;
		
    	PlayerProfile PP = Users.getProfile(player);
    	if(PP != null)
    	{
	    	if(!PP.getAbilityUse() || PP.getSuperBreakerMode() || PP.getSerratedStrikesMode() || PP.getTreeFellerMode() || PP.getGreenTerraMode() || PP.getBerserkMode() || PP.getGigaDrillBreakerMode())
	    		return;
	    	if(mcPermissions.getInstance().miningAbility(player) && m.isMiningPick(player.getItemInHand()) && !PP.getPickaxePreparationMode())
	    	{
	    		if(!PP.getSuperBreakerMode() && !cooldownOver(player, (PP.getSuperBreakerDeactivatedTimeStamp()*1000), LoadProperties.superBreakerCooldown))
	    		{
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, (PP.getSuperBreakerDeactivatedTimeStamp()*1000), LoadProperties.superBreakerCooldown)+"s)");
		    		return;
		    	}
	    		if(LoadProperties.enableAbilityMessages)
	    			player.sendMessage(mcLocale.getString("Skills.ReadyPickAxe"));
				PP.setPickaxePreparationATS(System.currentTimeMillis());
				PP.setPickaxePreparationMode(true);
	    	}
	    	if(mcPermissions.getInstance().excavationAbility(player) && m.isShovel(player.getItemInHand()) && !PP.getShovelPreparationMode())
	    	{
	    		if(!PP.getGigaDrillBreakerMode() && !cooldownOver(player, (PP.getGigaDrillBreakerDeactivatedTimeStamp()*1000), LoadProperties.gigaDrillBreakerCooldown))
	    		{
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, (PP.getGigaDrillBreakerDeactivatedTimeStamp()*1000), LoadProperties.gigaDrillBreakerCooldown)+"s)");
		    		return;
		    	}
	    		if(LoadProperties.enableAbilityMessages)
	    			player.sendMessage(mcLocale.getString("Skills.ReadyShovel"));
				PP.setShovelPreparationATS(System.currentTimeMillis());
				PP.setShovelPreparationMode(true);
	    	}
	    	if(mcPermissions.getInstance().swordsAbility(player) && m.isSwords(player.getItemInHand()) && !PP.getSwordsPreparationMode())
	    	{
	    		if(!PP.getSerratedStrikesMode() && !cooldownOver(player, (PP.getSerratedStrikesDeactivatedTimeStamp()*1000), LoadProperties.serratedStrikeCooldown))
	    		{
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, (PP.getSerratedStrikesDeactivatedTimeStamp()*1000), LoadProperties.serratedStrikeCooldown)+"s)");
		    		return;
		    	}
	    		if(LoadProperties.enableAbilityMessages)
	    			player.sendMessage(mcLocale.getString("Skills.ReadySword"));
				PP.setSwordsPreparationATS(System.currentTimeMillis());
				PP.setSwordsPreparationMode(true);
	    	}
	    	if(mcPermissions.getInstance().unarmedAbility(player) && player.getItemInHand().getTypeId() == 0 && !PP.getFistsPreparationMode())
	    	{
		    	if(!PP.getBerserkMode() && !cooldownOver(player, (PP.getBerserkDeactivatedTimeStamp()*1000), LoadProperties.berserkCooldown))
		    	{
		    		player.sendMessage(mcLocale.getString("Skills.TooTired")
		    				+ChatColor.YELLOW+" ("+calculateTimeLeft(player, (PP.getBerserkDeactivatedTimeStamp()*1000), LoadProperties.berserkCooldown)+"s)");
		    		return;
		    	}
		    	if(LoadProperties.enableAbilityMessages)
		    		player.sendMessage(mcLocale.getString("Skills.ReadyFists"));
				PP.setFistsPreparationATS(System.currentTimeMillis());
				PP.setFistsPreparationMode(true);
	    	}
	    	if((mcPermissions.getInstance().axesAbility(player) || mcPermissions.getInstance().woodCuttingAbility(player)) && !PP.getAxePreparationMode())
	    	{
	    		if(m.isAxes(player.getItemInHand()))
	    		{
	    			if(LoadProperties.enableAbilityMessages)
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
		if(skillType != SkillType.ALL)
			ps.statVal = PP.getSkillLevel(skillType);
		else
			ps.statVal = m.getPowerLevel(player);
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
				
				McMMOPlayerLevelUpEvent eventToFire = new McMMOPlayerLevelUpEvent(player, skillType);
				Bukkit.getPluginManager().callEvent(eventToFire);
			}
			
			if(!LoadProperties.useMySQL)
			{
				ProcessLeaderboardUpdate(skillType, player);
				ProcessLeaderboardUpdate(SkillType.ALL, player);
			}
			
			String capitalized = m.getCapitalized(skillType.toString());
						
			//Contrib stuff
			
			if(LoadProperties.spoutEnabled && player instanceof SpoutPlayer)
			{
				SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
				if(sPlayer.isSpoutCraftEnabled())
				{
					SpoutStuff.levelUpNotification(skillType, sPlayer);
				} else 
				{
					player.sendMessage(mcLocale.getString("Skills."+capitalized+"Up", new Object[] {String.valueOf(skillups), PP.getSkillLevel(skillType)}));
				}
			}
				else
					player.sendMessage(mcLocale.getString("Skills."+capitalized+"Up", new Object[] {String.valueOf(skillups), PP.getSkillLevel(skillType)}));
		}
		if(LoadProperties.xpbar && LoadProperties.spoutEnabled)
		{
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled())
			{
				SpoutStuff.updateXpBar(sPlayer);
			}
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
    	skillname = skillname.toUpperCase();
    	for(SkillType x : SkillType.values())
    	{
    		if(x.toString().equals(skillname))
    			return true;
    	}
    	return false;
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
    	if(mcPermissions.getInstance().axes(player) || mcPermissions.getInstance().archery(player) || mcPermissions.getInstance().swords(player) || mcPermissions.getInstance().taming(player) || mcPermissions.getInstance().unarmed(player))
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
