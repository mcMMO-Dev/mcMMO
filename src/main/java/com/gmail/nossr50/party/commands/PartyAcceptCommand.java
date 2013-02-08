package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyAcceptCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.accept")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 1:
            player = (Player) sender;
            mcMMOPlayer = Users.getPlayer(player);

            if (!mcMMOPlayer.hasPartyInvite()) {
                sender.sendMessage(LocaleLoader.getString("mcMMO.NoInvites"));
                return true;
            }

            // Changing parties
            if (mcMMOPlayer.inParty()) {
                playerParty = mcMMOPlayer.getParty();

                if (!PartyManager.handlePartyChangeEvent(player, playerParty.getName(), mcMMOPlayer.getPartyInvite().getName(), EventReason.CHANGED_PARTIES)) {
                    return true;
                }

                PartyManager.removeFromParty(player.getName(), playerParty);
            }
            else if (!PartyManager.handlePartyChangeEvent(player, null, mcMMOPlayer.getPartyInvite().getName(), EventReason.JOINED_PARTY)) {
                return true;
            }

            PartyManager.joinInvitedParty(player, mcMMOPlayer);
            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "accept"));
            return true;
        }
    }
}
