package com.gmail.nossr50.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;

public class XprateCommand implements CommandExecutor {
	private static int oldrate = LoadProperties.xpGainMultiplier;
	
	public static boolean xpevent = false;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			if(args.length <= 0)
			{
				System.out.println(mcLocale.getString("Commands.xprate.proper", new Object[] {"xprate"}));
				System.out.println(mcLocale.getString("Commands.xprate.proper2", new Object[] {"xprate"}));
			}
			
			if(args.length == 1 && args[0].equalsIgnoreCase("reset"))
			{
				if(xpevent)
				{
					for(Player x : Bukkit.getServer().getOnlinePlayers())
						x.sendMessage(mcLocale.getString("Commands.xprate.over"));
					xpevent = !xpevent;
					LoadProperties.xpGainMultiplier = oldrate;
				} else
				{
					LoadProperties.xpGainMultiplier = oldrate;
				}
			}
			
			if(args.length >= 1 && m.isInt(args[0]))
			{
				oldrate = LoadProperties.xpGainMultiplier;
				
				if(args.length >= 2 && (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")))
				{
					if(args[1].equalsIgnoreCase("true"))
						xpevent = true;
					else
						xpevent = false;
				} else
				{
					System.out.println(mcLocale.getString("Commands.xprate.proper3"));
					return true;
				}
				LoadProperties.xpGainMultiplier = m.getInt(args[0]);
				if(xpevent = true)
					for(Player x : Bukkit.getServer().getOnlinePlayers())
					{
						x.sendMessage(ChatColor.GOLD+"XP EVENT FOR mcMMO HAS STARTED!"); //TODO: Needs more locale.
						x.sendMessage(ChatColor.GOLD+"mcMMO XP RATE IS NOW "+LoadProperties.xpGainMultiplier+"x!!"); //TODO: Needs more locale.
					}
				
				System.out.println("The XP RATE was modified to "+LoadProperties.xpGainMultiplier); //TODO: Needs more locale.
			}
			
			return true;
		}
		
		Player player = (Player) sender;
		
		if(!mcPermissions.getInstance().admin(player))
		{
			player.sendMessage(ChatColor.YELLOW+"[mcMMO] "+ChatColor.DARK_RED +mcLocale.getString("mcPlayerListener.NoPermission"));  
			return true;
		}
		if(args.length <= 0)
		{
			player.sendMessage(mcLocale.getString("Commands.xprate.proper", new Object[] {"xprate"}));
			player.sendMessage(mcLocale.getString("Commands.xprate.proper2", new Object[] {"xprate"}));
		}
		if(args.length == 1 && args[0].equalsIgnoreCase("reset"))
		{
			if(xpevent)
			{
				for(Player x : Bukkit.getServer().getOnlinePlayers())
					x.sendMessage(mcLocale.getString("Commands.xprate.over"));
				xpevent = !xpevent;
				LoadProperties.xpGainMultiplier = oldrate;
			} else
			{
				LoadProperties.xpGainMultiplier = oldrate;
			}
		}
		if(args.length >= 1 && m.isInt(args[0]))
		{
			oldrate = LoadProperties.xpGainMultiplier;
			
			if(args.length >= 2 && (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")))
			{
				if(args[1].equalsIgnoreCase("true"))
					xpevent = true;
				else
					xpevent = false;
			} else
			{
				player.sendMessage(mcLocale.getString("Commands.xprate.proper3"));
				return true;
			}
			LoadProperties.xpGainMultiplier = m.getInt(args[0]);
			if(xpevent = true)
				for(Player x : Bukkit.getServer().getOnlinePlayers())
				{
					x.sendMessage(mcLocale.getString("Commands.xprate.started"));
					x.sendMessage(mcLocale.getString("Commands.xprate.started2", new Object[] {LoadProperties.xpGainMultiplier}));
				}
		}
		
		return true;
	}
}
