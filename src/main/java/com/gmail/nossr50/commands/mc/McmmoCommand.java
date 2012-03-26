package com.gmail.nossr50.commands.mc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;

public class McmmoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;

		String description = mcLocale.getString("mcMMO.Description", new Object[] { "mcc" });
		String[] mcSplit = description.split(",");

		for (String x : mcSplit) {
			player.sendMessage(x);
		}

		if (LoadProperties.spoutEnabled && player instanceof SpoutPlayer) {
			SpoutPlayer sPlayer = (SpoutPlayer) player;
			if (LoadProperties.donateMessage)
				sPlayer.sendMessage(ChatColor.YELLOW + "[mcMMO]"+ChatColor.GOLD+" Donate! Paypal "+ChatColor.GREEN+"theno1yeti@gmail.com"); //Where'd our notification go?
		} else {
			if (LoadProperties.donateMessage)
			    player.sendMessage(ChatColor.YELLOW + "[mcMMO]"+ChatColor.GOLD+" Donate! Paypal "+ChatColor.GREEN+"theno1yeti@gmail.com");
		}

		return true;
	}
}
