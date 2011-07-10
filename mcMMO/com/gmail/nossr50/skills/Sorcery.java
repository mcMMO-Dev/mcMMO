package com.gmail.nossr50.skills;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Messages;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Sorcery
{
	mcMMO plugin = null;
	
	public Sorcery(mcMMO pluginx)
	{
		plugin = pluginx;
	}
	
	public Block getSpellTargetBlock(Player player)
	{
		List<Block> blocks = player.getLineOfSight(null, 25);
		for(Block x : blocks)
		{
			Material y = Material.AIR;
			if(x.getType() != y)
				return x;
		}
		return null;
	}
	public void informSelected(String spellname, int cost, Player player)
	{
		player.sendMessage(Messages.getString("Sorcery.SpellSelected", new Object[] {spellname, cost}));
	}
	
	public void informSpell(String spellname, Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		player.sendMessage(Messages.getString("Sorcery.HasCast") +" "+spellname+" "+ Messages.getString("Sorcery.Current_Mana")+ChatColor.YELLOW+"("+ChatColor.GRAY+PP.getCurrentMana()+ChatColor.YELLOW+"/"+ChatColor.GREEN+PP.getMaxMana()+ChatColor.YELLOW+")");
	}
	
	public void shoutSpell(String spellname, Player player)
	{
		for(Player x : plugin.getServer().getOnlinePlayers())
		{
			if(x == player)
				continue;
			if(m.getDistance(x.getLocation(), player.getLocation()) < 35)
			{
				x.sendMessage(ChatColor.GREEN+"-"+ChatColor.DARK_AQUA+"="+ChatColor.GOLD+spellname+ChatColor.DARK_AQUA+"="+ChatColor.GREEN+"-"
						+" "+ChatColor.YELLOW+"["+ChatColor.DARK_RED+player.getName()+ChatColor.YELLOW+"]");
			}
		}
	}
	
	public void handleGreenDyeCycle(Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		
		//The selector is used to account for permissions removing spells from this order.
		
		//Check if player has access to curative spell
		if(!hasCurativeSpellPermission(player))
			PP.setDyeChanged(true);
		
		switch(PP.getGreenDyeCycleSel())
		{
		case 0:
			if(mcPermissions.getInstance().sorceryCurativeHealSelf(player))
			{
				PP.setDyeChanged(true);
				PP.setGreenDyeCycleSel(1);
				PP.setGreenDyeCycle(0);
				
				informSelected(Messages.getString("Sorcery.Curative.Self"), LoadProperties.cure_self, player);
				break;
			}
			
			else
			{
				PP.setGreenDyeCycleSel(1);
			}
		case 1:
			if(mcPermissions.getInstance().sorceryCurativeHealOther(player))
			{
				PP.setDyeChanged(true);
				PP.setGreenDyeCycleSel(0);
				PP.setGreenDyeCycle(1);
				
				informSelected(Messages.getString("Sorcery.Curative.Other"), LoadProperties.cure_other, player);
				break;
			} 
			else
			{
				PP.setGreenDyeCycleSel(0);
			}
		}
	}
	public boolean hasCurativeSpellPermission (Player player)
	{
		if(mcPermissions.getInstance().sorceryCurativeHealOther(player) == false 
				&& mcPermissions.getInstance().sorceryCurativeHealSelf(player) == false)
			return false;
		else
			return true;
	}
	
}
