package com.gmail.nossr50.commands.spout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.HUDType;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.spout.SpoutStuff;

public class MchudCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!LoadProperties.spoutEnabled) {
			sender.sendMessage("This command is not enabled."); //TODO: Needs more locale.
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command does not support console useage."); //TODO: Needs more locale.
			return true;
		}

		Player player = (Player) sender;
		PlayerProfile PP = Users.getProfile(player);
		
		if(args.length >= 1)
		{
			for(HUDType x : HUDType.values())
			{
				if(x.toString().toLowerCase().equals(args[0].toLowerCase()))
				{
					if(SpoutStuff.playerHUDs.containsKey(player))
					{
						SpoutStuff.playerHUDs.get(player).resetHUD();
						SpoutStuff.playerHUDs.remove(player);
						PP.setHUDType(x);
						SpoutStuff.playerHUDs.put(player, new HUDmmo(player));
					}
				}
			}
		}
		
		return true;
	}
}
