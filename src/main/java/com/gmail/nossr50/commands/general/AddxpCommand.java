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
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class AddxpCommand implements CommandExecutor {
	private final mcMMO plugin;

	public AddxpCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!mcPermissions.permissionsEnabled) {
			sender.sendMessage("This command requires permissions.");
			return true;
		}

		if (!LoadProperties.addxpEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				// No console aliasing yet
				// System.out.println("Usage is /"+LoadProperties.addxp+" playername skillname xp");
				System.out.println("Usage is /addxp playername skillname xp");
				return true;
			} else if (args.length == 3) {
				if ((plugin.getServer().getPlayer(args[0]) != null) && m.isInt(args[2]) && Skills.isSkill(args[1])) {
					int newvalue = Integer.valueOf(args[2]);
					Users.getProfile(plugin.getServer().getPlayer(args[0])).addXP(Skills.getSkillType(args[1]), newvalue, plugin.getServer().getPlayer(args[0]));
					plugin.getServer().getPlayer(args[0]).sendMessage(ChatColor.GREEN + "Experience granted!");
					System.out.println(args[1] + " has been modified for " + plugin.getServer().getPlayer(args[0]).getName() + ".");
					Skills.XpCheckAll(plugin.getServer().getPlayer(args[0]));
				}
			} else {
				// No console aliasing yet
				// System.out.println("Usage is /"+LoadProperties.addxp+" playername skillname xp");
				System.out.println("Usage is /addxp playername skillname xp");
			}
			return true;
		}

		Player player = (Player) sender;

		if (!mcPermissions.getInstance().mmoedit(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage is /addxp playername skillname xp");
			return true;
		}
		if (args.length == 3) {
			if ((plugin.getServer().getPlayer(args[0]) != null) && m.isInt(args[2]) && Skills.isSkill(args[1])) {
				int newvalue = Integer.valueOf(args[2]);
				Users.getProfile(plugin.getServer().getPlayer(args[0])).addXP(Skills.getSkillType(args[1]), newvalue, plugin.getServer().getPlayer(args[0]));
				plugin.getServer().getPlayer(args[0]).sendMessage(ChatColor.GREEN + "Experience granted!");
				player.sendMessage(ChatColor.RED + args[1] + " has been modified.");
				Skills.XpCheckAll(plugin.getServer().getPlayer(args[0]));
			}
		} else if (args.length == 2 && m.isInt(args[1]) && Skills.isSkill(args[0])) {
			int newvalue = Integer.valueOf(args[1]);
			Users.getProfile(player).addXP(Skills.getSkillType(args[0]), newvalue, player);
			player.sendMessage(ChatColor.RED + args[0] + " has been modified.");
		} else {
			player.sendMessage(ChatColor.RED + "Usage is /addxp playername skillname xp");
		}

		return true;
	}
}
