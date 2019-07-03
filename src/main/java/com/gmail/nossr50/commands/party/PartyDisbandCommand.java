package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyDisbandCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public PartyDisbandCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                Party playerParty = pluginRef.getUserManager().getPlayer((Player) sender).getParty();
                String partyName = playerParty.getName();

                for (Player member : playerParty.getOnlineMembers()) {
                    if (!pluginRef.getPartyManager().handlePartyChangeEvent(member, partyName, null, EventReason.KICKED_FROM_PARTY)) {
                        return true;
                    }

                    member.sendMessage(pluginRef.getLocaleManager().getString("Party.Disband"));
                }

                pluginRef.getPartyManager().disbandParty(playerParty);
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "party", "disband"));
                return true;
        }
    }
}
