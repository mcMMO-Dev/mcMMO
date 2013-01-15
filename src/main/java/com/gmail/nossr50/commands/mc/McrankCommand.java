package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Misc;

public class McrankCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		//I'm being lazy and making this only work on yourself, I or someone else will make this work on other players in the future :D
		Leaderboard.updateLeaderboards(); //Make sure the information is up to date
		
		if(arg0 instanceof Player) {
			Player player = (Player) arg0;
			String playerName = player.getName();
			
			arg0.sendMessage(ChatColor.GOLD + "-=PERSONAL RANKINGS=-");
			arg0.sendMessage(ChatColor.RED+"TARGET: "+ChatColor.WHITE+playerName);
			
			if(Config.getInstance().getUseMySQL()) {
				//MySQL Version
			} else {
				for(SkillType skillType : SkillType.values()) {
					if(skillType.equals(SkillType.ALL))
						continue; //We want the overall ranking to be at the bottom
					arg0.sendMessage(ChatColor.YELLOW+Misc.getCapitalized(skillType.name())+ChatColor.GREEN+" - "+ChatColor.GOLD+"Rank "+ChatColor.WHITE+"#"+ChatColor.GREEN+Leaderboard.getPlayerRank(playerName, skillType));
				}
				arg0.sendMessage(ChatColor.YELLOW+"Overall"+ChatColor.GREEN+" - "+ChatColor.GOLD+"Rank "+ChatColor.WHITE+"#"+ChatColor.GREEN+Leaderboard.getPlayerRank(playerName, SkillType.ALL));
			}
		
		} else {
			arg0.sendMessage("Command currently not supported for console.");
		}
		
		return true;
	}
}