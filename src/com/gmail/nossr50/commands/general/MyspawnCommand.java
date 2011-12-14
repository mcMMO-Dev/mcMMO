package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class MyspawnCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.myspawnEnable || !LoadProperties.enableMySpawn) {
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
		if (System.currentTimeMillis() < (PP.getMySpawnATS() * 1000) + 3600000) {
			long x = (((PP.getMySpawnATS() * 1000) + 3600000) - System.currentTimeMillis());
			int y = (int) (x / 60000);
			int z = (int) ((x / 1000) - (y * 60));
			player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnTimeNotice", new Object[] { y, z }));
			return true;
		}
		PP.setMySpawnATS(System.currentTimeMillis());
		if (PP.getMySpawn(player) != null) {
			Location mySpawn = PP.getMySpawn(player);

			if (mySpawn != null) {
				// It's done twice because it acts oddly when you are in another world
				player.teleport(mySpawn);
				player.teleport(mySpawn);
			}
		} else {
			player.sendMessage(mcLocale.getString("mcPlayerListener.MyspawnNotExist"));
		}

		return true;
	}
}
