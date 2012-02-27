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
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Leaderboard;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.ToolType;
import com.gmail.nossr50.events.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.locale.mcLocale;

public class Skills 
{
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	public static boolean cooldownOver(Player player, long oldTime, int cooldown){
		long currentTime = System.currentTimeMillis();
		if(currentTime - oldTime >= (cooldown * 1000))
			return true;
		else
			return false;
	}
    
    public static int calculateTimeLeft(Player player, long deactivatedTimeStamp, int cooldown)
    {	
    	return (int) (((deactivatedTimeStamp + (cooldown * 1000)) - System.currentTimeMillis())/1000);
    }
    
    public static void watchCooldown(Player player, PlayerProfile PP, long curTime, SkillType skill)
    {
    	AbilityType ability = skill.getAbility();
    	
    	if(!ability.getInformed(PP) && curTime - (PP.getSkillDATS(ability) * 1000) >= (ability.getCooldown() * 1000))
    	{
    		ability.setInformed(PP, true);
    		player.sendMessage(ability.getAbilityRefresh());
    	}
    }
    
    public static void activationCheck(Player player, SkillType skill)
    {
    	if(LoadProperties.enableOnlyActivateWhenSneaking && !player.isSneaking())
			return;
    	
    	PlayerProfile PP = Users.getProfile(player);
    	AbilityType ability = skill.getAbility();
    	ToolType tool = skill.getTool();
    	
    	if(!PP.getAbilityUse() || PP.getSuperBreakerMode() || PP.getSerratedStrikesMode() || PP.getTreeFellerMode() || PP.getGreenTerraMode() || PP.getBerserkMode() || PP.getGigaDrillBreakerMode())
    		return;
    	
    	if(ability.getPermissions(player) && tool.inHand(player.getItemInHand()) && !tool.getToolMode(PP))
    	{
    		player.sendMessage(mcLocale.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + calculateTimeLeft(player, (PP.getSkillDATS(ability) * 1000), ability.getCooldown()) + "s)");
    		return;
    	}
    	
    	if(LoadProperties.enableAbilityMessages)
    		player.sendMessage(tool.getRaiseTool());
    	
    	tool.setToolATS(PP, System.currentTimeMillis());
    	tool.setToolMode(PP, true);
    }
    
    public static void monitorSkill(Player player, PlayerProfile PP, long curTime, SkillType skill){
    	ToolType tool = skill.getTool();
    	AbilityType ability = skill.getAbility();
    	if(tool.getToolMode(PP) && curTime - (tool.getToolATS(PP) * 1000) >= 4000)
    	{
    		tool.setToolMode(PP, false);
    		player.sendMessage(tool.getLowerTool());
    	}
    	
    	if(ability.getPermissions(player))
    	{
    		if(ability.getMode(PP) && (PP.getSkillDATS(ability) * 1000) <= curTime)
    		{
    			ability.setMode(PP, false);
    			ability.setInformed(PP, false);
    			player.sendMessage(ability.getAbilityOff());
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
				if(skillType.getMaxLevel() >= PP.getSkillLevel(skillType) + 1) 
				{
					skillups++;
					PP.removeXP(skillType, PP.getXpToLevel(skillType));
					PP.skillUp(skillType, 1);
					
					McMMOPlayerLevelUpEvent eventToFire = new McMMOPlayerLevelUpEvent(player, skillType);
					Bukkit.getPluginManager().callEvent(eventToFire);
				} else
				{
					PP.removeXP(skillType, PP.getXpToLevel(skillType));
				}
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
    
    //We should probably rework this - it's a fairly ugly way to do this, compared to our other command formatting.
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
    	if(mcPermissions.getInstance().excavation(player) || mcPermissions.getInstance().fishing(player) || mcPermissions.getInstance().herbalism(player) || mcPermissions.getInstance().mining(player) || mcPermissions.getInstance().woodcutting(player))
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
    
    /**
     * Check to see if an ability can be activated.
     * 
     * @param player The player activating the ability
     * @param type The skill the ability is based on
     */
    public static void abilityCheck(Player player, SkillType type)
    {    	
    	PlayerProfile PP = Users.getProfile(player);
    	AbilityType ability = type.getAbility();
    	if(type.getTool().inHand(player.getItemInHand()))
    	{
    		if(type.getTool().getToolMode(PP))
    			type.getTool().setToolMode(PP, false);
    		
    		int ticks = 2 + (PP.getSkillLevel(type) / 50);
    		if(!ability.getMode(PP) && cooldownOver(player, PP.getSkillDATS(ability), ability.getCooldown()))
    		{
    			player.sendMessage(ability.getAbilityOn());
    			for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ability.getAbilityPlayer(player));
	    		}
    			PP.setSkillDATS(ability, System.currentTimeMillis()+(ticks*1000));
    			ability.setMode(PP, true);
    		}
    	}
    }
}
