package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class InviteCommand implements CommandExecutor {
	private final mcMMO plugin;

	public InviteCommand(mcMMO instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().party(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		Party Pinstance = Party.getInstance();

		if (!PP.inParty()) {
			player.sendMessage(mcLocale.getString("mcPlayerListener.NotInParty"));
			return true;
		}
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Usage is /invite <playername>"); //TODO: Needs more locale.
			return true;
		}
		if (PP.inParty() && args.length >= 1 && (plugin.getServer().getPlayer(args[0]) != null)) {
			if (Pinstance.canInvite(player, PP)) {
				Player target = plugin.getServer().getPlayer(args[0]);
				PlayerProfile PPt = Users.getProfile(target);
				PPt.modifyInvite(PP.getParty());

				player.sendMessage(mcLocale.getString("mcPlayerListener.InviteSuccess"));
				target.sendMessage(mcLocale.getString("mcPlayerListener.ReceivedInvite1", new Object[] { PPt.getInvite(), player.getName() }));
				target.sendMessage(mcLocale.getString("mcPlayerListener.ReceivedInvite2", new Object[] { "accept" }));
			} else {
				player.sendMessage(mcLocale.getString("Party.Locked"));
				return true;
			}
		}

		return true;
	}
}
