package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Users;

public class PartyDisbandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
        case 1:
            Party playerParty = Users.getPlayer((Player) sender).getParty();

            for (Player member : playerParty.getOnlineMembers()) {
                if (!PartyManager.handlePartyChangeEvent(member, playerParty.getName(), null, EventReason.KICKED_FROM_PARTY)) {
                    return true;
                }

                member.sendMessage(LocaleLoader.getString("Party.Disband"));
            }

            PartyManager.disbandParty(playerParty);
            return true;

        default:
            sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "disband"));
            return true;
        }
    }
}
