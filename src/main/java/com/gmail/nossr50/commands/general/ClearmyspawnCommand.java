package com.gmail.nossr50.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class ClearmyspawnCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.clearmyspawnEnable || !LoadProperties.enableMySpawn) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().mySpawn(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		double x = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getX();
		double y = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getY();
		double z = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getZ();
		String worldname = Bukkit.getServer().getWorlds().get(0).getName();
		PP.setMySpawn(x, y, z, worldname);
		player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnCleared"));

		return true;
	}
}
