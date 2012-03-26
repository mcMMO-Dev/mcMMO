package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class McabilityCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

		if (player != null && !mcPermissions.getInstance().mcAbility(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

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
