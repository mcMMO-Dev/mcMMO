package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class MmoeditCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
        if (sender instanceof Player) 
        {
            player = (Player) sender;
        }
		
        if (player != null && !mcPermissions.getInstance().mmoedit(player)) {
			sender.sendMessage("This command requires permissions."); //TODO: Needs more locale.
			return true;
		}

		if (!(sender instanceof Player)) 
		{
			if (args.length < 2) 
			{
				System.out.println("Usage is /mmoedit playername skillname newvalue"); //TODO: Needs more locale.
				return true;
			} else if (args.length == 3)
			{
			    PlayerProfile PPt = null;
			    
			    if(Users.players.containsKey(args[0].toLowerCase())) {
			        PPt = Users.players.get(args[0].toLowerCase());
			    }
			    
			    if(PPt == null)
			        Users.getOfflineProfile(args[0]); //Only grab offline profile if the above failed
			        
			    if(!PPt.isLoaded())
		        {
		            sender.sendMessage("Player does not exist in the database!"); //TODO: Needs more locale.
		            return true;
		        }
			    
				if (m.isInt(args[2]) && Skills.isSkill(args[1])) 
				{
					int newvalue = Integer.valueOf(args[2]);
					Users.getOfflineProfile(args[0]).modifyskill(Skills.getSkillType(args[1]), newvalue);
					System.out.println(args[1] + " has been modified for " + args[0] + "."); //TODO: Needs more locale.
				}
			} else 
			{
				System.out.println("Usage is /mmoedit playername skillname newvalue"); //TODO: Needs more locale.
			}

			return true;
		}

		if (!mcPermissions.getInstance().mmoedit(player)) 
		{
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		
		if (args.length < 2) 
		{
			player.sendMessage(ChatColor.RED + "Usage is /mmoedit playername skillname newvalue"); //TODO: Needs more locale.
			return true;
		}
		if (args.length == 3) 
		{
		    PlayerProfile PPt = null;
            
            if(Users.players.containsKey(args[0].toLowerCase())) {
                PPt = Users.players.get(args[0].toLowerCase());
            }
            
            if(PPt == null)
                Users.getOfflineProfile(args[0]); //Only grab offline profile if the above failed
            
		    if(!PPt.isLoaded())
	        {
	            sender.sendMessage("Player does not exist in the database!"); //TODO: Needs more locale.
	            return true;
	        }
		    
			if (m.isInt(args[2]) && Skills.isSkill(args[1])) 
			{
				int newvalue = Integer.valueOf(args[2]);
				PPt.modifyskill(Skills.getSkillType(args[1]), newvalue);
				player.sendMessage(ChatColor.RED + args[1] + " has been modified for "+args[0]); //TODO: Needs more locale.
			}
		} else if (args.length == 2) 
		{
			if (m.isInt(args[1]) && Skills.isSkill(args[0])) 
			{
			    PlayerProfile PP = Users.getProfile(player);
				int newvalue = Integer.valueOf(args[1]);
				PP.modifyskill(Skills.getSkillType(args[0]), newvalue);
				player.sendMessage(ChatColor.RED + args[0] + " has been modified."); //TODO: Needs more locale.
			}
		} else 
		{
			player.sendMessage(ChatColor.RED + "Usage is /mmoedit playername skillname newvalue"); //TODO: Needs more locale.
		}

		return true;
	}
}
