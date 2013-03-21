package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;

public class PartyRenameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
                Party playerParty = mcMMOPlayer.getParty();

                String oldPartyName = playerParty.getName();
                String newPartyName = args[1];

                // This is to prevent party leaders from spamming other players with the rename message
                if (oldPartyName.equalsIgnoreCase(newPartyName)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Rename.Same"));
                    return true;
                }

                Player player = mcMMOPlayer.getPlayer();
                Party newParty = PartyManager.getParty(newPartyName);

                // Check to see if the party exists, and if it does cancel renaming the party
                if (PartyManager.checkPartyExistence(player, newParty, newPartyName)) {
                    return true;
                }

                String leaderName = playerParty.getLeader();

                for (Player member : playerParty.getOnlineMembers()) {
                    if (!PartyManager.handlePartyChangeEvent(member, oldPartyName, newPartyName, EventReason.CHANGED_PARTIES)) {
                        return true;
                    }

                    if (!member.getName().equalsIgnoreCase(leaderName)) {
                        member.sendMessage(LocaleLoader.getString("Party.InformedOnNameChange", leaderName, newPartyName));
                    }
                }

                playerParty.setName(newPartyName);

                sender.sendMessage(LocaleLoader.getString("Commands.Party.Rename", newPartyName));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "rename", "<" + LocaleLoader.getString("Commands.Usage.PartyName") + ">"));
                return true;
        }
    }
}
