package com.gmail.nossr50.commands.party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.mcLocale;

public class PCommand implements CommandExecutor {
	public PCommand() {}

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

			McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent("Console", args[0], pMessage);
			Bukkit.getPluginManager().callEvent(chatEvent);

			if(chatEvent.isCancelled()) return true;

			pMessage = chatEvent.getMessage();

			String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE + "*Console*" + ChatColor.GREEN + ") ";

			Bukkit.getLogger().info("[P](" + args[0] + ")" + "<*Console*> " + pMessage);

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
			if(!PP.inParty()) {
				player.sendMessage("You're not in a party."); //TODO: Use mcLocale
				return true;
			}
			
			String pMessage = args[0];
			for (int i = 1; i <= args.length - 1; i++) {
				pMessage = pMessage + " " + args[i];
			}
			
			PP.togglePartyChat();
			player.chat(pMessage);
			PP.togglePartyChat();

			return true;
		}

		if (PP.getAdminChatMode())
			PP.toggleAdminChat();

		PP.togglePartyChat();

		if (PP.getPartyChatMode()) {
			player.sendMessage(mcLocale.getString("mcPlayerListener.PartyChatOn"));
		} else {
			player.sendMessage(mcLocale.getString("mcPlayerListener.PartyChatOff"));
		}

		return true;
	}
}
