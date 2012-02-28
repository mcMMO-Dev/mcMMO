package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;

public class PtpCommand implements CommandExecutor {
	private final mcMMO plugin;

	public PtpCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().partyTeleport(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Usage is /ptp <playername>");
			return true;
		}
		if (plugin.getServer().getPlayer(args[0]) == null) {
			player.sendMessage("That is not a valid player");
		}

		if (plugin.getServer().getPlayer(args[0]) != null) {
			Player target = plugin.getServer().getPlayer(args[0]);
			PlayerProfile PPt = Users.getProfile(target);
			if (PP.getParty().equals(PPt.getParty())) {
				player.teleport(target);
				player.sendMessage(ChatColor.GREEN + "You have teleported to " + target.getName());
				target.sendMessage(ChatColor.GREEN + player.getName() + " has teleported to you.");
			}
		}

		return true;
	}
}
