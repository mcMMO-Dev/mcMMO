package com.gmail.nossr50.commands.party;

import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.gmail.nossr50.party.Party;

public class PCommand implements CommandExecutor {
	private Logger log;

	public PCommand() {
		this.log = Logger.getLogger("Minecraft");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// Console message?
		if (!(sender instanceof Player)) {
			if (args.length < 2)
				return true;
			String pMessage = args[1];
			for (int i = 2; i <= args.length - 1; i++) {
				pMessage = pMessage + " " + args[i];
			}

			String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE + "*Console*" + ChatColor.GREEN + ") ";

			log.log(Level.INFO, "[P](" + args[0] + ")" + "<*Console*> " + pMessage);

			for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
				if (Users.getProfile(herp).inParty()) {
					if (Users.getProfile(herp).getParty().equalsIgnoreCase(args[0])) {
						herp.sendMessage(pPrefix + pMessage);
					}
				}
			}
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);

		if (!mcPermissions.getInstance().party(player)) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		// Not a toggle, a message

		if (args.length >= 1) {
			String pMessage = args[0];
			for (int i = 1; i <= args.length - 1; i++) {
				pMessage = pMessage + " " + args[i];
			}

			String name = (LoadProperties.pDisplayNames) ? player.getDisplayName() : player.getName();
			String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE + name + ChatColor.GREEN + ") ";
			log.log(Level.INFO, "[P](" + PP.getParty() + ")<" + name + "> " + pMessage);

			for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
				if (Users.getProfile(herp).inParty()) {
					if (Party.getInstance().inSameParty(herp, player))
						herp.sendMessage(pPrefix + pMessage);
				}
			}

			return true;
		}

		if (PP.getAdminChatMode())
			PP.toggleAdminChat();

		PP.togglePartyChat();

		if (PP.getPartyChatMode()) {
			// player.sendMessage(ChatColor.GREEN + "Party Chat Toggled On");
			player.sendMessage(mcLocale.getString("mcPlayerListener.PartyChatOn"));
		} else {
			// player.sendMessage(ChatColor.GREEN + "Party Chat Toggled " + ChatColor.RED + "Off");
			player.sendMessage(mcLocale.getString("mcPlayerListener.PartyChatOff"));
		}

		return true;
	}
}
