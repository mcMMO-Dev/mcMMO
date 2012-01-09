package com.gmail.nossr50.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.locale.mcLocale;

public class MmoupdateCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;

		if (!mcPermissions.getInstance().admin(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		player.sendMessage(ChatColor.GRAY + "Starting conversion...");
		Users.clearUsers();
		m.convertToMySQL();
		for (Player x : Bukkit.getServer().getOnlinePlayers()) {
			Users.addUser(x);
		}
		player.sendMessage(ChatColor.GREEN + "Conversion finished!");

		return true;
	}
}
