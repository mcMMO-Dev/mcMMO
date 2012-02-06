package com.gmail.nossr50.commands.mc;

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

public class McgodCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.mcgodEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (mcPermissions.isEnabled()) {
			if (!mcPermissions.getInstance().mcgod(player)) {
				player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
				return true;
			}

			if (PP.getGodMode()) {
				player.sendMessage(mcLocale.getString("mcPlayerListener.GodModeDisabled"));
				PP.toggleGodMode();
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.GodModeEnabled"));
				PP.toggleGodMode();
			}
		}

		return true;
	}
}
