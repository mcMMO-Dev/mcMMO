package com.gmail.nossr50.commands.mc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class McabilityCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!mcPermissions.isEnabled()) {
			sender.sendMessage("This command requires permissions.");
			return true;
		}

		if (!LoadProperties.mcabilityEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (PP.getAbilityUse()) {
			player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesOff"));
			PP.toggleAbilityUse();
		} else {
			player.sendMessage(mcLocale.getString("mcPlayerListener.AbilitiesOn"));
			PP.toggleAbilityUse();
		}

		return true;
	}
}
