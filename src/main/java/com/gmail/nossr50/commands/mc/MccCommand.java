package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;

public class MccCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.mccEnable) {
			sender.sendMessage("This command is not enabled.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;

		player.sendMessage(ChatColor.RED + "---[]" + ChatColor.YELLOW + "mcMMO Commands" + ChatColor.RED + "[]---");

		if (mcPermissions.getInstance().party(player)) {
			player.sendMessage(mcLocale.getString("m.mccPartyCommands"));
			player.sendMessage(LoadProperties.party + " " + mcLocale.getString("m.mccParty"));
			player.sendMessage(LoadProperties.party + " q " + mcLocale.getString("m.mccPartyQ"));

			if (mcPermissions.getInstance().partyChat(player))
				player.sendMessage("/p " + mcLocale.getString("m.mccPartyToggle"));

			player.sendMessage(LoadProperties.invite + " " + mcLocale.getString("m.mccPartyInvite"));
			player.sendMessage(LoadProperties.accept + " " + mcLocale.getString("m.mccPartyAccept"));

			if (mcPermissions.getInstance().partyTeleport(player))
				player.sendMessage(LoadProperties.ptp + " " + mcLocale.getString("m.mccPartyTeleport"));
		}
		player.sendMessage(mcLocale.getString("m.mccOtherCommands"));
		player.sendMessage(LoadProperties.stats + ChatColor.RED + " " + mcLocale.getString("m.mccStats"));
		player.sendMessage("/mctop <skillname> <page> " + ChatColor.RED + mcLocale.getString("m.mccLeaderboards"));

		if (mcPermissions.getInstance().mySpawn(player)) {
			player.sendMessage(LoadProperties.myspawn + " " + ChatColor.RED + mcLocale.getString("m.mccMySpawn"));
			player.sendMessage(LoadProperties.clearmyspawn + " " + ChatColor.RED + mcLocale.getString("m.mccClearMySpawn"));
		}

		if (mcPermissions.getInstance().mcAbility(player))
			player.sendMessage(LoadProperties.mcability + ChatColor.RED + " " + mcLocale.getString("m.mccToggleAbility"));

		if (mcPermissions.getInstance().adminChat(player))
			player.sendMessage("/a " + ChatColor.RED + mcLocale.getString("m.mccAdminToggle"));

		if (mcPermissions.getInstance().whois(player))
			player.sendMessage(LoadProperties.whois + " " + mcLocale.getString("m.mccWhois"));

		if (mcPermissions.getInstance().mmoedit(player))
			player.sendMessage(LoadProperties.mmoedit + mcLocale.getString("m.mccMmoedit"));

		if (mcPermissions.getInstance().mcgod(player))
			player.sendMessage(LoadProperties.mcgod + ChatColor.RED + " " + mcLocale.getString("m.mccMcGod"));

		player.sendMessage(mcLocale.getString("m.mccSkillInfo"));
		player.sendMessage(LoadProperties.mcmmo + " " + mcLocale.getString("m.mccModDescription"));

		return true;
	}
}
