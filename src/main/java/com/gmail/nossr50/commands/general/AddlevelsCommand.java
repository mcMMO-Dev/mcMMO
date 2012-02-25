package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class AddlevelsCommand implements CommandExecutor{
	private final mcMMO plugin;

	public AddlevelsCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
		
        if (player != null && !mcPermissions.getInstance().mmoedit(player)) {
			sender.sendMessage("This command requires permissions.");
			return true;
		}

		if (!LoadProperties.addlevelsEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				System.out.println("Usage is /addlevels playername skillname levels");
				return true;
			} else if (args.length == 3) {
				if ((plugin.getServer().getPlayer(args[0]) != null) && m.isInt(args[2]) && Skills.isSkill(args[1])) {
					int levels = Integer.valueOf(args[2]);
					Users.getProfile(plugin.getServer().getPlayer(args[0])).addLevels(Skills.getSkillType(args[1]), levels);
					System.out.println(args[1] + " has been modified for " + plugin.getServer().getPlayer(args[0]).getName() + ".");
				}
			} else {
				System.out.println("Usage is /addlevels playername skillname levels");
			}

			return true;
		}

		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().mmoedit(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage is /addlevels playername skillname levels");
			return true;
		}
		if (args.length == 3) {
			if ((plugin.getServer().getPlayer(args[0]) != null) && m.isInt(args[2]) && Skills.isSkill(args[1])) {
				int levels = Integer.valueOf(args[2]);
				Users.getProfile(plugin.getServer().getPlayer(args[0])).addLevels(Skills.getSkillType(args[1]), levels);
				player.sendMessage(ChatColor.RED + args[1] + " has been modified.");
			}
		} else if (args.length == 2) {
			if (m.isInt(args[1]) && Skills.isSkill(args[0])) {
				int levels = Integer.valueOf(args[1]);
				PP.addLevels(Skills.getSkillType(args[0]), levels);
				player.sendMessage(ChatColor.RED + args[0] + " has been modified.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "Usage is /addlevels playername skillname newvalue");
		}

		return true;
	}
}
