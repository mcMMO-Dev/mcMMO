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

public class ACommand implements CommandExecutor {
	private Logger log;

	public ACommand() {
		this.log = Logger.getLogger("Minecraft");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// Console message?
		if (!(sender instanceof Player) && args.length >= 1) {
			String aMessage = args[0];
			for (int i = 1; i <= args.length - 1; i++) {
				aMessage = aMessage + " " + args[i];
			}

			String aPrefix = ChatColor.AQUA + "{" + ChatColor.WHITE + "*Console*" + ChatColor.AQUA + "} ";

			log.log(Level.INFO, "[A]<*Console*> " + aMessage);

			for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
				if (mcPermissions.getInstance().adminChat(herp) || herp.isOp())
					herp.sendMessage(aPrefix + aMessage);
			}
			return true;
		}

		Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

		if (player != null && !mcPermissions.getInstance().adminChat(player) && !player.isOp()) {
			player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
			return true;
		}

		// Not a toggle, a message

		if (args.length >= 1) {
			String aMessage = args[0];
			for (int i = 1; i <= args.length - 1; i++) {
				aMessage = aMessage + " " + args[i];
			}

			String name = (LoadProperties.aDisplayNames) ? player.getDisplayName() : player.getName();
			String aPrefix = ChatColor.AQUA + "{" + ChatColor.WHITE + name + ChatColor.AQUA + "} ";
			log.log(Level.INFO, "[A]<" + name + "> " + aMessage);
			for (Player herp : Bukkit.getServer().getOnlinePlayers()) {
				if (mcPermissions.getInstance().adminChat(herp) || herp.isOp())
					herp.sendMessage(aPrefix + aMessage);
			}
			return true;
		}

		if(player != null)
		{
			PlayerProfile PP = Users.getProfile(player);
			
			if (PP.getPartyChatMode())
				PP.togglePartyChat();
	
			PP.toggleAdminChat();
	
			if (PP.getAdminChatMode()) {
				player.sendMessage(mcLocale.getString("mcPlayerListener.AdminChatOn"));
				// player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.GREEN + "On");
			} else {
				player.sendMessage(mcLocale.getString("mcPlayerListener.AdminChatOff"));
				// player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.RED + "Off");
			}
		}
		return true;
	}
}
