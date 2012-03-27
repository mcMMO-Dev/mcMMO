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
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class AcceptCommand implements CommandExecutor {
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

		if (PP.hasPartyInvite()) {
			Party Pinstance = Party.getInstance();

			if (PP.inParty()) {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, PP.getParty(), PP.getInvite(), EventReason.CHANGED_PARTIES);
                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

				Pinstance.removeFromParty(player, PP);
			}
			else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, PP.getInvite(), EventReason.JOINED_PARTY);
                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }
			}
            PP.acceptInvite();
            Pinstance.addToParty(player, PP, PP.getParty(), true);

		} else {
			player.sendMessage(mcLocale.getString("mcPlayerListener.NoInvites"));
		}

		return true;
	}
}
