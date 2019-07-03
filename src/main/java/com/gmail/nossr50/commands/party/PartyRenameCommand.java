package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyRenameCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyRenameCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer((Player) sender);
                Party playerParty = mcMMOPlayer.getParty();

                String oldPartyName = playerParty.getName();
                String newPartyName = args[1];

                // This is to prevent party leaders from spamming other players with the rename message
                if (oldPartyName.equalsIgnoreCase(newPartyName)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Rename.Same"));
                    return true;
                }

                Player player = mcMMOPlayer.getPlayer();

                // Check to see if the party exists, and if it does cancel renaming the party
                if (pluginRef.getPartyManager().checkPartyExistence(player, newPartyName)) {
                    return true;
                }

                String leaderName = playerParty.getLeader().getPlayerName();

                for (Player member : playerParty.getOnlineMembers()) {
                    if (!pluginRef.getPartyManager().handlePartyChangeEvent(member, oldPartyName, newPartyName, EventReason.CHANGED_PARTIES)) {
                        return true;
                    }

                    if (!member.getName().equalsIgnoreCase(leaderName)) {
                        member.sendMessage(pluginRef.getLocaleManager().getString("Party.InformedOnNameChange", leaderName, newPartyName));
                    }
                }

                playerParty.setName(newPartyName);

                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Rename", newPartyName));
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "rename", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.PartyName") + ">"));
                return true;
        }
    }
}
