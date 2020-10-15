package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyMember;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyDisbandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (mcMMO.getUserManager().queryMcMMOPlayer((Player) sender) == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return true;
            }

            Party playerParty = mcMMO.getUserManager().queryMcMMOPlayer((Player) sender).getParty();
            String partyName = playerParty.getPartyName();

            for (PartyMember member : playerParty.getPartyMembers()) {
                if (!mcMMO.getPartyManager().handlePartyChangeEvent(member, partyName, null, EventReason.KICKED_FROM_PARTY)) {
                    return true;
                }

                member.sendMessage(LocaleLoader.getString("Party.Disband"));
            }

            mcMMO.getPartyManager().disbandParty(playerParty);
            return true;
        }
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "disband"));
        return true;
    }
}
