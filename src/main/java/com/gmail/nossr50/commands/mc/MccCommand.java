package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.locale.mcLocale;

public class MccCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage.");
			return true;
		}

		Player player = (Player) sender;

		player.sendMessage(ChatColor.RED + "---[]" + ChatColor.YELLOW + "mcMMO Commands" + ChatColor.RED + "[]---"); //TODO: Needs more locale.

		if (mcPermissions.getInstance().party(player)) {
			player.sendMessage(mcLocale.getString("m.mccPartyCommands"));
			player.sendMessage("/party " + mcLocale.getString("m.mccParty"));
			player.sendMessage("/party q " + mcLocale.getString("m.mccPartyQ"));

			if (mcPermissions.getInstance().partyChat(player))
				player.sendMessage("/p " + mcLocale.getString("m.mccPartyToggle"));

			player.sendMessage("/invite " + mcLocale.getString("m.mccPartyInvite"));
			player.sendMessage("/invite " + mcLocale.getString("m.mccPartyAccept"));

			if (mcPermissions.getInstance().partyTeleport(player))
				player.sendMessage("/ptp " + mcLocale.getString("m.mccPartyTeleport"));
		}
		player.sendMessage(mcLocale.getString("m.mccOtherCommands"));
		player.sendMessage("/mcstats " + ChatColor.RED + mcLocale.getString("m.mccStats"));
		player.sendMessage("/mctop <skillname> <page> " + ChatColor.RED + mcLocale.getString("m.mccLeaderboards"));

		if (mcPermissions.getInstance().mcAbility(player))
			player.sendMessage("/mcability " + ChatColor.RED + mcLocale.getString("m.mccToggleAbility"));

		if (mcPermissions.getInstance().adminChat(player))
			player.sendMessage("/a " + ChatColor.RED + mcLocale.getString("m.mccAdminToggle"));

		if (mcPermissions.getInstance().inspect(player))
			player.sendMessage("/inspect " + mcLocale.getString("m.mccInspect"));

		if (mcPermissions.getInstance().mmoedit(player))
			player.sendMessage("/mmoedit " + mcLocale.getString("m.mccMmoedit"));

		if (mcPermissions.getInstance().mcgod(player))
			player.sendMessage("/mcgod " + ChatColor.RED + mcLocale.getString("m.mccMcGod"));

		player.sendMessage(mcLocale.getString("m.mccSkillInfo"));
		player.sendMessage("/mcmmo " + mcLocale.getString("m.mccModDescription"));

		return true;
	}
}
